package com.softwareoverflow.hangtight.repository.billing

import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.softwareoverflow.hangtight.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.min

class BillingRepository(
    private val applicationContext: Context
) : PurchasesUpdatedListener, BillingClientStateListener, PurchasesResponseListener,
    ProductDetailsResponseListener, DefaultLifecycleObserver, AcknowledgePurchaseResponseListener {

    private val externalScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _oneTimeProductPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val oneTimeProductPurchases = _oneTimeProductPurchases.asStateFlow()

    val oneTimeProductWithProductDetails = MutableLiveData<ProductDetails?>()

    /**
     * Cached in-app product purchases details.
     */
    private var cachedPurchasesList: List<Purchase>? = null

    /**
     * Instantiate a new BillingClient instance.
     */
    private var billingClient =
        BillingClient.newBuilder(applicationContext).setListener(this).enablePendingPurchases()
            .build()


    private var connectionRetryCount = 0

    init {
        if (!billingClient.isReady) {
            Timber.d("BillingClient: Start connection...")
            billingClient.startConnection(this)
        }
    }

    override fun onBillingServiceDisconnected() {

        externalScope.launch {
            connectionRetryCount++

            val delay = min(connectionRetryCount * 2000L, 300000L)
            Timber.i("Billing Service disconnected. Reconnection attempt $connectionRetryCount will be tried in ${delay}ms")

            delay(delay)
            billingClient.startConnection(this@BillingRepository)
        }

    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("onBillingSetupFinished: $responseCode $debugMessage")

        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready.
            // You can query product details and purchases here.
            queryOneTimeProductDetails()
            queryOneTimeProductPurchases()
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult, purchases: MutableList<Purchase>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Timber.d("onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("onPurchasesUpdated: User canceled the purchase")
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Timber.i("onPurchasesUpdated: The user already owns this item")
            }

            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Timber.e(
                    "onPurchasesUpdated: Developer error means that Google Play does not recognize the configuration. If you are just getting started, make sure you have configured the application correctly in the Google Play Console. The product ID must match and the APK you are using must be signed with release keys."
                )
            }
        }
    }

    private fun queryOneTimeProductDetails() {
        Timber.d("queryOneTimeProductDetails")
        val params = QueryProductDetailsParams.newBuilder()

        val productList = INAPP_SKUS.map { product ->
            QueryProductDetailsParams.Product.newBuilder().setProductId(product)
                .setProductType(BillingClient.ProductType.INAPP).build()
        }

        params.apply {
            setProductList(productList)
        }.let { productDetailsParams ->
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }
    }

    /**
     * Query Google Play Billing for existing one-time product purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    fun queryOneTimeProductPurchases() {
        if (!billingClient.isReady) {
            Timber.e("queryOneTimeProductPurchases: BillingClient is not ready")
            billingClient.startConnection(this)
        }

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build(), this
        )
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult, purchasesList: MutableList<Purchase>
    ) {
        processPurchases(purchasesList)
    }

    /**
     * Send purchase to StateFlow, which will trigger network call to verify the subscriptions
     * on the sever.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Timber.d("processPurchases: ${purchasesList?.size} purchase(s)")
        purchasesList?.let { list ->
            if (isUnchangedPurchaseList(list)) {
                Timber.d("processPurchases: Purchase list has not changed")
                return
            }
            externalScope.launch {
                val oneTimeProductPurchaseList = list.filter { purchase ->
                    purchase.products.contains(PRO_UPGRADE)
                }

                // TODO future version try implementing API call to https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/products/{productId}/tokens/{token}

                _oneTimeProductPurchases.emit(oneTimeProductPurchaseList)

                externalScope.launch {
                    acknowledgePurchases(list)
                }
            }

        }
    }

    private suspend fun acknowledgePurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {

                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    val ackPurchaseResult = withContext(Dispatchers.IO) {
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                    }

                    Timber.i("Attempting acknowledge of purchase ${purchase.orderId}. Result ${ackPurchaseResult.responseCode} - ${ackPurchaseResult.debugMessage}")
                }
            }
        }
    }

    override fun onAcknowledgePurchaseResponse(result: BillingResult) {
        Timber.i("Acknowledge response ${result.responseCode} - ${result.debugMessage}")
    }

    /**
     * Check whether the purchases have changed before posting changes.
     */
    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>): Boolean {
        val isUnchanged = purchasesList == cachedPurchasesList
        if (!isUnchanged) {
            cachedPurchasesList = purchasesList
        }
        return isUnchanged
    }

    /**
     * Receives the result from [querySubscriptionProductDetails].
     *
     * Store the ProductDetails and post them in the [basicSubProductWithProductDetails] and
     * [premiumSubProductWithProductDetails]. This allows other parts of the app to use the
     *  [ProductDetails] to show product information and make purchases.
     *
     * onProductDetailsResponse() uses method calls from GPBL 5.0.0. PBL5, released in May 2022,
     * is backwards compatible with previous versions.
     * To learn more about this you can read:
     * https://developer.android.com/google/play/billing/compatibility
     */
    override fun onProductDetailsResponse(
        billingResult: BillingResult, productDetailsList: MutableList<ProductDetails>
    ) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                processProductDetails(productDetailsList)
            }

            response.isTerribleFailure -> {
                // These response codes are not expected.
                Timber.w("onProductDetailsResponse - Unexpected error: ${response.code} $debugMessage")
            }

            else -> {
                Timber.e("onProductDetailsResponse: ${response.code} $debugMessage")
            }

        }
    }

    /**
     * This method is used to process the product details list returned by the [BillingClient]
     *
     * @param productDetailsList The list of product details.
     *
     */
    private fun processProductDetails(productDetailsList: MutableList<ProductDetails>) {
        val expectedProductDetailsCount = INAPP_SKUS.size
        if (productDetailsList.isEmpty()) {
            Timber.e(
                "processProductDetails: Expected ${expectedProductDetailsCount}, Found null ProductDetails. Check to see if the products you requested are correctly published in the Google Play Console."
            )
            postProductDetails(emptyList())
        } else {
            postProductDetails(productDetailsList)
        }
    }

    /**
     * This method is used to post the product details to the [basicSubProductWithProductDetails]
     * and [premiumSubProductWithProductDetails] live data.
     *
     * @param productDetailsList The list of product details.
     *
     */
    private fun postProductDetails(productDetailsList: List<ProductDetails>) {
        productDetailsList.forEach { productDetails ->
            when (productDetails.productType) {
                BillingClient.ProductType.INAPP -> {
                    if (productDetails.productId == PRO_UPGRADE) {
                        oneTimeProductWithProductDetails.postValue(productDetails)
                    }
                }
            }
        }
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
        if (!billingClient.isReady) {
            Timber.e("launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(activity, params)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    /**
     * ONLY to be used in debug
     */
    suspend fun consumePurchase(consumeParams: ConsumeParams): ConsumeResult? {
        if (BuildConfig.DEBUG) {
            return billingClient.consumePurchase(consumeParams)
        }
        return null
    }

    companion object {
        const val PRO_UPGRADE = "upgrade_to_pro"

        val INAPP_SKUS = listOf(PRO_UPGRADE)

        private const val MAX_RETRY_ATTEMPT = 3
    }
}

@JvmInline
private value class BillingResponse(val code: Int) {
    val isOk: Boolean
        get() = code == BillingClient.BillingResponseCode.OK
    val canFailGracefully: Boolean
        get() = code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    val isRecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ERROR,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
        )
    val isNonrecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
        )
    val isTerribleFailure: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
            BillingClient.BillingResponseCode.USER_CANCELED,
        )
}
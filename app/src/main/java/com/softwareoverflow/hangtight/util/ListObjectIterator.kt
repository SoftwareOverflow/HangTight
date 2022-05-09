package com.softwareoverflow.hangtight.util

/**
 * Helper class to ensure calls to [ListIterator.previous] & [ListIterator.next]
 * retrieve the previous and next objects respectively, relative to the last retrieved object.
 * Default ListIterator impl moves a cursor, so calling .previous then .next will return the same object. This class will return different list objects
 */
class ListObjectIterator<T>(private val iterator: ListIterator<T>) {

    private var nextWasCalled = false
    private var previousWasCalled = false

    /**
     * Returns the next element in the list relative to the most recent element retrieved
     */
    fun next(): T {
        nextWasCalled = true

        if (previousWasCalled) {
            previousWasCalled = false
            iterator.next()
        }

        return iterator.next()
    }

    /**
     * Returns the previous element in the list relative to the most recent element retrieved
     * Calling when at the first object will throw [NoSuchElementException]
     */
    fun previous(): T {
        previousWasCalled = true

        if (nextWasCalled) {
            nextWasCalled = false
            iterator.previous()
        }

        return iterator.previous()
    }

    /**
     * Tries to get the previous element in the iterator in a null-safe way
     */
    fun tryGetPrevious() : T? {
        return try {
            previous()
        } catch (e: NoSuchElementException) {
            previousWasCalled = false
            null
        }
    }

    /**
     * Tries to get the next element in the iterator in a null-safe way
     */
    fun tryGetNext() : T? {
        return try {
            next()
        } catch (e: NoSuchElementException) {
            nextWasCalled = false
            null
        }
    }
}
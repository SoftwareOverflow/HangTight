package com.softwareoverflow.hangtight.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.softwareoverflow.hangtight.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class for misc helper methods
 */
public class SharedPreferenceHelper {

    private static final int NOT_FOUND = -999;

    private static final Map<String, Integer> soundMap = new LinkedHashMap<String, Integer>() {{
        put("beep", R.raw.beep); put("blooper", R.raw.blooper); put("censor", R.raw.censor);
        put("ding", R.raw.ding); put("ring", R.raw.ding);
    }};

    public static Integer getSoundId(String soundName){
        return soundMap.get(soundName.toLowerCase());
    }

    /**
     * Handles backwards compatibility for old way of saving sound object
     * @param preferences The SharedPreferences.Editor object
     * @return the Id of the sound object saved in the shared prefs. Null if user has selected no sounds.
     */
    public static Integer getSavedSound(@NonNull SharedPreferences preferences){
        String soundName = getSavedSoundName(preferences);

        if(soundName.equals("None")) return null;
        else return soundMap.get(soundName.toLowerCase());
    }

    private static String getSavedSoundName(@NonNull  SharedPreferences preferences) {
        // Try to load using the newer method
        String soundName = preferences.getString("soundName", null);

        if(soundName != null){
            return soundName;
        }

        // Fall-back to older method
        boolean playSounds = preferences.getBoolean("sound", true);
        if (playSounds) {
            int soundIndex = preferences.getInt("beepSound", NOT_FOUND);
            if(soundIndex == NOT_FOUND) soundName = "None";
            else soundName = soundMap.keySet().toArray(new String[0])[soundIndex];
        } else {
            soundName = "None";
        }

        // Update to newer method
        preferences.edit().putString("soundName", soundName).apply();

        return soundName;
    }

    public static int getSavedSoundIndex(Context context,  SharedPreferences preferences){
        String soundName = getSavedSoundName(preferences);
        String[] keys = context.getResources().getStringArray(R.array.spinner_sound_options);

        for(int i=0; i<keys.length; i++)
            if(keys[i].equals(soundName))
                return i;

        return 0;
    }
}

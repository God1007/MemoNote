package com.example.memonote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {
    private static final String PREF_NAME = "secure_prefs";
    private static final String KEY_PASSWORD = "memo_password";
    private final SharedPreferences preferences;

    public PasswordManager(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasPassword() {
        return preferences.contains(KEY_PASSWORD);
    }

    public void savePassword(String password) {
        preferences.edit().putString(KEY_PASSWORD, hash(password)).apply();
    }

    public boolean validate(String password) {
        String stored = preferences.getString(KEY_PASSWORD, null);
        return stored != null && stored.equals(hash(password));
    }

    private String hash(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return input;
        }
    }
}

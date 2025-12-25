package com.example.demo.domain.academic;

import com.example.demo.domain.AcademicProfile;


public final class StreamKeyUtil {

    private StreamKeyUtil() {
    }

    public static String forProfile(AcademicProfile profile, String fallbackSubject) {
        if (profile == null) {
            return normalize(fallbackSubject);
        }

        String raw = profile.getEducationLevel() + "_" +
                     profile.getMainStream() + "_" +
                     profile.getSpecialization();

        return normalize(raw);
    }

    private static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "general";
        }
        return raw.toLowerCase()
                  .trim()
                 
                  .replaceAll("[^a-z0-9]+", "_")
                
                  .replaceAll("^_+|_+$", "");
    }
}
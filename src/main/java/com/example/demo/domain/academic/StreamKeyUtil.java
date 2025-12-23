package com.example.demo.domain.academic;

import com.example.demo.domain.AcademicProfile;

/**
 * AcademicProfile se ek normalized stream key banata hai.
 * Example:
 *   level = "Graduation", stream = "Engineering", spec = "Computer Science"
 *   -> "graduation_engineering_computer_science"
 */
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
                  // saare non a-z/0-9 ko underscore
                  .replaceAll("[^a-z0-9]+", "_")
                  // starting / ending extra underscores hata do
                  .replaceAll("^_+|_+$", "");
    }
}
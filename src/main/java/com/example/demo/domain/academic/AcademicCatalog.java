package com.example.demo.domain.academic;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class AcademicCatalog {

    private AcademicCatalog() {
    }


    public static final Set<String> EDUCATION_LEVELS = new HashSet<>();
    public static final Set<String> MAIN_STREAMS     = new HashSet<>();
    public static final Set<String> SPECIALIZATIONS  = new HashSet<>();


    private static final Set<String> VALID_COMBINATIONS = new HashSet<>();



    static {

        // ---------- 11th ----------
        add("11th", "Science", "PCM");
        add("11th", "Science", "PCB");
        add("11th", "Science", "PCMB");
        add("11th", "Science", "Science General");

        add("11th", "Commerce", "Commerce with Maths");
        add("11th", "Commerce", "Commerce without Maths");

        add("11th", "Arts", "Humanities / Arts");

        // ---------- 12th ----------
        add("12th", "Science", "PCM");
        add("12th", "Science", "PCB");
        add("12th", "Science", "PCMB");
        add("12th", "Science", "Science General");

        add("12th", "Commerce", "Commerce with Maths");
        add("12th", "Commerce", "Commerce without Maths");

        add("12th", "Arts", "Humanities / Arts");

        // ---------- Diploma ----------
        add("Diploma", "Engineering", "Computer Science");
        add("Diploma", "Engineering", "Information Technology");
        add("Diploma", "Engineering", "Mechanical");
        add("Diploma", "Engineering", "Civil");
        add("Diploma", "Engineering", "Electrical");
        add("Diploma", "Engineering", "Electronics & Communication");
        add("Diploma", "Engineering", "Automobile");

        add("Diploma", "Medical", "Nursing");
        add("Diploma", "Medical", "Pharmacy");
        add("Diploma", "Medical", "Medical Lab Technology");

        add("Diploma", "IT & Computer", "Software Engineering");
        add("Diploma", "IT & Computer", "Cyber Security");
        add("Diploma", "IT & Computer", "Data Science Basics");

        // ---------- Graduation ----------
        // Engineering
        add("Graduation", "Engineering", "Computer Science");
        add("Graduation", "Engineering", "Information Technology");
        add("Graduation", "Engineering", "Electronics & Communication");
        add("Graduation", "Engineering", "Electrical");
        add("Graduation", "Engineering", "Mechanical");
        add("Graduation", "Engineering", "Civil");
        add("Graduation", "Engineering", "Chemical");
        add("Graduation", "Engineering", "Aerospace");
        add("Graduation", "Engineering", "Automobile");
        add("Graduation", "Engineering", "Biomedical");

        // IT non-engineering
        add("Graduation", "IT & Computer", "BCA");
        add("Graduation", "IT & Computer", "BSc Computer Science");
        add("Graduation", "IT & Computer", "BSc IT");
        add("Graduation", "IT & Computer", "Data Science");
        add("Graduation", "IT & Computer", "AI & ML");
        add("Graduation", "IT & Computer", "Cyber Security");

        // Medical
        add("Graduation", "Medical", "MBBS");
        add("Graduation", "Medical", "BDS");
        add("Graduation", "Medical", "BAMS");
        add("Graduation", "Medical", "BHMS");
        add("Graduation", "Medical", "BSc Nursing");
        add("Graduation", "Medical", "Physiotherapy");
        add("Graduation", "Medical", "Pharmacy");

        // Commerce / Management
        add("Graduation", "Commerce", "BCom General");
        add("Graduation", "Commerce", "BCom Honours");
        add("Graduation", "Management", "BBA");
        add("Graduation", "Management", "Hotel Management");

        // Arts / Others
        add("Graduation", "Arts", "BA Humanities");
        add("Graduation", "Arts", "BA Psychology");
        add("Graduation", "Arts", "BA Political Science");
        add("Graduation", "Arts", "BA Economics");

        // Law
        add("Graduation", "Law", "BA LLB");
        add("Graduation", "Law", "BBA LLB");

        // Design
        add("Graduation", "Design", "BDes");
        add("Graduation", "Design", "Graphic Design");

        // ---------- Masters ----------
        add("Masters", "IT & Computer", "MCA");
        add("Masters", "IT & Computer", "MSc Computer Science");
        add("Masters", "IT & Computer", "Data Science");
        add("Masters", "IT & Computer", "AI & ML");

        add("Masters", "Engineering", "MTech Computer Science");
        add("Masters", "Engineering", "MTech Information Technology");
        add("Masters", "Engineering", "MTech Electronics");
        add("Masters", "Engineering", "MTech Mechanical");
        add("Masters", "Engineering", "MTech Civil");

        add("Masters", "Management", "MBA");
        add("Masters", "Management", "MBA Finance");
        add("Masters", "Management", "MBA Marketing");
        add("Masters", "Management", "MBA Operations");

        add("Masters", "Commerce", "MCom");

        add("Masters", "Medical", "MD");
        add("Masters", "Medical", "MS");
        add("Masters", "Medical", "MDS");

        add("Masters", "Arts", "MA Economics");
        add("Masters", "Arts", "MA Psychology");
        add("Masters", "Arts", "MA Political Science");

        // ---------- Doctorate ----------
        add("Doctorate", "IT & Computer", "PhD Computer Science");
        add("Doctorate", "IT & Computer", "PhD Data Science");

        // ---------- Professional ----------
        add("Professional", "IT & Computer", "Cloud Computing");
        add("Professional", "IT & Computer", "DevOps");
        add("Professional", "IT & Computer", "Data Engineering");
        add("Professional", "IT & Computer", "Cyber Security");

        add("Professional", "Management", "Project Management");
        add("Professional", "Management", "Product Management");

        add("Professional", "Design", "UI/UX Design");
        add("Professional", "Other", "Digital Marketing");
    }


    private static void add(String level, String stream, String specialization) {
        if (level != null && !level.isBlank()) EDUCATION_LEVELS.add(level);
        if (stream != null && !stream.isBlank()) MAIN_STREAMS.add(stream);
        if (specialization != null && !specialization.isBlank()) SPECIALIZATIONS.add(specialization);

        VALID_COMBINATIONS.add(comboKey(level, stream, specialization));
    }


    private static String comboKey(String level, String stream, String specialization) {
        return norm(level) + "|" + norm(stream) + "|" + norm(specialization);
    }

    private static String norm(String s) {
        return (s == null) ? "" : s.trim().toUpperCase(Locale.ROOT);
    }

    public static Set<String> getValidCombos() {
        return VALID_COMBINATIONS;
    }
    public static boolean isValidCombination(String level, String stream, String specialization) {

        if (norm(level).equals("10TH")) return true;

        if (stream == null || stream.isBlank()) return false;

        if (specialization == null || specialization.isBlank()) return true;

        return VALID_COMBINATIONS.contains(comboKey(level, stream, specialization));
    }
public static Set<String> getValidCombinationKeys() {
    return new HashSet<>(VALID_COMBINATIONS);
}
}
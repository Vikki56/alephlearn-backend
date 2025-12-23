package com.example.demo.domain;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public final class AcademicCatalog {

    private AcademicCatalog() {}

    // 1) Education Level (top dropdown)
    public static final List<String> EDUCATION_LEVELS = List.of(
            "10th",
            "11-12",
            "Diploma",
            "Undergraduate",
            "Postgraduate",
            "PhD / Research"
    );

    // 2) For each education level → available main streams
    public static final Map<String, List<String>> MAIN_STREAMS = Map.ofEntries(
            entry("10th", List.of("General")),

            entry("11-12", List.of(
                    "Science",
                    "Commerce",
                    "Arts / Humanities"
            )),

            entry("Diploma", List.of(
                    "Engineering Diploma",
                    "Computer Applications",
                    "Other"
            )),

            entry("Undergraduate", List.of(
                    "Engineering",
                    "Medical",
                    "Science",
                    "Commerce",
                    "Arts / Humanities",
                    "Management",
                    "Law",
                    "Design / Creative",
                    "Computer Applications"
            )),

            entry("Postgraduate", List.of(
                    "Engineering",
                    "Science",
                    "Commerce",
                    "Arts / Humanities",
                    "Management",
                    "Law",
                    "Medical",
                    "Other"
            )),

            entry("PhD / Research", List.of(
                    "Engineering",
                    "Science",
                    "Management",
                    "Other"
            ))
    );

    // 3) For each main stream → specializations
    public static final Map<String, List<String>> SPECIALIZATIONS = Map.ofEntries(

            entry("Science", List.of(
                    "Physics",
                    "Chemistry",
                    "Mathematics",
                    "Biology",
                    "Computer Science",
                    "Statistics"
            )),

            entry("Commerce", List.of(
                    "Accounting & Finance",
                    "Banking & Insurance",
                    "Business Analytics",
                    "Economics"
            )),

            entry("Arts / Humanities", List.of(
                    "English",
                    "History",
                    "Political Science",
                    "Psychology",
                    "Sociology",
                    "Economics"
            )),

            entry("Engineering", List.of(
                    "Computer Science & Engineering",
                    "Information Technology",
                    "Electronics & Communication",
                    "Electrical Engineering",
                    "Mechanical Engineering",
                    "Civil Engineering",
                    "Chemical Engineering",
                    "Biotechnology",
                    "Aerospace Engineering"
            )),

            entry("Engineering Diploma", List.of(
                    "Computer Engineering",
                    "Electronics",
                    "Electrical",
                    "Mechanical",
                    "Civil",
                    "Automobile"
            )),

            entry("Computer Applications", List.of(
                    "BCA",
                    "MCA",
                    "Data Science",
                    "AI & ML"
            )),

            entry("Medical", List.of(
                    "MBBS",
                    "BDS",
                    "BAMS",
                    "BHMS",
                    "Nursing",
                    "Pharmacy"
            )),

            entry("Management", List.of(
                    "General Management",
                    "Marketing",
                    "Finance",
                    "Human Resources",
                    "Operations / Supply Chain",
                    "Business Analytics"
            )),

            entry("Law", List.of(
                    "BA LLB",
                    "BBA LLB",
                    "LLB",
                    "LLM"
            )),

            entry("Design / Creative", List.of(
                    "UX / UI Design",
                    "Graphic Design",
                    "Product Design",
                    "Animation & VFX",
                    "Game Design"
            )),

            entry("Other", List.of(
                    "Interdisciplinary Studies",
                    "Open / Distance Learning"
            )),

            entry("General", List.of(
                    "General Studies"
            ))
    );
}
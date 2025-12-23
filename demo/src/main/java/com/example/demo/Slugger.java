package com.example.demo;

public final class Slugger {
    private Slugger(){}
    public static String slugify(String s){
        String x = s == null ? "" : s.toLowerCase().trim();
        x = x.replaceAll("[^a-z0-9\\s-]", "");
        x = x.replaceAll("\\s+", "-");
        x = x.replaceAll("-{2,}", "-");
        if (x.isEmpty()) x = "room";
        return x;
    }
}

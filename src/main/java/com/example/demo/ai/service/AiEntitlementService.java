package com.example.demo.ai.service;

import com.example.demo.user.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class AiEntitlementService {

    public boolean isPremium(User user) {
        if (user == null) return false;

        Boolean b = tryBoolMethod(user, "isPremium");
        if (b != null) return b;

        b = tryBoolMethod(user, "getIsPremium");
        if (b != null) return b;

        b = tryBoolMethod(user, "getPremium");
        if (b != null) return b;

        String plan = tryStringMethod(user, "getPlan");
        if (plan != null && isPremiumPlan(plan)) return true;

        String tier = tryStringMethod(user, "getTier");
        if (tier != null && isPremiumPlan(tier)) return true;

        Boolean fb = tryBoolField(user, "premium");
        if (fb != null) return fb;

        fb = tryBoolField(user, "isPremium");
        if (fb != null) return fb;

        return false;
    }

    private boolean isPremiumPlan(String s) {
        String v = s.trim().toUpperCase();
        return v.contains("PREMIUM") || v.contains("PRO") || v.contains("ELITE");
    }

    private Boolean tryBoolMethod(Object o, String name) {
        try {
            Object v = o.getClass().getMethod(name).invoke(o);
            return (v instanceof Boolean) ? (Boolean) v : null;
        } catch (Exception ignored) { return null; }
    }

    private String tryStringMethod(Object o, String name) {
        try {
            Object v = o.getClass().getMethod(name).invoke(o);
            return v == null ? null : v.toString();
        } catch (Exception ignored) { return null; }
    }

    private Boolean tryBoolField(Object o, String field) {
        try {
            Field f = o.getClass().getDeclaredField(field);
            f.setAccessible(true);
            Object v = f.get(o);
            return (v instanceof Boolean) ? (Boolean) v : null;
        } catch (Exception ignored) { return null; }
    }
}

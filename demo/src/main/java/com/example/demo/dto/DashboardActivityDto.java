package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class DashboardActivityDto {

    private long doubtsThisWeek;
    private long answersThisMonth;
    private long solutionsAcceptedAllTime;

    private long daysActiveThisYear;
    private LocalDate lastLoginDate;
    private List<String> loginDatesThisYear;

    public DashboardActivityDto() {
    }

    public long getDoubtsThisWeek() {
        return doubtsThisWeek;
    }

    public void setDoubtsThisWeek(long doubtsThisWeek) {
        this.doubtsThisWeek = doubtsThisWeek;
    }

    public long getAnswersThisMonth() {
        return answersThisMonth;
    }

    public void setAnswersThisMonth(long answersThisMonth) {
        this.answersThisMonth = answersThisMonth;
    }

    public long getSolutionsAcceptedAllTime() {
        return solutionsAcceptedAllTime;
    }

    public void setSolutionsAcceptedAllTime(long solutionsAcceptedAllTime) {
        this.solutionsAcceptedAllTime = solutionsAcceptedAllTime;
    }

    public long getDaysActiveThisYear() {
        return daysActiveThisYear;
    }

    public void setDaysActiveThisYear(long daysActiveThisYear) {
        this.daysActiveThisYear = daysActiveThisYear;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public List<String> getLoginDatesThisYear() {
        return loginDatesThisYear;
    }

    public void setLoginDatesThisYear(List<String> loginDatesThisYear) {
        this.loginDatesThisYear = loginDatesThisYear;
    }
}
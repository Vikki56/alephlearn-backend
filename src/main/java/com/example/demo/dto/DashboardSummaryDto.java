package com.example.demo.dto;

import java.util.List;

public class DashboardSummaryDto {

    private ProfileSummaryDto profile;              // already existing DTO

    private DashboardQuickStatsDto quickStats;      // top 4 cards
    private DashboardActivityDto activity;          // streak + week/month stats

    private long activeChats;                       // TODO: wire with chat WS later

    private List<DashboardTrendingItemDto> trending;
    private List<DashboardRecentItemDto> recent;

    public DashboardSummaryDto() {
    }

    public ProfileSummaryDto getProfile() {
        return profile;
    }

    public void setProfile(ProfileSummaryDto profile) {
        this.profile = profile;
    }

    public DashboardQuickStatsDto getQuickStats() {
        return quickStats;
    }

    public void setQuickStats(DashboardQuickStatsDto quickStats) {
        this.quickStats = quickStats;
    }

    public DashboardActivityDto getActivity() {
        return activity;
    }

    public void setActivity(DashboardActivityDto activity) {
        this.activity = activity;
    }

    public long getActiveChats() {
        return activeChats;
    }

    public void setActiveChats(long activeChats) {
        this.activeChats = activeChats;
    }

    public List<DashboardTrendingItemDto> getTrending() {
        return trending;
    }

    public void setTrending(List<DashboardTrendingItemDto> trending) {
        this.trending = trending;
    }

    public List<DashboardRecentItemDto> getRecent() {
        return recent;
    }

    public void setRecent(List<DashboardRecentItemDto> recent) {
        this.recent = recent;
    }
}
package com.example.demo.domain.dto.quiz;

import java.util.List;

public class SubmitQuizAttemptRequest {

    private List<Integer> selectedOptions;
    private long timeTakenMillis;
    private boolean realtime;

    public List<Integer> getSelectedOptions() { return selectedOptions; }
    public void setSelectedOptions(List<Integer> selectedOptions) { this.selectedOptions = selectedOptions; }

    public long getTimeTakenMillis() { return timeTakenMillis; }
    public void setTimeTakenMillis(long timeTakenMillis) { this.timeTakenMillis = timeTakenMillis; }

    public boolean isRealtime() { return realtime; }
    public void setRealtime(boolean realtime) { this.realtime = realtime; }
}
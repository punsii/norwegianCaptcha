package com.urbandroid.sleep.captcha.norwegian;

import java.time.LocalDate;

public class Word {
    private static final int[] PHASE_LENGTS = {1,3,8,21,55,89};

    private final int id;
    private final String norwegian;
    private final String english;
    private int phase;
    private LocalDate date;

    public Word(int id, String norwegian, String english, int phase, LocalDate date) {
        this.id = id;
        this.norwegian = norwegian;
        this.english = english;
        this.phase = phase;
        this.date = date;
    }

    public static int[] getPhaseLengts() {
        return PHASE_LENGTS;
    }

    public int getId() {
        return id;
    }

    public String getNorwegian() {
        return norwegian;
    }

    public String getEnglish() {
        return english;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

package com.thresholdsoft.apollofeedback.utils;

import java.time.LocalTime;

public class GreetingMaker {
    private static final LocalTime MORNING = LocalTime.of(0, 0, 0);
    private static final LocalTime AFTER_NOON = LocalTime.of(12, 0, 0);
    private static final LocalTime EVENING = LocalTime.of(16, 0, 0);
    private static final LocalTime NIGHT = LocalTime.of(21, 0, 0);

    private LocalTime now;

    public GreetingMaker(LocalTime now) {
        this.now = now;
    }

    public void printTimeOfDay() { // or return String in your case
        if (between(MORNING, AFTER_NOON)) {
            System.out.println("Good Morning");
        } else if (between(AFTER_NOON, EVENING)) {
            System.out.println("Good Afternoon");
        } else if (between(EVENING, NIGHT)) {
            System.out.println("Good Evening");
        } else {
            System.out.println("Good Night");
        }
    }

    private boolean between(LocalTime start, LocalTime end) {
        return (!now.isBefore(start)) && now.isBefore(end);
    }
}

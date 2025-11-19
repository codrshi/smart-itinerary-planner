package com.codrshi.smart_itinerary_planner.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    private final String prefix;
    private final AtomicInteger counter;

    public Counter(String prefix) {
        this.counter = new AtomicInteger(0);
        this.prefix = prefix;
    }

    public String next() {
        return  String.format("%s%03d",prefix, counter.incrementAndGet());
    }

    public void reset() {
        counter.set(0);
    }
}

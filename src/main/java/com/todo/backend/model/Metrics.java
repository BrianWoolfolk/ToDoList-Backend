package com.todo.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    private PriorityCounter high = new PriorityCounter();
    private PriorityCounter mid = new PriorityCounter();
    private PriorityCounter low = new PriorityCounter();
    private LastMetrics values = new LastMetrics();

    public void calculateFinalValues() {
        high.calculate();
        mid.calculate();
        low.calculate();

        values.highAvg = high.getAverage();
        values.highPend = high.getPendingValue();
        values.midAvg = mid.getAverage();
        values.midPend = mid.getPendingValue();
        values.lowAvg = low.getAverage();
        values.lowPend = low.getPendingValue();

        int doneCount = high.getDoneCounter() + mid.getDoneCounter() + low.getDoneCounter();
        int pendingCounter = high.getPending() + mid.getPending() + low.getPending();
        long totalSum = high.getSum() + mid.getSum() + low.getSum();

        values.totalAvg = doneCount > 0 ? (double) totalSum / doneCount : 0;
        values.totalPend = values.totalAvg * pendingCounter;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityCounter {
        private long sum = 0;
        private int doneCounter = 0;
        private int pendingCounter = 0;
        private double average = 0;
        private double pendingValue = 0;

        public void restart() {
            this.sum = 0;
            this.doneCounter = 0;
            this.pendingCounter = 0;
            this.average = 0;
            this.pendingValue = 0;
        }

        public void addSum(long diff) {
            this.sum += diff;
            this.doneCounter++;
        }

        public void addPending() {
            this.pendingCounter++;
        }

        public void calculate() {
            this.average = doneCounter > 0 ? (double) sum / doneCounter : 0;
            this.pendingValue = this.average * pendingCounter;
        }
    }

    @Getter
    @Setter
    @NoArgsContructor
    @AllArgsConstructor
    public static class LastMetrics {
        public double highAvg = 0;
        public double highPend = 0;
        public double midAvg = 0;
        public double midPend = 0;
        public double lowAvg = 0;
        public double lowPend = 0;
        public double totalAvg = 0;
        public double totalPend = 0;
    }
}
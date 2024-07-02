package com.todo.backend;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.todo.backend.ToDo.Priority;

public class Metrics {
    // #region ################################ PROPERTIES
    private PriorityCounter high = new PriorityCounter();
    private PriorityCounter mid = new PriorityCounter();
    private PriorityCounter low = new PriorityCounter();

    private LastMetrics values = new LastMetrics();
    // #endregion

    // #region ################################ GETTERS
    public PriorityCounter getHigh() {
        return high;
    }

    public PriorityCounter getMid() {
        return mid;
    }

    public PriorityCounter getLow() {
        return low;
    }

    public LastMetrics getValues() {
        return this.values;
    }
    // #endregion

    // #region ################################ CALCULATE
    public LastMetrics calculate(List<ToDo> DB) {
        high.restart();
        mid.restart();
        low.restart();

        for (ToDo toDo : DB) {
            Instant done_date = toDo.getDone_date();
            // ALREADY DONE TASKS
            if (done_date != null) {
                Instant creation_date = toDo.getCreation_date();
                long diff = ChronoUnit.MINUTES.between(creation_date, done_date);

                switch (toDo.getPriority()) {
                    case Priority.HIGH:
                        high.addsum(diff);
                        break;
                    case Priority.MEDIUM:
                        mid.addsum(diff);
                        break;
                    case Priority.LOW:
                        low.addsum(diff);
                        break;
                    default:
                        break;
                }

            } else {
                // PENDING TASKS
                switch (toDo.getPriority()) {
                    case Priority.HIGH:
                        high.addpending();
                        break;
                    case Priority.MEDIUM:
                        mid.addpending();
                        break;
                    case Priority.LOW:
                        low.addpending();
                        break;
                    default:
                        break;
                }
            }
        }

        this.values.high_avg = high.average();
        this.values.high_pend = this.values.high_avg * high.getPending();

        this.values.mid_avg = mid.average();
        this.values.mid_pend = this.values.mid_avg * mid.getPending();

        this.values.low_avg = low.average();
        this.values.low_pend = this.values.low_avg * low.getPending();

        int done_count = high.getDoneCounter() + mid.getDoneCounter() + low.getDoneCounter();
        int pending_counter = high.getPending() + mid.getPending() + low.getPending();
        long total_sum = high.getSum() + mid.getSum() + low.getSum();

        this.values.total_avg = 0;
        if (done_count > 0)
            this.values.total_avg = total_sum / done_count;
        this.values.total_pend = this.values.total_avg * pending_counter;

        return this.values;
    }
    // #endregion

    // #region ################################ PRIORITY COUNTER
    /**
     * PriorityCounter
     */
    public class PriorityCounter {
        // ################################ PROPERTIES
        private long sum = 0;
        private int done_counter = 0;
        private int pending_counter = 0;

        // ################################ CALCULATE & PROCESSING
        public void restart() {
            this.sum = 0;
            this.done_counter = 0;
            this.pending_counter = 0;
        }

        public void addsum(long diff) {
            this.sum += diff;
            this.done_counter++;
        }

        public void addpending() {
            this.pending_counter++;
        }

        public double average() {
            double avg = 0;
            if (this.done_counter > 0)
                avg = this.sum / this.done_counter;

            return avg;
        }

        // ################################ GETTERS
        public int getPending() {
            return this.pending_counter;
        }

        public long getSum() {
            return this.sum;
        }

        public int getDoneCounter() {
            return this.done_counter;
        }

        /**
         * String Method
         */
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("Sum: ").append(this.sum).append(", Done Count: ").append(this.done_counter)
                    .append(", Pending Count: ").append(this.pending_counter);
            return str.toString();
        }
    }
    // #endregion

    // #region ################################ LAST METRICS
    /**
     * LastMetrics quick wrapper
     */
    public class LastMetrics {
        public double high_avg = 0;
        public double high_pend = 0;
        public double mid_avg = 0;
        public double mid_pend = 0;
        public double low_avg = 0;
        public double low_pend = 0;
        public double total_avg = 0;
        public double total_pend = 0;
    }
    // #endregion
}

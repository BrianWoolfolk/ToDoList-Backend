package com.todo.backend.service.impl;

import com.todo.backend.model.Metrics;
import com.todo.backend.model.ToDo;
import com.todo.backend.model.Metrics.LastMetrics;
import com.todo.backend.service.MetricsService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MetricsServiceImpl implements MetricsService {

    private final Metrics metrics = new Metrics();

    @Override
    public LastMetrics calculateMetrics(List<ToDo> todos) {
        metrics.getHigh().restart();
        metrics.getMid().restart();
        metrics.getLow().restart();

        for (ToDo toDo : todos) {
            Instant doneDate = toDo.getDoneDate();
            if (doneDate != null) {
                Instant creationDate = toDo.getCreationDate();
                long diff = ChronoUnit.SECONDS.between(creationDate, doneDate);
                switch (toDo.getPriority()) {
                    case HIGH:
                        metrics.getHigh().addSum(diff);
                        break;
                    case MEDIUM:
                        metrics.getMid().addSum(diff);
                        break;
                    case LOW:
                        metrics.getLow().addSum(diff);
                        break;
                    default:
                        break;
                }
            } else {
                switch (toDo.getPriority()) {
                    case HIGH:
                        metrics.getHigh().addPending();
                        break;
                    case MEDIUM:
                        metrics.getMid().addPending();
                        break;
                    case LOW:
                        metrics.getLow().addPending();
                        break;
                    default:
                        break;
                }
            }
        }

        metrics.calculateFinalValues();
        return metrics.getValues();
    }

    @Override
    public LastMetrics getLastMetrics() {
        return metrics.getValues();
    }
}

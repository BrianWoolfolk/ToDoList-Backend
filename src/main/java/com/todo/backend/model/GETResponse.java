package com.todo.backend.model;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import com.todo.backend.model.Metrics.LastMetrics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ResponseBody
@Getter
@RequiredArgsConstructor
public class GETResponse {
    final private List<ToDo> data;
    final private int page;
    final private int maxpage;
    final private LastMetrics metrics;
}

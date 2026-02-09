package com.example.taskmanager.event;


public record TaskEvent(
        Long taskId,
        TaskEventType type
) {}

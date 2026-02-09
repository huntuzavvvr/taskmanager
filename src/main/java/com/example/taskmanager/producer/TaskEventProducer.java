package com.example.taskmanager.producer;

import com.example.taskmanager.event.TaskEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    @Retryable(
            retryFor = Exception.class,
            backoff = @Backoff(delay = 2000),
            maxAttempts = 5
    )
    public void sendMessage(TaskEvent taskEvent) {
        log.info("Trying to send task event to kafka topic: {}", taskEvent);
        try {
            kafkaTemplate.send("tasks-topic", taskEvent).get();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void retry(Exception e, TaskEvent taskEvent) {
        log.error("Failed after retries: {}", taskEvent, e);
    }

}

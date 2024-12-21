package deti.fitmonitor.users.services.kafka;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

@Service
public class consumer {

    private final Map<String, CompletableFuture<String>> pendingReplies = new ConcurrentHashMap<>();

    public void addPendingReply(String correlationId, CompletableFuture<String> future) {
        pendingReplies.put(correlationId, future);
    }

    @KafkaListener(topics = "reply-topic", groupId = "group_id")
    public void consumeReply(ConsumerRecord<String, String> record) {
        String correlationId = record.key();
        String replyMessage = record.value();
        CompletableFuture<String> future = pendingReplies.get(correlationId);

        if (future != null) {
            future.complete(replyMessage);
        }
    }

    public String waitForReply(String correlationId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        addPendingReply(correlationId, future);

        try {
            // Wait for the reply with a timeout
            return future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get reply", e);
        } finally {
            pendingReplies.remove(correlationId);
        }
    }



    
}

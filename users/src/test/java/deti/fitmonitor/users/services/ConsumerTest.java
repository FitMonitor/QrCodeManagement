package deti.fitmonitor.users.services;

import static org.mockito.Mockito.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import deti.fitmonitor.users.services.kafka.consumer;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class ConsumerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private consumer replyConsumer;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private String correlationId = "test-correlation-id";

    @BeforeEach
    public void setup() {
        // Initialize any setup you need, e.g., mock KafkaTemplate if needed

        //initialize replyConsumer
        replyConsumer = new consumer();


        
    }

    @Test
    public void testConsumeReplyCompletesFuture() {
        // Create a future to simulate waiting for a reply
        CompletableFuture<String> future = new CompletableFuture<>();
        replyConsumer.addPendingReply(correlationId, future);

        // Simulate Kafka reply being received
        replyConsumer.consumeReply(new ConsumerRecord<>("reply-topic", 0, 0, correlationId, "Reply message"));

        // Ensure the future is completed correctly
        assertEquals("Reply message", future.getNow(null));
    }

    @Test
    public void testWaitForReplyTimeout() {
        String correlationId = "test-correlation-id-timeout";

        // Call waitForReply and expect a TimeoutException due to no reply
        assertThrows(RuntimeException.class, () -> {
            replyConsumer.waitForReply(correlationId);
        });
    }





}


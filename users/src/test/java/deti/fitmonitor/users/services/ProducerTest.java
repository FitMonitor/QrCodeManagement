package deti.fitmonitor.users.services;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.KafkaHeaders;

import deti.fitmonitor.users.services.kafka.producer;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private producer producer;

    private String correlationId = "test-correlation-id";
    private String message = "test message";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes mocks
        producer = new producer(kafkaTemplate); // Initialize producer with mock KafkaTemplate
    }

    @Test
    void testSendMachineSuccess() {
        // Arrange
        ProducerRecord<String, String> record = new ProducerRecord<>("machine", message);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes()));
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        // Mock behavior
        when(kafkaTemplate.send(record)).thenReturn(future);

        // Act
        producer.sendMachine(message, correlationId);

        // Assert
        verify(kafkaTemplate).send(record);
    }

    @Test
    void testSendGymEntranceSuccess() {
        // Arrange
        ProducerRecord<String, String> record = new ProducerRecord<>("user", message);
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes()));
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        // Mock behavior
        when(kafkaTemplate.send(record)).thenReturn(future);

        // Act
        producer.sendGymEntrance(message, correlationId);

        // Assert
        verify(kafkaTemplate).send(record);
    }


}

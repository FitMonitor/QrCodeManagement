package deti.fitmonitor.users.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;

import deti.fitmonitor.users.services.kafka.consumer;
import deti.fitmonitor.users.services.kafka.producer;

public class QrCodeServiceTest {

    private QrCodeService qrCodeService;

    @Mock
    private producer kafkaProducer;

    @Mock
    private consumer replyConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        qrCodeService = new QrCodeService(kafkaProducer, replyConsumer);
    }

    @Test
    public void testChangeMachineState() {
        // Arrange
        String machineId = "machine123";
        String intention = "START";
        String usersub = "user123";
        String expectedReply = "Success";
        String correlationId = "mock-correlation-id";

        // Mock behavior
        doNothing().when(kafkaProducer).sendMachine(anyString(), anyString());
        when(replyConsumer.waitForReply(anyString())).thenReturn(expectedReply);

        // Act
        String actualReply = qrCodeService.changeMachineState(machineId, intention, usersub);

        // Assert
        assertEquals(expectedReply, actualReply);

        // Verify interactions
        verify(kafkaProducer).sendMachine(eq("machine123 START user123"), anyString());
        verify(replyConsumer).waitForReply(anyString());
    }

    @Test
    public void testGymEntrance() {
        // Arrange
        String token = "123456";

        String expectedReply = "Success";

        // Mock behavior
        doNothing().when(kafkaProducer).sendGymEntrance(anyString(), anyString());
        when(replyConsumer.waitForReply(anyString())).thenReturn(expectedReply);

        // Act
        String actualReply = qrCodeService.gymEntrance(token);

        // Assert
        assertEquals(expectedReply, actualReply);

        // Verify interactions
        verify(kafkaProducer).sendGymEntrance(eq("123456"), anyString());
        verify(replyConsumer).waitForReply(anyString());

    }


}


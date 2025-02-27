package deti.fitmonitor.qrcode.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import deti.fitmonitor.qrcode.services.kafka.producer;
import deti.fitmonitor.qrcode.services.kafka.consumer;

@Service
public class QrCodeService {

    private final producer kafkaProducer;
    private final consumer replyConsumer;

    public QrCodeService(producer kafkaProducer, consumer replyConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.replyConsumer = replyConsumer;
    }

    public String changeMachineState(String machineId, String intention, String usersub) {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.sendMachine(machineId + " " + intention + " " + usersub, correlationId);
        return replyConsumer.waitForReply(correlationId);
    }

    public String gymEntrance(String token) {
        String correlationId = UUID.randomUUID().toString();
        kafkaProducer.sendGymEntrance(token, correlationId);
        return replyConsumer.waitForReply(correlationId);
    }

    

    
}

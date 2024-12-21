package deti.fitmonitor.users.services.kafka;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;


@Service
public class producer {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(producer.class);

    private static final String MachineTopic = "machine";
    private static final String UserTopic = "user";
    
    private final KafkaTemplate<String, String> kafkaTemplate;

    public producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMachine(String message, String correlationId) {
        ProducerRecord<String, String> record = new ProducerRecord<>(MachineTopic, message);
        record.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes());
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                 //failure
                log.info("Unable to send message=[" +
                message + "] due to : " + ex.getMessage());
            } else {
                System.out.println("Sent message to machine topic: " + message);
            }
        });
        
    }

    public void sendUser(String message) {
        this.kafkaTemplate.send(UserTopic, message);
    }
    
}

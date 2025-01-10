package deti.fitmonitor.qrcode.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic exampleTopic() {
        return new NewTopic("reply-topic", 3, (short) 1); // topic name, partitions, replication factor
    }

    @Bean
    public NewTopic anotherTopic() {
        return new NewTopic("machine", 5, (short) 2); 
    }

    @Bean
    public NewTopic userTopic() {
        return new NewTopic("user", 5, (short) 2); 
    }
}

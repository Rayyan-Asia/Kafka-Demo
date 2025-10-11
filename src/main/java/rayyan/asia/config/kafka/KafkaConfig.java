package rayyan.asia.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

// added imports
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConfig {

    @Value("${app.topic.order-completed:order-completed}")
    private String topic;

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        var recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex)
                        -> new org.apache.kafka.common.TopicPartition(record.topic() + ".DLQ", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler errorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public Collection<NewTopic> topics() {
        if (topic == null || topic.isBlank()) return Collections.emptyList();
        return List.of(
                TopicBuilder.name(topic).partitions(1).replicas(1).build(),
                TopicBuilder.name(topic + ".DLQ").partitions(1).replicas(1).build()
        );
    }

}
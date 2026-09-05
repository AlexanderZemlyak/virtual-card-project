package com.example.pdf_document_service.config;

import com.example.pdf_document_service.consumers.ApplicationApprovedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Bean
    public ConsumerFactory<String, ApplicationApprovedEvent>
    consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaBootstrapServers
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "docgen-service-group"
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );

        props.put(
                "spring.json.value.default.type",
                ApplicationApprovedEvent.class.getName()
        );

        props.put(
                "spring.json.use.type.headers",
                false
        );

        props.put(
                "spring.json.trusted.packages",
                "com.example.notification_service"
        );

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            ApplicationApprovedEvent
            > kafkaListenerContainerFactory() {

        var factory =
                new ConcurrentKafkaListenerContainerFactory<
                        String,
                        ApplicationApprovedEvent>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
package com.pagar.finance_api.infrastructure.client.rabbit;

import com.pagar.finance_api.infrastructure.dto.OcrMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OcrRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String routingKey;

    public OcrRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(OcrMessageDTO message) {
        rabbitTemplate.convertAndSend(routingKey, message);
    }
}

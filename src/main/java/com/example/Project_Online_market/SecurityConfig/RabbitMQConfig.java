package com.example.Project_Online_market.SecurityConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Queue names
    public static final String ORDER_QUEUE = "order_queue";
    public static final String PAYMENT_QUEUE = "payment_queue";
    public static final String EMAIL_QUEUE = "email_queue";
    
    // Exchange names
    public static final String ORDER_EXCHANGE = "order_exchange";
    public static final String PAYMENT_EXCHANGE = "payment_exchange";
    
    // Routing keys
    public static final String ORDER_ROUTING_KEY = "order_routing_key";
    public static final String PAYMENT_ROUTING_KEY = "payment_routing_key";
    public static final String EMAIL_ROUTING_KEY = "email_routing_key";
    
    // Queues
    @Bean
    Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }
    
    @Bean
    Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }
    
    @Bean
    Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }
    
    // Exchanges
    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }
    
    // Bindings
    @Bean
    Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }
    
    @Bean
    Binding paymentBinding(Queue paymentQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentQueue).to(paymentExchange).with(PAYMENT_ROUTING_KEY);
    }
    
    @Bean
    Binding emailBinding(Queue emailQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(emailQueue).to(paymentExchange).with(EMAIL_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
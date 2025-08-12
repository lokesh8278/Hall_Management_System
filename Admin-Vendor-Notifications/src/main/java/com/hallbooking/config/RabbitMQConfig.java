package com.hallbooking.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String OTP_QUEUE = "otp_queue";
    public static final String BOOKING_QUEUE = "booking_queue";
    public static final String EMAIL_QUEUE = "email_queue";
    public static final String PUSH_QUEUE = "push_queue";
    public static final String SMS_QUEUE = "sms_queue";

    @Bean
    public Queue otpQueue() {
        return new Queue(OTP_QUEUE);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue pushQueue() {
        return new Queue(PUSH_QUEUE, true);
    }

    @Bean
    public Queue bookingQueue() {
        return new Queue(BOOKING_QUEUE);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("notification_exchange");
    }

    @Bean
    public Binding otpBinding(Queue otpQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(otpQueue).to(directExchange).with("otp.routing.key");
    }

    @Bean
    public Binding bookingBinding(Queue bookingQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(bookingQueue).to(directExchange).with("booking.routing.key");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.hallbooking.dto"); // Update as needed
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}

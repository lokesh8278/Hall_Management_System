package com.hallbooking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ✅ Queue Names
    public static final String OTP_QUEUE = "otp_queue";
    public static final String BOOKING_QUEUE = "booking_queue";
    public static final String EMAIL_QUEUE = "email_queue";
    public static final String PUSH_QUEUE = "push_queue";
    public static final String SMS_QUEUE = "sms_queue";
    public static final String WAITLIST_QUEUE = "waitlist.queue";

    // ✅ Exchange Names
    public static final String NOTIFICATION_EXCHANGE = "notification_exchange";
    public static final String WAITLIST_EXCHANGE = "waitlist.exchange";

    // ✅ Define Queues
    @Bean public Queue otpQueue() { return new Queue(OTP_QUEUE); }
    @Bean public Queue smsQueue() { return new Queue(SMS_QUEUE, true); }
    @Bean public Queue emailQueue() { return new Queue(EMAIL_QUEUE, true); }
    @Bean public Queue pushQueue() { return new Queue(PUSH_QUEUE, true); }
    @Bean public Queue bookingQueue() { return new Queue(BOOKING_QUEUE); }
    @Bean public Queue waitlistQueue() { return new Queue(WAITLIST_QUEUE, true); }

    // ✅ Define Exchanges
    @Bean public DirectExchange directExchange() { return new DirectExchange(NOTIFICATION_EXCHANGE); }
    @Bean public TopicExchange waitlistExchange() { return new TopicExchange(WAITLIST_EXCHANGE); }

    // ✅ Bindings
    @Bean
    public Binding otpBinding() {
        return BindingBuilder.bind(otpQueue()).to(directExchange()).with("otp.routing.key");
    }

    @Bean
    public Binding bookingBinding() {
        return BindingBuilder.bind(bookingQueue()).to(directExchange()).with("booking.routing.key");
    }

    @Bean
    public Binding waitlistBinding() {
        return BindingBuilder.bind(waitlistQueue()).to(waitlistExchange()).with("waitlist.send");
    }

    // ✅ JSON Converter for RabbitMQ
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.hallbooking.dto", "com.hallbooking.bookingService.dto");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    // ✅ RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

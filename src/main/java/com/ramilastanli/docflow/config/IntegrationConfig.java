package com.ramilastanli.docflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executors;

@Configuration
@EnableIntegration
public class IntegrationConfig {

    @Bean
    public MessageChannel documentInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel statusUpdateChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel validDocumentChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel errorChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public MessageChannel sendMailChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel approvalChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel persistenceChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel emailNotificationChannel() {
        return new PublishSubscribeChannel(Executors.newFixedThreadPool(5));
    }

    @Bean
    public MessageChannel rejectionNotificationChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public RequestHandlerRetryAdvice retryAdvice(MessageChannel errorChannel) {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();

        advice.setRecoveryCallback(new ErrorMessageSendingRecoverer(errorChannel));

        return advice;
    }
}
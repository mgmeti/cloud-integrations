package com.datababa.sqs_demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;

@Service
public class SqsService {
    private static final Logger log = LoggerFactory.getLogger(SqsService.class);

    private final String queueUrl;
    private final Region region;
    private SqsClient sqsClient;

    public SqsService(@Value("${aws.sqs.queueUrl}") String queueUrl,
                      @Value("${aws.region}") String region){
        this.queueUrl=queueUrl;
        this.region=Region.of(region);
    }

    @PostConstruct
    private void init(){
        this.sqsClient = SqsClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        log.info("Initialized SQS client for region {} and queue {}", region, queueUrl);
    }

    public void sendMessage(String messageBody){
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        sqsClient.sendMessage(request);
        log.info("Sent message: {}", messageBody);
    }

    public void receiveAndDeleteMessage(){
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(5)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        for(Message message:messages){
            log.info("Received message: {}", message.body());
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteRequest);
            log.info("Deleted message.");
        }
    }
}

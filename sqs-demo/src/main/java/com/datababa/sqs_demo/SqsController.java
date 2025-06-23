package com.datababa.sqs_demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sqs")
public class SqsController {
    private final SqsService sqsService;

    public SqsController(SqsService sqsService){
        this.sqsService=sqsService;
    }


    @PostMapping("/send")
    public String send(@RequestParam String message) {
        sqsService.sendMessage(message);
        return "Message sent!";
    }


    @GetMapping("/receive")
    public String receive() {
        sqsService.receiveAndDeleteMessage();
        return "Message received (check logs).";
    }
}

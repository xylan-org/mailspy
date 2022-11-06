package org.xylan.mailspy.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
public class TestWsController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    public void init() {
        System.out.println("asd");
    }

    @MessageMapping("/test")
    public void test() {
        simpMessagingTemplate.convertAndSend("/app/topic/test-response", "test");
    }

}

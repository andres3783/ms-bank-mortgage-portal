package com.bank.mortgage.portal.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @JmsListener(destination = "my-queue")
    public void receive(String message) {
        System.out.println("Received: " + message);
    }
}

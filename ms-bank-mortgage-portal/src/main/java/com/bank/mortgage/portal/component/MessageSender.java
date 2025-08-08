package com.bank.mortgage.portal.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);
    }
}

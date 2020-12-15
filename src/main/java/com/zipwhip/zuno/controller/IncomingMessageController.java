package com.zipwhip.zuno.controller;

import com.zipwhip.zuno.config.ZunoProperties;
import com.zipwhip.zuno.domain.ZipwhipMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class IncomingMessageController {

    private final JmsTemplate jmsTemplate;
    private final ZunoProperties zunoProperties;

    @PostMapping("/messages")
    public ResponseEntity<?> incomingMessage(@RequestBody ZipwhipMessage message) {
        jmsTemplate.convertAndSend(zunoProperties.getJms().getQueue(), message);
        return ResponseEntity.ok().build();
    }

}

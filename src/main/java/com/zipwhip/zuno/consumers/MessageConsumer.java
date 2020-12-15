package com.zipwhip.zuno.consumers;

import com.zipwhip.zuno.domain.ZipwhipMessage;
import com.zipwhip.zuno.service.InstructionParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
@RequiredArgsConstructor
public class MessageConsumer implements MessageListener {

    private final InstructionParser instructionParser;

    @SneakyThrows
    @Override
    @JmsListener(destination = "${zuno.jms.queue}")
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        ZipwhipMessage zipwhipMessage = (ZipwhipMessage) objectMessage.getObject();
        instructionParser.parseAndExecute(zipwhipMessage.getFinalSource(), zipwhipMessage.getBody());
    }
}

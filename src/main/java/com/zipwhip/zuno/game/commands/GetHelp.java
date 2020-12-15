package com.zipwhip.zuno.game.commands;

import com.zipwhip.zuno.game.text.MessageUtils;
import com.zipwhip.zuno.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHelp implements Command {

    private final MessageUtils messageUtils;
    private final NotificationDispatcher notificationDispatcher;

    @Override
    public String keyword() {
        return "/help";
    }

    @Override
    public void execute(String source, String[] args) {
        notificationDispatcher.notifySource(source, messageUtils.help());
    }
}

package com.codrshi.smart_itinerary_planner.common.advisor;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SanitizeAdvisor implements CallAdvisor {

    public static final Pattern EMAIL_REGEX = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    public static final Pattern PHONE_REGEX = Pattern.compile("\\b[0-9]{10}\\b");

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        Assert.notNull(chatClientRequest, "the chatClientRequest cannot be null");

        String sanitizedUserMessage = sanitizeMessages(chatClientRequest.prompt().getUserMessage());
        log.debug("Sanitized user message: {}", sanitizedUserMessage);

        ChatClientRequest sanitizedRequest = chatClientRequest.mutate()
                .prompt(chatClientRequest.prompt().augmentUserMessage(sanitizedUserMessage))
                .build();

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(sanitizedRequest);

        return chatClientResponse;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private String sanitizeMessages(UserMessage userMessage) {

        if(userMessage == null) {
            return null;
        }

        String sanitizedText = userMessage.getText();
        sanitizedText = EMAIL_REGEX.matcher(sanitizedText).replaceAll(Constant.EMAIL_REDACTED);
        sanitizedText = PHONE_REGEX.matcher(sanitizedText).replaceAll(Constant.PHONE_REDACTED);

        return sanitizedText;
    }
}

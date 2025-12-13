package com.codrshi.smart_itinerary_planner.common.advisor;

import com.codrshi.smart_itinerary_planner.util.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class LoggingAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        Assert.notNull(chatClientRequest, "the chatClientRequest cannot be null");
        long startTime = System.currentTimeMillis();
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        long duration = System.currentTimeMillis() - startTime;
        String username = RequestContext.getCurrentContext().getUsername();

        log.debug("LLM query for {} took {} ms.", username, duration);
        log.debug("LLM query for {} consumed {} tokens.", username, chatClientResponse.chatResponse().getMetadata().getUsage());

        return chatClientResponse;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

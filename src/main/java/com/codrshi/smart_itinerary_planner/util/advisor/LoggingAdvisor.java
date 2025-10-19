package com.codrshi.smart_itinerary_planner.util.advisor;

import com.codrshi.smart_itinerary_planner.util.RequestContext;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LoggingAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        Assert.notNull(chatClientRequest, "the chatClientRequest cannot be null");
        long startTime = System.currentTimeMillis();
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        long duration = System.currentTimeMillis() - startTime;
        String username = RequestContext.getCurrentContext().getUsername();

        System.out.println("LLM query for " + username + " took " + duration + "ms");
        System.out.println("LLM query for " + username + " consumed " + chatClientResponse.chatResponse().getMetadata().getUsage() + " tokens.");

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

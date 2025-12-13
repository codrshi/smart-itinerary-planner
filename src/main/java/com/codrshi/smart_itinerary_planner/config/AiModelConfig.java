package com.codrshi.smart_itinerary_planner.config;

import com.codrshi.smart_itinerary_planner.common.advisor.LoggingAdvisor;
import com.codrshi.smart_itinerary_planner.common.advisor.SanitizeAdvisor;
import com.codrshi.smart_itinerary_planner.util.tool.CreateItineraryTool;
import com.codrshi.smart_itinerary_planner.util.tool.FetchItineraryTool;
import com.codrshi.smart_itinerary_planner.util.tool.MailItineraryTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Configuration
public class AiModelConfig {

    @Value("classpath:ai-model/itinerary-summarize-assistant-system-message.txt")
    private Resource itinerarySummarizeAssistantSystemMessage;

    @Value("classpath:ai-model/itinerary-queries-assistant-system-message.txt")
    private Resource itineraryQueriesAssistantSystemMessage;

    @Bean("mailItineraryAssistantChatClient")
    public ChatClient mailItineraryAssistantChatClient(OpenAiChatModel openAiChatModel, LoggingAdvisor loggingAdvisor) {

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.5)
                .frequencyPenalty(0.8)
                .presencePenalty(0.4)
                .build();

        return ChatClient.builder(openAiChatModel)
                .defaultSystem(itinerarySummarizeAssistantSystemMessage, StandardCharsets.UTF_8)
                .defaultOptions(chatOptions)
                .defaultAdvisors(loggingAdvisor)
                .build();
    }

    @Bean("itineraryQueriesAssistantChatClient")
    public ChatClient itineraryQueriesAssistantChatClient(OpenAiChatModel openAiChatModel, LoggingAdvisor loggingAdvisor,
                                                          SanitizeAdvisor sanitizeAdvisor, FetchItineraryTool fetchItineraryTool,
                                                          CreateItineraryTool createItineraryTool, MailItineraryTool mailItineraryTool) {

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.2)
                .frequencyPenalty(0.2)
                .presencePenalty(0.2)
                .build();

        return ChatClient.builder(openAiChatModel)
                .defaultSystem(itineraryQueriesAssistantSystemMessage, StandardCharsets.UTF_8)
                .defaultOptions(chatOptions)
                .defaultAdvisors(loggingAdvisor, sanitizeAdvisor)
                .defaultTools(fetchItineraryTool, createItineraryTool, mailItineraryTool)
                .build();
    }

    @Bean
    public ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return new DefaultToolExecutionExceptionProcessor(true);
    }
}

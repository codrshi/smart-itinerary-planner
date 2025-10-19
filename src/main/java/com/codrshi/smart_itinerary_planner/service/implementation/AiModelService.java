package com.codrshi.smart_itinerary_planner.service.implementation;

import com.codrshi.smart_itinerary_planner.dto.implementation.FlattenedActivityDTO;
import com.codrshi.smart_itinerary_planner.service.IAiModelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AiModelService implements IAiModelService {

    @Value("classpath:ai-model/itinerary-summarize-assistant-prompt.txt")
    private Resource itinerarySummarizeAssistantPrompt;

    @Autowired
    @Qualifier("mailItineraryAssistantChatClient")
    private ChatClient mailItineraryAssistantChatClient;

    @Autowired
    @Qualifier("itineraryQueriesAssistantChatClient")
    private ChatClient itineraryQueriesAssistantChatClient;

    //TODO: populate ToolContext using AOP
    @Override
    public String generateItinerarySummary(List<FlattenedActivityDTO> itineraries) {
        PromptTemplate promptTemplate = new PromptTemplate(itinerarySummarizeAssistantPrompt);
        Prompt prompt = new Prompt(new UserMessage(promptTemplate.render(Map.of("activitiesJson", itineraries))));

        ChatResponse chatResponse = mailItineraryAssistantChatClient.prompt(prompt).call().chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }

    @Override
    public String handleItineraryQuery(String query) {
        ChatResponse chatResponse = itineraryQueriesAssistantChatClient.prompt(query).call().chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }
}

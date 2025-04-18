package com.eventmanagement.event_ai.service;

import com.eventmanagement.event_ai.config.GeminiConfig;
import com.eventmanagement.event_ai.exception.GeminiAPIException;
import com.eventmanagement.event_ai.model.Event;
import com.eventmanagement.event_ai.service.gemini.GeminiRequest;
import com.eventmanagement.event_ai.service.gemini.GeminiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private final WebClient webClient;
    private final String apiKey;
    private final String modelName;

    public AIService(WebClient geminiWebClient, GeminiConfig geminiConfig) {
        this.webClient = geminiWebClient;
        this.apiKey = geminiConfig.getApiKey();
        this.modelName = geminiConfig.getModelName();
    }

    public String generateEventSummary(Event event) {
        String prompt = String.format(
                "Create a compelling summary for this event in 2-3 sentences:\n" +
                "Title: %s\nDescription: %s\nLocation: %s\nDate: %s to %s",
                event.getTitle(), event.getDescription(), event.getLocation(),
                event.getStartDateTime(), event.getEndDateTime());

        GeminiRequest request = createRequest(prompt);

        return callGeminiAPI(request)
                .map(response -> {
                    if (response.getCandidates() != null &&
                        !response.getCandidates().isEmpty() &&
                        response.getCandidates().get(0).getContent() != null &&
                        !response.getCandidates().get(0).getContent().getParts().isEmpty()) {
                        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
                    }
                    return "AI summary unavailable";
                })
                .onErrorMap(GeminiAPIException.class, ex -> ex) // Preserve custom exception
                .block(); // Blocking call for simplicity
    }

    public Double rateEventQuality(Event event) {
        String prompt = String.format(
                "Rate the quality of this event description on a scale from 1-10 (1=bad, 10=excellent). " +
                "Consider clarity, appeal, and completeness. Return only the number:\n" +
                "Title: %s\nDescription: %s\nLocation: %s\nDate: %s to %s",
                event.getTitle(), event.getDescription(), event.getLocation(),
                event.getStartDateTime(), event.getEndDateTime());

        GeminiRequest request = createRequest(prompt);

        return callGeminiAPI(request)
                .map(response -> {
                    if (response.getCandidates() != null &&
                        !response.getCandidates().isEmpty() &&
                        response.getCandidates().get(0).getContent() != null &&
                        !response.getCandidates().get(0).getContent().getParts().isEmpty()) {
                        String text = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                        try {
                            return Double.parseDouble(text.trim());
                        } catch (NumberFormatException e) {
                            logger.error("Failed to parse AI response as a number", e);
                            return 5.0; // Default rating if parsing fails
                        }
                    }
                    return 5.0; // Default rating if no valid response
                })
                .onErrorMap(GeminiAPIException.class, ex -> ex) // Preserve custom exception
                .block(); // Blocking call for simplicity
    }

    private GeminiRequest createRequest(String prompt) {
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
        return new GeminiRequest(Collections.singletonList(content));
    }

    private Mono<GeminiResponse> callGeminiAPI(GeminiRequest request) {
        logger.debug("Calling Gemini API with prompt: {}", 
            request.getContents().get(0).getParts().get(0).getText());

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/" + modelName + ":generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new GeminiAPIException(
                            "Gemini API error: " + response.statusCode() + " - " + errorBody))
                        )
                )
                .bodyToMono(GeminiResponse.class)
                .doOnError(e -> logger.error("Gemini API call failed", e))
                .doOnSuccess(r -> logger.debug("Gemini API call successful"))
                .onErrorResume(WebClientResponseException.class, ex ->
                    Mono.error(new GeminiAPIException("Gemini API communication error", ex))
                )
                .onErrorResume(Exception.class, ex ->
                    Mono.error(new GeminiAPIException("Unexpected error calling Gemini API", ex))
                );
    }
}
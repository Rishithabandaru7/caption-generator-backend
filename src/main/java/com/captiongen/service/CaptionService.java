package com.captiongen.service;

import com.captiongen.dto.CaptionRequest;
import com.captiongen.dto.CaptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CaptionService {

    private final RestTemplate restTemplate;

    // Injected from application.properties
    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    public CaptionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Main method: takes the user's topic + tone, sends a structured prompt
     * to Groq, then parses the response into a CaptionResponse object.
     */
    public CaptionResponse generateCaptions(CaptionRequest request) {

        // 1. Build the prompt
        String prompt = buildPrompt(request.getTopic(), request.getTone());

        // 2. Build the Groq API request body (OpenAI-compatible format)
        Map<String, Object> requestBody = Map.of(
            "model", groqModel,
            "messages", List.of(
                Map.of("role", "system", "content",
                    "You are a creative social media content expert. Always respond ONLY in the exact format requested. No extra explanation."),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.8,   // Slightly creative but not chaotic
            "max_tokens", 400
        );

        // 3. Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);   // Authorization: Bearer <key>

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. Call Groq API
        ResponseEntity<Map> response = restTemplate.exchange(
            groqApiUrl, HttpMethod.POST, entity, Map.class
        );

        // 5. Extract the AI text from the response
        String aiText = extractTextFromResponse(response.getBody());

        // 6. Parse the structured text into CaptionResponse fields
        return parseAiResponse(aiText);
    }

    // ─────────────────────────────────────────
    // PROMPT BUILDER
    // ─────────────────────────────────────────

    private String buildPrompt(String topic, String tone) {
        return String.format("""
            Generate social media content for the following topic and tone.
            
            Topic: %s
            Tone: %s
            
            Respond in EXACTLY this format (labels must match exactly):
            
            CAPTION: <one Instagram caption with emojis>
            VIRAL_CAPTION: <one POV or trending-style viral caption with emojis>
            HASHTAGS: <10 relevant hashtags separated by spaces>
            REEL_HOOK: <one short engaging opening line for a Reel, under 15 words>
            """, topic, tone);
    }

    // ─────────────────────────────────────────
    // RESPONSE EXTRACTOR
    // ─────────────────────────────────────────

    /**
     * Groq returns a standard OpenAI-format JSON.
     * Structure: choices[0].message.content
     */
    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map responseBody) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq API response: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // RESPONSE PARSER
    // ─────────────────────────────────────────

    /**
     * Parses AI text like:
     *   CAPTION: Chasing sunsets 🌅
     *   VIRAL_CAPTION: POV: You found peace ✨
     *   HASHTAGS: #sunset #beach ...
     *   REEL_HOOK: Wait till the end 👀
     */
    private CaptionResponse parseAiResponse(String text) {
        String caption = extractField(text, "CAPTION:");
        String viralCaption = extractField(text, "VIRAL_CAPTION:");
        String hashtags = extractField(text, "HASHTAGS:");
        String reelHook = extractField(text, "REEL_HOOK:");

        return new CaptionResponse(caption, viralCaption, hashtags, reelHook);
    }

    /**
     * Extracts the value after a label like "CAPTION:" up to the next label or end of string.
     */
    private String extractField(String text, String label) {
        int start = text.indexOf(label);
        if (start == -1) return "Could not parse this field.";

        start += label.length();
        int end = text.length();

        // Find the next label to determine where this field ends
        String[] labels = {"CAPTION:", "VIRAL_CAPTION:", "HASHTAGS:", "REEL_HOOK:"};
        for (String nextLabel : labels) {
            int nextStart = text.indexOf(nextLabel, start);
            if (nextStart != -1 && nextStart < end) {
                end = nextStart;
            }
        }

        return text.substring(start, end).trim();
    }

}
package com.summarizer.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.summarizer.backend.dto.SummarizeRequest;
import com.summarizer.backend.dto.SummarizeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class SummarizeController {

    // Reads openai.api.key from application.properties
    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/summarize")
    public SummarizeResponse summarize(@RequestBody SummarizeRequest request) {
        String text = request.getText();

        // Handle case where there is no text
        if (text == null || text.isBlank()) {
            return new SummarizeResponse("No text was provided to summarize.");
        }

        // 1. Build the prompt to send to OpenAI
        String prompt = "Summarize this articles key points in 4 to five sentences:\n\n" + text;

        // 2. Construct the JSON body for OpenAI
        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1-mini");
        body.put("input", prompt);

        // 3. Set up headers: Authorization + JSON content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 4. Call OpenAI; get raw JSON string back
        ResponseEntity<String> openAiResponse = restTemplate.postForEntity(
                "https://api.openai.com/v1/responses",
                entity,
                String.class);

        if (!openAiResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("OpenAI API returned status: " + openAiResponse.getStatusCode());
        }

        String responseBody = openAiResponse.getBody();

        // 5. Extract the summary text from the JSON
        String summaryText = extractSummaryText(responseBody);

        if (summaryText == null || summaryText.isBlank()) {
            summaryText = "OpenAI did not return a summary.";
        }

        // 6. Wrap it in your DTO and return
        return new SummarizeResponse(summaryText);
    }

    private String extractSummaryText(String json) {
        try {

            JsonNode root = objectMapper.readTree(json);
            JsonNode outputArray = root.path("output");

            if (!outputArray.isArray() || outputArray.isEmpty()) {
                return null;
            }

            JsonNode firstOutput = outputArray.get(0);
            JsonNode contentArray = firstOutput.path("content");

            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return null;
            }

            JsonNode firstContent = contentArray.get(0);
            JsonNode textNode = firstContent.path("text");

            return textNode.isMissingNode() ? null : textNode.asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }

    /*
     * private String extractSummaryText(String json) {
     * try {
     * JsonNode root = objectMapper.readTree(json);
     * 
     * // First try the easiest / most reliable field
     * JsonNode outputTextNode = root.path("output_text");
     * if (!outputTextNode.isMissingNode() && !outputTextNode.asText().isBlank()) {
     * return outputTextNode.asText();
     * }
     * 
     * // Fallback: walk through output -> content
     * JsonNode outputArray = root.path("output");
     * if (outputArray.isArray()) {
     * for (JsonNode outputItem : outputArray) {
     * JsonNode contentArray = outputItem.path("content");
     * if (contentArray.isArray()) {
     * for (JsonNode contentItem : contentArray) {
     * JsonNode textNode = contentItem.path("text");
     * if (!textNode.isMissingNode() && !textNode.asText().isBlank()) {
     * return textNode.asText();
     * }
     * }
     * }
     * }
     * }
     * 
     * return null;
     * } catch (IOException e) {
     * throw new RuntimeException("Failed to parse OpenAI response", e);
     * }
     * }
     */
}

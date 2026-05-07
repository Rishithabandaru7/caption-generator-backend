package com.captiongen.controller;

import com.captiongen.dto.CaptionRequest;
import com.captiongen.dto.CaptionResponse;
import com.captiongen.service.CaptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CaptionController {

    private final CaptionService captionService;

    public CaptionController(CaptionService captionService) {
        this.captionService = captionService;
    }

    /**
     * POST /api/generate
     *
     * Request body:
     * {
     *   "topic": "sunset beach photo",
     *   "tone": "aesthetic"
     * }
     *
     * Response:
     * {
     *   "caption": "...",
     *   "viralCaption": "...",
     *   "hashtags": "...",
     *   "reelHook": "..."
     * }
     */
    @PostMapping("/generate")
    public ResponseEntity<CaptionResponse> generateCaptions(@RequestBody CaptionRequest request) {

        // Basic validation
        if (request.getTopic() == null || request.getTopic().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Default tone if not provided
        if (request.getTone() == null || request.getTone().isBlank()) {
            request.setTone("aesthetic");
        }

        CaptionResponse response = captionService.generateCaptions(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/health
     * Simple health check — useful for Render free-tier (keeps server awake)
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Caption Generator API is running ✅");
    }

}
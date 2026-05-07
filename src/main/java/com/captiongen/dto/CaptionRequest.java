package com.captiongen.dto;

import lombok.Data;

/**
 * Request body sent by the frontend.
 *
 * Example JSON:
 * {
 *   "topic": "sunset beach photo",
 *   "tone": "aesthetic"
 * }
 */
@Data
public class CaptionRequest {

    private String topic;   // e.g. "sunset beach photo"
    private String tone;    // e.g. "aesthetic", "funny", "emotional", "motivational"

}
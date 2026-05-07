package com.captiongen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response sent back to the frontend after AI generation.
 *
 * Example JSON:
 * {
 *   "caption": "Chasing sunsets and peace 🌅",
 *   "viralCaption": "POV: You finally found peace at the beach ✨",
 *   "hashtags": "#sunset #beachvibes #goldenhour ...",
 *   "reelHook": "Wait till the end for the best sunset view 👀"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptionResponse {

    private String caption;        // Standard Instagram caption
    private String viralCaption;   // POV / trending style caption
    private String hashtags;       // Space-separated hashtags
    private String reelHook;       // Opening hook for a Reel

}
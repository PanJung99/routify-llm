package io.github.panjung99.routify.core.model.entity;

import io.github.panjung99.routify.core.model.enums.ModelCategory;
import lombok.Data;


/**
 * Logical Model entity representing a unified model interface for API calls.
 * This is the abstract model that users interact with, which can be mapped to
 * multiple vendor models through ModelBinding relationships.
 */
@Data
public class LogicModel {

    /**
     * Unique identifier name for API calls.
     * This is the model name that users specify when making requests.
     */
    private String name;

    /**
     * Indicates whether this model is free to use.
     * Free models may have usage limitations or different routing strategies.
     */
    private Boolean isFree;

    /**
     * Category of the model defining its capabilities.
     * Categories include: chat, image, audio, video, embedding, 3d
     * - chat: conversational AI models
     * - image: image generation and processing models
     * - audio: speech recognition and synthesis models
     * - video: video processing and generation models
     * - embedding: text embedding and vector models
     * - three_d: 3D model generation and processing
     */
    private ModelCategory category;

}

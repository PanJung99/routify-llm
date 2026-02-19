package io.github.panjung99.routify.core.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiKey {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String apiKey;

    /**
     *
     */
    private LocalDateTime expiresAt;
}
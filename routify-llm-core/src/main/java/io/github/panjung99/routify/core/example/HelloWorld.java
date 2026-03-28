package io.github.panjung99.routify.core.example;

import io.github.panjung99.routify.core.client.RoutifyClient;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.*;
import io.github.panjung99.routify.core.model.enums.ModelCategory;
import io.github.panjung99.routify.core.model.enums.VendorType;
import io.github.panjung99.routify.core.repository.ConfigRepository;
import io.github.panjung99.routify.core.repository.SimpleConfigRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HelloWorld {

    public static void main(String[] args) {
        String baseUrl = "";
        String modelName = "gemini";
        String apiKey = "123456";
        SimpleConfigRepository repository = new SimpleConfigRepository(modelName, VendorType.OPEN_AI, baseUrl, apiKey);

        RoutifyClient client = RoutifyClient.builder()
                .configRepository(repository)
                .build();

    }
}

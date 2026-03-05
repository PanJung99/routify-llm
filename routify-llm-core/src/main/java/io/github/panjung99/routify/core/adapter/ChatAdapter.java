package io.github.panjung99.routify.core.adapter;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.client.StreamResponseHandler;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.Vendor;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.model.enums.VendorType;

/**
 * Adapter interface for interacting with various LLM vendor chat APIs.
 * Implementations translate unified {@link RoutifyRequest} into vendor-specific protocols.
 */
public interface ChatAdapter {

    /**
     * Executes a non-streaming chat request.
     * @param request The unified chat request.
     * @param model The specific vendor model configuration.
     * @param vendor The vendor metadata.
     * @param apiKey The API key for authentication.
     * @param context The current routing context and metadata.
     * @return The standardized response from the vendor.
     */
    RoutifyResponse chat(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context);

    /**
     * Executes a streaming chat request.
     * @param request The unified chat request.
     * @param model The specific vendor model configuration.
     * @param vendor The vendor metadata.
     * @param apiKey The API key for authentication.
     * @param context The current routing context and metadata.
     * @param handler Callback handler for processing stream events.
     */
    void chatStream(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler);

    /**
     * Retrieves a specific ChatAdapter instance by vendor type.
     * @param vendorType The type of the LLM vendor (e.g., OPEN_AI, ANTHROPIC).
     * @return The corresponding adapter instance, or {@code null} if not supported.
     */
    static ChatAdapter getAdapter(VendorType vendorType) {
        return null;
    }

}

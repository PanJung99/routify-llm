package io.github.panjung99.routify.core.adapter;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.client.StreamResponseHandler;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.Vendor;
import io.github.panjung99.routify.core.model.entity.VendorModel;

/**
 * Base class for {@link ChatAdapter} implementations.
 * <p>
 * This class provides a template for vendor-specific chat adapters,
 * delegating the core execution logic to protected "doChat" methods
 * which must be implemented by subclasses.
 */
public abstract class AbstractChatAdapter implements ChatAdapter {

    /**
     * Subclasses must implement this method to handle the actual
     * non-streaming chat with the specific LLM vendor.
     *
     * @see #chat(RoutifyRequest, VendorModel, Vendor, ApiKey, RoutingContext)
     *
     * @param request The unified chat request.
     * @param model The specific vendor model configuration.
     * @param vendor The vendor metadata.
     * @param apiKey The API key for authentication.
     * @param context The current routing context and metadata.
     * @return The standardized response from the vendor.
     */
    protected abstract RoutifyResponse doChat(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context);

    /**
     * Subclasses must implement this method to handle the actual
     * streaming chat with the specific LLM vendor.
     *
     * @see #chatStream(RoutifyRequest, VendorModel, Vendor, ApiKey, RoutingContext, StreamResponseHandler)
     *
     * @param request The unified chat request.
     * @param model The specific vendor model configuration.
     * @param vendor The vendor metadata.
     * @param apiKey The API key for authentication.
     * @param context The current routing context and metadata.
     * @param handler Callback handler for processing stream events.
     */
    protected abstract void doChatStream(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler);

    /**
     * {@inheritDoc}
     */
    @Override
    public final RoutifyResponse chat(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context) {
        return doChat(request, model, vendor, apiKey, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chatStream(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler) {
        doChatStream(request, model, vendor, apiKey, context, handler);
    }
}

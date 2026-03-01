package io.github.panjung99.routify.core.adapter;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.client.StreamResponseHandler;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.model.enums.VendorType;

public interface ChatAdapter {

    /**
     * 执行非流式聊天请求
     * @param request
     * @param model
     * @param apiKey
     * @return
     */
    RoutifyResponse chat(RoutifyRequest request, VendorModel model, ApiKey apiKey, RoutingContext context);

    /**
     * Executes a specific stream request.
     * @param request chat request
     * @param model a vendor model for the vendor
     * @param apiKey
     */
    void chatStream(RoutifyRequest request, VendorModel model, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler);

    static ChatAdapter getAdapter(VendorType vendorType) {
        return null;
    }

}

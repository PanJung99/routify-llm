package io.github.panjung99.routify.core.adapter;

import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.VendorModel;

public interface ChatAdapter {

    /**
     * 执行非流式聊天请求
     * @param request
     * @param model
     * @param apiKey
     * @return
     */
    RoutifyResponse chat(RoutifyRequest request, VendorModel model, ApiKey apiKey);

    /**
     * 执行流式聊天请求
     * @param request 聊天请求
     * @param model
     * @param apiKey
     */
    void chatStream(RoutifyRequest request, VendorModel model, ApiKey apiKey);

}

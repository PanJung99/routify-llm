package io.github.panjung99.routify.core.adapter;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.client.StreamResponseHandler;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.VendorModel;

public abstract class AbstractChatAdapter implements ChatAdapter {

    /**
     * 子类实现具体的厂商调用逻辑。
     * @param request 请求对象
     * @param model 服务商模型
     * @param apiKey 使用的 API Key
     * @param context 路由上下文（包含记录器、指标等）
     * @return 响应对象
     */
    protected abstract RoutifyResponse doChat(RoutifyRequest request, VendorModel model, ApiKey apiKey, RoutingContext context);

    protected abstract void doChatStream(RoutifyRequest request, VendorModel model, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler);

    @Override
    public final RoutifyResponse chat(RoutifyRequest request, VendorModel model, ApiKey apiKey, RoutingContext context) {
        return doChat(request, model, apiKey, context);
    }

    @Override
    public void chatStream(RoutifyRequest request, VendorModel model, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler) {

    }
}

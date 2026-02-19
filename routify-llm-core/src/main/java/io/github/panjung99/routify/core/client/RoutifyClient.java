package io.github.panjung99.routify.core.client;

import io.github.panjung99.routify.core.model.dto.RoutifyChunk;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.repository.ConfigRepository;
import lombok.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Builder
public class RoutifyClient {

    private ConfigRepository configRepository;

    /**
     * 阻塞式非流式调用，返回完整响应。
     *
     * @param request 请求参数
     * @return 模型响应
     */
    public RoutifyResponse chat(RoutifyRequest request) {
        return null;
    }

    /**
     * 阻塞式流式调用，通过回调处理器接收数据块。
     *
     * @param request 请求参数
     * @param handler 回调处理器，用于接收流式数据块、错误和完成事件
     */
    public void chatStream(RoutifyRequest request, StreamResponseHandler handler) {

    }

    /**
     * 响应式非流式调用，返回 Mono<RoutifyResponse>。
     * <p>
     * 注意：如果底层 VendorAdapter 的实现是阻塞的，该 Mono 会在提供的阻塞调度器上执行，避免阻塞事件循环。
     *
     * @param request 请求参数
     * @return Mono 包装的响应
     */
    public Mono<RoutifyResponse> chatReactive(RoutifyRequest request) {
        return null;
    }

    /**
     * 响应式流式调用，直接返回 Flux<RoutifyChunk>。
     * <p>
     * 底层 VendorAdapter 的 chatStream 应返回 Flux，此处直接透传。
     *
     * @param request 请求参数
     * @return 流式数据块 Flux
     */
    public Flux<RoutifyChunk> chatStreamReactive(RoutifyRequest request) {
        return null;
    }
}

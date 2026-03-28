package io.github.panjung99.routify.core.client;

import io.github.panjung99.routify.core.model.dto.RoutifyChunk;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;

/**
 * 流式响应回调处理器，用于 {@link RoutifyClient#chatStream(RoutifyRequest, StreamResponseHandler)}。
 * <p>
 * 实现类需要处理以下三种事件：
 * <ul>
 *   <li>{@link #onNext(RoutifyChunk)}：每当收到一个响应块时调用</li>
 *   <li>{@link #onError(Throwable)}：流处理过程中发生错误时调用</li>
 *   <li>{@link #onComplete()}：流正常结束时调用</li>
 * </ul>
 * 注意：回调可能在不同的线程中执行，实现类需确保线程安全。
 * </p>
 */
public interface StreamResponseHandler {

    /**
     * 接收到一个响应块时调用。
     *
     * @param chunk 包含部分响应内容的块
     */
    void onNext(RoutifyChunk chunk);

    /**
     * 发生错误时调用。
     *
     * @param throwable 异常信息
     */
    void onError(Throwable throwable);

    /**
     * 流正常结束时调用。
     */
    void onComplete();
}

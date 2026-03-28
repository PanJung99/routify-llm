package io.github.panjung99.routify.core.adapter.deepseek;

import io.github.panjung99.routify.core.adapter.AbstractChatAdapter;
import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.client.StreamResponseHandler;
import io.github.panjung99.routify.core.model.dto.RoutifyChunk;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.Vendor;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.util.JsonUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import java.nio.charset.StandardCharsets;

@Slf4j
public class DeepSeekChatAdapter extends AbstractChatAdapter {

    private final DeepSeekChatReqMapper deepSeekChatReqMapper;

    public DeepSeekChatAdapter() {
        this.deepSeekChatReqMapper = DeepSeekChatReqMapper.INSTANCE;
    }

    @Override
    public RoutifyResponse doChat(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context) {

        return null;
    }

    @Override
    protected void doChatStream(RoutifyRequest request, VendorModel model, Vendor vendor, ApiKey apiKey, RoutingContext context, StreamResponseHandler handler) {
        //TODO 校验 model apiKey


        // 1. 获取基础 HttpClient（建议从配置中获取复用的实例）
        HttpClient httpClient = HttpClient.create()
                .doOnConnected(conn -> {
                    // Netty 自带的行解码器
                    // 1024 * 64 表示单行最大长度 64KB
                    conn.addHandlerLast(new io.netty.handler.codec.LineBasedFrameDecoder(64 * 1024));
                    // 可选：再加上字符串解码器，这样 inbound.receive().asString() 拿到的就是完整的一行
                    conn.addHandlerLast(new io.netty.handler.codec.string.StringDecoder(StandardCharsets.UTF_8));
                })
                .baseUrl(vendor.getApiBaseUrl())
                .headers(h -> {
                    h.add(HttpHeaderNames.AUTHORIZATION, "Bearer " + apiKey.getApiKey());
                    h.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                    h.add(HttpHeaderNames.ACCEPT, "text/event-stream");
                });

        DeepSeekChatRequest deepSeekRequest = deepSeekChatReqMapper.toDeepSeekChatReq(request);
        deepSeekRequest.setStream(true);

        httpClient
                .post()
                .uri("/chat/completions")
                .send(ByteBufFlux.fromString(Mono.just(JsonUtil.toJson(deepSeekRequest)))) // 序列化 Body
                .responseConnection((res, conn) -> {
                    // TODO 状态码检查（对应你之前的 TODO）

                    // 处理 SSE 流
                    return conn.inbound()
                            .receive()
                            .asString()  // switches to String
                            .filter(line -> line.startsWith("data:")) // dispose data that not follow with SSE protocol.
                            .map(line -> line.substring(5).trim())
                            .filter(data -> !data.isEmpty() && !"[DONE]".equals(data))
                            .mapNotNull(data -> JsonUtil.readValue(data, RoutifyChunk.class));
                })
                .doOnError(error -> log.error("Error processing Netty stream", error))
                .doOnComplete(() -> log.debug("Netty Stream completed"));
    }
}

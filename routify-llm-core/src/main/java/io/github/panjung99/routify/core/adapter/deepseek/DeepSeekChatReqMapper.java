package io.github.panjung99.routify.core.adapter.deepseek;

import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface DeepSeekChatReqMapper {
    
    DeepSeekChatReqMapper INSTANCE = Mappers.getMapper(DeepSeekChatReqMapper.class);

    @Mapping(target = "messages", expression = "java(mapMessages(request.getMessages()))")
    DeepSeekChatRequest toDeepSeekChatReq(RoutifyRequest request);

    default List<DeepSeekChatRequest.Message> mapMessages(List<RoutifyRequest.Message> messages) {
        if (messages == null) {
            return null;
        }
        return messages.stream()
                .map(this::mapMessage)
                .collect(Collectors.toList());
    }

    default DeepSeekChatRequest.Message mapMessage(RoutifyRequest.Message message) {
        if (message == null) {
            return null;
        }
        DeepSeekChatRequest.Message deepSeekMessage = new DeepSeekChatRequest.Message();
        deepSeekMessage.setRole(message.getRole());
        deepSeekMessage.setContent(message.getFlattenText());
        deepSeekMessage.setName(message.getName());
        deepSeekMessage.setToolCallId(message.getToolCallId());
        return deepSeekMessage;
    }
}
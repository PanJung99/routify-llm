package io.github.panjung99.routify.core.model.dto;

import lombok.Data;

import java.util.List;

// OpenAI 流式响应块 (SSE)
@Data
public class RoutifyChunk {

    /**
     * 必需参数：模型生成的响应选项列表
     * 当请求参数 n > 1 时返回多个选项
     */
    private List<Choice> choices;

    /**
     * 必需参数：响应创建时间（Unix时间戳，秒级）
     * 示例: 1677652288
     */
    private long created;

    /**
     * 必需参数：本次请求的唯一标识符
     * 格式: "chatcmpl-123"
     */
    private String id;

    /**
     * 必需参数：使用的模型名称
     * 示例: "gpt-3.5-turbo-0613"
     */
    private String model;

    /**
     * 必需参数：对象类型
     * 固定值: "chat.completion.chunk"
     */
    private String object = "chat.completion.chunk";

    /**
     * 服务层级 (auto/default/flex/priority)
     */
    private String serviceTier;


    private String systemFingerprint;


    /**
     * 使用统计 (仅最后一个分块包含)
     */
    private Usage usage;

    @Data
    public static class Choice {

        private int index;

        private Delta delta;

        private String finishReason;

        private Object logprobs;
    }

    @Data
    public static class Delta {

        private String role;

        private String content;

        private String reasoningContent;

        /**
         * 图像信息
         */
        private Image image;

        /**
         * 模型生成的工具调用
         */
        private List<ToolCallDelta> toolCalls;
    }

    @Data
    public static class ToolCallDelta {

        /**
         * 工具调用 ID
         */
        private String id;

        /**
         * 工具调用索引
         */
        private Integer index;


        /**
         * 工具类型，目前仅支持 "function"
         */
        private String type;

        /**
         * 函数调用对象
         */
        private Function function;

        @Data
        public static class Function {

            /**
             * 函数名称
             */
            private String name;

            /**
             * 函数调用参数（JSON 字符串）
             */
            private String arguments;
        }
    }



    @Data
    public static class TokenDetails {
        private String tokenType;

        private int tokenCount;

        private int byteCount;

        private List<String> tokens;
    }

    /**
     * 图像信息对象
     */
    @Data
    public static class Image {
        /**
         * 图像URL
         */
        private String url;
    }
}
package io.github.panjung99.routify.core.model.dto;

public class Usage {
    /**
     * 提示令牌数
     */
    private int promptTokens;

    /**
     * 补全令牌数
     */
    private int completionTokens;

    /**
     * 总令牌数
     */
    private int totalTokens;

    /**
     * 计费单位
     */
    private int billedUnits;

    /**
     * 补全令牌详细信息
     */
    private CompletionTokensDetails completionTokensDetails;

    /**
     * 计费单位详细信息
     */
    private BilledUnitsDetails billedUnitsDetails;

    public Usage() {

    }

    public Usage(int promptTokens, int completionTokens, int totalTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }

    /**
     * 补全令牌详细信息内部类
     */
    public static class CompletionTokensDetails {
        /**
         * 推理令牌数
         */
        private int reasoningTokens;

        public CompletionTokensDetails() {
        }

        public CompletionTokensDetails(int reasoningTokens) {
            this.reasoningTokens = reasoningTokens;
        }
    }

    /**
     * 计费单位详细信息内部类
     */
    public static class BilledUnitsDetails {
        /**
         * 图像单位数
         */
        private int imageUnits;

        public BilledUnitsDetails() {
        }

        public BilledUnitsDetails(int imageUnits) {
            this.imageUnits = imageUnits;
        }
    }
}
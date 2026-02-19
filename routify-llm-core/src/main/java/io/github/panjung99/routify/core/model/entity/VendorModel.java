package io.github.panjung99.routify.core.model.entity;

import io.github.panjung99.routify.core.model.enums.ModelCategory;
import lombok.Data;


/**
 * 服务商模型表实体类
 */
@Data
public class VendorModel {

    /**
     * 模型名称(服务商内部名称)
     */
    private String name;

    /**
     * 模型所属服务商
     */
    private Vendor vendor;

    /**
     * 模型类别：chat-对话模型,image-绘画模型,audio-语音模型,video-视频模型,embedding-多模态向量模型
     */
    private ModelCategory category;
}

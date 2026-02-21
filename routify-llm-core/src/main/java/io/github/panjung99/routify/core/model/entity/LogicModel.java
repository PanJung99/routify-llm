package io.github.panjung99.routify.core.model.entity;

import io.github.panjung99.routify.core.model.enums.ModelCategory;
import lombok.Data;

import java.util.List;


/**
 * 模型标签表实体类
 */
@Data
public class LogicModel {

    /**
     * API调用的标识名称
     */
    private String name;

    /**
     * 逻辑模型与服务商模型的绑定关系
     */
    private List<ModelBinding> bindings;

    /**
     * 是否免费
     */
    private Boolean isFree;

    /**
     * chat,image,audio,video,embedding
     * 对话模型，绘画模型，语音模型，视频模型，多模态向量模型
     */
    private ModelCategory category;

}

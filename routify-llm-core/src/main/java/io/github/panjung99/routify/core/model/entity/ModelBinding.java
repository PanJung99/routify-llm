package io.github.panjung99.routify.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
public class ModelBinding {


    /** 平台模型ID */
    private Long modelId;

    /** 服务商模型ID */
    private Long venModelId;

    /** 是否启用：0-禁用 1-启用 */
    private Integer isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

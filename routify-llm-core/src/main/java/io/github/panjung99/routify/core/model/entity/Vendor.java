package io.github.panjung99.routify.core.model.entity;

import io.github.panjung99.routify.core.model.enums.VendorType;
import lombok.Data;

import java.util.List;

/**
 * 服务商基本信息表实体类
 */
@Data
public class Vendor {

    /**
     * 服务商名称
     */
    private String name;

    /**
     * 服务商类型
     */
    private VendorType vendorType;

    private List<ApiKey> apiKeys;

    /**
     * API基础地址
     */
    private String apiBaseUrl;

}
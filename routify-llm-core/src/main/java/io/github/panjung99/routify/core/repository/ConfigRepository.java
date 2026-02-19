package io.github.panjung99.routify.core.repository;

import io.github.panjung99.routify.core.model.entity.ApiKey;
import io.github.panjung99.routify.core.model.entity.LogicModel;
import io.github.panjung99.routify.core.model.entity.Vendor;

import java.util.List;
import java.util.Optional;

public interface ConfigRepository {
    /**
     * 根据逻辑模型名称获取对应的映射配置。
     * 实现方可以自由选择数据来源（内存、数据库、配置中心等），并可自行添加缓存。
     */
    Optional<LogicModel> getModel(String modelName);

    /**
     * 根据厂商名称获取厂商的完整配置。
     */
    Optional<Vendor> getVendor(String vendorName);

    /**
     * 根据 API Key 的值获取对应的配置（用于配额管理等场景）。
     */
    Optional<ApiKey> getApiKey(String keyValue);


    /**
     * 可选：获取所有映射关系（类似 listAllVendors）。
     */
    default List<LogicModel> listAllMappings() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

package io.github.panjung99.routify.core.repository;

import io.github.panjung99.routify.core.model.entity.*;
import io.github.panjung99.routify.core.model.enums.ModelCategory;
import io.github.panjung99.routify.core.model.enums.VendorType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple ConfigRepository implementation for quick Hello World.
 * Just providing modelName、baseUrl、apiKey, and this class will create a fully functional int-memory ConfigRepository instance.
 */
public class SimpleConfigRepository implements ConfigRepository {
    
    private final Map<String, LogicModel> logicModels = new ConcurrentHashMap<>();
    private final Map<String, Vendor> vendors = new ConcurrentHashMap<>();
    private final Map<String, List<VendorModel>> vendorModels = new ConcurrentHashMap<>();
    
    private final String baseUrl;
    private final String apiKey;
    private final String modelName;
    private final VendorType vendorType;

    /**
     * Constructs a new repository with essential credentials.
     * @param modelName The critical name of the model, it should be same as your vendor name.
     * @param baseUrl The base API endpoint for the vendor.
     * @param apiKey The authentication key for the vendor.
     */
    public SimpleConfigRepository(String modelName, VendorType vendorType, String baseUrl, String apiKey) {
        this.modelName = modelName;
        this.vendorType = vendorType;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;

        createSingleConfig();
    }

    /**
     * Use the construction param to create a single config that has 1 logicModel, 1 vendorModel and 1 vendor.
     */
    private void createSingleConfig() {
        Vendor vendor = new Vendor();
        vendor.setVendorId("single-vendor");
        vendor.setVendorType(vendorType);
        vendor.setApiBaseUrl(baseUrl);
        vendors.put(vendor.getVendorId(), vendor);

        VendorModel vendorModel = new VendorModel();
        vendorModel.setVendorModelId(modelName);
        vendorModel.setName(modelName);
        vendorModel.setVendorId(vendor.getVendorId());
        vendorModel.setWeight(100);

        // 3. 创建逻辑模型
        LogicModel logicModel = new LogicModel();
        logicModel.setName(modelName);
        logicModel.setIsFree(false);
        logicModel.setCategory(ModelCategory.chat);
        logicModels.put(modelName, logicModel);

        vendorModels.put(modelName, Arrays.asList(vendorModel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LogicModel> getModel(String modelName) {
        if (this.modelName.equals(modelName)) {
            return Optional.ofNullable(logicModels.get(modelName));
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VendorModel> getVendorModels(String modelName) {
        if (this.modelName.equals(modelName)) {
            return vendorModels.getOrDefault(modelName, Collections.emptyList());
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Vendor> getVendor(String vendorId) {
        return Optional.ofNullable(vendors.get(vendorId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApiKey> getApiKeysByVendor(String vendorId) {
        List<ApiKey> apiKeys = new ArrayList<>();
        ApiKey apiKey = new ApiKey();
        apiKey.setVendorId(vendorId);
        apiKey.setApiKey(this.apiKey);
        apiKeys.add(apiKey);
        return apiKeys;
    }
}
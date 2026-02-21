package io.github.panjung99.routify.core.example;

import io.github.panjung99.routify.core.client.RoutifyClient;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.*;
import io.github.panjung99.routify.core.model.enums.ModelCategory;
import io.github.panjung99.routify.core.model.enums.VendorType;
import io.github.panjung99.routify.core.repository.ConfigRepository;

import java.util.Arrays;
import java.util.Optional;

public class HelloWorld {

    public static void main(String[] args) {
        Vendor vendor = new Vendor();
        vendor.setName("gemini");
        vendor.setVendorType(VendorType.GEMINI);
        vendor.setApiBaseUrl("");

        VendorModel vendorModel = new VendorModel();
        vendorModel.setVendor(vendor);
        vendorModel.setCategory(ModelCategory.chat);
        ModelBinding binding = new ModelBinding();
        binding.setVendorModel(vendorModel);

        LogicModel model = new LogicModel();
        model.setName("");
        model.setCategory(ModelCategory.chat);
        model.setIsFree(false);
        model.setBindings(Arrays.asList(binding));

        ConfigRepository simpleRepository = new ConfigRepository() {
            @Override
            public Optional<LogicModel> getModel(String modelName) {
                return Optional.of(model);
            }

            @Override
            public Optional<Vendor> getVendor(String vendorName) {
                return Optional.of(vendor);
            }

            @Override
            public Optional<ApiKey> getApiKey(String keyValue) {
                return Optional.empty();
            }
        };

        RoutifyClient client = RoutifyClient.builder()
                .configRepository(simpleRepository)
                .build();

        RoutifyRequest request = new RoutifyRequest();
        RoutifyResponse response = client.chat(request);

    }
}

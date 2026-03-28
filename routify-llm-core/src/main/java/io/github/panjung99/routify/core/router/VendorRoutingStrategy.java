package io.github.panjung99.routify.core.router;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.model.entity.LogicModel;
import io.github.panjung99.routify.core.model.entity.VendorModel;

public interface VendorRoutingStrategy {

    /**
     * 从给定的绑定列表中选择一个服务商模型
     * @param context 路由上下文（包含请求信息、用户传入的成本偏好等）
     * @return 选中的 VendorModel，若无可用则返回 null
     */
    VendorModel route(RoutingContext context);


}

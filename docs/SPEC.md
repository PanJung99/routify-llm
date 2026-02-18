# Routify-LLM 功能规格说明书 (Functional Specification)

Routify-LLM 是专为 Java 生态打造的全厂商模型路由网关 SDK：它既能用一套协议兼容所有主流模型请求（输入兼容 OpenAI/Claude 等，输出对接通义、文心、Gemini 等全网模型），又内置了分布式智能路由与负载均衡，实时根据延迟、错误率、配额自动调度；同时提供高可用容灾和灵活模型映射（逻辑模型→多厂商模型），让每一次调用都自动走向最稳、最快、最省的那个接口。


---

## 1. 核心目标 (Core Objectives)
- **极简集成**：零依赖（Core 包），兼容 Java 8，适配传统与现代企业级环境。
- **智能调度**：不只是简单的轮询，而是基于实时表现（耗时、成功率、成本）的动态路由。
- **抗污染能力**：通过分布式样本聚合算法，屏蔽异常实例的噪声数据，确保全局路由评分的真实性。
- **自适应性能**：自动探测运行环境，在 Java 21+ 环境下自动开启虚拟线程优化。

---

## 2. 功能模块划分 (Module Roadmap)

### A. Routify-LLM-Core (核心路由引擎)
> 灵魂所在，负责“选模型”的逻辑。
- **抽象协议层**：定义统一的 `ChatRequest` 与 `ChatResponse` 接口。
- **科学打分系统 (Scoring System)**：
    - 基于 **EWMA (指数加权移动平均)** 的时间衰减评分。
    - 多维度加权：响应耗时 ($W_{lat}$)、错误率 ($W_{err}$)、Token 吞吐量 ($W_{tp}$)。
- **动态路由器 (Dynamic Router)**：
    - 支持加权随机、最小负载优先、以及基于评分的 Top-K 过滤。
    - **熔断机制**：当某个模型评分低于阈值时，自动进入隔离区。
- **环境探测器**：反射探测 `VirtualThread` 类，实现跨版本的高性能并发调度。

### B. Routify-LLM-Metrics (指标与状态管理)
> 负责“记数据”的逻辑。
- **样本聚合器 (Aggregator)**：支持将多实例的局部观测数据（本地打分）合并为全局状态。
- **异常值剔除 (3σ 原则)**：识别并忽略偏离正态分布的异常实例数据，防止局部网络污染全局路由。
- **SPI 存储接口**：定义 `RouteStateStore`，支持内存、Redis、MySQL 等多种状态持久化方式。

### C. Routify-LLM-Adapters (生态适配器)
> 负责“接模型”的逻辑。
- **Model Adapters**：内置 OpenAI、DashScope (通义千问)、DeepSeek 等协议转换器。
- **Framework Adapters**：
    - `routify-llm-adapter-langchain4j`：深度集成 LangChain4j。
    - `routify-llm-adapter-agentscope`：作为 AgentScope 的智能路由插件。

---

## 3. 核心算法逻辑 (Algorithm Logic)

### 科学聚合评分公式
$$Score_{new} = \frac{\sum (Score_{local} \times Weight_{local}) + Score_{global} \times Weight_{global}}{Weight_{local} + Weight_{global}}$$

### 抗污染策略
1. **数据清洗**：剔除样本量不足（Count < N）的实例反馈。
2. **偏差校验**：若 `Local_Latency` > `Global_Avg_Latency` * 3，则判定为该实例网络受损，其数据不计入全局评分。

---

## 4. 技术限制与规范 (Constraints)
- **JDK 版本**：编译目标为 1.8，确保向下兼容。
- **依赖控制**：`core` 模块禁止引入 Jackson/Fastjson/Spring 等第三方库，采用 SPI 或 Adapter 模式处理依赖冲突。
- **包名规范**：`io.github.panjung99.routify`
- **开源协议**：MIT License


```
public class ExecutorFactory {
    private static final Logger log = Logger.getLogger(ExecutorFactory.class.getName());

    public static ExecutorService createExecutor(int fallbackThreadCount) {
        try {
            // 1. 尝试寻找 Java 21 引入的虚拟线程方法
            Method newVirtualThreadMethod = Executors.class.getMethod("newVirtualThreadPerTaskExecutor");
            log.info("Detected Java 21+, using Virtual Thread Executor.");
            return (ExecutorService) newVirtualThreadMethod.invoke(null);
        } catch (NoSuchMethodException e) {
            // 2. 如果找不到方法，说明是低版本 Java
            log.info("Detected pre-Java 21, falling back to Fixed Thread Pool.");
            return Executors.newFixedThreadPool(fallbackThreadCount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize executor", e);
        }
    }
}
```
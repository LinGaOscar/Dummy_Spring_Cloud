# Dev Notes

## 需求

- JDK 8（`java.version` 在父 POM 寫死為 `1.8`）
- 不需要另外裝 Maven：每個模組目錄下有自己的 `mvnw` / `mvnw.cmd`

## 本機開發：啟動兩個服務

```bash
# Terminal 1：先啟動 Eureka Server
cd DummySpringCloud/DummyDiscovery
./mvnw spring-boot:run
# -> http://localhost:8761 是 Eureka Dashboard

# Terminal 2：再啟動 Client
cd DummySpringCloud/DummyClient
./mvnw spring-boot:run -Deureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**已知坑**：`DummyClient/src/main/resources/application.yml` 把 `eureka.client.service-url.defaultZone` 寫死成 `http://dummy_discovery:8761/eureka/`。`dummy_discovery` 這個主機名只有在透過 `podman-compose`（容器間用服務名稱互相解析）時才能連得到。純本機用 Maven 分別啟動兩個服務時，Client 端會因為解析不到主機名而註冊失敗、不斷重試——上面指令用 `-D` 覆寫成 `localhost` 即可繞過，或改成在 `/etc/hosts` 加一行 `127.0.0.1 dummy_discovery`。

啟動成功後，打開 http://localhost:8761 確認 `DummyClient` 出現在「Instances currently registered with Eureka」清單中即代表註冊成功。

## 跑測試

```bash
cd DummySpringCloud/DummyDiscovery && ./mvnw test
cd DummySpringCloud/DummyClient && ./mvnw test
```

目前只有 Spring Boot 預設產生的 `contextLoads()` 空測試（`DummyClientApplicationTests`），沒有 `DummyDiscovery` 的測試檔案，也沒有任何實際的單元/整合測試邏輯。

## 結構

```
DummySpringCloud/            # 父 POM（packaging: pom，純聚合，不能單獨執行）
├── DummyDiscovery/          # Eureka Server，port 8761
│   └── src/main/java/com/oscar/dummydiscovery/DummyDiscoveryApplication.java
├── DummyClient/              # Eureka Client，port 8080，無業務邏輯
│   └── src/main/java/com/oscar/dummyclient/DummyClientApplication.java
└── podman-compose.yaml      # 容器化部署設定（image 需自行建置，見下方）
```

- 兩個模組各自的 `application.yml` 在 `src/main/resources/`，是唯一的設定來源（沒有 `application-*.yml` profile 分檔）。
- Spring Cloud 版本 2021.0.3 + Spring Boot 2.7.8（都寫在父 `pom.xml` 的 `<properties>`）。
- **沒有 Dockerfile**：`podman-compose.yaml` 引用 `your_registry/dummy_discovery:latest` 與 `your_registry/dummy_client:latest` 兩個 image，但 repo 內找不到對應的 Dockerfile，要用 compose 跑起來得自己補上。
- `podman-compose.yaml` 裡 `dummy_client` 的環境變數 `EUREKA_SERVER_URL` 目前沒有作用（屬性名對不上 Spring 讀取的 `eureka.client.service-url.defaultZone`），不要被它誤導。

## 沒有的東西（誠實列出，避免猜測）

- 沒有 CI 設定（`.github/workflows` 不存在）
- 沒有 Config Server、沒有 API Gateway
- 沒有 lint / formatter 設定
- 沒有硬編碼的密碼或 API key（已檢查過）

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Dummy Spring Cloud 是一個學習用的 Maven 多模組專案，示範 Spring Cloud + Netflix Eureka 的服務註冊與發現機制；`DummyClient` 目前只完成 Eureka 註冊，沒有任何業務邏輯或 REST endpoint。

## Commands

`mvnw` 只存在於各模組目錄下（`DummySpringCloud/DummyDiscovery/mvnw`、`DummySpringCloud/DummyClient/mvnw`），父目錄 `DummySpringCloud/` 沒有自己的 wrapper——父 POM 是 `packaging: pom` 的聚合模組，不可執行 `spring-boot:run`。所有指令都要先 `cd` 進對應模組目錄。

```bash
# 建置＋跑測試（在各模組目錄下）
cd DummySpringCloud/DummyDiscovery && ./mvnw clean verify
cd DummySpringCloud/DummyClient && ./mvnw clean verify

# 只跑測試
./mvnw test

# 啟動單一服務
./mvnw spring-boot:run
```

啟動順序：先啟動 `DummyDiscovery`（Eureka Server，port 8761），再啟動 `DummyClient`（Eureka Client，port 8080）；反過來 Client 會註冊失敗並持續重試。

## Architecture

- **DummyDiscovery**（`DummySpringCloud/DummyDiscovery`）— Netflix Eureka Server，port 8761。`@EnableEurekaServer`；`eureka.client.register-with-eureka: false` 與 `fetch-registry: false`（自己不向自己註冊、不拉取註冊表）。
- **DummyClient**（`DummySpringCloud/DummyClient`）— Eureka Client，port 8080。`@EnableDiscoveryClient` + `spring-boot-starter-web`，目前只完成向 Eureka 註冊，沒有實作任何 Controller 或業務邏輯。
- 服務發現位址寫死在 `DummyClient/src/main/resources/application.yml`：`eureka.client.service-url.defaultZone: http://dummy_discovery:8761/eureka/`。這個 hostname 只有在 Podman/Docker Compose 網路內（服務名稱即主機名）才能解析；直接用 Maven 在本機各自啟動兩個服務時無法連上，需覆寫該屬性或編輯 `/etc/hosts`（細節見 `dev.md`）。
- 沒有 API Gateway、沒有 Config Server，是最小可行的 Eureka 註冊/發現示範。
- `DummySpringCloud/podman-compose.yaml` 定義了 `dummy_discovery` / `dummy_client` 兩個容器，但 repo 內**沒有 Dockerfile**，需自行撰寫才能建置對應 image。compose 中的環境變數 `EUREKA_SERVER_URL` 與程式碼實際讀取的屬性名（`eureka.client.service-url.defaultZone`）不一致，目前不會生效。

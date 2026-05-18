# Dummy Spring Cloud

一個用於學習 **Spring Cloud** 與 **Netflix Eureka** 服務發現的練習專案。

## 專案結構

```
Dummy_Spring_Cloud/
└── DummySpringCloud/          # Parent Maven Project
    ├── DummyDiscovery/         # Eureka 服務發現伺服器
    ├── DummyClient/            # 註冊到 Eureka 的 Client 服務
    ├── pom.xml                 # Spring Cloud 2021.0.3 + Boot 2.7.8
    └── podman-compose.yaml     # Docker 部署設定
```

## 技術棧

| 技術 | 版本 |
|------|------|
| Java | 1.8 |
| Spring Boot | 2.7.8 |
| Spring Cloud | 2021.0.3 |
| Netflix Eureka | 3.1.3 |

## 模組說明

### DummyDiscovery（服務發現中心）
- **Port**: 8761
- **用途**: Netflix Eureka Server，所有服務在此註冊與被發現
- `eureka.client.register-with-eureka: false` — 不向自己註冊
- `eureka.client.fetch-registry: false` — 不獲取註冊表

### DummyClient（服務客戶端）
- **Port**: 8080
- **用途**: 連線至 Eureka Server 並完成註冊
- 包含 `spring-boot-starter-web` 可提供簡單的 Web 端點

## 本地運行

### 方式一：使用 Podman Compose（推薦）

```bash
cd DummySpringCloud

# 建置 Image（需先修改 podman-compose.yaml 中的 image 位置）
podman-compose up --build

# 訪問 Eureka Dashboard
open http://localhost:8761
```

### 方式二：使用 Maven 直接啟動

```bash
cd DummySpringCloud

# 先啟動 Discovery
cd DummyDiscovery
../mvnw spring-boot:run

# 再啟動 Client（新開一個終端）
cd DummyClient
../mvnw spring-boot:run

# 或指定 port 運行
../mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

### 方式三：使用 IDE

1. 用 IDE 開啟 `DummySpringCloud/pom.xml` 作為 Maven 專案
2. 先執行 `DummyDiscoveryApplication`（會在 port 8761 啟動）
3. 再執行 `DummyClientApplication`（會在 port 8080 啟動）

## 驗證

啟動完成後，開啟瀏覽器访问：

- **Eureka Dashboard**: http://localhost:8761
  - 確認 `DummyClient` 已註冊在「Instances currently registered with Eureka」列表中

## Docker 建置（可選）

如要自行建置 Image，需修改 `podman-compose.yaml` 中的 `image` 路徑：

```bash
# 建置 Discovery Image
cd DummySpringCloud/DummyDiscovery
docker build -t your_registry/dummy_discovery:latest .

# 建置 Client Image
cd DummySpringCloud/DummyClient
docker build -t your_registry/dummy_client:latest .
```

## 學習重點

- Maven Multi-Module 專案結構
- Spring Cloud 服務註冊與發現機制
- Netflix Eureka Server 與 Client 的配置
- 使用 Podman Compose 部署多服務架構
- 服務之間透過服務名稱（而非 IP）進行呼叫

## 注意事項

- 此專案為學習用途，DummyClient 目前沒有實作具体的業務邏輯
- 如需修改 Eureka 註冊相關配置，可編輯 `DummyDiscovery/src/main/resources/application.yml` 與 `DummyClient/src/main/resources/application.yml`
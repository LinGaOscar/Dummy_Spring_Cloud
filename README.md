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

> 注意：`mvnw` 只存在於 `DummyDiscovery/` 與 `DummyClient/` 各自目錄下，父目錄 `DummySpringCloud/` 沒有自己的 wrapper，需在各模組目錄內用 `./mvnw`（而非 `../mvnw`）執行。
>
> 另外 `DummyClient` 的 `eureka.client.service-url.defaultZone` 寫死為 `http://dummy_discovery:8761/eureka/`，這個 hostname 只有在 Podman/Docker Compose 網路內才能被解析。直接用 Maven 在本機各自啟動兩個服務時，`dummy_discovery` 這個主機名無法解析，Client 會註冊失敗並持續重試；需自行在 `/etc/hosts` 加上 `127.0.0.1 dummy_discovery`，或用 `-Deureka.client.service-url.defaultZone=http://localhost:8761/eureka/` 覆寫。

```bash
# 先啟動 Discovery
cd DummySpringCloud/DummyDiscovery
./mvnw spring-boot:run

# 再啟動 Client（新開一個終端，需先處理上方的 hostname 問題）
cd DummySpringCloud/DummyClient
./mvnw spring-boot:run -Deureka.client.service-url.defaultZone=http://localhost:8761/eureka/
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

repo 目前**沒有附 Dockerfile**，`podman-compose.yaml` 裡的 image（`your_registry/dummy_discovery:latest`、`your_registry/dummy_client:latest`）僅為佔位路徑。要用「方式一」跑起來，需自行為兩個模組各寫一支 Dockerfile 再建置，例如：

```bash
# 建置 Discovery Image（需自備 Dockerfile）
cd DummySpringCloud/DummyDiscovery
docker build -t your_registry/dummy_discovery:latest .

# 建置 Client Image（需自備 Dockerfile）
cd DummySpringCloud/DummyClient
docker build -t your_registry/dummy_client:latest .
```

另外 `podman-compose.yaml` 中 `dummy_client` 服務設定的環境變數 `EUREKA_SERVER_URL` 目前不會生效——程式碼實際讀取的屬性是 `eureka.client.service-url.defaultZone`（寫死在 `application.yml`），不是 `EUREKA_SERVER_URL`，兩者對不上。若要讓 compose 也能覆寫 Eureka 位址，需把環境變數改名為 `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`。

## 學習重點

- Maven Multi-Module 專案結構
- Spring Cloud 服務註冊與發現機制
- Netflix Eureka Server 與 Client 的配置
- 使用 Podman Compose 部署多服務架構
- 服務之間透過服務名稱（而非 IP）進行呼叫

## 注意事項

- 此專案為學習用途，DummyClient 目前沒有實作具体的業務邏輯
- 如需修改 Eureka 註冊相關配置，可編輯 `DummyDiscovery/src/main/resources/application.yml` 與 `DummyClient/src/main/resources/application.yml`
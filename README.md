# EchoSphere - A Distributed Chat System

![EchoSphere Banner](https://raw.githubusercontent.com/Isomorphismss/echo-sphere/refs/heads/main/banner.svg)

## Introduction
**EchoSphere** is a distributed, real-time messaging platform designed for high concurrency and scalability. It supports **one-on-one messaging, group chat, social feeds, and a drifting bottle feature**, offering a seamless communication experience across multiple devices.

Built on a **microservices architecture**, EchoSphere leverages **Spring Cloud Alibaba**, **Netty**, and **RabbitMQ** to ensure efficient message delivery, while **Redis**, **MinIO**, and **Elasticsearch** provide a robust backend for caching, media storage, and real-time search.

## ✨ Features
- 🔐 **Secure Authentication**: Phone number login with **AWS**, JWT-based distributed session management.
- 🚀 **High-Performance Messaging**:
    - One-on-one chat: **Netty clusters** + **RabbitMQ (routing mode)**
    - Group chat: **RabbitMQ (broadcast mode)**
    - **Offline messaging** stored in a queue until the user reconnects.
- 💾 **Distributed Storage**:
    - **MinIO** for storing media files (profile pictures, images, voice messages).
    - **Redis** for real-time caching and session management.
- 🔄 **Service Discovery & Routing**:
    - **Nacos** for service registration & configuration.
    - **Spring Cloud Gateway + Redis** for API routing & rate limiting.
- 🛡️ **Resilience & Monitoring**:
    - **SkyWalking** for distributed tracing & performance analysis.
    - **Sentinel** for circuit breaking & rate limiting.
- 🔗 **Netty Cluster Management**:
    - **Zookeeper** for tracking server status & user distribution.
    - **Curator** for distributed read/write locks.
    - **Redis** for dynamic Netty port allocation.
- 📍 **Geo-Search & Social Features**:
    - **Elasticsearch** for **geo-based search** (used in the drifting bottle feature).
    - **Redis + Caffeine** for social feed likes.
    - **Google ZXing** to generate user QR codes.

## 🛠️ Tech Stack
| Category        | Technology |
|----------------|------------|
| Backend        | Spring Cloud Alibaba, Spring Boot, Netty, RabbitMQ |
| Storage        | MySQL, Redis, MinIO, Elasticsearch |
| API Gateway    | Spring Cloud Gateway |
| Service Discovery | Nacos |
| Monitoring     | SkyWalking, Sentinel |
| Cluster Management | Zookeeper, Curator |
| Frontend       | Vue, Uni-APP |
| Authentication | JWT, Tencent Cloud SMS SDK |
| Caching        | Redis, Caffeine |
| Messaging Queue | RabbitMQ |

## 🚀 Running Guide
### 1️⃣ Prerequisites
Ensure you have the following installed and configured:
- **JDK 17+**
- **MySQL 8+**
- **Redis**
- **Nacos**
- **RabbitMQ**
- **MinIO**
- **Elasticsearch**
- **Zookeeper**

Make sure **environment variables** are correctly set, including database connection credentials, Redis configurations, and other service authentication details.

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/Isomorphismss/echo-sphere.git
cd echo-sphere
```

### 3️⃣ Start Services
Start the required services in the following order:
```bash
# Start Nacos
sh startup.sh -m standalone

# Start Redis
redis-server

# Start RabbitMQ
rabbitmq-server

# Start MinIO
minio server /data

# Start Elasticsearch
elasticsearch

# Start Zookeeper
zkServer.sh start
```

### 4️⃣ Start the Chat Server
After starting all required services, launch the chat server:
```bash
sh start.sh
```
By default, **the chat server runs in cluster mode**. You can:
- Switch to **single-instance mode** based on the configuration.
- Adjust the **number of Netty servers** dynamically in cluster mode.

### 5️⃣ Running the Frontend
To experience the full functionality of EchoSphere, you need to start the frontend. Navigate to the [frontend repo](https://github.com/Isomorphismss/echo-sphere-uniapp) and follow the instructions in the readme.

### ⚠️ Troubleshooting
- If `start.sh` does not **automatically create database tables**, please contact me for the SQL schema.
- Ensure **all service connections (database, Redis, MinIO, etc.) are correctly configured in the environment variables**.

## License
This project is licensed under the **MIT License**.



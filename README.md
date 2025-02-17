# EchoSphere - A Distributed Chat System

<svg width="600" height="100" viewBox="0 0 600 100" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bgGradient" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0%" stop-color="#00c6ff" />
      <stop offset="100%" stop-color="#0072ff" />
    </linearGradient>

    <linearGradient id="textGradient" x1="0" y1="0" x2="0" y2="1" spreadMethod="pad">
      <stop offset="0%" stop-color="#FFD700" />
      <stop offset="100%" stop-color="#FF8C00" />
    </linearGradient>
  </defs>

  <rect width="600" height="100" fill="url(#bgGradient)" />

  <path d="M0,70 C150,110 450,10 600,70 L600,100 L0,100 Z" fill="#ffffff" opacity="0.25" />

  <path d="M0,60 C150,100 450,0 600,60 L600,100 L0,100 Z" fill="#ffffff" opacity="0.25" />

<text
x="50%"
y="50%"
alignment-baseline="middle"
text-anchor="middle"
font-family="'Brush Script MT', cursive"
font-size="36px"
font-weight="bold"
stroke="#000000"
stroke-width="1"
fill="url(#textGradient)"
>
    EchoSphere
  </text>
</svg>

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

### ⚠️ Troubleshooting
- If `start.sh` does not **automatically create database tables**, please contact me for the SQL schema.
- Ensure **all service connections (database, Redis, MinIO, etc.) are correctly configured in the environment variables**.

## License
This project is licensed under the **MIT License**.



# EchoSphere - A Distributed Chat System

![EchoSphere Banner](https://raw.githubusercontent.com/Isomorphismss/echo-sphere/refs/heads/main/banner.svg)

## 🌟 Introduction
EchoSphere is a next-generation **real-time chat system**, designed for seamless communication across multiple devices.  
It supports **one-on-one messaging, group chats, social feeds, and a drifting bottle feature**, bringing a unique social experience to users.

With its **high-performance backend** and **cloud-based microservice architecture**, EchoSphere ensures reliable and fast message delivery, even under heavy traffic.

## ✨ Features

- 🔐 **Secure Authentication**
    - Register and log in using a **phone number** and **secure authentication system**.
    - Supports session management for seamless cross-device access.

- 📩 **Real-Time Messaging**
    - **One-on-one chats** with real-time message delivery.
    - **Group chats** for easy communication with friends and teams.
    - **Offline messaging** ensures you never miss important messages.

- 🌍 **Drifting Bottle - A Geo-Based Feature**
    - Send anonymous messages as a "drifting bottle" into the world.
    - Discover and interact with bottles from users **near your location**.

- ❤️ **Social Feed (Moments)**
    - Share updates, images, and thoughts with your friends.
    - Like and comment on posts, creating an engaging social experience.

- 🏷️ **QR Code Friend Search**
    - Easily add friends by scanning their unique **QR codes**.

- ⚡ **High Performance & Scalability**
    - Built for **high concurrency**, supporting thousands of active users.
    - **Smart message routing** ensures optimized delivery.
    - **Geo-based search** makes discovery easier.

[//]: # (![Chat Screenshot]&#40;https://raw.githubusercontent.com/Isomorphismss/echo-sphere/main/screenshots/chat.png&#41;  )

## 🚀 Getting Started

### 1️⃣ Prerequisites
To run EchoSphere, ensure you have the following installed:
- **JDK 17+**
- **MySQL 8+**
- **Redis**
- **Nacos**
- **RabbitMQ**
- **MinIO**
- **Elasticsearch**
- **Zookeeper**

> ⚠️ Make sure environment variables are correctly set for database connections and authentication.

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/Isomorphismss/echo-sphere.git
cd echo-sphere
```

### 3️⃣ Start Required Services
Make sure all dependent services are running:
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
After all services are ready, launch the backend:
```bash
sh start.sh
```
**By default, the server runs in cluster mode.**  
You can configure it to **single-instance mode** if needed.

### 5️⃣ Running the Frontend
To access EchoSphere, start the frontend application. Follow the instructions in the frontend repository:  
🔗 [Frontend Repository](https://github.com/Isomorphismss/echo-sphere-uniapp)


## 📜 License
This project is licensed under the **MIT License**.

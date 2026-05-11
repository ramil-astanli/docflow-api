# 📄 DocumentFlow — Automated Document Approval Workflow API

**DocumentFlow** is a high-level backend system designed to automate document submission and approval processes within an organization. The project leverages **Spring Integration** to manage asynchronous messaging flows, email notifications, and real-time status updates.

## 🛠 Tech Stack

* **Java 21+**: Utilizing the latest LTS features.


* **Spring Boot 4.x**: Built on the cutting edge of the Spring ecosystem.


* **Spring Integration**: Orchestrating event-driven workflow management.


* **Spring Security & JWT**: Secure API access and stateless authentication.


* **Spring Data JPA**: Efficient database management and ORM.


* **PostgreSQL**: Robust and persistent data storage.


* **Docker & Docker Compose**: Seamless containerization and deployment.


* **WebSocket**: Real-time status updates for users and approvers (Bonus Feature).


* **Swagger/OpenAPI**: Interactive API documentation and testing.



## ✨ Key Features

* **Document Submission**: Users upload documents to trigger automated workflows instantly.


* **Automated Email Notifications**: Approvers receive requests via **Spring Integration Mail** automatically.


* **Approval/Rejection API**: Approvers submit decisions through secure REST endpoints with immediate status synchronization.


* **Workflow Status Management**: Precise tracking of document states (SUBMITTED, PENDING, APPROVED, REJECTED).


* **Audit Logging**: Comprehensive history of every action, including submissions, notifications, and decisions.


* **Error Handling**: Built-in **Retry mechanisms** for handling transient failures like email delivery issues.



## 🚀 Installation & Deployment

### 🐳 Dockerization

The project is fully containerized for consistency across development and production environments.

1. **Dockerfile**: A multi-stage build `Dockerfile` is provided to compile the Java application and create a lightweight runtime image.


2. **Docker Compose**: Orchestrates the application and the PostgreSQL database.



### Step-by-Step Setup

1. **Clone the Repository**:
```bash
git clone https://github.com/ramil-astanli/docflow-api.git
cd docflow-api

```


2. **Run with Docker Compose**:
This command builds the image using the **Dockerfile** and starts all necessary services:



```bash
    docker-compose up --build
    ```

3.  **API Documentation**:
    Once the system is running, explore and test the APIs at:
    `http://localhost:8080/swagger-ui.html`[cite: 1]

## 🏗 Architecture (Spring Integration Flow)

Messages move through the system via dedicated channels:
1.  **Document Submission Channel**: Handles incoming documents[cite: 1].
2.  **Notification Router**: Distributes email and WebSocket alerts to designated approvers[cite: 1].
3.  **Approval Gateway**: Processes decisions from the REST API to update the document state[cite: 1].

## 🔒 Security
All API endpoints are protected using **JWT** (JSON Web Token)[cite: 1]. Users must include the `Authorization: Bearer <token>` header in their requests after logging in[cite: 1].

---
**Author:** Ramil Astanli  
**License:** MIT

```

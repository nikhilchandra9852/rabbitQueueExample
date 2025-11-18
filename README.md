# RabbitMQ Microservices Application

## Overview

This project demonstrates a **microservice architecture** using **Spring Boot** and **RabbitMQ** to process orders. It consists of three services:

1. **Producer Service** – Receives/creates orders and sends them to the inventory queue.
2. **Inventory Service** – Consumes orders from the inventory queue, performs business logic, handles **retry + DLQ**, and forwards messages to the billing queue.
3. **Billing Service** – Consumes orders from the billing queue, handles retry + DLQ, and completes the order processing.

All services use **message metadata** (`messageId`, `correlationId`, `orderId`) for logging and traceability.

---

## Architecture
      +-------------------+
      |  Producer Service |
      +-------------------+
                |
                |  order.exchange / order.created.inventory.routingkey
                v
      +-------------------+       +-------------------+
      | Inventory Service |-----> | Billing Service   |
      | Consumer + Producer|      | Consumer          |
      +-------------------+       +-------------------+

- Each service processes messages independently.
- Manual acknowledgement is used for safe message handling.
- Dead-letter queues are used for messages that fail processing after retries.
- Idempotency is implemented using `orderId` to avoid duplicate processing.

## RabbitMQ Setup

**Queues:**

- `order.created.inventory.queue`
- `order.created.billing.queue`
- `inventory.retry.queue`
- `inventory.dlq`
- `billing.retry.queue`
- `billing.dlq`

**Exchanges:** `order.exchange` (Topic Exchange)  

**Routing Keys:**

- Producer → Inventory: `order.created.inventory.routingkey`
- Inventory → Billing: `order.created.billing.routingkey`

**Retry Mechanism:**

- Messages failing in Inventory/Billing are moved to a retry queue with TTL.
- After TTL, messages are re-published to the original queue.
- Failed messages after max retries go to DLQ.

---

## Message Structure

**Payload (`OrderCreateDto`):**

```json
{
  "orderId":2,
  "customerId":3,
  "orderAmount":100.00
}


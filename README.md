![ScyllaDBLogo](https://avatars.githubusercontent.com/u/14364730?s=200&v=4)

# ScyllaDB Documentation

| **Author** | **Created on** | **Version** | **Last updated by** | **Internal Reviewer** | **Reviewer L0** | **Reviewer L1** | **Reviewer L2** |
|------------|----------------|-------------|----------------------|-----------------------|----------------|----------------|----------------|
| Prashant Sharma | 17-02-2025    | version-1   | Prashant Sharma | Siddharth Pawar |                |                |                |

---
## Table of Contents
- [Introduction](#introduction)
    - [What is Scylladb](what-is-scylladb)
    - [Why we choose scylladb](why-we-choose-scylladb)
- [Features](#features)
- [System Requirements](#system-requirements)
- [Important Ports](#important-ports)
- [Architecture](#architecture)
- [Advantages](#advantages)
- [Disdvantages](#disadvantages)
- [Installation](#installation)
- [Use Cases](#use-cases)
- [Conclusion](#conclusion)
- [Contacts](#contacts)
- [References](#references)


## Introduction
## What is ScyllaDB?

ScyllaDB is a fast and scalable NoSQL database that can handle a lot of data and process it quickly. It was created as an alternative to Apache Cassandra, but it is built to perform better and use resources more efficiently. ScyllaDB is written in C++ to make the most of modern computer hardware. It's perfect for applications that need to manage large amounts of data and handle high traffic without slowing down.

## Why we choose Scylladb in Employee API?
We chose ScyllaDB for the Employee API because it can handle all types of data, whether it's organized or not. It's fast and reliable, making it great for managing large amounts of employee information like IDs, names, emails, phone numbers, and job roles. It also handles heavy traffic without slowing down. ScyllaDB works well with Redis, which helps speed up the API by storing frequently accessed data in memory. Since ScyllaDB can easily scale and perform in real time, our API remains quick and responsive.
## Features 

|       Features     |             Description                     |
|--------------------|-----------------------------------------|
| **Scalability**   | ScyllaDB can handle more data and growing workloads by adding more resources.               | 
| **Performance**   | ScyllaDB provides fast data processing, making it great for applications that need high speed.        |
| **Fault Tolerance**   | It keeps your data safe and available, even if some parts of the system fail, by storing copies of the data in multiple places. |
| **Flexibility**     | It can work with different types of applications, letting you organize data in many ways.  |
| **Community and Enterprise Support**     | It is available in both open-source and enterprise editions. |  


## System Requirements 

|   System Requirement              |             Minimum                     |
|-----------------------------------|-----------------------------------------|
| Processor                         |         Dual-Core  / t2.medium instance                     | 
| RAM                               |            4 GB                         |
| Disk Space                        |            20 GB                        |
| OS Required (Linux Distributions) | Ubuntu 22.04 & above |



## Important Ports

 | Port   | Description                  |
|--------|------------------------------|
| 7000   | Inter-node communication     |
| 7001   | TLS inter-node communication |
| 7199   | JMX monitoring                |
| 9042   | CQL native transport          |
| 9142   | SSL CQL native transport      |
| 9160   | Thrift client API             |


## Architecture

- **Sharded Architecture:** ScyllaDB divides the data into smaller parts called "shards." Each CPU core is in charge of one or more of these shards. This way, there’s no need for a central controller, which helps ScyllaDB scale better and work faster.
- **Data Model:** cyllaDB organizes data in tables, like a spreadsheet with rows and columns. Each piece of data is identified by a primary key, which is made up of a "partition key"  and optional "clustering columns".
- **Replication and Consistency:** ScyllaDB allows you to choose how consistent you want your data to be, balancing between making sure the data is always available and keeping it in sync across multiple places.
- **Write and Read Path:** When data is written, it's first stored in memory (MemTable) for quick access, and later saved to disk (SSTable). For reading data, ScyllaDB combines data in memory and on disk, using tools like Bloom Filters to make the process fast and efficient.


## Advantages 

- **High Performance**: ScyllaDB is optimized for low-latency and high-throughput workloads, ensuring fast data processing.
- **Scalable**: It can easily scale horizontally, meaning it can handle more data and traffic by simply adding more resources.  
- **Fault Tolerant**: ScyllaDB has independent nodes with automatic data replication, which ensures high availability even if some parts of the system fail.
- **Cassandra Compatibility**: It is easy for users familiar with Cassandra to migrate to ScyllaDB.
- **Flexible**: ScyllaDB allows dynamic scaling without downtime, meaning you can add or remove resources without interrupting service.                        
           
---

## Disadvantages 
- **Resource Intensive**: To achieve optimal performance, ScyllaDB requires a significant amount of CPU and memory.
- **Complex Setup**: The initial setup and configuration can be tricky and might require fine-tuning to work properly. 
- **Smaller Ecosystem**: Compared to Cassandra, ScyllaDB has fewer third-party tools and a smaller community for support.
- **Learning Curve**: New users may face a learning curve to understand ScyllaDB’s unique architecture.


## Installation 
Follow the link for the installation Document:
[ScyllaDB Installation](https://github.com/Snaatak-Skyops/Documentation/blob/ea796dacd3337b197106ee30449661f0299f7d97/OT%20MS%20Understanding/Database/Scylla%20DB/POC/README.md)


## Use Cases 

- **Analytics:** It can quickly process and analyze large sets of data, making it useful for businesses or systems that need to extract insights from big data fast.
- **IoT (Internet of Things):** ScyllaDB is great for managing large amounts of data from connected devices, like sensors or smart devices, in real-time.
- **Social Media:** It can store and manage large amounts of user-generated content like posts, comments, and likes, with quick retrieval and updates.
- **Gaming:** ScyllaDB can handle real-time game data, such as player scores, game state, and user interactions, while supporting large numbers of players at once.
- **Machine Learning:** ScyllaDB helps store and manage large datasets for training machine learning models, allowing for fast reads and writes during model development.

## Conclusion
In the OT-Microservices system, ScyllaDB is used as the main database for the Employee API. It helps store and retrieve employee data quickly and efficiently, thanks to its high performance, ability to scale easily, and low response times. Since it works well with Cassandra, it's easy to integrate into the system. ScyllaDB's ability to handle failures and keep data available makes it a reliable choice for handling heavy workloads.

## Contacts

| Name| Email Address      |
|-----|--------------------------|
| Prashant Sharma | prashant.sharma@mygurukulam.co |



## References

| Source                                                                                     | Description                                |
| ------------------------------------------------------------------------------------------ | ------------------------------------------ |
| [ScyllaDB Installation Guide](https://opensource.docs.scylladb.com/stable/getting-started/install-scylla/install-on-linux.html) | Comprehensive guide for installing ScyllaDB on Linux. |
| [ScyllaDB Configuration Guide](https://www.scylladb.com/download/?platform=ubuntu-22.04&version=scylla-5.4#open-source) | Step-by-step instructions for configuring ScyllaDB. |

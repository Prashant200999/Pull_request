# POC of Application Monitoring


<img src="https://github.com/user-attachments/assets/e0495a0e-f0f7-4179-8a7f-c6e5031b1be3" width="700" /> <br>


| **Author** | **Created on** | **Version** | **Last updated by**|**Last Edited On**|**Level** |**Reviewer** |
|------------|---------------------------|-------------|----------------|-----|-------------|-------------| 
| Prashant Sharma |   13-04-2025      | v1          | Prashant Sharma   |13-04-2025  |  Internal Reviewer | Siddharth Pawar |


---

## Table of Contents
- [Prerequisites](#prerequisites)
- [Update Redis Configuration](#step-2-update-redis-configuration)
- [Setup Prometheus](#step-3-setup-prometheus)
- [Setup Grafana](#step-4-setup-grafana)
- [Setup Redis Exporter](#step-5-setup-redis-exporter)
- [Configuring Redis Exporter Service](#step-6--configuring-redis-exporter-service)
- [Setting up Grafana Dashboards for Redis Metrics](#step-8-setting-up-grafana-dashboards-for-redis-metrics)
- [Best practices](#best-practices)
- [Conclusion](#conclusion)
- [Contact Information](#contact-information)
- [Reference Links](#reference-links)

## Introduction

This document explains how to monitor Redis using Prometheus and Grafana. It highlights the importance of monitoring Redis for performance, health, and resource usage. Prometheus collects data from Redis, while Grafana visualizes this data, allowing for the creation of custom dashboards. This setup provides real-time insights, helping you detect issues early, optimize performance, and maintain a healthy Redis environment.

## Prerequisites
|                                                 |
|-------------------------------------------------|
| AWS Account with Ubuntu 22.04 LTS EC2 Instance. |
| Basic knowledge of AWS services, Prometheus, and Grafana. |

## Getting Started

![image](https://github.com/user-attachments/assets/f0c2dc92-ab5e-43ac-bf3c-f9a5b98709f6)


###  Step 3. **Setup Prometheus**
 - To install Prometheus on your system, please follow the link below for the Prometheus Setup Guide. :-[ Prometheus Setup  Guide](https://github.com/snaatak-Zero-Downtime-Crew/Documentation/blob/Nikita-SCRUM-104/Common/Software/Prometheus/README.md)

   
- **Access Prometheus in the browser**
``` bash
<server-public-ip>:9090
```
![Screenshot 2025-04-08 161204](https://github.com/user-attachments/assets/6251666e-1d35-4cd9-9494-41170600ce6e)



###  Step 4. **Setup Grafana**
 - To Setup Grafana on your system, please follow the link below for the Grafana Setup Guide. :-[ Grafana Setup  Guide](https://github.com/snaatak-Zero-Downtime-Crew/Documentation/blob/Nikita-SCRUM-104/Common/Software/Grafana/README.md)

   
- **Access in browser**
``` bash
<instance_ip>:3000
```

![Screenshot 2025-04-08 161153](https://github.com/user-attachments/assets/847b6c82-840e-4b4f-bece-59d73de8de64)



## Step 7. Configuring Metrics of Applications in Prometheus Configuration

- Lets update our configuration file using below command:

``` bash
sudo nano /etc/prometheus/prometheus.yml

```
```
  - job_name: "employee-app"
    metrics_path: '/metrics'
    static_configs:
      - targets: ["13.60.97.49:8080"]

```

- Save the file and restart the Prometheus service:
```
sudo systemctl restart prometheus.service
```

- Now go to Prometheus dashboard and click on status, select target, you can see our metrics are up and running.


![Screenshot 2025-04-09 141835](https://github.com/user-attachments/assets/707e40dc-63d3-4456-a463-4abf9c052b44)




___
## Step 8. Setting up Grafana Dashboards 

- go to the Connections and select the Data sources option.

![image](https://github.com/user-attachments/assets/478be982-e4c8-4f94-a88d-54726baa63ae)

___

- Search for Prometheus in the search bar and select it.

![image](https://github.com/user-attachments/assets/00531fd9-f75e-4617-9db6-4dccefe18b7c)

___

- In connection, in Prometheus server URL, give the server url on which our prometheus is running.


![image](https://github.com/user-attachments/assets/d321d584-0774-4893-9d18-04556296f6b7)

___

- After this click on save and test button. You will see the message for prometheus being successfully queried.


![image](https://github.com/user-attachments/assets/a94f3bac-9563-4025-9f5b-2d92db789d8d)

___

- Here you can start your own new dashboard by adding a visualization.


  So click on + Add visualization option button.

  Here you can import dashboard.

![image](https://github.com/user-attachments/assets/3fe72a54-0383-43a3-9341-993d6946ae6e)

___


![image](https://github.com/user-attachments/assets/21b35188-1331-4ede-a719-300741748d38)

___

####  **Prometheus Dashboards status target**
![image](https://github.com/user-attachments/assets/042fa96b-2a5e-4066-959d-4e75e69fb548)


## Best practices

- **Select Relevant Metrics:** Choose key metrics to monitor (e.g., latency, memory usage).
- **Simulate Scenarios:** Test with various workloads to evaluate performance.
- **Optimize Dashboards:** Create clear and actionable dashboards.
- **Document Findings:** Record issues and resolutions for future reference.

## Conclusion
In conclusion, implementing a monitoring solution for Redis databases using Prometheus and Grafana is essential for maintaining the health, performance, and reliability of your Redis environment. By following the steps outlined in this guide, you can effectively set up and configure Prometheus to collect metrics from Redis instances, visualize these metrics in Grafana dashboards, and proactively manage your Redis infrastructure.

___

### **Contact Information**

| **Name** | **Email address**            |
|----------|-------------------------------|
| Prashant Sharma | prashant.sharma@mygurukulam.co | 


## Reference Links

| **Links**                                           | 
|-----------------------------------------------------|
| [Redis monitoring guide](https://www.site24x7.com/learn/redis-monitoring-metrics.html)  | 
| [What are Hit and Miss Ratios?](https://wp-rocket.me/blog/calculate-hit-and-miss-ratios/) | 
|[Middleware Monitoring Documentation](https://github.com/snaatak-Zero-Downtime-Crew/Documentation/blob/Sheetal-SCRUM-411/Design%20Monitoring/Middleware/Key%20Performance%20Metrices/README.md)|

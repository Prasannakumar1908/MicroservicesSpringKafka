# Microservices using spring, kafa, axon, eureka server
This repository contains event-driven microservices using kafka, axon, eureka server with saga design pattern.

kafka zookeeper and broker starting commands using bash
```bash
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```
```bash
kafka-server-start /usr/local/etc/kafka/server.properties
```
```
helm install kafka bitnami/kafka \
--set replicaCount=1 \
--set externalAccess.enabled=true \
--set externalAccess.autoDiscovery.enabled=true \
--set rbac.create=true \
--set controller.automountServiceAccountToken=true \
--set broker.automountServiceAccountToken=true
```
```bash
helm uninstall kafka
```


# Search Service

Так как приложение состоит из двух сервисов, запускать их необходимо в порядке
1. Analytic Service
2. Search Service

## Описание
Это демонстрационное приложение написанное на Spring Boot, предназначено для поиска адресных данных с использованием Elasticsearch, хранения этих данных в PostgreSQL, и выгрузки статистики по запросам через Kafka в аналитический сервис. Приложение также включает функциональность для парсинга CSV файлов и загрузки данных в Elasticsearch.

## Архитектура

1. Spring Boot является основой приложения, позволяя использовать все преимущества Spring.
2. Spring Security JWT кастомная реализация безопасности на JWT токенах.
3. PostgreSQL используется для хранения данных о пользователях, адресах и запросах.
4. Kafka используется для передачи статистики по использованию запросов в аналитический сервис.
5. Elasticsearch используется для быстрого поиска и индексации адресных данных.

## Стек:

1. JDK 23
2. Spring Boot
3. Spring Security
4. Spring Data
5. Spring Web
6. Docker (для запуска зависимостей)
7. PostgreSQL
8. Elasticsearch
9. Kafka

## Установка

1. ```git clone https://github.com/MilkevichEgor/search-metrics-search-service.git```
2. ```cd search-metrics-search-service```
3. ```docker compose up -d```
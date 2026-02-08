# ЭТАП 1: Сборка (Build)
# Используем Maven с поддержкой самой свежей Java
FROM maven:3.9.12-eclipse-temurin-25-alpine AS build

# Указываем рабочую папку для сборки
WORKDIR /app

# Сначала копируем только файл настроек зависимостей (для кэширования)
COPY pom.xml .
RUN mvn dependency:go-offline

# Теперь копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests

# 1. Берем базовый образ с Java
FROM eclipse-temurin:25-jre-alpine
# 2. Указываем рабочую директорию
WORKDIR /app
# Копируем готовый jar-файл из первого этапа (build)
COPY --from=build /app/target/*.jar app.jar
# 4. Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]


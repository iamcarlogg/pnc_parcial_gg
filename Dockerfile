# Dockerfile
# Etapa 1: build usando un Maven con JDK-21 oficial :contentReference[oaicite:0]{index=0}
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1) Copiamos solo el pom para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2) Copiamos el código fuente y compilamos sin tests
COPY src ./src
RUN mvn package -DskipTests -B

# Etapa 2: runtime ligero con JRE-21 :contentReference[oaicite:1]{index=1}
FROM eclipse-temurin:21-jre
WORKDIR /app

# Publique el puerto de Spring Boot
EXPOSE 8080

# Copiamos el JAR compilado desde la etapa de build
COPY --from=build /app/target/parcial-final-n-capas-0.0.1-SNAPSHOT.jar ./app.jar

# Arrancamos la aplicación
ENTRYPOINT ["java","-jar","app.jar"]

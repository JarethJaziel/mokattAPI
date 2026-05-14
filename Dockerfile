# Paso 1: Construcción con Maven y Java 25
FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecución con Java 25 (Imagen ligera)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
# Especificamos "mokatta-api-*" para que solo tome el empaquetado principal
COPY --from=build /app/target/mokatta-api-*.jar app.jar

# Configuración de memoria para el plan gratuito de Render (512MB)
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
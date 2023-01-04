FROM amazoncorretto:18
RUN mkdir /app
WORKDIR /app
ADD target/order-server-1.0-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java", "-jar", "order-server-1.0-SNAPSHOT.jar"]
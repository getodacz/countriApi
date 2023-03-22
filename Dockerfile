# Preparation steps
FROM bellsoft/liberica-openjdk-alpine:17 AS BUILD_IMAGE
ENV APP_HOME=/root/dev/country-api-app/
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
# Copy all the files
COPY ./build.gradle ./gradlew ./gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle
COPY ./src $APP_HOME/src/
RUN chmod +x ./gradlew
# Build desirable JAR
RUN ./gradlew clean build -x test

FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /root/
COPY --from=BUILD_IMAGE '/root/dev/country-api-app/build/libs/country-api-app-0.0.1-SNAPSHOT.jar' '/app/country-api-app.jar'
COPY src/main/resources/keystore.p12 /keystore.p12
EXPOSE 8443
ENV SERVER_PORT=8443
ENV SERVER_SSL_ENABLED=true
ENV SERVER_SSL_KEY_STORE=/keystore.p12
ENV SERVER_SSL_KEY_STORE_PASSWORD=Api3543534!
ENV SERVER_SSL_KEY_STORE_TYPE=PKCS12
ENV SERVER_SSL_KEY_ALIAS=countryApi
CMD ["java","-jar","/app/country-api-app.jar"]
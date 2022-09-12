FROM gradle:4.10.2-jdk8-alpine as builder
USER root
COPY . .
ARG apiVersion
RUN gradle --no-daemon -PapiVersion=${apiVersion} build

FROM gcr.io/distroless/java:8
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
WORKDIR /app
COPY --from=builder /home/gradle/build/deps/external/*.jar /app/
COPY --from=builder /home/gradle/build/deps/fint/*.jar /app/
COPY --from=builder /home/gradle/src/main/resources/wsdl/*.wsdl /app/
COPY --from=builder /home/gradle/build/libs/fint-sikri-arkiv-adapter-*.jar /app/fint-sikri-arkiv-adapter.jar
ENV fint.sikri.wsdl-location=/app/
CMD ["/app/fint-sikri-arkiv-adapter.jar"]

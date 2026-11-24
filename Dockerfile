FROM eclipse-temurin:25 AS runtime-base

ENV JAVA_TOOL_OPTIONS=-XX:+ExitOnOutOfMemoryError

WORKDIR /app
COPY src/main/resources/wsdl/*.wsdl /app/
COPY build/deps/external/*.jar /app/
COPY build/deps/fint/*.jar /app/
COPY build/libs/fint-sikri-arkiv-adapter-*.jar /app/fint-sikri-arkiv-adapter.jar
ENV fint.sikri.wsdl-location=/app/

ENTRYPOINT ["java", "-jar"]
CMD ["/app/fint-sikri-arkiv-adapter.jar"]

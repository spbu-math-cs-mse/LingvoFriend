FROM gradle:8.10.2-jdk17

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :bootJar

ENTRYPOINT ["gradle", ":bootRun", "-Dspring.data.mongodb.uri=mongodb://rootuser:rootpass@mongodb:27017/lfDB?authSource=admin"]

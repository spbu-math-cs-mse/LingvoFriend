FROM gradle:8.11.0-jdk17

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

ENTRYPOINT ["gradle", "run"]

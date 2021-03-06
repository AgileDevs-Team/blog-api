FROM adoptopenjdk/openjdk8-openj9

RUN mkdir /opt/shareclasses
RUN mkdir /opt/app

ADD target/*.jar /opt/app.jar

EXPOSE 8081

CMD ["java", "-Xmx512m", "-XX:+IdleTuningGcOnIdle", "-Xtune:virtualized", "-Xscmx512m", "-Xscmaxaot100m", "-Xshareclasses:cacheDir=/opt/shareclasses", "-jar", "/opt/app.jar"]


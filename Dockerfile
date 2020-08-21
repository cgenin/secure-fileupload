FROM node:10.22.0-alpine3.9 AS buildfront
WORKDIR /usr/fileupload/wc
COPY wc .
# use changes to package.json to force Docker not to use the cache
# when we change our application's nodejs dependencies:
COPY wc/package.json /tmp/package.json
RUN cd /tmp && npm install
RUN mkdir -p /usr/fileupload/wc && cp -a /tmp/node_modules /usr/fileupload/wc/
RUN cd /usr/fileupload/wc
RUN npm run build-wc

FROM gradle:6.6-jdk8 AS build
COPY --chown=gradle:gradle build.gradle /usr/fileupload/build.gradle
COPY --chown=gradle:gradle settings.gradle /usr/fileupload/settings.gradle
COPY --chown=gradle:gradle src /usr/fileupload/src
COPY --chown=gradle:gradle --from=buildfront /usr/fileupload/src/main/resources/webroot/wc /usr/fileupload/src/main/resources/webroot/wc
WORKDIR /usr/fileupload
# RUN ls src/main/resources/webroot/wc
RUN gradle build --no-daemon

FROM java:8-jdk-alpine
WORKDIR /usr/app
RUN mkdir /upload
COPY --from=build /usr/fileupload/build/libs/secure-fileupload-*fat.jar .
ENTRYPOINT ["java", "-jar", "/usr/app/secure-fileupload-1.0.0-SNAPSHOT-fat.jar"]



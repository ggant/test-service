# Start with OL runtime.
# tag::from[]
FROM openjdk:11
# end::from[]

# tag::user-root[]
#USER root
# end::user-root[]

# tag::copy[]
COPY cacerts /usr/local/openjdk-11/lib/security/cacerts
COPY target/test-service-1.0.0-SNAPSHOT.jar /home/test-service-1.0.0-SNAPSHOT.jar
WORKDIR /home
CMD ["java","-jar","/home/test-service-1.0.0-SNAPSHOT.jar"]


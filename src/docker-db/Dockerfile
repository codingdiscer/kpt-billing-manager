# Based on the PostgreSQL Docker Community image: https://hub.docker.com/_/postgres/

FROM postgres:9.6.2-alpine

MAINTAINER Kinesphere PT, engineering@kinespherept.com

# Used by POSTGRES base image to set default credentials and database
ENV POSTGRES_USER=postgres POSTGRES_PASSWORD=postgres

# Initialize the test database. *.sh and *.sql in this directory will be executed upon container creation.
ADD init-db.sh /docker-entrypoint-initdb.d/init-db.sh

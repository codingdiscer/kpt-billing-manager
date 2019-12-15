# Kafka Audit DB

Drone Status: [![Build Status](https://drone5.target.com/api/badges/data-movement/kafka-operations-db-docker/status.svg)](https://drone5.target.com/data-movement/kafka-operations-db-docker)

This creates an ephemeral container for simulating the Kafka Operations database for development purposes.


## Database instance details

Upon spinning up the image, a single schema and table are created.  The details of the table can be found in the [init script](init-db.sh).

Here's some more relevant info about the postgres instance:

| Aspect | Detail |
| --- | --- |
| Database / Schema name | postgres |
| Username | postgres |
| Password | postgres |


## Running the image locally

Build the image locally:

```
$ docker build -t local/kpt-billing-manager-db . 
```


Run the image locally:

```
$ docker run -p 5432:5432 --name kpt-billing-manager-db -itd local/kpt-billing-manager-db 
```


Find the IP of the local docker machine:

```
$ docker-machine ip
```


Build and run the image:

```
$  docker build -t local/kpt-billing-manager-db . && docker run -p 5432:5432 --name kpt-billing-manager-db -itd local/kpt-billing-manager-db
```


```
$ docker ps -a
```

Connect to it via command line with:

```
docker exec -it kpt-billing-manager-db psql -h localhost -U postgres
```



Build the image with test data:

```
$ docker build -f Dockerfile-testdata -t local/kpt-billing-manager-db:testdata .
```

Run the image with test data:
```
$ docker run -p 5432:5432 --name kpt-billing-manager-db -itd local/kpt-billing-manager-db:testdata
```

Build and run the image with test data:

```
$ docker build -f Dockerfile-testdata -t local/kpt-billing-manager-db:testdata . && docker run -p 5432:5432 --name kpt-billing-manager-db -itd local/kpt-billing-manager-db:testdata
```


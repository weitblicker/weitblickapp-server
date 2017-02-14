## Weitblick-Server

This set of scripts and files is used to set up and build a dedicated webserver hosting the weitblick-server website. The built is done using
[Maven] (https://maven.apache.org/).

## Prerequesites

In order to build the website and host [Maven] (https://maven.apache.org/) is required. The instructions are contained in an `options.properties` located
in the directory of the project. Further, a running `MySQL` server is required for this project.

## Building the webserver

### Edit the `options.properties`

An example is given below. Basically, all you need is to modify paths and ports.

```
weitblickapp-server.BASE_URI=https://weitblick-server:443/
weitblickapp-server.DB.url.jpa-weitblick=jdbc:mysql://localhost:3306/weitblick
weitblickapp-server.DB.user=weitblick
weitblickapp-server.DB.password=weitblick
weitblickapp-server.DB.ddl-generation=create-or-extend-tables
weitblickapp-server.DEFAULT_LANGUAGE=en
weitblickapp-server.keystore.location=/keystore/location
weitblickapp-server.keystore.password=password
weitblickapp-server.use_ssl=true

```

### Compiling and running

In the parent directory type

```
mvn package

```
After compilation, start the MySQL server and check whether the right ports are specified in the `options.properties` (in the example above it is port 3306). Create a database called 'weitblick' on the MySQL server. Do not create any tables within. This is taken care of by the application after it is launched as described below. Now, again in the parent directory of the project, run

```
java -jar target/weitblickapp-server-0.8-jar-with-dependencies.jar
```
Note, that your .jar might be named slightly different. If you start the aaplication the first time you have to create the initial user. In order to work, the email address must contain an '@'.




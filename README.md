## Weitblick-Server

This set of scripts and files is used to set up and build a dedicated webserver hosting the weitblick-server website. The built is done using
[Maven] (https://maven.apache.org/).

## Prerequesites

In order to build the website and host [Maven] (https://maven.apache.org/) is required. The instructions are contained in an `options.properties` located
in the directory of the project. Further, a running `MySQL` server is required.

## Building the webserver

### Edit the `options.properties`. An example is given below. Basically, all you need is to modify paths and ports.

```
weitblickapp-server.BASE_URI=https://weitblick-server:443/
weitblickapp-server.DB.url.jpa-weitblick=jdbc:mysql://localhost/weitblick
weitblickapp-server.DB.user=weitblick
weitblickapp-server.DB.password=weitblick
weitblickapp-server.DB.ddl-generation=create-or-extend-tables
weitblickapp-server.DEFAULT_LANGUAGE=en
weitblickapp-server.keystore.location=/keystore/location
weitblickapp-server.keystore.password=password
weitblickapp-server.use_ssl=true

```



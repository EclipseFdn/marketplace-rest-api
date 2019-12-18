# Eclipse Foundation Marketplace Client API

## Summary

Proof of concept project within the Microservice initiative, the Foundation looks to leverage Quarkus to renew their MPC API. The specifications to the API can be found in the `marketplace-rest-api-specs` project within the EclipseFdn group on GitHub.

## Requirements

1. Installed and configured JDK 1.8+
1. Apache Maven 3.5.3+
1. Running instance of MongoDB (Docker instructions below)
1. GraalVM (for compilation of native-image)

### Optional requirements

1. Node JS + NPM (if using sample data script)

## Configuration

This section will outline configuration values that need to be checked and updated to run the API in a local environment. Unless otherwise stated, all values to be updated will be in `./src/main/resources/application.properties`.

1. In order to properly detect MongoDB, a connection string needs to be updated. `quarkus.mongodb.connection-string` designates the location of MongoDB to quarkus in the form of `mongodb://<host>:<port>/`. By default, this value points at `mongodb://localhost:27017`, the default location for local installs of MongoDB.
1. Update `quarkus.mongodb.credentials.username` to be a known user with write permissions to MongoDB instance.
1. Create a copy of `./config/sample.secret.properties` named `secret.properties` in a location of your choosing on the system, with the config folder in the project root being default configured. If changed, keep this path as it is needed to start the environment later.
1. Update `quarkus.mongodb.credentials.password` to be the password for the MongoDB user in the newly created `secret.properties` file.
1. By default, this application binds to port 8090. If port 8090 is occupied by another service, the value of `quarkus.http.port` can be modified to designate a different port. 
1. In order to protect endpoints for write operations, an introspection endpoint has been configured to validate OAuth tokens. This introspection endpoint should match the requirements set out by the OAuth group for such endpoints. The URL should be set in `quarkus.oauth2.introspection-url`.
1. As part of the set up of this client, an OAuth client ID and secret need to be defined in the `secret.properties` file. These values should be set in `quarkus.oauth2.client-id` and `quarkus.oauth2.client-secret`. These are required for introspection to avoid token fishing attempts.

If you are compiling from source, in order to properly pass tests in packaging, some additional set up sill need to be done. There are two options for setting up test variables for the project.

1. Option 1 - Combined file
    - This method is useful when working in development environments, as the compilation and running of the application using the development build command uses the same configuration file. Thanks to scoping native to Quarkus using profiles, there is no risk of overlap between the 2 profiles.
    - To ensure that the tests pass, copy the contents of the `config/test.secret.properties` file into the development copy of the secret properties.

1. Option 2 - Separate files
    - This method is suitable for production environments where test data should be kept separate from real world settings. This can be used for the following build instructions:
        - Build and run test  
        - Build native  
        - Build native & docker image  
    - Create a copy of `config/test.secret.properties` somewhere on the file system, with the config folder in the project root being default configured. If changed, keep this path as it is needed for compilations of the product.

For users looking to build native images and docker files, an install of GraalVM is required to compile the image. Please retrieve the version **19.2.0** from the [GraalVM release page](https://github.com/oracle/graal/releases) for your given environment. Once installed, please ensure your `GRAAL_HOME`, `GRAALVM_HOME` are set to the installed directory, and the GraalVM `/bin` folder has been added to your `PATH`. Run `sudo gu install native-image` to retrieve imaging functionality from GitHub for GraalVM on Linux and MacOS based environments. 


## Build

* Development 

    $ mvn compile quarkus:dev -Dconfig.secret.path=<full path to secret file>
   
* Build and run test

    $ mvn clean package -Dconfig.secret.path=<full path to test secret file>
    
* Build native 

    $ mvn package -Pnative -Dconfig.secret.path=<full path to test secret file>
    
* Build native & docker image

```
    $ mvn package -Pnative -Dnative-image.docker-build=true -Dconfig.secret.path=<full path to test secret file>  
    docker build -f src/main/docker/Dockerfile.native -t eclipse/mpc . --build-arg SECRET_LOCATION=/var/secret --build-arg LOCAL_SECRETS=config/secret.properties  
    docker run -i --rm -p 8080:8090 eclipse/mpc  
```

See https://quarkus.io for more information.  

The property ` -Dconfig.secret.path` is added to each line as the location needs to be fed in at runtime where to find the secret properties data. By default, Quarkus includes surefire as part of its native imagine build plug-in, which needs the given path in order for the given packages to pass.

The Docker build-arg `LOCAL_SECRETS` can be configured on the `docker build` command if the secrets file exists outside of the standard location of `config/secret.properties`. It has been set to the default value in the sample command for example purposes on usage.

The Docker build-arg `GRAALVM_HOME` must be configured on the `docker build` command to properly import SSL certificate information into the native image. Without this, all calls to authenticate users will fail.

## Sample data

For ease of use, a script has been created to load sample data into a MongoDB instance using Node JS and a running instance of the API. This script will load a large amount of listings into the running MongoDB using the API for use in testing different queries without having to retrieve real world data.

1. In root of project, run `npm install` to retrieve dependencies for sample data loader script.
1. Run `npm run-script load-listings -- -s <api-url>`, replacing `<api-url>` with the URL of a running instance of this API (e.g. http://localhost:8090). This should take a couple of moments, as by default the loader will load 5000 dummy entries into MongoDB. This can be changed using a `-c` flag followed by the number of entries to be created. 

## Dockerizing MongoDB

The following command can be used in any environment with Docker fully configured to run MongoDB with some basic settings. The password and username can be configured to be more secure, so long as the application.properties and secret.properties are updated to reflect the changes (`quarkus.mongodb.credentials.username` and `quarkus.mongodb.credentials.password` respectively).  

```
docker run -p 127.0.0.1:27017:27017/tcp \
    --env MONGO_INITDB_ROOT_USERNAME=root \
    --env MONGO_INITDB_ROOT_PASSWORD=example \
    mongo
```

### Additional MongoDB commands needed:

```
use mpc;
db.listings.createIndex(
     {
       body:"text", 
       teaser:"text",
       title:"text"
     },
     {
      weights: {
       title: 10,
       teaser: 3
     },
     name: "TextIndex"
  });
db.listings.createIndex({ listing_id: 1 }, {name: "lid"});
db.listings.createIndex({ changed: 1 }, {name: "updated"});
db.listings.createIndex({ created: 1 }, {name: "created"});
db.listings.createIndex({ license_type: 1 }, {name: "lic_type"});
db.listings.createIndex({ category_ids: 1 }, {name: "cats"});
db.listings.createIndex({ market_ids: 1 }, {name: "mkts"});
db.listing_versions.createIndex({ listing_id: 1 }, {name: "lid"});
db.listing_versions.createIndex({ compatible_versions: 1 }, {name: "compat"});
db.listing_versions.createIndex({ min_java_version: 1 }, {name: "min_java"});
db.listing_versions.createIndex({ platforms: 1 }, {name: "platforms"});
db.installs.createIndex({listing_id: 1 }, {name: "lid"});
```

### Creating a backup

In order to create a backup of an existing data set, read access to the MongoDB instance is required, and is best done with shell access.  

To use the following commands, replace `<user>` with the name of the user with write access to the MPC data set. Each of these commands will initiate a password challenge request, and cannot be together in a script easily. Adding the `--password=<>` is not recommended as it is a vulnerability as the logs will remain on the server with plain text passwords. Additionally, the `<date>` placeholder should be replaced with whatever date stamp has been set in the snapshot .gz files.  

In container:  

```
mkdir snapshot
mongodump --username=<user> --archive=snapshot/listings_<date>.gz --db=mpc --collection=listings --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --gzip
mongodump --username=<user> --archive=snapshot/markets_<date>.gz --db=mpc --collection=markets --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --gzip
mongodump --username=<user> --archive=snapshot/categories_<date>.gz --db=mpc --collection=categories --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --gzip
mongodump --username=<user> --archive=snapshot/catalogs_<date>.gz --db=mpc --collection=catalogs --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --gzip
mongodump --username=<user> --archive=snapshot/listing_versions_<date>.gz --db=mpc --collection=listing_versions --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --gzip
```

If using Docker to host:
`docker cp dev_mongo_1:/snapshot ./snapshot`

### Restoring from backup

In order to restore data from backup snapshot, write access to the MongoDB instance is required, and is best done with shell access. As `mongorestore` doesn't update or overwrite records, the existing data set should be wiped before proceeding with restoring from backup snapshots. 

To use the following commands, replace `<user>` with the name of the user with write access to the MPC data set. Each of these commands will initiate a password challenge request, and cannot be together in a script easily. Adding the `--password=<>` is not recommended as it is a vulnerability as the logs will remain on the server with plain text passwords. Additionally, the `<date>` placeholder should be replaced with whatever date stamp has been set in the snapshot .gz files.

From host machine to Docker:
`docker cp ./snapshot dev_mongo_1:/`

In container:  

```
mongorestore --username=<user> --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --nsInclude=mpc.* --gzip --archive=snapshot/listings_<date>.gz
mongorestore --username=<user> --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --nsInclude=mpc.* --gzip --archive=snapshot/markets_<date>.gz
mongorestore --username=<user> --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --nsInclude=mpc.* --gzip --archive=snapshot/catalogs_<date>.gz
mongorestore --username=<user> --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --nsInclude=mpc.* --gzip --archive=snapshot/categories_<date>.gz
mongorestore --username=<user> --authenticationDatabase admin --authenticationMechanism SCRAM-SHA-256 --nsInclude=mpc.* --gzip --archive=snapshot/listing_versions_<date>.gz
```


## Copyright 

Copyright (c) 2019 Eclipse Foundation and others.
This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-v20.html,

SPDX-License-Identifier: EPL-2.0

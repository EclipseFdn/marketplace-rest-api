# Eclipse Foundation Marketplace Client API

## Summary

Proof of concept project within the Microservice initiative, the Foundation looks to leverage Quarkus to renew their MPC API. The specifications to the API can be found in the `marketplace-rest-api-specs` project within the EclipseFdn group on GitHub.

## Requirements

1. Installed and configured JDK 1.8+
1. Apache Maven 3.5.3+
1. Running instance of MongoDB

### Optional requirements

1. Node JS + NPM (if using sample data script)

## Configuration

This section will outline configuration values that need to be checked and updated to run the API in a local environment. Unless otherwise stated, all values to be updated will be in `./src/main/resources/application.properties`.

1. In order to properly detect MongoDB, a connection string needs to be updated. `quarkus.mongodb.connection-string` designates the location of MongoDB to quarkus in the form of `mongodb://<host>:<port>/`. By default, this value points at `mongodb://localhost:27017`, the default location for local installs of MongoDB.
1. Update `quarkus.mongodb.credentials.username` to be a known user with write permissions to MongoDB instance.
1. Create a copy of `./src/main/resources/secret.sample.properties` named `secret.properties` in the same folder
1. Update `quarkus.mongodb.credentials.password` to be the password for the MongoDB user in the newly created `secret.properties` file.
1. By default, this application binds to port 8090. If port 8090 is occupied by another service, the value of `quarkus.http.port` can be modified to designate a different port.

## Build

* Development 

    $ mvn compile quarkus:dev
   
* Build and run test

    $ mvn clean package
    
* Build native 

    $ mvn package -Pnative
    
* Build native & docker image

    $ mvn package -Pnative -Dnative-image.docker-build=true
    
See https://quarkus.io for more information.  

## Sample data

For ease of use, a script has been created to load sample data into a MongoDB instance using Node JS and a running instance of the API. This script will load a large amount of listings into the running MongoDB using the API for use in testing different queries without having to retrieve real world data.

1. In root of project, run `npm install` to retrieve dependencies for sample data loader script.
1. Run `npm run-script load-listings -- -s <api-url>`, replacing `<api-url>` with the URL of a running instance of this API (e.g. http://localhost:8090). This should take a couple of moments, as by default the loader will load 5000 dummy entries into MongoDB. This can be changed using a `-c` flag followed by the number of entries to be created. 

### Additional MongoDB commands needed:

- db.listings.createIndex({body:"text", teaser:"text",title:"text"})

## Copyright 

Copyright (c) 2019 Eclipse Foundation and others.
This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-v20.html,

SPDX-License-Identifier: EPL-2.0

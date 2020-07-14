# Eclipse Foundation Marketplace Client API

## Summary

Proof of concept project within the Microservice initiative, the Foundation looks to leverage Quarkus to renew their MPC API. The specifications to the API can be found in the `marketplace-rest-api-specs` project within the EclipseFdn group on GitHub.

## Requirements

1. Installed and configured JDK 1.8+
1. Apache Maven 3.5.3+
1. Running instance of MariaDB (Docker instructions below)
1. Running instance of Solr server (version 5.5.5 currently supported)
1. GraalVM (for compilation of native-image)

### Optional requirements

1. Node JS + NPM (if using sample data script)
1. OAuth 2.0 server

## Configuration

This section will outline configuration values that need to be checked and updated to run the API in a local environment. Unless otherwise stated, all values to be updated will be in `./src/main/resources/application.properties`.

1. In order to properly detect MariaDB, a connection string needs to be updated. `quarkus.datasource.url` designates the location of MariaDB to quarkus in the form of `jdbc:mariadb://<host>:<port>/<db>`. By default, this value points at `jdbc:mariadb://localhost:3306/mpc_db`, the default location for local installs of MariaDB with a database of `mpc_db`.
1. Update `quarkus.datasource.username` to be a known user with write permissions to MariaDB instance.
1. Create a copy of `./config/sample.secret.properties` named `secret.properties` in a location of your choosing on the system, with the config folder in the project root being default configured. If changed, keep this path as it is needed to start the environment later.
1. Update `quarkus.datasource.password` to be the password for the MariaDB user in the newly created `secret.properties` file.
1. Log in to the MariaDB instance and ensure that the database defined in the JDBC string exists. By default, the name of the database is `mpc_db`. This database can be created using the command `CREATE DATABASE mpc_db;`. 
1. When using the Solr search engine, a couple of properties are needed to be added to the properties and secret.properties file. The first is the Solr host and core. The host property (`eclipse.solr.host`) should be the root URL to your Solr instance (e.g. http://localhost:8093/solr) to allow connections for search indexing. The core property (`eclipse.solr.core`) should be the name of the core that will store your indexes for marketplace. If a core does not exist yet, create one through the admin panel of the Solr server and update the core value if needed.
1. To properly enable the core to work with this application, the configuration on the Solr server should be updated. Copy the contents of ./config/mpc_dev into your cores configuration folder. An example path for this folder is `/opt/solr/server/solr/marketplace`. This may change based on how the server is installed and configured. Ensure that these files match ownership of the other files in this location, otherwise the Solr core may not work as intended. 
1. By default, this application binds to port 8090. If port 8090 is occupied by another service, the value of `quarkus.http.port` can be modified to designate a different port. 
1. In order to protect endpoints for write operations, an introspection endpoint has been configured to validate OAuth tokens. This introspection endpoint should match the requirements set out by the OAuth group for such endpoints. The URL should be set in `quarkus.oauth2.introspection-url`.  
    * A property meant for development purposes has been added to this stack to bypass OAuth calls. If set, all calls will return as if authenticated as an admin. The property and value `eclipse.oauth.override=true` can be set in the `application.properties` file to enable this feature.
1. As part of the set up of this client, an OAuth client ID and secret should be defined in the `secret.properties` file. These values should be set in `quarkus.oauth2.client-id` and `quarkus.oauth2.client-secret`. These are required for introspection to avoid token fishing attempts.

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

For ease of use, a script has been created to load sample data into the database instance using Node JS and a running instance of the API. This script will load a large amount of listings into the running database using the API for use in testing different queries without having to retrieve real world data.

1. In root of project, run `npm install` to retrieve dependencies for sample data loader script.
1. Run `npm run-script load-listings -- -s <api-url>`, replacing `<api-url>` with the URL of a running instance of this API (e.g. http://localhost:8090). This should take a couple of moments, as by default the loader will load dummy entries into the dataset. Options for this script can be seen using the help command.

## Dockerizing MariaDB

For a simple image, using the command `docker run --name mpc-api_mariadb_1 -e MYSQL_ROOT_PASSWORD=my-secret-pw -p 3306:3306 -d mariadb:latest` will create a Docker container for mariadb. For the current use case, no other applications need to run in sequence for development and a docker file can be skipped. The password and port can be changed to suit needs, so long as it is reflected within the configuration for the JDBC connection string. For more configurable settings, please refer to the [Docker Hub page for the image](https://hub.docker.com/_/mariadb).

### Additional MariaDB commands needed:

TODO

### Creating a backup

Data for installs has been separated from the raw table data as keeping it separate is better for backing up. As a separate entity with no direct relationship to any other table, it doesn't need to be locked to prevent data inconsistencies/mismatches. Since Metric Period and Install Metrics are transient tables that are populated by procedures, they don't need to be backed up and have been ignored from the dump. Below is a set of sample commands to backup the data:

```
mysqldump --user=root --password --lock-tables mpc_db --ignore-table=mpc_db.Install --ignore-table=mpc_db.MetricPeriod --ignore-table=mpc_db.InstallMetrics --ignore-table=mpc_db.InstallMetrics_MetricPeriod > /data/backup/db.sql
mysqldump --user=root --password mpc_db Install > /data/backup/db_installs.sql
```

### Restoring from backup

TODO

```
mysql --user=root --password mpc_db < /data/backup/db.sql
mysql --user=root --password mpc_db Install < /data/backup/db_installs.sql
```

## Copyright 

Copyright (c) 2019 Eclipse Foundation and others.
This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-v20.html,

SPDX-License-Identifier: EPL-2.0

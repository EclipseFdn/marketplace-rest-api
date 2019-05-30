# Eclipse Foundation Marketplace Client API

## Summary

Proof of concept project within the Microservice initiative, the Foundation looks to leverage Quarkus to renew their MPC API. The specifications to the API can be found in the `marketplace-rest-api-specs` project within the EclipseFdn group on GitHub.

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

## Copyright 

Copyright (c) 2019 Eclipse Foundation and others.
This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-v20.html,

SPDX-License-Identifier: EPL-2.0

[![banner](https://raw.githubusercontent.com/oceanprotocol/art/master/github/repo-banner%402x.png)](https://oceanprotocol.com)

# Squid-java
🦑 Java client library for Ocean Protocol

> 🐳 Ocean client Library (Java)
> [oceanprotocol.com](https://oceanprotocol.com)

[![Travis (.com)](https://img.shields.io/travis/com/oceanprotocol/squid-java.svg)](https://travis-ci.com/oceanprotocol/squid-java)

---

## Table of Contents

   * [Squid-java](#squid-java)
      * [Table of Contents](#table-of-contents)
      * [Features](#features)
      * [Next methods to integrate](#next-methods-to-integrate)
        * [Installing the library](#installing-the-library)
      * [How to run the integration tests](#how-to-run-the-integration-tests)
      * [New Version](#new-version)
      * [License](#license)


---

## Features

This library enables to integrate the Ocean Protocol capabilities from JVM clients.

The list of methods implemented are:

**Ocean**

* resolveDID
* registerAsset

**Asset**

* searchAssets
* getId
* getDDO
* getDID
* publishMetadata
* getMetadata
* updateMetadata

**Accounts**

* getId
* getAccounts
* getAccountBalance
* getEthAccountBalance
* getOceanAccountBalance
* requestTokens

**Secret Store**

* encryptDocument
* decryptDocument


## Next methods to integrate

**Ocean**


* getOrder
* getAsset
* getServiceAgreement
* getOrdersByAccount (Nice to Have)
* searchOrders (Nice to Have)

**Asset**

* purchase
* retireMetadata (Nice to Have)
* getServiceAgreements (Nice to Have)

**ServiceAgreement**

* getId
* getPrice
* getStatus
* publish
* retire
* getAccess

**Order**

* getId
* getStatus
* commit
* pay
* verifyPayment
* consume


A complete description of the Squid API can be found on the [Squid documentation page](https://github.com/oceanprotocol/dev-ocean/blob/master/doc/development/squid.md)


### Installing the library

Typically in Maven you could add the dependency:

```xml
<dependency>
  <groupId>com.oceanprotocol</groupId>
  <artifactId>squid</artifactId>
  <version>0.3.0</version>
</dependency>
```


## How to run the tests

### Unit Tests

You can execute the unit tests using the following command:

```bash
mvn clean test
```

### Integration Tests

The execution of the integration tests require to have running the complete Ocean stack using [Ocean Barge](https://github.com/oceanprotocol/barge).

After having `barge` in your environment, you can run the components needed running:

```bash
./start_ocean.sh --latest --local-spree-node --no-pleuston
```

You can execute the integration tests using the following command:

```bash
mvn clean verify -P integration-test
```

### Documentation

You can generate the Javadoc using the following command:

```bash
mvn javadoc:javadoc
```

### All the tests

You can run the unit and integration tests running:

```bash
mvn clean verify -P all-test
```

### Code Coverage

The code coverage reports are generated using the JaCoCo Maven plugin. Reports are generated in the `target/site` folder.


## New Version

The `bumpversion.sh` script helps to bump the project version. You can execute the script using as first argument {major|minor|patch} to bump accordingly the version.


## License

```
Copyright 2018 Ocean Protocol Foundation Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


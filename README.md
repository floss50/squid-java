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
      * [License](#license)


---

## Features

This library enables to integrate the Ocean Protocol capabilities from JVM clients.

The list of methods implemented are:

**Ocean**

*

**Asset**

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

* searchAssets
* register
* resolveDID
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
  <version>0.0.1</version>
</dependency>
```


## How to run the integration tests

To run the integration tests you need to run Ganache using the following command:

```bash
ganache-cli --account="0x4d5db4107d237df6a3d58ee5f70ae63d73d7658d4026f2eefd2f204c81682cb7,100000000000000000000000000000000000"
```

To run the integration tests using maven:

```bash
mvn clean -Dtest=*IT test
```

Run the Secret Store methods require to connect to a Parity EVM client, so if you are using Ganache it's not going to work.



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


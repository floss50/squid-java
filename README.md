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
      * [Using the API](#using-the-api)
        * [Dealing with Flowables](#dealing-with-flowables)
        * [Installing the library](#installing-the-library)
      * [How to run the integration tests](#how-to-run-the-integration-tests)
      * [New Version](#new-version)
      * [License](#license)


---

## Features

This library enables to integrate the Ocean Protocol capabilities from JVM clients.

The list of methods implemented are:


**Asset**

* create
* resolve
* search
* query
* order
* consume

**Accounts**

* list
* balance
* requestTokens

**Secret Store**

* encrypt
* decrypt


## Using the API

You can configure the library using TypeSafe Config or a Java Properties Object

In case you want to use TypeSafe Config you would need an application.conf file with this shape:

```
keeper.url="http://localhost:8545"
keeper.gasLimit=4712388
keeper.gasPrice=100000000000

aquarius.url="http://localhost:5000"

secretstore.url="http://localhost:12001"

# Contracts addresses
contract.token.address="0xe749e2f8482810b11b838ae8c5eb69e54d610411"
contract.didRegistry.address="0x611f28ef25d778afc5a0034aea94297e2c215a42"
contract.dispenser.address="0x83d35336e2cC9C69F6bD22c6D8412e4Ad59134ec"
contract.serviceExecutionAgreement.address="0xdeAF2aa754287628d5d30Ca99d94a0CAd2AD4CAb"
contract.paymentConditions.address="0xf9e633cbeeb2a474d3fe22261046c99e805beec4"
contract.accessConditions.address="0xfe0145caf0ec55d23dc1b08431b071f6e1123a76"

consume.basePath = "/tmp"

## Main account
account.main.address="0xaabbcc"
account.main.password="pass"
account.main.credentialsFile="/accounts/parity/aabbcc.json.testaccount"
```

And you can instantiate the API with the following lines:

```java
 Config config = ConfigFactory.load();
 OceanAPI oceanAPI = OceanAPI.getInstance(config);
```

Remember that TypeSafe Config allows you to overwrite the values using Environment Variables or arguments passed to the JVM

If you want to use Java's Properties, you just need to create a Properties Object with the same properties of the application.conf.
You can read this Properties from a properties file, or define the values of these properties in your code

```java
    // Default values for KEEPER_URL, KEEPER_GAS_LIMIT, KEEPER_GAS_PRICE, AQUARIUS_URL, SECRETSTORE_URL, CONSUME_BASE_PATH
    Properties properties = new Properties();
    properties.put(OceanConfig.MAIN_ACCOUNT_ADDRESS, "0xaabbcc");
    properties.put(OceanConfig.MAIN_ACCOUNT_PASSWORD,"pass");
    properties.put(OceanConfig.MAIN_ACCOUNT_CREDENTIALS_FILE,"/accounts/parity/aabbcc.json.testaccount");
    properties.put(OceanConfig.DID_REGISTRY_ADDRESS, "0x01daE123504DDf108E0C65a42190516E5c5dfc07");
    properties.put(OceanConfig.SERVICE_EXECUTION_AGREEMENT_ADDRESS, "0x21668cE2116Dbc48AC116F31678CfaaeF911F7aA");
    properties.put(OceanConfig.PAYMENT_CONDITIONS_ADDRESS, "0x38A531cc85A58adCb01D6a249E33c27CE277a2D1");
    properties.put(OceanConfig.ACCESS_CONDITIONS_ADDRESS, "0x605FAF898Fc7c2Aa847Ba0D558b5251c0F128Fd7");
    properties.put(OceanConfig.TOKEN_ADDRESS, "0xe749e2f8482810b11b838ae8c5eb69e54d610411");
    properties.put(OceanConfig.OCEAN_MARKET_ADDRESS, "0xf9e633cbeeb2a474d3fe22261046c99e805beec4");

    OceanAPI oceanAPIFromProperties = OceanAPI.getInstance(properties);
```

Once you have initialized the API you can call the methods through their correspondent API class. For instance:

```java
 Balance balance = oceanAPI.getAccountsAPI().balance(oceanAPI.getMainAccount());

 String filesJson = metadataBase.toJson(metadataBase.base.files);
 String did = DID.builder().getHash();
 String encryptedDocument = oceanAPI.getSecretStoreAPI().encrypt(did, filesJson, 0);

 Flowable<OrderResult> response = oceanAPI.getAssetsAPI().order(did, SERVICE_DEFINITION_ID);
 boolean result = oceanAPI.getAssetsAPI().consume(orderResult.getServiceAgreementId(), did, SERVICE_DEFINITION_ID, "/tmp");
```

Note: Due to [this issue](https://github.com/oceanprotocol/squid-java/issues/67), is necessary to register the Access Service's template using squid-py

### Dealing with Flowables

Squid-java uses web3j to interact with Solidity's Smart Contracts. It relies on [RxJava](https://github.com/ReactiveX/RxJava) to deal with asynchronous calls.
The order method in AssetsAPI returns a Flowable over an OrderResult object. It's your choice if you want to handle this in a synchronous or asynchronous fashion.
If you prefer to deal with this method in a synchronous way, you will need to block the current thread until you get a response:

```java
 Flowable<OrderResult> response = oceanAPI.getAssetsAPI().order(did, SERVICE_DEFINITION_ID);
 OrderResult orderResult = response.blockingFirst();
```
On the contrary, if you want to handle the response asynchronously, you will need to subscribe to the Flowable:

```java
response.subscribe(
     orderResultEvent -> {
         if (orderResultEvent.isAccessGranted())
             System.out.println("Access Granted for Service Agreement " + orderResultEvent.getServiceAgreementId());
         else if (orderResultEvent.isPaymentRefund())
             System.out.println("There was a problem with Service Agreement " + orderResultEvent.getServiceAgreementId() + " .Payment Refund");
     }
 );
```

The subscribe method will launch a new Thread to react to the events of the Flowable.
More information: [RxJava](https://github.com/ReactiveX/RxJava/wiki) , [Flowable](http://reactivex.io/RxJava/2.x/javadoc/)


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


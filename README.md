# squid-java
🦑 Java client library for Ocean Protocol

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
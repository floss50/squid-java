#!/bin/sh -x

DIDREGISTRY_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/DIDRegistry.spree.json")
SERVICEAGREEMENT_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/ServiceExecutionAgreement.spree.json")
PAYMENTCONDITIONS_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/PaymentConditions.spree.json")
ACCESSCONDITIONS_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/AccessConditions.spree.json")
OCEANTOKEN_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/OceanToken.spree.json")
OCEANMARKET_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/OceanMarket.spree.json")

sed -i "s/contract.didRegistry.address=.*/contract.didRegistry.address=\"$DIDREGISTRY_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.serviceExecutionAgreement.address=.*/contract.serviceExecutionAgreement.address=\"$SERVICEAGREEMENT_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.paymentConditions.address=.*/contract.paymentConditions.address=\"$PAYMENTCONDITIONS_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.accessConditions.address=.*/contract.accessConditions.address=\"$ACCESSCONDITIONS_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.token.address=.*/contract.token.address=\"$OCEANTOKEN_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.oceanmarket.address=.*/contract.oceanmarket.address=\"$OCEANMARKET_ADDRESS\"/g" src/test/resources/application.conf


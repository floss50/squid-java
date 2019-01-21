#!/bin/sh -x

DIDREGISTRY_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/DIDRegistry.spree.json")
SERVICEAGREEMENT_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/ServiceAgreement.spree.json")
PAYMENTCONDITIONS_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/PaymentConditions.spree.json")
ACCESSCONDITIONS_ADDRESS=$(jq -r .address "${HOME}/.ocean/keeper-contracts/artifacts/AccessConditions.spree.json")
sed -i "s/contract.didRegistry.address=.*/contract.didRegistry.address=\"$DIDREGISTRY_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.serviceAgreement.address=.*/contract.serviceAgreement.address=\"$SERVICEAGREEMENT_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.paymentConditions.address=.*/contract.paymentConditions.address=\"$PAYMENTCONDITIONS_ADDRESS\"/g" src/test/resources/application.conf
sed -i "s/contract.accessConditions.address=.*/contract.accessConditions.address=\"$ACCESSCONDITIONS_ADDRESS\"/g" src/test/resources/application.conf


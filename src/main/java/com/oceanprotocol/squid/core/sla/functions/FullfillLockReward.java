package com.oceanprotocol.squid.core.sla.functions;

import com.oceanprotocol.keeper.contracts.LockRewardCondition;
import com.oceanprotocol.squid.exceptions.LockRewardFullfillException;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.models.asset.BasicAssetInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public class FullfillLockReward {

    static final Logger log= LogManager.getLogger(FullfillLockReward.class);

    /**
     * Executes a lock payment function for a Service Agreement between publisher and consumer
     * @param lockRewardCondition the address of the LockRewardCondition contract
     * @param serviceAgreementId the service agreement id
     * @param assetInfo basic info of the asset
     * @return a flag that indicates if the function was executed correctly
     * @throws LockRewardFullfillException LockRewardFullfillException
     */
    public static Boolean  executeFullfill(LockRewardCondition lockRewardCondition,
                                              String serviceAgreementId,
                                              String escrowRewardAddress,
                                              BasicAssetInfo assetInfo) throws LockRewardFullfillException {

        byte[] serviceId;
        Integer price = -1;

        try {

            escrowRewardAddress = Keys.toChecksumAddress(escrowRewardAddress);
            serviceId = EncodingHelper.hexStringToBytes(serviceAgreementId);

            TransactionReceipt receipt= lockRewardCondition.fulfill(
                    serviceId,
                    escrowRewardAddress,
                    BigInteger.valueOf(assetInfo.getPrice())
            ).send();

            if (!receipt.getStatus().equals("0x1")) {
                String msg = "The Status received is not valid executing LockRewardCondition.Fullfill: " + receipt.getStatus() + " for serviceAgreement " + serviceAgreementId;
                log.error(msg);
                throw new LockRewardFullfillException(msg);
            }

            log.debug("LockRewardCondition.Fullfill transactionReceipt OK for serviceAgreement " + serviceAgreementId);
            return true;

        } catch (Exception e) {

            String msg = "Error executing LockRewardCondition.Fullfill for serviceAgreement " + serviceAgreementId;
            log.error(msg+ ": " + e.getMessage());
            throw new LockRewardFullfillException(msg, e);
        }

    }
}

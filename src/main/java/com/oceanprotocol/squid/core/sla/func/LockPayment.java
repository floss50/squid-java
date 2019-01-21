package com.oceanprotocol.squid.core.sla.func;

import com.oceanprotocol.keeper.contracts.PaymentConditions;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.asset.BasicAssetInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class LockPayment {

    static final Logger log= LogManager.getLogger(LockPayment.class);


    public static Boolean  executeLockPayment(PaymentConditions paymentConditions,
                                              String serviceAgreementId,
                                              BasicAssetInfo assetInfo) {

        byte[] serviceId;
        byte[] assetId;
        Integer price = -1;

        try {

            serviceId = EncodingHelper.hexStringToBytes(serviceAgreementId);

            TransactionReceipt receipt= paymentConditions.lockPayment(
                    serviceId,
                    assetInfo.getAssetId(),
                    BigInteger.valueOf(assetInfo.getPrice())
            ).send();

            if (!receipt.getStatus().equals("0x1")) {
                log.error("The Status received is not valid executing lockPayment: " + receipt.getStatus());
                return false;
            }

            log.debug("LockPayment transactionReceipt OK");
            return true;

        } catch (Exception e) {
            log.error("Error executing lockPayment " + e.getMessage());
            return false;
        }

    }

}

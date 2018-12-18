package com.oceanprotocol.squid.core.sla.func;

import com.oceanprotocol.keeper.contracts.AccessConditions;
import com.oceanprotocol.keeper.contracts.PaymentConditions;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.asset.BasicAssetInfo;
import com.oceanprotocol.squid.models.service.AccessService;
import com.oceanprotocol.squid.models.service.Condition;
import com.oceanprotocol.squid.models.service.Service;
import io.reactivex.Flowable;
import jnr.constants.platform.Access;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;

public class LockPayment {


    public static Boolean  executeLockPayment(PaymentConditions paymentConditions,
                                              String serviceAgreementId,
                                              DDO ddo,
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

            if (!receipt.getStatus().equals("0x1"))
                return false;
            return true;

        } catch (UnsupportedEncodingException e) {

        }

        finally {
            return false;
        }

    }

}

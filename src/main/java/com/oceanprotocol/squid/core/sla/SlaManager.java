package com.oceanprotocol.squid.core.sla;


import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.UUID;

public class SlaManager {

    static final Logger log= LogManager.getLogger(SlaManager.class);

    public SlaManager() {
    }

    public static String generateSlaId()    {
        String token= UUID.randomUUID().toString() + UUID.randomUUID().toString();
        return token.replaceAll("-", "");
    }

    public void registerExecuteAgreementFlowable(Flowable<ServiceAgreement.ExecuteAgreementEventResponse> flowable) {
        flowable.subscribe(event -> {
            String did= EncodingHelper.toHexString(event.did);
            String templateId= EncodingHelper.toHexString(event.templateId);
            log.debug("Receiving event - " + EncodingHelper.toHexString(event.serviceAgreementId));
            // 1. Resolve DID
        });
    }

}

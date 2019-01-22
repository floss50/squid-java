package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.OrderResult;
import com.oceanprotocol.squid.models.service.ServiceEndpoints;
import io.reactivex.Flowable;

import java.util.List;
import java.util.Map;

/**
 *
 */
// TODO Define SquidExceptions
public interface AssetsAPI {

    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints, int threshold);
    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints);
    public DDO resolve(DID did);

    public List<DDO> search(String text);
    public List<DDO> search(String text,  int offset, int page);
    public List<DDO> search(Map<String, Object> params, int offset, int page, int sort);
    // TODO Not implemented
    public List<DDO> query(String query);

    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath, int threshold);
    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath);
    Flowable<OrderResult> order(DID did, String serviceDefinitionId, Account consumerAccount, String serviceAgreementId);



}

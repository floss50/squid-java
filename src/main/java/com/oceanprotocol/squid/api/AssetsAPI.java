package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.exceptions.ConsumeServiceException;
import com.oceanprotocol.squid.exceptions.DDOException;
import com.oceanprotocol.squid.exceptions.EthereumException;
import com.oceanprotocol.squid.exceptions.OrderException;
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
public interface AssetsAPI {

    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints, int threshold) throws DDOException;
    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints) throws DDOException;
    public DDO resolve(DID did) throws EthereumException, DDOException;

    public List<DDO> search(String text) throws DDOException;
    public List<DDO> search(String text,  int offset, int page) throws DDOException;
    public List<DDO> query(Map<String, Object> params, int offset, int page, int sort) throws DDOException;
    public List<DDO> query(Map<String, Object> params) throws DDOException;

    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath, int threshold) throws ConsumeServiceException;
    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath) throws ConsumeServiceException;
    Flowable<OrderResult> order(DID did, String serviceDefinitionId, Account consumerAccount) throws OrderException;



}

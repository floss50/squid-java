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
 * Exposes the Public API related with Assets
 */
public interface AssetsAPI {

    /**
     * Creates a new DDO, registering it on-chain through DidRegistry contract and off-chain in Aquarius
     * @param metadata
     * @param publisherAccount
     * @param serviceEndpoints
     * @param threshold
     * @return an instance of the DDO created
     * @throws DDOException
     */
    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints, int threshold) throws DDOException;

    /**
     *  Creates a new DDO, registering it on-chain through DidRegistry contract and off-chain in Aquarius
     * @param metadata
     * @param publisherAccount
     * @param serviceEndpoints
     * @return an instance of the DDO created
     * @throws DDOException
     */
    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints) throws DDOException;

    /**
     * Gets a DDO from a DID
     * @param did
     * @return an instance of the DDO represented by the DID
     * @throws EthereumException
     * @throws DDOException
     */
    public DDO resolve(DID did) throws EthereumException, DDOException;

    /**
     * Gets all the DDO that match the search criteria
     * @param text
     * @return a List with all the DDOs found
     * @throws DDOException
     */
    public List<DDO> search(String text) throws DDOException;

    /**
     * Gets all the DDOs that match the search criteria
     * @param text
     * @param offset
     * @param page
     * @return a List with all the DDOs found
     * @throws DDOException
     */
    public List<DDO> search(String text,  int offset, int page) throws DDOException;

    /**
     * Gets all the DDOs that match the parameters of the query
     * @param params
     * @param offset
     * @param page
     * @param sort
     * @return a List with all the DDOs found
     * @throws DDOException
     */
    public List<DDO> query(Map<String, Object> params, int offset, int page, int sort) throws DDOException;

    /**
     * Gets all the DDOs that match the parameters of the query
     * @param params
     * @return a List with all the DDOs found
     * @throws DDOException
     */
    public List<DDO> query(Map<String, Object> params) throws DDOException;

    /**
     * Downloads an Asset previously ordered through a Service Agreement
     * @param serviceAgreementId
     * @param did
     * @param serviceDefinitionId
     * @param consumerAccount
     * @param basePath
     * @param threshold
     * @return a flag that indicates if the consume flow was executed correctly
     * @throws ConsumeServiceException
     */
    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath, int threshold) throws ConsumeServiceException;

    /**
     *  Downloads an Asset previously ordered through a Service Agreement
     * @param serviceAgreementId
     * @param did
     * @param serviceDefinitionId
     * @param consumerAccount
     * @param basePath
     * @return a flag that indicates if the consume flow was executed correctly
     * @throws ConsumeServiceException
     */
    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath) throws ConsumeServiceException;

    /**
     * Purchases an Asset represented by a DID. It implies to initialize a Service Agreement between publisher and consumer
     * @param did
     * @param serviceDefinitionId
     * @param consumerAccount
     * @return a Flowable instance over an OrderResult to get the result of the flow in an asynchronous fashion
     * @throws OrderException
     */
    Flowable<OrderResult> order(DID did, String serviceDefinitionId, Account consumerAccount) throws OrderException;



}

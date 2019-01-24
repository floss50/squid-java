package com.oceanprotocol.squid.api.impl;

import com.oceanprotocol.squid.api.AssetsAPI;
import com.oceanprotocol.squid.manager.AssetsManager;
import com.oceanprotocol.squid.manager.OceanManager;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.OrderResult;
import com.oceanprotocol.squid.models.service.ServiceEndpoints;
import io.reactivex.Flowable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AssetsImpl implements AssetsAPI {

    private OceanManager oceanManager;
    private AssetsManager assetsManager;

    public AssetsImpl(OceanManager oceanManager, AssetsManager assetsManager) {

        this.oceanManager = oceanManager;
        this.assetsManager = assetsManager;
    }


    @Override
    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints, int threshold) {

        // TODO HANDLE Exception
        try {
            return oceanManager.registerAsset(metadata, publisherAccount.address, serviceEndpoints, threshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DDO create(AssetMetadata metadata, Account publisherAccount, ServiceEndpoints serviceEndpoints) {

        // TODO HANDLE Exception
        try {
            return oceanManager.registerAsset(metadata, publisherAccount.address, serviceEndpoints, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DDO resolve(DID did) {

        // TODO HANDLE Exception
        try {
            return oceanManager.resolveDID(did);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<DDO> search(String text) {

        // TODO
        return assetsManager.searchAssets(text, 0, 0);
    }

    @Override
    public List<DDO> search(String text, int offset, int page) {
        return assetsManager.searchAssets(text, offset, page);
    }

    @Override
    public List<DDO> search(Map<String, Object> params, int offset, int page, int sort) {
        return assetsManager.searchAssets(params, offset, page, sort);
    }

    // TODO Not Implemented
    @Override
    public List<DDO> query(String query) {
        return null;
    }

    @Override
    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath, int threshold) {

        // TODO HANDLE Exception
        try {
            return oceanManager.consume(serviceAgreementId, did, serviceDefinitionId, consumerAccount.address, basePath, threshold);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, Account consumerAccount, String basePath) {

        // TODO HANDLE Exception
        try {
            return oceanManager.consume(serviceAgreementId, did, serviceDefinitionId, consumerAccount.address, basePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Flowable<OrderResult> order(DID did, String serviceDefinitionId, Account consumerAccount) {

        // TODO HANDLE Exception
        try {
            return oceanManager.purchaseAsset(did, serviceDefinitionId, consumerAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

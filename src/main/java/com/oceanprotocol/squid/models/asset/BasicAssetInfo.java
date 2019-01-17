package com.oceanprotocol.squid.models.asset;

public class BasicAssetInfo {

    byte[] assetId;
    Integer price;

    public byte[] getAssetId() {
        return assetId;
    }

    public void setAssetId(byte[] assetId) {
        this.assetId = assetId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}

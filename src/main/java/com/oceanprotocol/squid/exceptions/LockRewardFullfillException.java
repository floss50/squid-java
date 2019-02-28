package com.oceanprotocol.squid.exceptions;

/**
 * Business Exception related with LockReward Fullfill issues
 */
public class LockRewardFullfillException extends OceanException {

    public LockRewardFullfillException(String message, Throwable e) {
        super(message, e);
    }

    public LockRewardFullfillException(String message) {
        super(message);
    }
}

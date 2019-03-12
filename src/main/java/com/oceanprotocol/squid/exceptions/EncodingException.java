/*
 * Copyright BigchainDB GmbH and BigchainDB contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.oceanprotocol.squid.exceptions;

/**
 * Business Exception related with Encoding/Decoding issues
 */
public class EncodingException extends OceanException {

    public EncodingException(String message, Throwable e) {
        super(message, e);
    }

    public EncodingException(String message) {
        super(message);
    }
}

package com.oceanprotocol.squid.helpers;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.core.methods.response.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EventsHelper {

    private static final String DIDAttributeRegisteredEvent= "DIDAttributeRegistered";

    // event DIDAttributeRegistered(bytes32 indexed did, address indexed owner,
    // bytes32 indexed key, string value,  ValueType valueType, uint updatedAt);

    public static Event getDIDAttributeRegisteredEvent()   {
        return new Event(DIDAttributeRegisteredEvent,
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Bytes32>(true) {},
                        new TypeReference<Address>(true) {},
                        new TypeReference<Bytes32>(true) {},
                        new TypeReference<Utf8String>() {},
                        new TypeReference<Uint8>() {},
                        new TypeReference<Uint256>() {})
        );

    }

    public static EventValues staticExtractEventParameters(Event event, Log log) {

        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

}

package com.oceanprotocol.squid.helpers;

import java.util.List;

public class StringsHelper {

    public static String wrapWithQuotesAndJoin(List<String> listOfStrings)   {
        return listOfStrings.isEmpty() ? "" : "\"" + String.join("\",\"", listOfStrings) + "\"";
    }
}

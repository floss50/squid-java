package com.oceanprotocol.squid.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class StringsHelper {

    public static String wrapWithQuotesAndJoin(List<String> listOfStrings)   {
        return listOfStrings.isEmpty() ? "" : "\"" + String.join("\",\"", listOfStrings) + "\"";
    }

    public static List<String> getStringsFromJoin(String joinedString) {

        return Stream.of(joinedString.split(","))
                .map(url -> url.replaceAll("\"", ""))
                .collect(toList());

    }


    public static String format(String format, Map<String, Object> values) {

        StringBuilder formatter = new StringBuilder(format);
        List<Object> valueList = new ArrayList<Object>();

        Matcher matcher = Pattern.compile("\\$\\{(\\w+)}").matcher(format);

        while (matcher.find()) {
            String key = matcher.group(1);

            String formatKey = String.format("${%s}", key);
            int index = formatter.indexOf(formatKey);

            if (index != -1) {
                formatter.replace(index, index + formatKey.length(), "%s");
                valueList.add(values.get(key));
            }
        }

        return String.format(formatter.toString(), valueList.toArray());
    }
}

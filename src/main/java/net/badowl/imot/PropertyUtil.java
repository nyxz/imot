package net.badowl.imot;

import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyUtil {

    private static final Pattern PATTERN_YEAR = Pattern.compile(".*(([12][0-9]{3})\\s?г\\s?).*");
    private static final String PATTERN_SIZE = "[^0-9]";
    private static final Pattern PATTERN_FLOOR = Pattern.compile("^(\\d+).*$");
    private static final Pattern PATTERN_TOTAL_FLOORS = Pattern.compile("^.+?(\\d+$)");
    private static final String CURRENCY = "EUR";

    public static Integer toBuildYear(String buildType, String description) {
        String yearFromBuildType = null;
        if (!StringUtils.isEmpty(buildType)) {
            yearFromBuildType = yearFromText(buildType);
        }
        if (!StringUtils.isEmpty(yearFromBuildType)) {
            return Integer.parseInt(yearFromBuildType);
        } else {
            return Optional.ofNullable(yearFromText(description)).map(Integer::parseInt)
                    .orElse(null);
        }
    }

    private static String yearFromText(String text) {
        return parseWithRegex(text, PATTERN_YEAR, 2);
    }


    public static Long toSize(String sizeStr) {
        if (StringUtils.isEmpty(sizeStr)) {
            return null;
        } else {
            return Long.valueOf(sizeStr.replaceAll(PATTERN_SIZE, ""));
        }
    }

    public static Long toPrice(String priceStr) {
        if (StringUtils.isEmpty(priceStr)) {
            return null;
        } else {
            int indexOfFirstCurrency = priceStr.trim().indexOf(CURRENCY);
            if (indexOfFirstCurrency < 0) {
                // probably the currency is wrong
                return null;
            }
            return Long.valueOf(priceStr.substring(0, indexOfFirstCurrency).replace(" ", ""));
        }
    }

    public static Integer toFloor(String floor) {
        return Optional.ofNullable(parseWithRegex(floor, PATTERN_FLOOR, 1))
                .map(Integer::parseInt)
                .orElse(null);
    }

    public static Integer toTotalFloors(String floor) {
        return Optional.ofNullable(parseWithRegex(floor, PATTERN_TOTAL_FLOORS, 1))
                .map(Integer::parseInt)
                .orElse(null);
    }

    private static String parseWithRegex(String text, Pattern pattern, int group) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        final Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return matcher.group(group);
        }
        return null;
    }
}

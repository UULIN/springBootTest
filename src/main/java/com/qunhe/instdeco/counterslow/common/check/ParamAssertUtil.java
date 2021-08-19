package com.qunhe.instdeco.counterslow.common.check;

import java.util.Collection;
import java.util.Map;

/**
 * @author tumei
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class ParamAssertUtil {
    public static void notNull(Object value, String valueDesc) {
        if (!AbstractValueChecks.notNull(value)) {
            throw new RuntimeException(String.format("%s is null", valueDesc));
        }
    }

    public static void notBlank(String value, String valueDesc) {
        if (!AbstractValueChecks.notBlank(value)) {
            throw new RuntimeException(String.format("%s is blank", valueDesc));
        }
    }

    public static void notBlank(String value, String msgTemplate, Object... args) {
        if (!AbstractValueChecks.notBlank(value)) {
            throw new RuntimeException(String.format(msgTemplate, args));
        }
    }

    public static void notEqual(String value, String valueDesc, String notEqVal) {
        if (!AbstractValueChecks.notEqual(value, notEqVal)) {
            throw new RuntimeException(String.format("%s must not equal to %s", valueDesc, notEqVal));
        }
    }

    public static void lenLte(String value, String valueDesc, int maxLen) {
        if (!AbstractValueChecks.lenLte(value, maxLen)) {
            throw new RuntimeException(
                    String.format("Length of %s must be less than or equal to %s", valueDesc, maxLen));
        }
    }

    public static void gt(long value, String valueDesc, long lowerBound) {
        if (!AbstractValueChecks.gt(value, lowerBound)) {
            throw new RuntimeException(
                    String.format("%s must be greater than %s", valueDesc, lowerBound));
        }
    }

    public static void gte(long value, String valueDesc, long lowerBound) {
        if (!AbstractValueChecks.gte(value, lowerBound)) {
            throw new RuntimeException(
                    String.format("%s must be greater than or equal to %s", valueDesc, lowerBound));
        }
    }

    public static void range(long value, String valueDesc, long lowerBound, long upperBound) {
        if (!AbstractValueChecks.range(value, lowerBound, upperBound)) {
            throw new RuntimeException(
                    String.format("%s must be in range of %s to %s", valueDesc, lowerBound, upperBound));
        }
    }

    public static void isPositiveInt(String value, String valueDesc) {
        if (!AbstractValueChecks.positiveInt(value)) {
            throw new RuntimeException(String.format("%s is negative integer", valueDesc));
        }
    }

    public static void notEmpty(Collection<?> value, String valueDesc) {
        if (!AbstractValueChecks.notEmpty(value)) {
            throw new RuntimeException(String.format("%s is null or empty", valueDesc));
        }
    }

    public static void notEmpty(Map<?, ?> value, String valueDesc) {
        if (!AbstractValueChecks.notEmpty(value)) {
            throw new RuntimeException(String.format("%s is null or empty", valueDesc));
        }
    }

    public static void letterNumHyphen(String value, String valueDesc) {
        if (!AbstractValueChecks.letterNumHyphen(value)) {
            throw new RuntimeException(
                    String.format("%s must consist of letters, numbers, -, _", valueDesc));
        }
    }

    public static void className(String value, String valueDesc) {
        if (!AbstractValueChecks.className(value)) {
            throw new RuntimeException(
                    String.format("%s [%s] isn't a valid class name", valueDesc, value));
        }
    }

    public static void fileNameAndExt(String value, String valueDesc) {
        if (!AbstractValueChecks.fileNameAndExt(value)) {
            throw new RuntimeException(
                    String.format("%s must match pattern '<name>.<extension>'; "
                            + "name consists of a-z, A-Z, 1-9, _ and -; "
                            + "extension consists of a-z, A-Z, and 1-9", valueDesc));
        }
    }

    public static void ipv4(String value, String valueDesc) {
        if (!AbstractValueChecks.ipv4(value)) {
            throw new RuntimeException(
                    String.format("%s isn't a ipv4 address", valueDesc));
        }
    }

    public static void assertTrue(boolean expr, String msgTemplate, Object... args) {
        if (!expr) {
            throw new RuntimeException(String.format(msgTemplate, args));
        }
    }

}

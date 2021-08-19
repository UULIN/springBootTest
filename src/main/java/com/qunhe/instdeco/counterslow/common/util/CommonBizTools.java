package com.qunhe.instdeco.counterslow.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;
import com.qunhe.instdeco.counterslow.common.enums.EnvStageEnum;
import com.qunhe.instdeco.counterslow.common.func.ThrowingSupplier;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author tumei
 */
@Slf4j
@SuppressWarnings("unused")
public class CommonBizTools {
    public static final EnvStageEnum ENV_STAGE;
    public static final boolean SCHEDULER_SWITCH;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        val stageName = Optional.ofNullable(System.getProperty("stage"))
                .map(String::toUpperCase)
                .orElse(EnvStageEnum.DEV.name());
        ENV_STAGE = EnvStageEnum.valueOf(stageName);
        SCHEDULER_SWITCH = isDevMode() && Optional.ofNullable(System.getProperty("schedulerOff"))
                .map(""::equals)
                .orElse(true);
    }

    /**
     * ENV INFO
     */
    public static boolean isDevMode() { return EnvStageEnum.DEV.equals(ENV_STAGE); }

    /**
     * Convert
     */
    public static String serializeParams(Object params) {
        try {
            return OBJECT_MAPPER.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            log.warn("serializeParams fail, {}", params);
            return null;
        }
    }

    public static List<Integer> parseStr2intLst(String str) {
        return Optional.ofNullable(str)
                .map(ts -> ts.split(","))
                .map(arr -> Arrays.stream(arr)
                        .distinct()
                        .map(String::trim)
                        .filter(s -> !StringUtils.isEmpty(s))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> List<T> convertQueryList(Object queryObject, Class<T> innerCls) {
        val javaType = getCollectionType(List.class, innerCls);
        return OBJECT_MAPPER.convertValue(queryObject, javaType);
    }

    public static String escapeRegStr(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("?", "\\?")
                .replace(",", "\\,")
                .replace(".", "\\.")
                .replace("&", "\\&");
    }

    /**
     * common tools
     */
    public static <T> T evalOrNull(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static <T> Supplier<T> wrapSp(ThrowingSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable throwable) {
                return null;
            }
        };
    }

    @SneakyThrows
    private static <T> List<String> vo2keys(@NonNull T vo, Set<String> ignoreKeys) {
        return Arrays.stream(Introspector.getBeanInfo(vo.getClass(), Object.class).getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(f -> !ignoreKeys.contains(f))
                .collect(Collectors.toList());
    }

    private static final String CLASS_POSTFIX = ".class";
    private static final Integer CLASS_POSTFIX_LENGTH = CLASS_POSTFIX.length();
    private static final String FILE_PROTOCOL = "file";

    @SneakyThrows
    public static List<? extends Class<?>> listPackageClazz(String packageName) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        Iterators.forEnumeration(
                                Thread.currentThread()
                                        .getContextClassLoader()
                                        .getResources(
                                                Optional.ofNullable(packageName)
                                                        .map(p -> packageName.replace('.', '/'))
                                                        .orElseThrow(() -> new RuntimeException("invalid package name:" + packageName)))),
                        Spliterator.ORDERED),
                false)
                .filter(u -> FILE_PROTOCOL.equals(u.getProtocol()))
                .map(u -> evalOrNull(() -> URLDecoder.decode(u.getFile(), StandardCharsets.UTF_8.name())))
                .filter(Objects::nonNull)
                .map(File::new)
                .filter(File::exists)
                .filter(File::isDirectory)
                .map(dir -> dir.listFiles(f -> f.getName().endsWith(CLASS_POSTFIX)))
                .filter(Objects::nonNull)
                .map(dirFiles -> Arrays.stream(dirFiles)
                        .filter(File::isFile)
                        .map(f -> evalOrNull(() -> Thread.currentThread()
                                .getContextClassLoader()
                                .loadClass(packageName + "." + f.getName().substring(0, f.getName().length() - CLASS_POSTFIX_LENGTH))))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static final Pattern ALL_MATCH_PATTERN = Pattern.compile(".*");

    public static List<? extends Class<?>> listPackageClazzEx(String packageName) {
        val provider = new ClassPathScanningCandidateComponentProvider(false);
        /* provider.addIncludeFilter(new AnnotationTypeFilter(Data.class)); */
        provider.addIncludeFilter(new RegexPatternTypeFilter(ALL_MATCH_PATTERN));
        val scanList = provider.findCandidateComponents(packageName);
        return scanList.stream()
                .map(BeanDefinition::getBeanClassName)
                .map(c -> evalOrNull(() -> Class.forName(c)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static final String OBFUSCATE_STR = "*";

    private static String obfuscateSingleString(String str) {
        val strLen = str.length();
        switch (strLen) {
            case 0:
                return "";
            case 1:
                return OBFUSCATE_STR;
            case 2:
                return str.substring(0, 1) + OBFUSCATE_STR;
            default:
                val showSize = (int) Math.max(1, Math.floor(strLen / 3.0));
                val hideSize = strLen - showSize * 2;
                return str.substring(0, showSize)
                        + String.join("", Collections.nCopies(hideSize, OBFUSCATE_STR))
                        + str.substring(showSize + hideSize);
        }
    }

    public static String obfuscateSingleObject(Object obj) {
        return Optional.ofNullable(obj)
                .map(Object::toString)
                .map(CommonBizTools::obfuscateSingleString)
                .orElse(null);
    }

}

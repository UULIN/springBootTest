package com.qunhe.instdeco.counterslow.common;

/**
 * @author tumei
 */
public class Apis {
    private static final String SERVICE_ROOT = "/counter";

    public static final class Adhoc {
        public static final String SQL_TOTAL = SERVICE_ROOT + "/sql/total";
        public static final String SQL_DETAILS = SERVICE_ROOT + "/sql/details";
        public static final String SQL_DIFF = SERVICE_ROOT + "/sql/diff";
        public static final String SQL_ALL = SERVICE_ROOT + "/sql/all";
    }

    public static final class Config {
        public static final String TETRIS_CK = SERVICE_ROOT + "/config/tetrisCk";
    }
}

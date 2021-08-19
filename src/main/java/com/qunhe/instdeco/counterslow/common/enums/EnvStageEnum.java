package com.qunhe.instdeco.counterslow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tumei
 */

@AllArgsConstructor
@Getter
public enum EnvStageEnum {
    // ENV INFO
    DEV((byte)0),
    PROD_TEST((byte)1),
    PROD((byte)2),
    Unknown((byte)3),
    ;
    private Byte code;

    public static EnvStageEnum code2envStageEnum(Byte code) {
        for (EnvStageEnum anEnum : EnvStageEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return Unknown;
    }
}

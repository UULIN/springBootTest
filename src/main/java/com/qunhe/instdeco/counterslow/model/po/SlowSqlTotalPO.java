package com.qunhe.instdeco.counterslow.model.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author tumei
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "slow_sql_total")
public class SlowSqlTotalPO implements Serializable {
    private static final long serialVersionUID = -8702626816012703563L;
    private static final String TIME_FORMAT = "yyyy-MM-dd";
    private static final String TIME_ZONE = "GMT+8";

    @TableId(type = IdType.AUTO)
    private int id;
    private String serverName;
    private Integer sqlCount;
    @JsonFormat(pattern = TIME_FORMAT, timezone = TIME_ZONE)
    @TableField(fill = FieldFill.INSERT)
    private LocalDate created;
}

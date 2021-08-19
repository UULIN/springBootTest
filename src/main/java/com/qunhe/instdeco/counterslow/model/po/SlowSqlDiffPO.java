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
@TableName(value = "slow_sql_diff")
public class SlowSqlDiffPO implements Serializable {
    private static final long serialVersionUID = 7996628995901524119L;
    private static final String TIME_FORMAT = "yyyy-MM-dd";
    private static final String TIME_ZONE = "GMT+8";

    @TableId(type = IdType.AUTO)
    private int id;
    private String sqlDiff;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = TIME_FORMAT, timezone = TIME_ZONE)
    private LocalDate created;
}

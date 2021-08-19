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
@TableName(value = "slow_sql_details")
public class SlowSqlDetailsPO implements Serializable {
    private static final long serialVersionUID = 2836335582751278031L;
    private static final String TIME_FORMAT = "yyyy-MM-dd";
    private static final String TIME_ZONE = "GMT+8";

    @TableId(type = IdType.AUTO)
    private int id;
    private String sqlContent;
    private Long sqlCount;
    private String timeRange;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = TIME_FORMAT, timezone = TIME_ZONE)
    private LocalDate created;
}

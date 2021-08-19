package com.qunhe.instdeco.counterslow.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author tumei
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AdhocSearchSingleRecord implements Serializable {
    private static final long serialVersionUID = -662918346124129064L;
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_ZONE = "GMT+8";
    private static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Asia/Shanghai");

    @JsonAlias("ct")
    @JsonFormat(pattern = TIME_FORMAT, timezone = TIME_ZONE)
    private LocalDateTime created;
    @JsonAlias("sn")
    private String dbName;
    @JsonAlias("da.tableName")
    private List<String> tableName;
    @JsonAlias("n")
    private String oprtType;
    @JsonAlias("da.tag")
    private String tag;
    @JsonAlias("uriPattern")
    private String uri;
    @JsonAlias("tid")
    private String traceId;
    @JsonAlias("aa.s.ps")
    private String statement;
    @JsonAlias("d")
    private Long cost;
    private String timeRange;
    @SuppressWarnings("unused")
    public void setCreated(Long created) {
        this.created = LocalDateTime.ofInstant(Instant.ofEpochMilli(created / 1000), LOCAL_ZONE_ID);
    }
}

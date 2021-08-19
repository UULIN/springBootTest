package com.qunhe.instdeco.counterslow.manager;

import com.google.common.collect.ImmutableList;
import com.qunhe.instdeco.counterslow.model.dto.AdhocSearchRangeDTO;
import com.qunhe.instdeco.counterslow.model.dto.AdhocSearchSingleRecord;
import com.qunhe.instdeco.counterslow.model.dto.TetrisSearchRangeDTO;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author tumei
 */
@SuppressWarnings("WeakerAccess")
public class AdhocManager {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final HttpHeaders ADHOC_HEADERS = new HttpHeaders();
    private static final String API_OPEN_RANGE = "http://adhoc-query.kube-aliyun.qunhequnhe.com/api/open/range";
    private static final String API_TETRIS_QUERY = "https://tetris.qunhequnhe.com/apm-api/hunter/query";
    private static final String ADHOC_MYSQL_TRACE_TABLE = "hunter_mysql_trace_new";
    private static final String TETRIS_MYSQL_TRACE_TABLE = "sql";
    private static final String QUERY_FORMAT = "{" +
            "  \"table\": \"%s\"," +
            "  \"interval\": {" +
            "    \"start\": %d," +
            "    \"end\": %d" +
            "  }," +
            "  \"limit\": {" +
            "    \"size\": %d," +
            "    \"from\": %d" +
            "  }," +
            "  \"db\": \"druid\"," +
            "  \"conditions\": {" +
            "    \"logicOp\": \"And\"," +
            "    \"assertConditions\": [" +
            "      {" +
            "        \"field\": \"owt\"," +
            "        \"operator\": \"Eq\"," +
            "        \"param\": \"web-be\"" +
            "      }," +
            "      {" +
            "        \"field\": \"pdl\"," +
            "        \"operator\": \"Eq\"," +
            "        \"param\": \"brandgood\"" +
            "      }," +
            "      {" +
            "        \"field\": \"service\"," +
            "        \"operator\": \"Eq\"," +
            "        \"param\": \"commoditycenterservice\"" +
            "      }," +
            "      {" +
            "        \"field\": \"cluster\"," +
            "        \"operator\": \"Eq\"," +
            "        \"param\": \"prod\"" +
            "      }," +
            "      {" +
            "        \"field\": \"da.totalCostLevel\"," +
            "        \"operator\": \"Eq\"," +
            "        \"param\": \"%s\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  \"granularity\": \"All\"," +
            "  \"groupFields\": []," +
            "  \"measures\": []," +
            "  \"order\": {}" +
            "}";

    static {
        ADHOC_HEADERS.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ADHOC_HEADERS.add("adhoc-auth", "exabrain.cms.commoditycenterservice");
        ADHOC_HEADERS.add("X-Tetris-Username", "tumei");
    }

    @Deprecated
    public static AdhocSearchRangeDTO searchAdhocApiRange(Long startTime, Long endTime, String timeRange) {
        return searchAdhocApiRange(startTime, endTime, 5000, 1, timeRange);
    }

    public static AdhocSearchRangeDTO searchAdhocApiRange(
            Long startTime, Long endTime, Integer pageSize, Integer pageNum, String timeRange) {
        val formEntity = new HttpEntity<>(String.format(
                QUERY_FORMAT, ADHOC_MYSQL_TRACE_TABLE, startTime, endTime, pageSize, pageNum - 1, timeRange),
                ADHOC_HEADERS);
        val adhocSearchResult = REST_TEMPLATE.postForObject(API_OPEN_RANGE, formEntity, AdhocSearchRangeDTO.class);
        Optional.ofNullable(adhocSearchResult)
                .ifPresent(dt -> dt.getData()
                        .forEach(d -> d.setTimeRange(timeRange)));
        return adhocSearchResult;
    }

    public static AdhocSearchRangeDTO searchTetrisApiRange(Long startTime, Long endTime, String timeRange, String ck) {
        return searchTetrisApiRange(startTime, endTime, 5000, 1, timeRange, ck);
    }

    public static AdhocSearchRangeDTO searchTetrisApiRange(
            Long startTime, Long endTime, Integer pageSize, Integer pageNum, String timeRange, String ck) {
        ADHOC_HEADERS.put(HttpHeaders.COOKIE, ImmutableList.of(ck));
        val formEntity = new HttpEntity<>(String.format(
                QUERY_FORMAT, TETRIS_MYSQL_TRACE_TABLE, startTime, endTime, pageSize, pageNum - 1, timeRange),
                ADHOC_HEADERS);
        val tetrisSearchResult = REST_TEMPLATE.postForObject(API_TETRIS_QUERY, formEntity, TetrisSearchRangeDTO.class);
        Optional.ofNullable(tetrisSearchResult)
                .ifPresent(dt -> dt.getData()
                        .forEach(d -> d.setTimeRange(timeRange)));
        return tetris2adhocSearchRangeDTO(tetrisSearchResult);
    }

    private static AdhocSearchRangeDTO tetris2adhocSearchRangeDTO(TetrisSearchRangeDTO tetrisData) {
        return Optional.ofNullable(tetrisData)
                .map(dt -> AdhocSearchRangeDTO.builder()
                        .total(dt.getTotal())
                        .cost(dt.getCost())
                        .data(dt.getData().stream()
                                .map(t -> AdhocSearchSingleRecord.builder()
                                        .statement(t.getPs())
                                        .timeRange(t.getTimeRange())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .orElse(null);
    }

}

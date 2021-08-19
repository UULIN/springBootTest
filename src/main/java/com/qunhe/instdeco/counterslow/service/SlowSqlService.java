package com.qunhe.instdeco.counterslow.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.qunhe.instdeco.counterslow.common.util.CljThreadPoolUtil;
import com.qunhe.instdeco.counterslow.configuration.CacheConfiguration;
import com.qunhe.instdeco.counterslow.model.dto.AdhocSearchRangeDTO;
import com.qunhe.instdeco.counterslow.model.po.SlowSqlDetailsPO;
import com.qunhe.instdeco.counterslow.model.po.SlowSqlDiffPO;
import com.qunhe.instdeco.counterslow.model.po.SlowSqlTotalPO;
import com.qunhe.instdeco.counterslow.service.crud.DynamicConfigService;
import com.qunhe.instdeco.counterslow.service.crud.SlowSqlDetailsServiceImpl;
import com.qunhe.instdeco.counterslow.service.crud.SlowSqlDiffServiceImpl;
import com.qunhe.instdeco.counterslow.service.crud.SlowSqlTotalServiceImpl;
import com.qunhe.instdeco.counterslow.common.util.Retryer;
import com.qunhe.instdeco.counterslow.manager.AdhocManager;
import com.qunhe.instdeco.counterslow.model.dto.AdhocSearchSingleRecord;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.qunhe.instdeco.counterslow.common.util.CommonBizTools.evalOrNull;
import static com.qunhe.instdeco.counterslow.common.util.CommonBizTools.serializeParams;

/**
 * @author tumei
 */
@Service
@AllArgsConstructor
@SuppressWarnings(value = {"UnusedReturnValue", "WeakerAccess", "rawtypes"})
public class SlowSqlService {
    private final SlowSqlTotalServiceImpl slowSqlTotalService;
    private final SlowSqlDetailsServiceImpl slowSqlDetailsService;
    private final SlowSqlDiffServiceImpl slowSqlDiffService;
    private final DynamicConfigService dynamicConfigService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String simplifySql(String sql) {
        return Optional.ofNullable(sql)
                .map(s -> s.replaceAll("_\\d+", "_N"))
                .map(s -> s.replace("/* /// */", ""))
                .map(s -> s.replaceAll("( \\? ,)+ \\? ", "?...?"))
                .map(s -> s.replaceAll("( \\?,)+ \\?", "?...?"))
                .map(s -> s.replaceAll(" (\\?,)+\\? ", "?...?"))
                .map(s -> s.replaceAll("(\\?,)+\\?", "?...?"))
                .orElse("");
    }

    /**
     * 保存 慢sql total数据
     *
     * @param slowSqlTotal SlowSqlTotalPO
     * @return SlowSqlTotalPO
     */
    private SlowSqlTotalPO storeSlowSqlTotal(SlowSqlTotalPO slowSqlTotal) {
        val saveSuccess = slowSqlTotalService.save(slowSqlTotal);
        if (!saveSuccess) {
            throw new RuntimeException("storeSlowSqlTotal fail");
        }
        return slowSqlTotal;
    }

    /**
     * 保存 慢sql details数据
     *
     * @param slowSqlDetailsList List<SlowSqlDetailsPO>
     * @return slowSqlDetailsList
     */
    private List<SlowSqlDetailsPO> storeSlowSqlDetailsList(List<SlowSqlDetailsPO> slowSqlDetailsList) {
        val saveSuccess = slowSqlDetailsService.saveBatch(slowSqlDetailsList);
        if (!saveSuccess) {
            throw new RuntimeException("storeSlowSqlDetailsList fail");
        }
        return slowSqlDetailsList;
    }

    /**
     * 保存 慢sql diff数据
     *
     * @param slowSqlDiff SlowSqlDiffPO
     * @return SlowSqlDiffPO
     */
    private SlowSqlDiffPO storeSlowSqlDiff(SlowSqlDiffPO slowSqlDiff) {
        final boolean saveSuccess = slowSqlDiffService.save(slowSqlDiff);
        if (!saveSuccess) {
            throw new RuntimeException("storeSlowSqlDiff fail");
        }
        return slowSqlDiff;
    }

    /**
     * 查询 慢sql total数据
     *
     * @return Map
     */
    @Cacheable(cacheNames = CacheConfiguration.SQL_TOTAL, unless = "#result==null")
    public Map<String, List> searchAdhocSearchSqlTotal(Long duration) {
        // 全部详情数据
        val conditionDetails = new QueryWrapper<SlowSqlTotalPO>().lambda()
                .orderByDesc(SlowSqlTotalPO::getCreated);
        val sqlDetails = slowSqlTotalService.list(conditionDetails);
        // 当前周期
        val currentDurationData = sqlDetails.stream()
                .limit(duration)
                .collect(Collectors.toList());
        // 环比, 如上周
        val preWeekDurationData = sqlDetails.stream()
                .limit(duration * 2)
                .skip(duration)
                .collect(Collectors.toList());
        // 同比数据对应日期
        val preMonthDurationDays = currentDurationData.stream()
                .map(SlowSqlTotalPO::getCreated)
                .map(day -> day.minusMonths(1))
                .collect(Collectors.toList());
        val preMonthDaysSet = ImmutableSet.copyOf(preMonthDurationDays);
        // 同比, 如上月
        val preMonthDurationData = sqlDetails.stream()
                .filter(dt -> preMonthDaysSet.contains(dt.getCreated()))
                .collect(Collectors.toList());
        return ImmutableMap.of(
                "current", currentDurationData,
                "preWeek", preWeekDurationData,
                "preMonth", preMonthDurationData
        );
    }

    /**
     * 查询 慢sql details数据
     *
     * @return List<SlowSqlDetailsPO
     */
    @Cacheable(cacheNames = CacheConfiguration.SQL_DETAIL, unless = "#result==null")
    public List<SlowSqlDetailsPO> searchAdhocSearchSqlDetails() {
        val condition = new QueryWrapper<SlowSqlDetailsPO>().lambda()
                .orderByDesc(SlowSqlDetailsPO::getCreated);
        return slowSqlDetailsService.list(condition);
    }

    /**
     * 查询 慢sql diff数据
     *
     * @return List<SlowSqlDiffPO
     */
    @Cacheable(cacheNames = CacheConfiguration.SQL_DIFF, unless = "#result==null")
    public List<SlowSqlDiffPO> searchAdhocSearchSqlDiff() {
        val condition = new QueryWrapper<SlowSqlDiffPO>().lambda()
                .orderByDesc(SlowSqlDiffPO::getCreated);
        return slowSqlDiffService.list(condition);
    }

    private static final List<String> TIME_RANGE_LIST = ImmutableList.of("300 ~ 500", "500 ~ 1000", "1000 ~ 2000", "2000 ~ 3000");

    /**
     * 保存 慢sql total + details + diff 数据
     */
    public void storeSlowSqlInfo() {
        val calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        val endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, -24);
        val startTime = calendar.getTimeInMillis();
        storeSlowSqlInfo(startTime, endTime);
    }

    public void storeSlowSqlInfo(Long startTime, Long endTime) {
        //  感觉监控不大稳定, 所以决定指数增加等待时间的重试下
        //  默认重试5次, 重试间隔 5s 30s 5min 30min 2h,
        val ck = dynamicConfigService.getTetrisCk();
        Function<String, AdhocSearchRangeDTO> searchTetrisByRangeSupplier = timeRange -> Retryer.newSimpleRetryer()
                .execWithRetry(() -> AdhocManager.searchTetrisApiRange(startTime, endTime, timeRange, ck));
        val costLevel = CljThreadPoolUtil.pmap4io(searchTetrisByRangeSupplier, TIME_RANGE_LIST);
        val mergedAdhocResult = costLevel.stream()
                .reduce((r1, r2) -> AdhocSearchRangeDTO.builder()
                        .cost(r1.getCost() + r2.getCost())
                        .total(r1.getTotal() + r2.getTotal())
                        .data(Stream.of(r1.getData(), r2.getData())
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                        .build())
                .orElseThrow(() -> new RuntimeException("searchAdhocApiRange failed"));

        val total = mergedAdhocResult.getData().size();
        val adhocSearchSqlTotal = SlowSqlTotalPO.builder()
                .serverName("commoditycenterservice")
                .sqlCount(total)
                .build();
        // 保存total数据
        storeSlowSqlTotal(adhocSearchSqlTotal);
        // 统计各sql条数和时间范围
        val groupedSqlMap = mergedAdhocResult.getData().stream()
                .collect(Collectors.groupingBy(
                        r -> simplifySql(r.getStatement()),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                vs -> ImmutableMap.builder()
                                        .put("count", vs.size())
                                        .put("timeRange", vs.stream()
                                                .collect(Collectors.groupingBy(
                                                        AdhocSearchSingleRecord::getTimeRange,
                                                        Collectors.counting())))
                                        .build())));
        val sqlDetails = mergedAdhocResult.getData().stream()
                .map(AdhocSearchSingleRecord::getStatement)
                .map(SlowSqlService::simplifySql)
                .distinct()
                .map(sql -> SlowSqlDetailsPO.builder()
                        .sqlContent(sql)
                        .sqlCount(Integer.valueOf((int) groupedSqlMap.get(sql).get("count")).longValue())
                        .timeRange(serializeParams(groupedSqlMap.get(sql).get("timeRange")))
                        .build())
                .collect(Collectors.toList());
        // 保存details数据
        storeSlowSqlDetailsList(sqlDetails);
        // 获取昨天的慢sql数据, 和今天的比较, 计算diff
        val preDayCondition = new QueryWrapper<SlowSqlDetailsPO>().lambda()
                .apply("DATEDIFF(created, NOW())=-1");
        val sqlDetailsLastDay = slowSqlDetailsService.list(preDayCondition).stream()
                .map(SlowSqlDetailsPO::getSqlContent)
                .collect(Collectors.toList());
        val lastDaySqlSet = ImmutableSet.copyOf(sqlDetailsLastDay);
        val moreSqlData = sqlDetails.stream()
                .filter(dt -> !lastDaySqlSet.contains(dt.getSqlContent()))
                .collect(Collectors.toList());
        val diffSqlStr = evalOrNull(() -> OBJECT_MAPPER.writeValueAsString(moreSqlData));
        val adhocSearchSqlDiff = SlowSqlDiffPO.builder()
                .sqlDiff(diffSqlStr)
                .build();
        // 保存diff数据
        storeSlowSqlDiff(adhocSearchSqlDiff);
    }
}

package com.qunhe.instdeco.counterslow.controller;

import com.google.common.collect.ImmutableMap;
import com.qunhe.instdeco.counterslow.common.Apis;
import com.qunhe.instdeco.counterslow.common.ServiceResultPlain;
import com.qunhe.instdeco.counterslow.common.check.ParamAssertUtil;
import com.qunhe.instdeco.counterslow.service.SlowSqlService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * @author tumei
 */
@RestController
@Slf4j
@AllArgsConstructor
public class SlowQueryCheckController {
    private final SlowSqlService slowSqlService;

    /**
     * 获取每日慢sql总数 - 废弃
     *
     * @param duration Long
     * @return ServiceResultPlain
     */
    @RequestMapping(value = Apis.Adhoc.SQL_TOTAL, method = RequestMethod.GET)
    @Deprecated
    public ServiceResultPlain getSqlTotal(
            @RequestParam(required = false, defaultValue = "7") Long duration) {
        ParamAssertUtil.range(duration, "duration", 1, 60);
        return ServiceResultPlain.auto(() -> slowSqlService.searchAdhocSearchSqlTotal(duration));
    }

    /**
     * 获取每日各条慢sql的个数, 返回最近n天的数据, 同时用于top10和详情表格 - 废弃
     *
     * @return ServiceResultPlain
     */
    @RequestMapping(value = Apis.Adhoc.SQL_DETAILS, method = RequestMethod.GET)
    @Deprecated
    public ServiceResultPlain getSqlDetails() {
        return ServiceResultPlain.auto(slowSqlService::searchAdhocSearchSqlDetails);
    }

    /**
     * 获取每日慢sql的diff数, 返回最近n天的数据 - 废弃
     *
     * @return ServiceResultPlain
     */
    @RequestMapping(value = Apis.Adhoc.SQL_DIFF, method = RequestMethod.GET)
    @Deprecated
    public ServiceResultPlain getSqlDiff() {
        return ServiceResultPlain.auto(slowSqlService::searchAdhocSearchSqlDiff);
    }

    /**
     * 慢sql total details diff
     *
     * @param duration Long
     * @return ServiceResultPlain
     */
    @RequestMapping(value = Apis.Adhoc.SQL_ALL, method = RequestMethod.GET)
    public ServiceResultPlain getSlowSqlAll(@RequestParam(required = false, defaultValue = "7") Long duration) {
        ParamAssertUtil.range(duration, "duration", 1, 60);
        val totalFuture = CompletableFuture.supplyAsync(() -> slowSqlService.searchAdhocSearchSqlTotal(duration));
        val detailFuture = CompletableFuture.supplyAsync(slowSqlService::searchAdhocSearchSqlDetails);
        val diffFuture = CompletableFuture.supplyAsync(slowSqlService::searchAdhocSearchSqlDiff);
        return ServiceResultPlain.auto(() ->
                ImmutableMap.builder()
                        .put("total", totalFuture.get())
                        .put("detail", detailFuture.get())
                        .put("diff", diffFuture.get())
                        .build()
        );
    }

}

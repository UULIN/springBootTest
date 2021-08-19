package com.qunhe.instdeco.counterslow.service.crud;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qunhe.instdeco.counterslow.dao.SlowSqlDiffMapper;
import com.qunhe.instdeco.counterslow.model.po.SlowSqlDiffPO;
import com.qunhe.instdeco.counterslow.service.crud.iservice.ISlowSqlDiffService;
import org.springframework.stereotype.Repository;

/**
 * @author tumei
 */
@Repository
public class SlowSqlDiffServiceImpl extends ServiceImpl<SlowSqlDiffMapper, SlowSqlDiffPO> implements ISlowSqlDiffService {}


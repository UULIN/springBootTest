package com.qunhe.instdeco.counterslow.service.crud;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qunhe.instdeco.counterslow.dao.SlowSqlTotalMapper;
import com.qunhe.instdeco.counterslow.model.po.SlowSqlTotalPO;
import com.qunhe.instdeco.counterslow.service.crud.iservice.ISlowSqlTotalService;
import org.springframework.stereotype.Repository;

/**
 * @author tumei
 */
@Repository
public class SlowSqlTotalServiceImpl extends ServiceImpl<SlowSqlTotalMapper, SlowSqlTotalPO> implements ISlowSqlTotalService {}


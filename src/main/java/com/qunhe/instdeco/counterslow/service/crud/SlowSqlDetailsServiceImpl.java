package com.qunhe.instdeco.counterslow.service.crud;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qunhe.instdeco.counterslow.dao.SlowSqlDetailsMapper;
import com.qunhe.instdeco.counterslow.model.po.SlowSqlDetailsPO;
import com.qunhe.instdeco.counterslow.service.crud.iservice.ISlowSqlDetailsService;
import org.springframework.stereotype.Repository;

/**
 * @author tumei
 */
@Repository
public class SlowSqlDetailsServiceImpl extends ServiceImpl<SlowSqlDetailsMapper, SlowSqlDetailsPO> implements ISlowSqlDetailsService {}


package com.qunhe.instdeco.counterslow.aop.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

/**
 * @author tumei
 */
@ConditionalOnExpression("{'prod', 'prod_test'}.contains('${stage}')")
public @interface ProdEnv {}

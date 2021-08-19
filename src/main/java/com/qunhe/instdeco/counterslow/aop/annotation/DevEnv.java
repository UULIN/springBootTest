package com.qunhe.instdeco.counterslow.aop.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

/**
 * @author tumei
 */
@ConditionalOnExpression("'${stage:dev}' == 'dev'")
public @interface DevEnv {}

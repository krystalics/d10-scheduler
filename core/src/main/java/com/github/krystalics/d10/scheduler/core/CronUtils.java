package com.github.krystalics.d10.scheduler.core;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.value.SpecialChar;

import static com.cronutils.model.CronType.UNIX;
import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.between;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;

/**
 * @author linjiabao001
 * @date 2021/10/1
 * @description
 * UNIX:不包含秒和年的表达式
 * CRON4J:java版本的UNIX、表达式与之相同
 * QUARTZ:包含 秒 分 时 天 周 月 年等完整的时间周期概念
 * SPRING:也包含完整的周期概念，与quartz的书写方式可能略有差异
 *
 * 本系统的crontab表达式选择的是：QUARTZ
 * 注意在表示周的时候 1 = 周日sunday
 */
public class CronUtils {
    private void init() {

    }


}

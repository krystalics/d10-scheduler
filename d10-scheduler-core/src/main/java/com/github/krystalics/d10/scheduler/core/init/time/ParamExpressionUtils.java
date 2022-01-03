package com.github.krystalics.d10.scheduler.core.init.time;

import com.github.krystalics.d10.scheduler.core.init.time.utils.TimeExpressionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 参数相关
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class ParamExpressionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamExpressionUtils.class);

    private final static String TIME_VARIABLE_REGEX = "\\$\\{time:(.*?)\\}";

    /**
     * @param param
     * @param taskVersionTime
     * @return
     */
    public static String handleTimeExpression(String param, String taskVersionTime) {
        if (StringUtils.isBlank(param)) {
            return null;
        }
        Pattern pattern = Pattern.compile(TIME_VARIABLE_REGEX);

        Matcher matcher = pattern.matcher(param);

        while (matcher.find()) {
            String expression = matcher.group(0);
            String timeExpression = matcher.group(1);
            String calculateTime = TimeExpressionUtils.calculateTimeExpression(timeExpression, taskVersionTime);
            param = StringUtils.replace(param, expression, calculateTime);
            LOGGER.info("find time param:{},and calculateTime is {}", expression, calculateTime);
        }
        return param;
    }

}

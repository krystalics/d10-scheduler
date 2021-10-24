package com.github.krystalics.d10.scheduler.common.utils;

import com.github.krystalics.d10.scheduler.common.common.Constants;
import com.github.krystalics.d10.scheduler.common.common.Pair;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: cyp
 * @create: 2021-01-22
 **/
public class DispatchUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DispatchUtils.class);

    private static final String METHOD_GET = "GET";

    private static final String METHOD_POST = "POST";

    public static final OkHttpClient DEFAULT_OK_HTTP_CLIENT = OKHttpUtils.getInstance(2000, 200, 60 * 10);

    public static Pair<Boolean, String> dispatch(OkHttpClient okHttpClient, String domainUrl, HttpServletRequest httpServletRequest, String userToken, Map<String, String> uriMappingMap) {
        String method = httpServletRequest.getMethod();
        Map<String, String> requestParam = RequestUtils.getAllRequestParam(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        final Request.Builder requestBuilder = new Request.Builder();
        String requestUrl = domainUrl + requestURI;
        /**
         * 可以指定匹配url：/tool/diaodu/test -> /diaodu/test
         * todo 这种依靠urlMapping需要手动输入对应的映射关系，不够灵活
         */
        if (uriMappingMap != null && !uriMappingMap.isEmpty()) {
            String mappingUri = uriMappingMap.get(requestURI);
            if (StringUtils.isNotBlank(mappingUri)) {
                requestUrl = domainUrl + mappingUri;
            }
        }
        LOG.info("dispatch request url:{}, method:{}, token:{}, params:{}", requestUrl, method, userToken, requestParam);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(requestUrl).newBuilder();
        if (StringUtils.isNotEmpty(userToken)) {
            requestBuilder.addHeader(Constants.SCHEDULER_USER_TOKEN, userToken);
        }
        /**
         * GET请求
         */
        if (StringUtils.equalsIgnoreCase(method, METHOD_GET)) {
            if (requestParam != null && !requestParam.isEmpty()) {
                requestParam.forEach((k, v) -> {
                    urlBuilder.addQueryParameter(k, v);
                });
            }
            requestBuilder.get();
            /**
             * POST请求
             */
        } else if (StringUtils.equalsIgnoreCase(method, METHOD_POST)) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (requestParam != null && !requestParam.isEmpty()) {
                requestParam.forEach((k, v) -> {
                    formBodyBuilder.add(k, v);
                });
            }
            requestBuilder.post(formBodyBuilder.build());
        }

        requestBuilder.url(urlBuilder.build());
        Request request = requestBuilder.build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return Pair.of(true, response.body().string());
        } catch (Exception e) {
            LOG.error("dispatch request url:{}, method:{}, params:{} error", requestUrl, method, requestParam, e);
            return Pair.of(false, e.toString());
        }

    }

    public static Pair<Boolean, String> dispatch(String domainUrl, HttpServletRequest httpServletRequest, String userToken) {
        return dispatch(DEFAULT_OK_HTTP_CLIENT, domainUrl, httpServletRequest, userToken, Collections.emptyMap());
    }

    public static Pair<Boolean, String> dispatch(String domainUrl, HttpServletRequest httpServletRequest, String userToken, Map<String, String> uriMappingMap) {
        return dispatch(DEFAULT_OK_HTTP_CLIENT, domainUrl, httpServletRequest, userToken, uriMappingMap);
    }

    public static Pair<Boolean, String> dispatch(String domainUrl, HttpServletRequest httpServletRequest, String userToken, String prefix) {
        return dispatch(DEFAULT_OK_HTTP_CLIENT, domainUrl, httpServletRequest, userToken, prefix);
    }

    /**
     * 获取结果并返回给前台
     *
     * @param domainUrl
     * @param httpServletRequest
     * @param userToken
     * @return
     */
    public static String dispatchWithResult4Page(String domainUrl, HttpServletRequest httpServletRequest, String userToken) {
        Pair<Boolean, String> result = DispatchUtils.dispatch(domainUrl, httpServletRequest, userToken);
        if (!result.getL()) {
            return ResponseUtils.error(result.getR());
        }
        return result.getR();
    }

    /**
     * 获取结果并返回给前台
     *
     * @param domainUrl
     * @param httpServletRequest
     * @param userToken
     * @return
     */
    public static String dispatchWithResult4Page(String domainUrl, HttpServletRequest httpServletRequest, String userToken, String prefix) {
        Pair<Boolean, String> result = DispatchUtils.dispatch(domainUrl, httpServletRequest, userToken, prefix);
        if (!result.getL()) {
            return ResponseUtils.error(result.getR());
        }
        return result.getR();
    }

    public static Pair<Boolean, String> dispatch(OkHttpClient okHttpClient, String domainUrl, HttpServletRequest httpServletRequest, String userToken, String prefix) {
        String method = httpServletRequest.getMethod();
        Map<String, String> requestParam = RequestUtils.getAllRequestParam(httpServletRequest);

        String requestURI = httpServletRequest.getRequestURI();
        requestURI = realUrl(requestURI, prefix);

        final Request.Builder requestBuilder = new Request.Builder();
        String requestUrl = domainUrl + requestURI;

        LOG.info("dispatch request url:{}, method:{}, token:{}, params:{}", requestUrl, method, userToken, requestParam);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder();
        if (StringUtils.isNotEmpty(userToken)) {
            requestBuilder.addHeader(Constants.SCHEDULER_USER_TOKEN, userToken);
        }
        /**
         * GET请求
         */
        if (StringUtils.equalsIgnoreCase(method, METHOD_GET)) {
            if (requestParam != null && !requestParam.isEmpty()) {
                requestParam.forEach(urlBuilder::addQueryParameter);
            }
            requestBuilder.get();
            /**
             * POST请求
             */
        } else if (StringUtils.equalsIgnoreCase(method, METHOD_POST)) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (requestParam != null && !requestParam.isEmpty()) {
                requestParam.forEach(formBodyBuilder::add);
            }
            requestBuilder.post(formBodyBuilder.build());
        }

        requestBuilder.url(urlBuilder.build());
        Request request = requestBuilder.build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return Pair.of(true, response.body().string());
        } catch (Exception e) {
            LOG.error("dispatch request url:{}, method:{}, params:{} error", requestUrl, method, requestParam, e);
            return Pair.of(false, e.toString());
        }

    }

    /**
     * 通过修改url来做到将前缀去掉：例如 /monitor/dag/task-rely -> /dag/task-rely
     * monitor表示调用的服务前缀名、在base_app表中注册的名字
     *
     * @param url    web-base后端与前端交互的url
     * @param prefix 服务前缀
     * @return 去掉前缀后、转发到目标服务的真正url
     */
    public static String realUrl(String url, String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return url;
        }
        final String[] splits = StringUtils.split(url, "/");
        for (int i = 0; i < splits.length; i++) {
            if (prefix.equals(splits[i])) {
                splits[i] = "";
                break;
            }
        }
        return String.join("/", splits);
    }
}

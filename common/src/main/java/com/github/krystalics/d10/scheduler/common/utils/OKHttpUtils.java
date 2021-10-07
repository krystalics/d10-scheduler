package com.github.krystalics.d10.scheduler.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.krystalics.d10.scheduler.common.common.Pair;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: cyp
 * @create: 2021-01-21
 **/
public class OKHttpUtils {

	private static final Logger LOG = LoggerFactory.getLogger(OKHttpUtils.class);

	public static final OkHttpClient DEFAULT_OK_HTTP_CLIENT = OKHttpUtils.getInstance(2000, 200, 10 * 60);

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static OkHttpClient getInstance(int maxPool, int maxPoolPerHost, int maxIdleConnections, int keepAliveDuration, int connectionTimeout, int writeTimeout, int readTimeout) {
		ConnectionPool pool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
		OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(connectionTimeout, TimeUnit.SECONDS)
			.followRedirects(true)
			.readTimeout(readTimeout, TimeUnit.SECONDS)
			.retryOnConnectionFailure(false)
			.writeTimeout(writeTimeout, TimeUnit.SECONDS)
			.connectionPool(pool)
			.addInterceptor(chain -> {
				Request request = chain.request().newBuilder()
					.addHeader("Connection", "close").build();
				return chain.proceed(request);
			})
			.build();
		client.dispatcher().setMaxRequests(maxPool);
		client.dispatcher().setMaxRequestsPerHost(maxPoolPerHost);
		return client;
	}

	public static OkHttpClient getInstance(int maxPool, int maxPoolPerHost, int timeout) {
		return getInstance(maxPool, maxPoolPerHost, 10, 300, timeout, timeout, timeout);
	}

	public static Pair<Boolean, String> get(OkHttpClient okHttpClient, String url, Map<String, String> params) {
		final Request.Builder requestBuilder = new Request.Builder();
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		if (params != null && params.size() > 0) {
			params.forEach((k, v) -> {
				urlBuilder.addQueryParameter(k, v);
			});
		}
		requestBuilder.url(urlBuilder.build());
		Request request = requestBuilder.build();
		try {
			Response response = okHttpClient.newCall(request).execute();
			return Pair.of(true, response.body().string());
		} catch (Exception e) {
			LOG.error("request get error, url:{} param:{}", url, params, e);
			return Pair.of(false, e.toString());
		}

	}

	public static Pair<Boolean, String> get(String url, Map<String, String> params) {
		return get(DEFAULT_OK_HTTP_CLIENT, url, params);
	}

	public static Pair<Boolean, String> post(OkHttpClient okHttpClient, String url, Map<String, String> params) {

		final Request.Builder requestBuilder = new Request.Builder();
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		if (params != null && params.size() > 0) {
			params.forEach((k, v) -> {
				formBodyBuilder.add(k, v);
			});
		}
		requestBuilder.url(urlBuilder.build());
		requestBuilder.post(formBodyBuilder.build());
		Request request = requestBuilder.build();
		try {
			Response response = okHttpClient.newCall(request).execute();
			return Pair.of(true, response.body().string());
		} catch (Exception e) {
			LOG.error("request post error, url:{} param:{}", url, params, e);
			return Pair.of(false, e.toString());
		}
	}

	public static Pair<Boolean, String> post(String url, Map<String, String> params) {
		return post(DEFAULT_OK_HTTP_CLIENT, url, params);
	}

	/**
	 * 和调度系统交互专用的方法
	 * 因为调度接口接受的都是这种格式的参数  produces = "application/json;charset=UTF-8"
	 *
	 * @param url  URL
	 * @param json 转化为 json 字符串之后的参数
	 * @return 请求结果
	 */
	public static Pair<Boolean, String> postJson(String url, String json) {

		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
			.url(url)
			.post(body)
			.build();
		try (Response response = DEFAULT_OK_HTTP_CLIENT.newCall(request).execute()) {
			return Pair.of(true, response.body().string());
		} catch (IOException e) {
			LOG.error("request post error, url={} param={}", url, json, e);
			return Pair.of(false, e.toString());
		}
	}

	/**
	 * @param url  目标
	 * @param json 参数
	 * @param code 业务的正确码
	 * @return
	 */
	public static Pair<Boolean, String> postJson(String url, String json, String code) {

		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
			.url(url)
			.post(body)
			.build();
		try (Response response = DEFAULT_OK_HTTP_CLIENT.newCall(request).execute()) {
			String bodyStr = response.body().string();
			JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(bodyStr);
			if (!code.equals(jsonObject.get("code"))) {
				String errMsg = String.format("发送失败: code[%s], msg: %s", jsonObject.get("code"), jsonObject.get("msg"));
				LOG.error(errMsg);
				return Pair.of(false, errMsg);
			}
			return Pair.of(true, bodyStr);
		} catch (IOException e) {
			LOG.error("request post error, url={} param={}", url, json, e);
			return Pair.of(false, e.toString());
		}
	}
}

package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.Response;
import com.example.entity.RestBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * HTTP 请求工具类，基于 Java 11+ HttpClient 实现
 */
@Component
@Slf4j
public class  netUtils<T> {
    private static final int DEFAULT_TIMEOUT = 30; // 默认超时时间（秒）
    private static final HttpClient client;

    static {
        // 初始化 HttpClient，支持连接池和超时配置
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT))
                .build();
    }

    // 私有化构造函数，防止实例化
    private netUtils() {
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求URL
     * @return 响应字符串
     */
    public static Response get(String url) throws IOException, InterruptedException {
        return get(url, new HashMap<>(), new HashMap<>());
    }

    /**
     * 发送带参数的 GET 请求
     *
     * @param url    请求URL
     * @param params 查询参数
     * @return 响应字符串
     */
    public static Response get(String url, Map<String, String> params) throws IOException, InterruptedException {
        return get(url, params, new HashMap<>());
    }

    /**
     * 发送带参数和请求头的 GET 请求
     *
     * @param url     请求URL
     * @param params  查询参数
     * @param headers 请求头
     * @return 响应字符串
     */
    public static Response get(String url, Map<String, String> params, Map<String, String> headers) {
        String fullUrl = buildUrlWithParams(url, params);
        HttpRequest request = null;
        try {
            //封装 HttpRequest 请求
            request = buildRequest(fullUrl, "GET", null, headers);
        } catch (Exception e) {
            //封装错误提示 请求路径错误,并返回 403
            log.error("请求路径:{}", url + "错误");
            return new Response(403, null, null, null);
        }
        return sendSync(request);
    }

    /**
     * 发送 POST 请求（JSON 格式）
     *
     * @param url      请求URL
     * @param jsonBody JSON 请求体
     * @return 响应字符串
     */
    public static Response postJson(String url, String jsonBody) throws IOException, InterruptedException {
        return postJson(url, jsonBody, new HashMap<>());
    }

    /**
     * 发送带请求头的 POST 请求（JSON 格式）
     *
     * @param url      请求URL
     * @param jsonBody JSON 请求体
     * @param headers  请求头
     * @return 响应字符串
     */
    public static Response postJson(String url, String jsonBody, Map<String, String> headers) throws IOException, InterruptedException {
        headers.put("Content-Type", "application/json");
        HttpRequest request = buildRequest(url, "POST", jsonBody, headers);
        return sendSync(request);
    }

    /**
     * 发送 POST 请求（表单格式）
     *
     * @param url      请求URL
     * @param formData 表单数据
     * @return 响应字符串
     */
    public static Response postForm(String url, Map<String, String> formData) throws IOException, InterruptedException {
        return postForm(url, formData, new HashMap<>());
    }

    /**
     * 发送带请求头的 POST 请求（表单格式）
     *
     * @param url      请求URL
     * @param formData 表单数据
     * @param headers  请求头
     * @return 响应字符串
     */
    public static Response postForm(String url, Map<String, String> formData, Map<String, String> headers) throws IOException, InterruptedException {
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        String formBody = buildFormData(formData);
        HttpRequest request = buildRequest(url, "POST", formBody, headers);
        return sendSync(request);
    }

    // 其他 HTTP 方法（PUT、DELETE 等）的实现类似，这里省略...

    /**
     * 异步发送 HTTP 请求
     *
     * @param request HttpRequest 对象
     * @return 包含响应结果的 CompletableFuture
     */
    public static CompletableFuture<String> sendAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(ex -> {
                    throw new RuntimeException("HTTP 请求失败: " + ex.getMessage(), ex);
                });
    }

    // 构建带查询参数的 URL
    private static String buildUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        return url + (url.contains("?") ? "&" : "?") + queryString;
    }

    // 构建表单数据
    private static String buildFormData(Map<String, String> formData) {
        if (formData == null || formData.isEmpty()) {
            return "";
        }
        return formData.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    // 构建 HttpRequest 对象
    private static HttpRequest buildRequest(String url, String method, String body, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT));

        // 添加请求头
        headers.forEach(builder::header);

        // 设置请求方法和请求体
        HttpRequest.BodyPublisher bodyPublisher = Optional.ofNullable(body)
                .map(HttpRequest.BodyPublishers::ofString)
                .orElse(HttpRequest.BodyPublishers.noBody());

        switch (method.toUpperCase()) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(bodyPublisher);
                break;
            case "PUT":
                builder.PUT(bodyPublisher);
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("不支持的 HTTP 方法: " + method);
        }

        return builder.build();
    }

    // 同步发送请求并处理响应
    private static Response sendSync(HttpRequest request) {
        HttpResponse<String> response;
        //发送请求,判断是否成功
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("请求! 路径:{}", request.uri() + "--失败");
            return new Response(403, null, null, null);
        }


        //请求成功
        log.info("请求! 路径:{}", request.uri() + "--成功");
        String body = response.body(); //获取请求成功后的返回信息
        RestBean restBean = JSONObject.parseObject(body, RestBean.class); //将响应值进行序列化,成RestBean


        //判断请求响应是否成功
        if (restBean.code() >= 200 && restBean.code() < 300) {
            log.info("请求响应成功");
            return new Response(200,"注册成功", null, restBean.data());
        }
        log.info("请求响应成功,请检查请求内容或者请求地址");
        return new Response(401, null, null, null);
    }

}

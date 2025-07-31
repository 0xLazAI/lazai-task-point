package com.lazai.core.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.exception.DomainException;
import com.lazai.utils.TraceIdUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class OpenProxyClient {

    @Autowired
    private Environment env;

    @Autowired
    private OkHttpClient okHttpClient;

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");
    private static final Logger RESULT_LOGGER = LoggerFactory.getLogger("RESULT_LOG");

    @Value("${open.proxy.baseUrl}")
    private String baseUrl;

    public JSONObject post(String uri, JSONObject body, Map<String, String> headers){
        //build header
        Headers.Builder headerBuilder = new Headers.Builder();
        if(CollectionUtils.isEmpty(headers)){
            headers = new HashMap<>();
        }
        headers.put("traceId", TraceIdUtil.getTraceId());
        headers.put("x-server-call", "true");
        Set<String> headerKeys = headers.keySet();
        for(String headerKey:headerKeys){
            headerBuilder.add(headerKey, headers.get(headerKey));
        }
        Headers okHeaders = headerBuilder.build();

        if(body == null){
            body = new JSONObject();
        }
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody okBody = RequestBody.create(JSON.toJSONString(body), mediaType);

        Request request = new Request.Builder()
                .url(baseUrl + uri)
                .headers(okHeaders)
                .method("POST", okBody)
                .build();
        Response response = null;
        JSONObject result = new JSONObject();
        try{
            response = okHttpClient.newCall(request).execute();
        }
        catch (Throwable e){
            ERROR_LOGGER.error("open proxy net error",e);
            throw new DomainException("open proxy net error", 500);
        }
        String responseStr = "";
        try{
            responseStr = response.body().string();
        }catch (Throwable t){
            throw new DomainException("IO exception", 500);
        }
        if(response.code() != 200){
            ERROR_LOGGER.error("open proxy http error, response:" + responseStr);
            throw new DomainException("open proxy http error", 500);
        }

        RESULT_LOGGER.info("open proxy http response:" + responseStr);
        JSONObject responseObj = JSON.parseObject(responseStr);
        return responseObj;
    }

}

package com.lazai.controller;

import com.alibaba.fastjson.JSONObject;
import com.lazai.annotation.ResultLog;
import com.lazai.core.common.CommonProxyClient;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.MethodTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    private CommonProxyClient commonProxyClient;

    @PostMapping("/postAgent")
    @ResultLog(name = "ProxyController.postAgent", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> postAgent(@RequestBody JSONObject bizRequest, @RequestParam String url, HttpServletRequest request){
        String contentLength = request.getHeader("Content-Length");
        return JsonApiResponse.success( commonProxyClient.post(url,bizRequest, null));
    }

}

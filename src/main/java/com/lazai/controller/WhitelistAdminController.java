package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.WhitelistAdminBizService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/whitelist")
public class WhitelistAdminController {

    @Autowired
    private WhitelistAdminBizService whitelistAdminBizService;

    @PostMapping("/dataStatistics")
    @ResultLog(name = "WhitelistAdminController.dataStatistics", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> dataStatistics(HttpServletRequest request){
        String token = request.getHeader("token");
        if(!token.equals("24627")){
            throw new DomainException("no access", 403);
        }
        return JsonApiResponse.success(whitelistAdminBizService.getAll());
    }

}

package com.lazai.controller;

import com.lazai.annotation.InternalServerCall;
import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.LazbubuWhitelistBizService;
import com.lazai.biz.service.TaskService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.User;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.*;
import com.lazai.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private LazbubuWhitelistBizService lazbubuWhitelistBizService;

    @PostMapping("/createTask")
    @ResultLog(name = "TaskController.createTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        taskCreateRequest.setOperator(operator);
        taskCreateRequest.setInnerUserId(loginUser.getId()+"");
        return JsonApiResponse.success(taskService.createTask(taskCreateRequest));
    }

    @PostMapping("/triggerTask")
    @ResultLog(name = "TaskController.triggerTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> triggerTask(@RequestBody TriggerTaskRequest triggerTaskRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        triggerTaskRequest.setOperator(operator);
        return JsonApiResponse.success(taskService.triggerTask(triggerTaskRequest));
    }

    @PostMapping("/createAndTriggerTask")
    @ResultLog(name = "TaskController.createAndTriggerTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createAndTriggerTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        taskCreateRequest.setInnerUserId(loginUser.getId()+"");
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        taskCreateRequest.setOperator(operator);
        taskService.createAndTriggerTask(taskCreateRequest);
        return JsonApiResponse.success(true);
    }

    @PostMapping("/claim")
    @ResultLog(name = "TaskController.claim", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> claim(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        taskCreateRequest.setInnerUserId(loginUser.getId()+"");
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        taskCreateRequest.setOperator(operator);
        taskService.claimTask(taskCreateRequest);
        return JsonApiResponse.success(true);
    }

    @InternalServerCall
    @PostMapping("/claimServer")
    @ResultLog(name = "TaskController.claimServer", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> claimServer(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        taskService.claimTask(taskCreateRequest);
        return JsonApiResponse.success(true);
    }

    @GetMapping("/userTaskRecords")
    @ResultLog(name = "TaskController.userTaskRecords", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> userTaskRecords(@ModelAttribute TaskQueryRequest bizRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        bizRequest.setInnerPlatformUserId(loginUser.getId()+"");
        return JsonApiResponse.success(taskService.userTaskRecords(bizRequest));
    }

    @GetMapping("/userTaskTemplatesUse")
    @ResultLog(name = "TaskController.userTaskTemplatesUse", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> userTaskTemplatesUse(@ModelAttribute TaskQueryRequest bizRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        bizRequest.setInnerPlatformUserId(loginUser.getId()+"");
        return JsonApiResponse.success(taskService.userTaskTemplatesUse(bizRequest));
    }

    @GetMapping("/isUserInWhiteList")
    @ResultLog(name = "TaskController.isUserInWhiteList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> isUserInWhiteList(@RequestParam String userId, HttpServletRequest request){
        return JsonApiResponse.success(lazbubuWhitelistBizService.isUserInWhiteList(userId));
    }

    @GetMapping("/friendTasksRank")
    @ResultLog(name = "TaskController.friendTasksRank", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> friendTasksRank(HttpServletRequest request){
        return JsonApiResponse.success(lazbubuWhitelistBizService.friendTasksRank());
    }


    @GetMapping("/friendTaskStatusList")
    @ResultLog(name = "TaskController.friendTaskStatusList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> friendTaskStatusList(@ModelAttribute FriendsTaskStatusPageQueryRequest bizRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        bizRequest.setUserId(loginUser.getId()+"");
        return JsonApiResponse.success(lazbubuWhitelistBizService.friendTaskStatusList(bizRequest));
    }

    @GetMapping("/getQualifiedFriends")
    @ResultLog(name = "TaskController.getQualifiedFriends", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> getQualifiedFriends(@ModelAttribute QualifiedFriendsQueryRequest bizRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        bizRequest.setUserId(loginUser.getId()+"");
        return JsonApiResponse.success(lazbubuWhitelistBizService.getQualifiedFriends(bizRequest));
    }
}

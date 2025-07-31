package com.lazai.graph;

import com.alibaba.fastjson.JSONObject;
import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.LazbubuWhitelistBizService;
import com.lazai.core.common.GraphQLResponse;
import com.lazai.entity.User;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.entity.vo.FriendTaskStatusVO;
import com.lazai.entity.vo.InvitingUserQualifiedResponseVO;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.FriendsTaskStatusPageQueryRequest;
import com.lazai.request.QualifiedFriendsQueryRequest;
import com.lazai.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class LazbubuWhitelistEndPoint {

    @Autowired
    private LazbubuWhitelistBizService lazbubuWhitelistBizService;

    @ResultLog(name = "LazbubuWhitelistEndPoint.friendTaskStatusList", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("friendTaskStatusList")
    public GraphQLResponse<CommonPageResult<FriendTaskStatusVO>> friendTaskStatusList(@Argument("bizReq") FriendsTaskStatusPageQueryRequest request){
        User loginUser = JWTUtils.getUser();
        request.setUserId(loginUser.getId()+"");
        return GraphQLResponse.success(lazbubuWhitelistBizService.friendTaskStatusList(request));
    }

    @ResultLog(name = "LazbubuWhitelistEndPoint.getQualifiedFriends", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getQualifiedFriends")
    public GraphQLResponse<InvitingUserQualifiedResponseVO> getQualifiedFriends(@Argument("bizReq") QualifiedFriendsQueryRequest request){
        User loginUser = JWTUtils.getUser();
        request.setUserId(loginUser.getId()+"");
        return GraphQLResponse.success(lazbubuWhitelistBizService.getQualifiedFriends(request));
    }

    @ResultLog(name = "LazbubuWhitelistEndPoint.getRank", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getRank")
    public GraphQLResponse<List<JSONObject>> getRank(){
        return GraphQLResponse.success(lazbubuWhitelistBizService.friendTasksRank());
    }

    @ResultLog(name = "LazbubuWhitelistEndPoint.isUserInWhiteList", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("isUserInWhiteList")
    public GraphQLResponse<Boolean> isUserInWhiteList(@Argument String userId){

        return GraphQLResponse.success(lazbubuWhitelistBizService.isUserInWhiteList(userId));
    }

}

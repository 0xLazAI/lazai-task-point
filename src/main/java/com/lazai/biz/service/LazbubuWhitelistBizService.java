package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.entity.vo.FriendTaskStatusVO;
import com.lazai.entity.vo.InvitingUserQualifiedResponseVO;
import com.lazai.request.FriendsTaskStatusPageQueryRequest;
import com.lazai.request.QualifiedFriendsQueryRequest;

import java.util.List;

public interface LazbubuWhitelistBizService {

    InvitingUserQualifiedResponseVO getQualifiedFriends(QualifiedFriendsQueryRequest request);

    CommonPageResult<FriendTaskStatusVO> friendTaskStatusList(FriendsTaskStatusPageQueryRequest request);

    List<JSONObject> friendTasksRank();

    Boolean isUserInWhiteList(String userId);

}

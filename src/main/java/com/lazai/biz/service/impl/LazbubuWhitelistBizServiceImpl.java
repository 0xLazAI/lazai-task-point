package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.lazai.biz.service.LazbubuWhitelistBizService;
import com.lazai.entity.*;
import com.lazai.entity.dto.*;
import com.lazai.entity.vo.FriendTaskStatusVO;
import com.lazai.entity.vo.InvitingUserQualifiedResponseVO;
import com.lazai.enums.TaskStatusEnum;
import com.lazai.repostories.*;
import com.lazai.request.FriendsTaskStatusPageQueryRequest;
import com.lazai.request.QualifiedFriendsQueryRequest;
import com.lazai.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LazbubuWhitelistBizServiceImpl implements LazbubuWhitelistBizService {

    @Autowired
    private LazbubuWhitelistRepository lazbubuWhitelistRepository;

    @Autowired
    private UserInvitesRepository userInvitesRepository;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private ScoreBalanceRepository scoreBalanceRepository;

    public static final String WHITE_LIST_RANK_SINGLE = "white_list_rank_single_";

    public InvitingUserQualifiedResponseVO getQualifiedFriends(QualifiedFriendsQueryRequest request){
        InvitingUserQualifiedResponseVO result = new InvitingUserQualifiedResponseVO();
        List<UserInvites> userInvites = userInvitesRepository.getByInvitingUserId(request.getUserId());
        result.setTotalCount(userInvites.size());
        result.setQualifiedCount(0);
        if(!CollectionUtils.isEmpty(userInvites)){
            LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
            lazbubuWhitelistQueryParam.setUserIds(userInvites.stream().map(UserInvites::getInvitedUser).toList());
            lazbubuWhitelistQueryParam.setStatusList(Collections.singletonList("ACTIVE"));
            List<LazbubuDataWhitelist> lazbubuDataWhitelistList = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);
            result.setQualifiedCount(lazbubuDataWhitelistList.size());
        }
        ScoreBalanceQueryParam scoreBalanceQueryParam = new ScoreBalanceQueryParam();
        scoreBalanceQueryParam.setUserId(request.getUserId());
        List<String> scoreTypes = new ArrayList<>();
        scoreTypes.add("joinLazbubuWhitelist");
        scoreTypes.add("invitedUserReward");
        scoreBalanceQueryParam.setScoreTypes(scoreTypes);
        List<ScoreBalance> scoreBalanceList = scoreBalanceRepository.queryList(scoreBalanceQueryParam);
        result.setTotalScore(BigInteger.ZERO);
        if(!CollectionUtils.isEmpty(scoreBalanceList)){
            result.setTotalScore(scoreBalanceList.stream().map(ScoreBalance::getScore).filter(Objects::nonNull).reduce(BigInteger.ZERO, BigInteger::add));
        }
        return result;
    }

    public List<JSONObject> friendTasksRank(){
        List<JSONObject> result = new ArrayList<>();
        Collection<ScoredEntry<String>> scoredEntryCollection = RedisUtils.ZREVRANGE_REVERSED(AddScoreActionAndWhitelistActiveHandler.WHITE_LIST_KEY,0,10);
        if(!CollectionUtils.isEmpty(scoredEntryCollection)){
            for(ScoredEntry<String> single: scoredEntryCollection){
                Double cnt = single.getScore();
                String jsonRaw = single.getValue();
                JSONObject userJson = JSON.parseObject(jsonRaw);
                JSONObject item = new JSONObject();
                item.put("userId", userJson.getString("id"));
                item.put("ethAddress", userJson.getString("ethAddress"));
                item.put("cnt", cnt.intValue());
                result.add(item);
            }
        }
        if(StringUtils.isNotBlank(RedisUtils.get(WHITE_LIST_RANK_SINGLE))){
            JSONObject tmp = JSON.parseObject(RedisUtils.get(WHITE_LIST_RANK_SINGLE));
            result.add(tmp);
            result = result.stream().sorted(Comparator.comparingInt(o -> ((JSONObject) o).getInteger("cnt")).reversed()).toList();
        }
        if(result.size() > 10){
            result = result.subList(0,10);
        }
        return result;
    }

    public Boolean isUserInWhiteList(String userId){
        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
        lazbubuWhitelistQueryParam.setUserId(userId);
        lazbubuWhitelistQueryParam.setStatusList(Collections.singletonList("ACTIVE"));
        List<LazbubuDataWhitelist> lazbubuDataWhitelistList = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);
        return !CollectionUtils.isEmpty(lazbubuDataWhitelistList);
    }

    public CommonPageResult<FriendTaskStatusVO> friendTaskStatusList(FriendsTaskStatusPageQueryRequest request){
        CommonPageResult<FriendTaskStatusVO> result = new CommonPageResult<>();
        CommonPageDTO commonPageDTO = new CommonPageDTO();
        commonPageDTO.setPageNum(request.getPageSize());
        commonPageDTO.setPageSize(request.getPageSize());
        commonPageDTO.setTotalCount(0);
        result.setPagination(commonPageDTO);
        Integer offset = (request.getPage()-1) * request.getPageSize();
        List<UserInvites> userInvites = userInvitesRepository.getByInvitingUserIdPagination(request.getUserId(), offset, request.getPageSize());
        Integer totalCnt = userInvitesRepository.getByInvitingUserIdCnt(request.getUserId());
        commonPageDTO.setTotalCount(totalCnt);
        List<FriendTaskStatusVO> items = new ArrayList<>();

        if(!CollectionUtils.isEmpty(userInvites)){
            TaskTemplate taskTemplate = taskTemplateRepository.selectByCode("invitedFriendsComplete");
            JSONObject taskTemplateContent = JSON.parseObject(taskTemplate.getContent());
            JSONObject taskTemplateContext = taskTemplateContent.getJSONObject("context");
            List<String> userIds = userInvites.stream().map(UserInvites::getInvitedUser).toList();
            List<BigInteger> userIdsInt = userInvites.stream().map(a-> new BigInteger(a.getInvitedUser())).toList();
            LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
            lazbubuWhitelistQueryParam.setUserIds(userIds);
            lazbubuWhitelistQueryParam.setStatusList(Collections.singletonList("ACTIVE"));
            List<LazbubuDataWhitelist> lazbubuDataWhitelistList = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);

            UserQueryListParam userQueryListParam = new UserQueryListParam();
            userQueryListParam.setUserIds(userIdsInt);
            List<User> userList = userRepository.queryList(userQueryListParam);
            Map<String, User> userMap = userList.stream().collect(Collectors.toMap(a->a.getId() + "", b->b));
            Map<String, LazbubuDataWhitelist> lazbubuDataWhitelistMap = lazbubuDataWhitelistList.stream().collect(Collectors.toMap(LazbubuDataWhitelist::getUserId, b->b));
            TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
            taskRecordQueryParam.setInnerUsers(userIds);
            List<String> templateIdsTmp = taskTemplateContext.getJSONArray("dependencyTasks");
            templateIdsTmp = templateIdsTmp.stream().filter(a->!a.equals("tgGroupJoin")).toList();
            taskRecordQueryParam.setTaskTemplateIds(templateIdsTmp);
            taskRecordQueryParam.setStatusList(Collections.singletonList(TaskStatusEnum.FINISH.value()));

            List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
            Map<String, Boolean> friendCompleteMap = new HashMap<>();
            Map<String, Integer> friendCompleteCntMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(taskRecords)){
                for(TaskRecord taskRecord:taskRecords){
                    if(!friendCompleteCntMap.containsKey(taskRecord.getInnerUser())){
                        friendCompleteCntMap.put(taskRecord.getInnerUser(),0);
                    }
                    friendCompleteCntMap.put(taskRecord.getInnerUser(), friendCompleteCntMap.get(taskRecord.getInnerUser()) + 1);
                    if(friendCompleteCntMap.get(taskRecord.getInnerUser()) >= templateIdsTmp.size()){
                        friendCompleteMap.put(taskRecord.getInnerUser(), true);
                    }
                }
            }
            for(UserInvites userInvite:userInvites){
                FriendTaskStatusVO friendTaskStatusVO = new FriendTaskStatusVO();
                if(userMap.containsKey(userInvite.getInvitedUser())){
                    friendTaskStatusVO.setAddress(userMap.get(userInvite.getInvitedUser()).getEthAddress());
                    friendTaskStatusVO.setStatus("Not Finished");
                    friendTaskStatusVO.setInWhiteList(false);
                    if(lazbubuDataWhitelistMap.containsKey(userInvite.getInvitedUser())){
                        friendTaskStatusVO.setInWhiteList(true);
                    }
                    if(friendCompleteMap.containsKey(userInvite.getInvitedUser())){
                        friendTaskStatusVO.setStatus("Completed");
                    }
                    items.add(friendTaskStatusVO);
                }

            }
        }
        result.setItems(items);
        return result;
    }



}

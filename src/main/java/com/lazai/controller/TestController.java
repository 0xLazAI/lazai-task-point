package com.lazai.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.annotation.InternalServerCall;
import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.*;
import com.lazai.biz.service.impl.LazbubuWhitelistBizServiceImpl;
import com.lazai.core.common.JsonApiResponse;

import com.lazai.core.common.RoleDTO;
import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.User;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.exception.DomainException;
import com.lazai.repostories.LazbubuWhitelistRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.request.FriendsTaskStatusPageQueryRequest;
import com.lazai.request.QualifiedFriendsQueryRequest;
import com.lazai.request.ScoreAddRequest;
import com.lazai.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import static com.lazai.biz.service.impl.AddScoreActionAndWhitelistActiveHandler.WHITE_LIST_KEY;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private TwitterService twitterService;

    @Autowired
    private LazbubuWhitelistSchedulerService lazbubuWhitelistSchedulerService;

    @Autowired
    private LazbubuWhitelistBizService lazbubuWhitelistBizService;

    @Autowired
    private LazbubuWhitelistRepository lazbubuWhitelistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScoreBalanceService scoreBalanceService;

    @GetMapping("/hello")
    @ResultLog(name = "TestController.sayHello", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> sayHello(@RequestParam String id, HttpServletRequest request){
        return JsonApiResponse.success(userService.getById(id));
    }

    @GetMapping("/test2")
    @ResultLog(name = "TestController.test2", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> test2(HttpServletRequest request){
        twitterService.checkFollowers();
        return JsonApiResponse.success(twitterService.getUserByUsername("elonmusk"));
    }


    @GetMapping("/fixUser")
    @ResultLog(name = "TestController.fixUser", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> fixUser(HttpServletRequest request){
        userService.fixUserEthAddressToLowerCase();
        return JsonApiResponse.success(true);
    }

    @GetMapping("/testWhitelistSceduler")
    @ResultLog(name = "TestController.testWhitelistSceduler", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> testWhitelistSceduler(HttpServletRequest request){
        lazbubuWhitelistSchedulerService.checkLazbubuWhitelist();
        return JsonApiResponse.success(true);
    }

    @GetMapping("/syncRankList")
    @ResultLog(name = "TestController.syncRankList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> syncRankList(HttpServletRequest request){
        lazbubuWhitelistSchedulerService.syncFinishCnt();
        return JsonApiResponse.success(true);
    }

    @PostMapping("/addSingleRankList")
    @ResultLog(name = "TestController.addSingleRankList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> addSingleRankList(@RequestBody JSONObject req, HttpServletRequest request){
        if(!"910904".equals(req.getString("pwd"))){
            throw new DomainException("can not do this", 403);
        }
        req.remove("pwd");
        RedisUtils.set(LazbubuWhitelistBizServiceImpl.WHITE_LIST_RANK_SINGLE, JSON.toJSONString(req));
        return JsonApiResponse.success(true);
    }

    @PostMapping("/removeSingleRankList")
    @ResultLog(name = "TestController.removeSingleRankList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> removeSingleRankList(@RequestBody JSONObject req, HttpServletRequest request){
        if(!"910904".equals(req.getString("pwd"))){
            throw new DomainException("can not do this", 403);
        }
        RedisUtils.delKys(LazbubuWhitelistBizServiceImpl.WHITE_LIST_RANK_SINGLE);
        return JsonApiResponse.success(true);
    }


    @GetMapping("/friendTaskStatusList")
    @ResultLog(name = "TestController.friendTaskStatusList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> friendTaskStatusList(HttpServletRequest request){
        FriendsTaskStatusPageQueryRequest friendsTaskStatusPageQueryRequest = new FriendsTaskStatusPageQueryRequest();
        friendsTaskStatusPageQueryRequest.setPage(1);
        friendsTaskStatusPageQueryRequest.setPageSize(10);
        friendsTaskStatusPageQueryRequest.setUserId("34");
        return JsonApiResponse.success(lazbubuWhitelistBizService.friendTaskStatusList(friendsTaskStatusPageQueryRequest));
    }

    @GetMapping("/getQualifiedFriends")
    @ResultLog(name = "TestController.getQualifiedFriends", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> getQualifiedFriends(HttpServletRequest request){
        QualifiedFriendsQueryRequest qualifiedFriendsQueryRequest = new QualifiedFriendsQueryRequest();
        qualifiedFriendsQueryRequest.setUserId("34");
        return JsonApiResponse.success(lazbubuWhitelistBizService.getQualifiedFriends(qualifiedFriendsQueryRequest));
    }

    @GetMapping("/testRank")
    @ResultLog(name = "TestController.testRank", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> testRank(HttpServletRequest request){
        return JsonApiResponse.success(lazbubuWhitelistBizService.friendTasksRank());
    }

    @GetMapping("/clearRank")
    @InternalServerCall
    @ResultLog(name = "TestController.clearRank", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> clearRank(HttpServletRequest request){
        RedisUtils.delKys(WHITE_LIST_KEY);
        return JsonApiResponse.success(true);
    }

    @InternalServerCall
    @PostMapping("/upload")
    public JsonApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个sheet
            int i = 0;
            for (Row row : sheet) {
                if(i != 0){
                    Cell cell = row.getCell(0); // 获取第一列
                    String ethAddress = cell.getStringCellValue(); // 根据类型取值
                    if(StringUtils.isNotBlank(ethAddress)){
                        ethAddress = ethAddress.toLowerCase();
                        User userExists = userRepository.findByEthAddress(ethAddress, false);
                        String userId = null;
                        if(userExists == null){
                            userExists = new User();
                            userExists.setEthAddress(ethAddress);
                            userExists.setName(ethAddress);
                            userId = userRepository.insert(userExists);
                        }else {
                            userId = userExists.getId() + "";
                        }
                        LazbubuWhitelistQueryParam lazbubuWhitelistQueryParam = new LazbubuWhitelistQueryParam();
                        lazbubuWhitelistQueryParam.setUserId(userId);
                        List<LazbubuDataWhitelist> whitelists = lazbubuWhitelistRepository.queryList(lazbubuWhitelistQueryParam);
                        if(CollectionUtils.isEmpty(whitelists)){
                            LazbubuDataWhitelist lazbubuDataWhitelist = new LazbubuDataWhitelist();
                            lazbubuDataWhitelist.setUserId(userId);
                            lazbubuDataWhitelist.setStatus("ACTIVE");
                            lazbubuDataWhitelist.setSource("white_list_manual");
                            lazbubuDataWhitelist.setBizType("manual");
                            lazbubuWhitelistRepository.insert(lazbubuDataWhitelist);
                            ScoreAddRequest scoreAddRequest = new ScoreAddRequest();
                            scoreAddRequest.setAppToken("lazpad");
                            scoreAddRequest.setScoreType("manual");
                            scoreAddRequest.setUserId(userId);
                            scoreAddRequest.setScore(BigInteger.valueOf(500));
                            scoreAddRequest.setDirection("ADD");
                            scoreAddRequest.setOperator(RoleDTO.getSystemOperator());
                            scoreBalanceService.addUserScore(scoreAddRequest);
                        }
                    }
                }
                i++;
            }
            return JsonApiResponse.success("true");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonApiResponse.failed("false", 500, "failed");
        }
    }

}

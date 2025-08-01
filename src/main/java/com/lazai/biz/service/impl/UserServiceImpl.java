package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TaskService;
import com.lazai.biz.service.UserService;
import com.lazai.core.common.OpenProxyClient;
import com.lazai.entity.ScoreBalance;
import com.lazai.entity.User;
import com.lazai.entity.UserInvites;
import com.lazai.entity.UserScore;
import com.lazai.entity.dto.ScoreBalanceQueryParam;
import com.lazai.entity.vo.UserVO;
import com.lazai.exception.DomainException;
import com.lazai.repostories.ScoreBalanceRepository;
import com.lazai.repostories.UserInvitesRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.repostories.UserScoreRepository;
import com.lazai.request.*;
import com.lazai.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @see UserService
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplateCommon;

    @Autowired
    private UserInvitesRepository userInvitesRepository;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScoreBalanceRepository scoreBalanceRepository;

    @Autowired
    private OpenProxyClient openProxyClient;

    @Autowired
    private Environment env;

    /**
     * @see UserService#getById
     */
    @Override
    public UserVO getById(String id){
        User userInfo =  userRepository.findById(id, false);
        if(userInfo == null){
            throw new DomainException("no user",404);
        }
        UserScore userScore = userScoreRepository.getByUserId(new BigInteger(id));
        String inviteCode = getInvitedCode(id);
        UserVO userVO = toUserVO(userInfo,userScore);
        userVO.setInvitedCode(inviteCode);
        List<UserInvites> userInvites = userInvitesRepository.getByInvitingUserId(id);
        userVO.setInvitesCount(userInvites.size());
        ScoreBalanceQueryParam scoreBalanceQueryParam = new ScoreBalanceQueryParam();
        scoreBalanceQueryParam.setUserId(id);
        List<String> scoreTypes = new ArrayList<>();
        scoreTypes.add("inviteUser");
        scoreBalanceQueryParam.setScoreTypes(scoreTypes);
        List<ScoreBalance> scoreBalances = scoreBalanceRepository.queryList(scoreBalanceQueryParam);
        BigInteger inviteScore = BigInteger.ZERO;
        if(!CollectionUtils.isEmpty(scoreBalances)){
            for(ScoreBalance scoreBalance:scoreBalances){
                inviteScore = inviteScore.add(scoreBalance.getScore());
            }
        }
        userVO.setInviteScore(inviteScore + "");
        return userVO;
    }

    private String getInvitedCode(String userId){
        if(StringUtils.isNotBlank(RedisUtils.get("user_invited_code_" + userId))){
            return RedisUtils.get("user_invited_code_" + userId);
        }
        String inviteCodeRaw = RandomStringUtils.one(5, RandomStringUtils.ALPHANUM_MIXED) + userId;
        String inviteCode = URLEncoder.encode(inviteCodeRaw, StandardCharsets.UTF_8);
        RedisUtils.set("user_invited_code_" + userId, inviteCode);
        return inviteCode;
    }

    public UserVO getByEthAddress(String address){
        address = address.toLowerCase();
        User userInfo =  userRepository.findByEthAddress(address, false);
        if(userInfo == null){
            throw new DomainException("no user",404);
        }
        UserScore userScore = userScoreRepository.getByUserId(userInfo.getId());
        String inviteCode = getInvitedCode(userInfo.getId() + "");
        UserVO userVO = toUserVO(userInfo,userScore);
        userVO.setInvitedCode(inviteCode);
        return userVO;
    }

    /**
     * @see UserService#createUser
     */
    @Override
    public String createUser(UserCreateRequest request){
        return userRepository.insert(convertCreateUserRequestToUserEntity(request));
    }

    /**
     * @see UserService#createUserAndLogin
     */
    @Override
    public JSONObject createUserAndLogin(UserCreateRequest request){
        User user = convertCreateUserRequestToUserEntity(request);
        String userId = userRepository.insert(user);
        String token = JWTUtils.createToken(user, 3600*24*20);
        JSONObject result = new JSONObject();
        result.put("userId", userId);
        result.put("token", token);
        return result;
    }

    /**
     * @see UserService#createAndLoginByTgInfo
     */
    @Override
    public JSONObject createAndLoginByTgInfo(UserLoginByTgRequest userLoginByTgRequest){
        JSONObject result = new JSONObject();
        User user = userRepository.findByTgId(userLoginByTgRequest.getTgId());
        Map<String, String> tgUserInfo = UrlParserUtils.parse(userLoginByTgRequest.getTgUserInfoStr());
        if(user == null){
            user = new User();
            user.setStatus("active");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tgUserInfo", tgUserInfo);
            user.setContent(JSON.toJSONString(jsonObject));
            user.setName(tgUserInfo.get("username"));
            user.setTgId(tgUserInfo.get("id"));
            user.setId(new BigInteger(userRepository.insert(user)));
        }
        result.put("userId", user.getId());
        String existsToken = JWTUtils.getTokenByUserId(user.getId()+"");
        if(!userLoginByTgRequest.getForce() && !StringUtils.isEmpty(existsToken)){
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user, 3600*24*20);
            result.put("token", token);
        }
        return result;
    }

    public void bindTgInfo(BindTgIdRequest bindTgIdRequest){
        Map<String, String> tgUserInfo = UrlParserUtils.parse(bindTgIdRequest.getTgUserInfoStr());
        User tgUserDB = userRepository.findByTgId(tgUserInfo.get("id"));
        User targetUser = userRepository.findById(bindTgIdRequest.getUserId(), false);
        if(tgUserDB != null){
            if(tgUserDB.getId().equals(targetUser.getId())){
                return;
            }else {
                throw new DomainException("This telegram user has been bound!", 872);
            }
        }

        JSONObject jsonObject = JSONObject.parseObject(targetUser.getContent());
        jsonObject.put("tgUserInfo", tgUserInfo);
        targetUser.setContent(JSON.toJSONString(jsonObject));
        targetUser.setTgId(tgUserInfo.get("id"));
        userRepository.updateById(targetUser);
    }

    private boolean verifySignByTsCode(String message, String sign, String expectAddress){
        JSONObject body = new JSONObject();
        body.put("message", message);
        body.put("sign", sign);
        body.put("expectedAddress",expectAddress);
        JSONObject verifyResult = openProxyClient.post("/verifySign", body, null);
        return verifyResult != null && verifyResult.getBoolean("success");
    }

    /**
     * @see UserService#login
     */
    @Override
    public JSONObject login(LoginRequest request){
        request.setEthAddress(request.getEthAddress().toLowerCase());
        String envStr = env.getProperty("env");
        boolean verifyResult = false;
        if("local".equals(envStr)){
            verifyResult = true;
        }else {
            String storedNonce = RedisUtils.get("NONCE_ON_ADDRESS_" + request.getEthAddress());
            if(StringUtils.isBlank(storedNonce)){
                throw new DomainException("Invalid eth address",500);
            }
            String message = String.format("Sign this message to authenticate your wallet address \nNonce: %s\nAddress: %s", storedNonce, request.getEthAddress());
            verifyResult = verifySignByTsCode(message, request.getSignature(), request.getEthAddress());
        }
        if(!verifyResult){
            throw new DomainException("sign verify failed! ", 403);
        }
        final User[] user = {userRepository.findByEthAddress(request.getEthAddress(), false)};
        JSONObject result = new JSONObject();
        if(user[0] == null){
            user[0] = convertCreateUserRequestToUserEntity(request);
            user[0].setId(new BigInteger(userRepository.insert(user[0])));
            String invitedCode = request.getInvitedCode();
            if(StringUtils.isNotBlank(invitedCode)){
                transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                    String invitingUser = insertInvitesInfo(user[0], invitedCode, request.getAppToken());
                    if(StringUtils.isNotBlank(invitingUser)){
                        user[0] = userRepository.findById(user[0].getId() + "", false);
                        JSONObject contentObj = JSON.parseObject(user[0].getContent());
                        contentObj.put("invitedByCode", invitedCode);
                        contentObj.put("invitedByUser", invitingUser);
                        user[0].setContent(JSON.toJSONString(contentObj));
                        userRepository.updateById(user[0]);
                    }
                });
            }
        }

        result.put("userId", user[0].getId());
        String existsToken = JWTUtils.getTokenByUserId(user[0].getId()+"");
        if(!request.getForce() && !StringUtils.isEmpty(existsToken)){
            JWTUtils.setTokenExpired(user[0].getId() + "", 3600*24*20);
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user[0], 3600*24*20);
            result.put("token", token);
        }
        return result;
    }

    private String insertInvitesInfo(User user, String invitedCode, String appToken){
        List<UserInvites> invitedInfo = userInvitesRepository.getByInvitedUserId(user.getId() + "");
        if(!CollectionUtils.isEmpty(invitedInfo)){
            return null;
        }
        invitedCode = URLDecoder.decode(invitedCode, StandardCharsets.UTF_8);
        if(StringUtils.isNotBlank(invitedCode)){
            String invitingUserId = invitedCode.substring(5);
            if((user.getId()+"").equals(invitingUserId)){
                throw new DomainException("can not invite yourself!", 403);
            }
            String storedInvitedCode = getInvitedCode(invitingUserId);
            if(!invitedCode.equals(storedInvitedCode)){
                //throw new DomainException("invalid invited code", 403);
            }
            if(StringUtils.isBlank(invitingUserId)){
                throw new DomainException("invalid invited code", 403);
            }
            List<UserInvites> invitingInfo = userInvitesRepository.getByInvitedUserId(invitingUserId);
            if(!CollectionUtils.isEmpty(invitingInfo)){
                if((user.getId()+"").equals(invitingInfo.get(0).getInvitingUser())){
                    throw new DomainException("They cannot be bound to each other", 403);
                }
            }
            UserInvites userInvites = new UserInvites();
            userInvites.setInvitedUser(user.getId() + "");
            userInvites.setInvitingUser(invitingUserId);
            userInvites.setContent("{}");
            userInvites.setStatus("ACTIVE");
            userInvitesRepository.insert(userInvites);
//            TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
//            taskCreateRequest.setTemplateCode("inviteUser");
//            taskCreateRequest.setInnerUserId(invitingUserId);
//            taskCreateRequest.setApp(appToken);
//            RoleDTO roleDTO = new RoleDTO();
//            roleDTO.setId(invitingUserId);
//            taskCreateRequest.setOperator(roleDTO);
//            taskService.claimTask(taskCreateRequest);
            return invitingUserId;
        }
        return null;
    }

    /**
     * @see UserService#loginWithEthAddress
     */
    @Override
    public JSONObject loginWithEthAddress(LoginRequest request){
        String storedNonce = RedisUtils.get("NONCE_ON_ADDRESS_" + request.getEthAddress());
        if(StringUtils.isBlank(storedNonce)){
            throw new DomainException("Invalid eth address",500);
        }
        boolean verified = EthereumAuthUtils.verifySignature(
                request.getEthAddress(),
                request.getSignature(),
                storedNonce
        );
        if(!verified){
            throw new DomainException("eth address verify failed",403);
        }
        final User[] user = {userRepository.findByEthAddress(request.getEthAddress(), false)};
        JSONObject result = new JSONObject();
        if(user[0] == null){
            user[0] = convertCreateUserRequestToUserEntity(request);
            user[0].setId(new BigInteger(userRepository.insert(user[0])));
            String invitedCode = request.getInvitedCode();
            if(StringUtils.isNotBlank(invitedCode)){
                transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                    String invitingUser = insertInvitesInfo(user[0], invitedCode, request.getAppToken());
                    if(StringUtils.isNotBlank(invitingUser)){
                        user[0] = userRepository.findById(user[0].getId() + "", false);
                        JSONObject contentObj = JSON.parseObject(user[0].getContent());
                        contentObj.put("invitedByCode", invitedCode);
                        contentObj.put("invitedByUser", invitingUser);
                        user[0].setContent(JSON.toJSONString(contentObj));
                        userRepository.updateById(user[0]);
                    }
                });
            }
        }
        result.put("userId", user[0].getId());
        String existsToken = JWTUtils.getTokenByUserId(user[0].getId()+"");
        if(!request.getForce() && !StringUtils.isEmpty(existsToken)){
            JWTUtils.setTokenExpired(user[0].getId() + "", 3600*24*20);
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user[0], 3600*24*20);
            result.put("token", token);
        }
        return result;
    }

    /**
     * @see UserService#bindUserEthAddress
     */
    @Override
    public void bindUserEthAddress(BindUserEthRequest request){
        String storedNonce = RedisUtils.get("NONCE_ON_ADDRESS_" + request.getEthAddress());
        if(StringUtils.isBlank(storedNonce)){
            throw new DomainException("Invalid eth address",500);
        }
        boolean verified = EthereumAuthUtils.verifySignature(
                request.getEthAddress(),
                request.getSignature(),
                storedNonce
        );
        if(!verified){
            throw new DomainException("eth address verify failed",403);
        }
        User user = userRepository.findByEthAddress(request.getEthAddress(), false);
        if(user != null){
            throw new DomainException("EthAddress has bound another user!", 403);
        }
        user = userRepository.findById(request.getUserId(), false);
        if(StringUtils.isBlank(user.getEthAddress())){
            user.setEthAddress(request.getEthAddress());
            userRepository.updateById(user);
        }
    }

    /**
     * @see UserService#bindEthAddressSimple
     */
    @Override
    public void bindEthAddressSimple(BindEthAddressRequest request){
        if(StringUtils.isBlank(request.getEthAddress())){
            throw new DomainException("The eth address is blank!", 403);
        }
        request.setEthAddress(request.getEthAddress().toLowerCase());
        if(!EthAddressValidateUtils.isValidEthereumAddress(request.getEthAddress())){
            throw new DomainException("The eth address is illegal!", 403);
        }
        User user = userRepository.findById(request.getUserId(), false);
        user.setEthAddress(request.getEthAddress());
        try{
            userRepository.updateById(user);
        }catch (Throwable t){
            throw new DomainException("The eth address has been used!", 403);
        }
    }



    public void bindUserInvitedCode(BindInvitingCodeRequest request){
        User user = userRepository.findById(request.getUserId(), false);
        String invitedCode = request.getInvitedCode();
        if(user == null){
            throw new DomainException("user not fund", 404);
        }
        transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
            String invitingUserId = insertInvitesInfo(user, invitedCode, request.getAppToken());
            if(StringUtils.isNotBlank(invitingUserId)){
                JSONObject contentObj = JSON.parseObject(user.getContent());
                contentObj.put("invitedByCode", invitedCode);
                contentObj.put("invitedByUser", invitingUserId);
                user.setContent(JSON.toJSONString(contentObj));
                userRepository.updateById(user);
            }
        });
    }

    /**
     * @see UserService#getNonce
     */
    @Override
    public String getNonce(String address) {
        address = address.toLowerCase();
        if(!StringUtils.isBlank(RedisUtils.get("NONCE_ON_ADDRESS_" + address))){
            return RedisUtils.get("NONCE_ON_ADDRESS_" + address);
        }
        String nonce = UUID.randomUUID().toString();
        RedisUtils.set("NONCE_ON_ADDRESS_" + address, nonce);
        return nonce;
    }


    /**
     * @see UserService#updateById
     */
    @Override
    public Integer updateById(UserUpdateRequest request){
        User existsUser = userRepository.findById(request.getId(),false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        JSONObject existsContent = JSON.parseObject(existsUser.getContent());

        User user = convertUpdateUserRequestToUserEntity(request);
        user.setId(existsUser.getId());
        if(request.getContent() != null){
            JsonUtils.mergeJsonObjects(existsContent, request.getContent());
            user.setContent(JSON.toJSONString(existsContent));
        }
        return userRepository.updateById(user);
    }

    /**
     * @see UserService#findByXId
     */
    @Override
    public User findByXId(String xId){
        return userRepository.findByXId(xId);
    }

    /**
     * @see UserService#updateByEthAddress
     */
    @Override
    public Integer updateByEthAddress(UserCreateRequest request){
        User existsUser = userRepository.findByEthAddress(request.getEthAddress(), false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        User user = convertCreateUserRequestToUserEntity(request);
        return userRepository.updateByEthAddress(user);
    }

    /**
     * convert to User
     * @param request
     * @return
     */
    public static User convertCreateUserRequestToUserEntity(UserCreateRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEthAddress(request.getEthAddress());
        user.setTgId(request.getTgId());
        user.setxId(request.getxId());
        user.setName(request.getName());
        return user;
    }

    /**
     * convert to User
     * @param request
     * @return
     */
    public static User convertUpdateUserRequestToUserEntity(UserCreateRequest request){
        User user = new User();
        if(StringUtils.isNotBlank(request.getName())){
            user.setName(request.getName());
        }
//        if(StringUtils.isNotBlank(request.getEthAddress())){
//            user.setEthAddress(request.getEthAddress());
//        }
        if(StringUtils.isNotBlank(request.getTgId())){
            user.setTgId(request.getTgId());
        }
        if(StringUtils.isNotBlank(request.getxId())){
            user.setxId(request.getxId());
        }
        return user;
    }

    /**
     * convert to UserVO
     * @param user
     * @param userScore
     * @return
     */
    public UserVO toUserVO(User user, UserScore userScore){
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setName(user.getName());
        userVO.setStatus(user.getStatus());
        userVO.setTgId(user.getTgId());
        userVO.setEthAddress(user.getEthAddress());
        userVO.setxId(user.getxId());
        userVO.setCreatedAt(user.getCreatedAt());
        userVO.setUpdatedAt(user.getUpdatedAt());
        userVO.setContent(user.getContent());
        if(StringUtils.isNotBlank(user.getContent())){
            userVO.setContentObj(JSON.parseObject(user.getContent()));
        }
        if(userScore != null){
            userVO.setScoreInfo(JSON.parseObject(userScore.getContent()));
        }
        return userVO;

    }

    public void fixUserEthAddressToLowerCase(){
        List<User> users = null;
        String minId = "0";
        Integer limit = 200;

        do{
            users = userRepository.findAll(minId, limit);
            for(User single:users){
                if(!StringUtils.isBlank(single.getEthAddress()) && single.getEthAddress().startsWith("0x")){
                    single.setEthAddress(single.getEthAddress().toLowerCase());
                    userRepository.updateById(single);
                }
                minId = single.getId() + "";
            }
        }while (!CollectionUtils.isEmpty(users));
    }

}

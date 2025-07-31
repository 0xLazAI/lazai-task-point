package com.lazai.repostories.impl;

import com.lazai.entity.UserInvites;
import com.lazai.mapper.UserInvitesMapper;
import com.lazai.repostories.UserInvitesRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class UserInvitesRepositoryImpl implements UserInvitesRepository {

    @Autowired
    private UserInvitesMapper userInvitesMapper;

    public Integer insert(UserInvites userInvites){
        if(StringUtils.isBlank(userInvites.getStatus())){
            userInvites.setStatus("ACTIVE");
        }
        if(userInvites.getCreatedAt() == null){
            userInvites.setCreatedAt(new Date());
        }
        if(userInvites.getUpdatedAt() == null){
            userInvites.setUpdatedAt(new Date());
        }
        return userInvitesMapper.insert(userInvites);
    }

    public Integer updateById(UserInvites userInvites){
        if(userInvites.getUpdatedAt() == null){
            userInvites.setUpdatedAt(new Date());
        }
        return userInvitesMapper.updateById(userInvites);
    }

    public List<UserInvites> getByInvitingUserId(String invitingUserId){
        return userInvitesMapper.getByInvitingUserId(invitingUserId);
    }

    public List<UserInvites> getByInvitingUserIdPagination(String invitingUserId, Integer offset, Integer limit){
        return userInvitesMapper.getByInvitingUserIdPagination(invitingUserId, offset, limit);
    }

    public Integer getByInvitingUserIdCnt(String invitingUserId){
        return userInvitesMapper.getByInvitingUserIdCnt(invitingUserId);
    }

    public List<UserInvites> getByInvitedUserIdPagination(String invitedUser, Integer offset, Integer limit){
        return userInvitesMapper.getByInvitedUserIdPagination(invitedUser, offset, limit);
    }

    public Integer getByInvitedUserIdCnt(String invitedUser){
        return userInvitesMapper.getByInvitedUserIdCnt(invitedUser);
    }

    public List<UserInvites> getByInvitedUserId(String invitedUser){
        return userInvitesMapper.getByInvitedUserId(invitedUser);
    }
}

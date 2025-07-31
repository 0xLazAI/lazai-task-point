package com.lazai.repostories;

import com.lazai.entity.UserInvites;

import java.util.List;

public interface UserInvitesRepository {

    Integer insert(UserInvites userInvites);

    Integer updateById(UserInvites userInvites);

    List<UserInvites> getByInvitingUserId(String invitingUserId);

    List<UserInvites> getByInvitingUserIdPagination(String invitingUserId, Integer offset, Integer limit);

    Integer getByInvitingUserIdCnt(String invitingUserId);

    List<UserInvites> getByInvitedUserId(String invitedUser);

    List<UserInvites> getByInvitedUserIdPagination(String invitedUser, Integer offset, Integer limit);

    Integer getByInvitedUserIdCnt(String invitedUser);
}

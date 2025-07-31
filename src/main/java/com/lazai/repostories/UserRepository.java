package com.lazai.repostories;

import com.lazai.entity.User;
import com.lazai.entity.dto.UserQueryListParam;

import java.util.List;

public interface UserRepository {

    List<User> findAll(String minId, Integer limit);

    User findById(String id, Boolean isLock);

    User findByEthAddress(String ethAddress, Boolean isLock);

    User findByXId(String xId);

    User findByTgId(String tgId);

    String insert(User user);

    Integer updateById(User user);

    Integer updateByEthAddress(User user);

    List<User> queryList(UserQueryListParam param);
}

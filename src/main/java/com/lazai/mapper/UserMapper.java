package com.lazai.mapper;

import com.lazai.entity.User;
import com.lazai.entity.dto.UserQueryListParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(String id);

    @Select("SELECT * FROM user WHERE id = #{id} FOR UPDATE")
    User findByIdLock(String id);

    @Select("SELECT * FROM user WHERE id > #{minId} ORDER BY ID ASC LIMIT #{limit}")
    List<User> findAll(String minId, Integer limit);

    @Select("SELECT * FROM user WHERE eth_address = #{ethAddress}")
    User findByEthAddress(String ethAddress);

    @Select("SELECT * FROM user WHERE eth_address = #{ethAddress} FOR UPDATE")
    User findByEthAddressLock(String ethAddress);

    @Select("SELECT * FROM user WHERE x_id = #{xId}")
    User findByXId(String xId);

    @Select("SELECT * FROM user WHERE tg_id = #{tgId}")
    User findByTgId(String tgId);

    Integer insert(User user);

    Integer updateById(User user);

    Integer updateByEthAddress(User user);

    List<User> queryList(UserQueryListParam param);
}

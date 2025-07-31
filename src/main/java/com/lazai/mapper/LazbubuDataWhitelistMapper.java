package com.lazai.mapper;

import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LazbubuDataWhitelistMapper {

    Integer insert(LazbubuDataWhitelist lazbubuDataWhitelist);


    Integer updateById(LazbubuDataWhitelist lazbubuDataWhitelist);


    List<LazbubuDataWhitelist> queryList(LazbubuWhitelistQueryParam param);

    List<LazbubuDataWhitelist> pageQueryList(LazbubuWhitelistQueryParam param);

    Integer pageQueryCnt(LazbubuWhitelistQueryParam param);

}

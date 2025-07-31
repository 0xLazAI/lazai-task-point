package com.lazai.repostories;

import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;

import java.util.List;

public interface LazbubuWhitelistRepository {

    String insert(LazbubuDataWhitelist lazbubuDataWhitelist);

    Integer updateById(LazbubuDataWhitelist lazbubuDataWhitelist);

    List<LazbubuDataWhitelist> queryList(LazbubuWhitelistQueryParam param);

    List<LazbubuDataWhitelist> pageQueryList(LazbubuWhitelistQueryParam param);

    Integer pageQueryCnt(LazbubuWhitelistQueryParam param);
}

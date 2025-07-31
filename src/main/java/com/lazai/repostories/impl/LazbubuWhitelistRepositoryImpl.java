package com.lazai.repostories.impl;

import com.lazai.entity.LazbubuDataWhitelist;
import com.lazai.entity.dto.LazbubuWhitelistQueryParam;
import com.lazai.mapper.LazbubuDataWhitelistMapper;
import com.lazai.repostories.LazbubuWhitelistRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LazbubuWhitelistRepositoryImpl implements LazbubuWhitelistRepository {

    @Autowired
    private LazbubuDataWhitelistMapper lazbubuDataWhitelistMapper;

    public String insert(LazbubuDataWhitelist lazbubuDataWhitelist){
        if(lazbubuDataWhitelist.getCreatedAt() == null){
            lazbubuDataWhitelist.setCreatedAt(new Date());
        }
        if(lazbubuDataWhitelist.getUpdatedAt() == null){
            lazbubuDataWhitelist.setUpdatedAt(new Date());
        }
        if(StringUtils.isBlank(lazbubuDataWhitelist.getStatus())){
            lazbubuDataWhitelist.setStatus("WAIT");
        }
        if(StringUtils.isEmpty(lazbubuDataWhitelist.getContent())){
            lazbubuDataWhitelist.setContent("{}");
        }
        lazbubuDataWhitelistMapper.insert(lazbubuDataWhitelist);
        return lazbubuDataWhitelist.getId().toString();
    }

    public Integer updateById(LazbubuDataWhitelist lazbubuDataWhitelist){
        lazbubuDataWhitelist.setUpdatedAt(new Date());
        return lazbubuDataWhitelistMapper.updateById(lazbubuDataWhitelist);
    }

    public List<LazbubuDataWhitelist> queryList(LazbubuWhitelistQueryParam param){
        return lazbubuDataWhitelistMapper.queryList(param);
    }

    public List<LazbubuDataWhitelist> pageQueryList(LazbubuWhitelistQueryParam param){
        return lazbubuDataWhitelistMapper.pageQueryList(param);
    }

    public Integer pageQueryCnt(LazbubuWhitelistQueryParam param){
        return lazbubuDataWhitelistMapper.pageQueryCnt(param);
    }

}

package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Service
public class UserServiceImpl implements UserService {
    /**
     * 微信登陆
     * @param userLoginDTO
     * @return
     */
    private static final  String URL = "https://api.weixin.qq.com/sns/jscode2session?";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 通过code获取openid
     * @param code
     * @return
     */
    private String getOpenidByCode(String code){
        Map<String ,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(URL, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //返回User对象
        //进入一个DTO，里面就一个code
        //通过code获得openid
        //利用官方的接口去获得openid
        //发送http请求
        String openid = getOpenidByCode(userLoginDTO.getCode());
        //看看openid是否合法
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //通过openid去用户表里查询user
        User user = userMapper.getUserByOpenid(openid);
        //判断user是不是空的，也就是新用户，是的话就注册，就是给user初始化一下
        if(user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //最后返回user
        return user;
    }
}

package com.xiaox.test.oauth.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

@RestController
public class ResourceController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitee.client.id}")
    private String clientId;

    @Value("${gitee.client.client_secret}")
    private String clientSecret;

    @Value("${gitee.client.redirect_uri}")
    private String redirectUri;

    @RequestMapping("/resource")
    public RedirectView getResource(){
        //模拟当前未登录，拦截重定向到码云授权接口
        String url = "https://gitee.com/oauth/authorize?client_id=f2121f7585e7bec253c0f085e7ddcbd23bf9897910210bada7782b56cacf6259&redirect_uri=http://10.50.33.191:8091/callback&response_type=code";
        RedirectView view = new RedirectView(url);
        return view;

    }


    /*
    * 回调获取授权资源
    * */
    @RequestMapping("/callback")
    public String callback(@RequestParam String code){

        String body = restTemplate.postForObject("https://gitee.com/oauth/token?" +
                "grant_type=authorization_code" +
                "&code="+code +
                "&client_id=" +clientId +
                "&redirect_uri=" +redirectUri+
                "&client_secret="+clientSecret,null,String.class);
        HashMap map = JSONObject.parseObject(body, HashMap.class);
        String accessToken = (String)map.get("access_token");
        //获取用户信息
        String user = restTemplate.getForObject("https://gitee.com/api/v5/user?access_token="+accessToken,String.class);
        return user;
    }



}

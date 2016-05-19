package com.company.umeng;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class Main {

    public static void main(String[] args) {
        UmHttpClient.APP_KEY = "替换成你的AppKey";
        UmHttpClient.APP_SECRECT = "替换成你的AppSecrect";
        UmHttpClient.SERVER_ADDRESS = "https://api.wsq.umeng.com";
        UmHttpClient.SDK_VERSION = "2.5.0";

        String accessTokenUrl = "/v2/user/token";// access token接口的地址
        String pointsUrl = "/v2/pointbank/currency/op/";//积分接口地址


        UmHttpClient umHttpClient = new UmHttpClient();

        // 获取access token
        HashMap<String,Object>  map = new HashMap<>();
        map.put("ak",UmHttpClient.APP_KEY);
        String result = umHttpClient.accessTokenRequest(map,accessTokenUrl,UmHttpClient.APP_SECRECT);
        System.out.println("access token result:"+result);//返回结果

        //解析access token
        JSONObject jsonObject = new JSONObject(result);
        String accessToken = jsonObject.optString("access_token","");
        UmHttpClient.ACCESS_TOKEN = accessToken;

        //调用积分接口
        HashMap<String,Object> data = new HashMap<>();
        data.put("fuid","562db244fe2cac3353bed98e");
        data.put("community_id","54ace7620bbbaf056895488d");
        data.put("currency",-1000);
        data.put("desc","中文测试");
        data.put("identity","lo23512");
        String pointsResult = umHttpClient.sentRequest(pointsUrl, UmHttpClient.HttpMethod.POST,data);
        System.out.println(pointsResult);
    }
}

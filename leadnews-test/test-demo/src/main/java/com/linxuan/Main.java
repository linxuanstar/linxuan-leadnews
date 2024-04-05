package com.linxuan;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // {针孔摄象=1, 蚁力神=1, 冰毒=1, 足球投注=1, 法轮功=1, 套牌车=1,
        // 信用卡提现=1, 小额贷款=1, 无抵押贷款=1, 广告代理=1, 地下钱庄=1,
        // 售肾=1, 出售答案=1, 私人侦探=1, 代开发票=1}
        Map<String, Integer> map = new HashMap<>();
        map.put("针孔摄像", 1);
        map.put("蚁力神", 1);
        map.put("冰毒", 1);
        map.put("足球投注", 1);
        map.put("法轮功", 1);
        map.put("套牌车", 1);
        map.put("信用卡提现", 1);
        map.put("小额贷款", 1);
        map.put("无抵押贷款", 1);
        map.put("广告代理", 1);
        map.put("地下钱庄", 1);
        map.put("售肾", 1);
        map.put("出售答案", 1);
        map.put("私人侦探", 1);
        map.put("代开发票", 1);
        System.out.println(map.toString().length());
        if (map.toString().length() > 50) {
            map = map.entrySet().stream()
                    .limit(5)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (existing, replacement) -> existing, HashMap::new));

            System.out.println("剪切后字符长度" + map.toString().length());
        }

        System.out.println(map);
    }
}
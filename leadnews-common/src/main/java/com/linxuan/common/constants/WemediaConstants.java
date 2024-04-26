package com.linxuan.common.constants;

public class WemediaConstants {
    // 收藏
    public static final Short COLLECT_MATERIAL = 1;
    // 取消收藏
    public static final Short CANCEL_COLLECT_MATERIAL = 0;

    // 类型
    public static final String WM_NEWS_TYPE = "type";
    // 文本
    public static final String WM_NEWS_TYPE_TEXT = "text";
    // 图片
    public static final String WM_NEWS_TYPE_IMAGE = "image";

    // 文章封面类型 0无图 1单图 3多图 -1自动
    public static final Short WM_NEWS_NONE_IMAGE = 0;
    public static final Short WM_NEWS_SINGLE_IMAGE = 1;
    public static final Short WM_NEWS_MANY_IMAGE = 3;
    public static final Short WM_NEWS_TYPE_AUTO = -1;

    // 素材图片引用类型 0内容引用、1封面引用
    public static final Short WM_CONTENT_REFERENCE = 0;
    public static final Short WM_COVER_REFERENCE = 1;

    // 人工审核成功
    public static final Short WM_NEWS_AUTH_PASS = 4;
    // 人工审核失败
    public static final Short WM_NEWS_AUTH_FAIL = 2;
}

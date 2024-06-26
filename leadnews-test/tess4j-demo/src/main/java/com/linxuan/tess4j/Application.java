package com.linxuan.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        try {
            // 获取本地图片
            File file = new File("D:\\test1.jpg");
            // 创建Tesseract对象
            ITesseract tesseract = new Tesseract();
            // 设置字体库路径，这里不需要具体指明文件名称
            tesseract.setDatapath("D:\\Java\\IdeaProjects\\lead_news");
            // 中文识别，指明chi_sim.traineddata文件
            tesseract.setLanguage("chi_sim");
            // 执行ocr识别
            String result = tesseract.doOCR(file);
            // 替换回车和tal键  使结果为一行
            result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");
            System.out.println("识别的结果为：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.design.im;

import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.github.wujiuye.mybatisplus.generator.EasyMybatisGenerator;

public class EasyGeneratorTest {
    public static void main(String[] args) throws Exception {
        // 配置包信息
        PackageConfig config = new PackageConfig()
                .setParent("com.design.im")
                .setEntity("model.po")
                .setMapper("dao")
                .setXml("dao");
        // 开始生成代码
        EasyMybatisGenerator.run(config,"ser", "Topic_Faq", "Topic", "Logout_Token", "IM_Message", "IM_Message_At_List", "Topic_Users");
    }

}

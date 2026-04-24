package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这个是一个配置类用于创建AliOssUtil对象，初始化这个对象，这个对象的upload方法是上传文件到阿里云端
 * 这个对象交给IOC管理，且只有一个，然后就可以注入到Controller里面使用
 */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean //项目启动时这个方法被调用到，然后创建出这个对象，交给容器管理
    @ConditionalOnMissingBean //保证整个容器里面只有一个这样类型的对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}

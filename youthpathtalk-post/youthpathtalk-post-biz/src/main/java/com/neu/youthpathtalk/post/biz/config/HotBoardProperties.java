package com.neu.youthpathtalk.post.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/04/09 10:50
 * @description 从配置文件加载热榜相关属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "hotboard")
public class HotBoardProperties {
    private int timeWindowHours=7*24;
    private int boardSize=20;
    private int batchSize=1000;
    private String cron="0 */10 * * * ?";
    private double decayBase=0.95;
    private ScoreWeight weight=new ScoreWeight();

    @Data
    public static class ScoreWeight{
        private double view=1.0;
        private double like=2.0;
        private double comment=3.0;
        private double favorite=4.0;
    }
}

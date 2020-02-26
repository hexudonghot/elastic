package cn.com.aig.recommend.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 配置，下面是单节点配置：
 * 官方wiki地址：https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#26-%E5%8D%95redis%E8%8A%82%E7%82%B9%E6%A8%A1%E5%BC%8F
 *
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String host;
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
       String [] hosts =  host.split(",");
        ClusterServersConfig configdd =  config.useClusterServers().setScanInterval(2000);
        for(String node : hosts)
        {
            configdd.addNodeAddress("redis://"+node);
        }
        return Redisson.create(config);
    }

}

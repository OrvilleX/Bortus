package com.orvillex.bortus.job.util;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用视图模型
 */
public class ShardingUtil {
    private static InheritableThreadLocal<ShardingVO> contextHolder = new InheritableThreadLocal<ShardingVO>();

    @Getter
    @Setter
    public static class ShardingVO {

        private int index;
        private int total;

        public ShardingVO(int index, int total) {
            this.index = index;
            this.total = total;
        }
    }

    public static void setShardingVo(ShardingVO shardingVo){
        contextHolder.set(shardingVo);
    }

    public static ShardingVO getShardingVo(){
        return contextHolder.get();
    }
}

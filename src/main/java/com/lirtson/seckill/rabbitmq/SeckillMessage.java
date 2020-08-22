package com.lirtson.seckill.rabbitmq;

import com.lirtson.seckill.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage implements Serializable {
    private String userId;
    private long goodsId;
}

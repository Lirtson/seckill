package com.lirtson.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="seckill_order")
public class SeckillOrder {
    @Id
    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;
}

package com.lirtson.seckill.service;

import com.lirtson.seckill.dao.OrderDao;
import com.lirtson.seckill.domain.OrderInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderService {
    @Resource
    OrderDao orderDao;

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteSeckillOrders();
    }
}

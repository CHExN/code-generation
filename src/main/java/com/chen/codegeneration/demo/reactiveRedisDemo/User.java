package com.chen.codegeneration.demo.reactiveRedisDemo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class User {
	private Long id;
	private String name;
	// 标签
	private String label;
	// 收货地址经度
	private Double deliveryAddressLon;
	// 收货地址维度
	private Double deliveryAddressLat;
	// 最新签到日
	private String lastSignInDay;
	// 积分
	private Integer score;
	// 权益
	private List<Rights> rights;
}


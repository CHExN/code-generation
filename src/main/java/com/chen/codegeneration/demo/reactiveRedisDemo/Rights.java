package com.chen.codegeneration.demo.reactiveRedisDemo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Rights {
	private Long id;
	private Long userId;
	private String name;
}

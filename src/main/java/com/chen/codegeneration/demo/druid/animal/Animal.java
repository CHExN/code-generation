package com.chen.codegeneration.demo.druid.animal;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("animel")
public class Animal {

    private Long id;
    private String name;
    private String color;
    private String type;

}

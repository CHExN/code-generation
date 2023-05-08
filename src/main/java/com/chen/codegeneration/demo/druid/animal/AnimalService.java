package com.chen.codegeneration.demo.druid.animal;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AnimalService extends IService<Animal> {

    List<Animal> findAnimals1();

    List<Animal> findAnimals2();

}

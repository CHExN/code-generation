package com.chen.codegeneration.demo.druid.animal;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.codegeneration.demo.druid.DS;
import com.chen.codegeneration.demo.druid.DSType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal> implements AnimalService {

    @Override
    public List<Animal> findAnimals1() {
        try {
            return baseMapper.selectList(null);
        } catch (Exception e) {
            log.error("获取信息失败", e);
            return new ArrayList<>();
        }
    }


    @DS(DSType.SLAVE)
    @Override
    public List<Animal> findAnimals2() {
        try {
            return baseMapper.selectList(null);
        } catch (Exception e) {
            log.error("获取信息失败", e);
            return new ArrayList<>();
        }
    }

}

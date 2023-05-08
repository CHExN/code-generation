package com.chen.codegeneration.demo.druid.animal;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class AnimalController {


    @Setter(onMethod_ = @Autowired)
    private AnimalService animalService;

    @GetMapping("/master")
    public List<Animal> findMasterAnimals() {
        return animalService.findAnimals1();
    }

    @GetMapping("/slave")
    public List<Animal> findSlaveAnimals() {
        return animalService.findAnimals2();
    }

}

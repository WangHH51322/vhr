package org.javaboy.vhr;

import org.javaboy.vhr.mapper.HrMapper;
import org.javaboy.vhr.model.Hr;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class VhrApplicationTests {

    @Autowired
    HrMapper hrMapper;

    @Test
    public void contextLoads() {
        List<Hr> allHr = hrMapper.getAllUser();
        System.out.println(allHr);
    }

    @Test
    public void loadUserByUsername() {
        Hr hr = hrMapper.loadUserByUsername("admin");
        System.out.println(hr);
    }


}

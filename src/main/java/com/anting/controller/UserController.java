package com.anting.controller;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anting on 17/1/22.
 */
@RestController
public class UserController {


    //@GetMapping("/hello")
   // @RequestMapping(value = "/hello")
    @PostMapping("/hello")
    public String hello() {

        List<String> ls = new ArrayList<>();
        ls.add("123");

        return JSON.toJSONString(ls);
    }


}

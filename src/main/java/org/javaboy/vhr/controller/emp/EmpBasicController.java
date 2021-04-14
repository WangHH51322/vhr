package org.javaboy.vhr.controller.emp;

import org.javaboy.vhr.model.EmpSalary;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/8/11 10:51
 */
@RestController
@RequestMapping("/emp/basic")
public class EmpBasicController {

    @Autowired
    EmployeeService employeeService;
    @GetMapping("/")
    public RespPageBean getEmployeeByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10")Integer size){
        return employeeService.getEmployeeByPage(page,size);
    }
}

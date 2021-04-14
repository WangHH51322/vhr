package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.EmployeeMapper;
import org.javaboy.vhr.model.Employee;
import org.javaboy.vhr.model.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/8/11 12:02
 */
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;
    public RespPageBean getEmployeeByPage(Integer page, Integer size) {
        if (page != null && size != null){
            page = (page-1)*size;
        }
        List<Employee> data = employeeMapper.getEmployeeByPage(page,size);
        Long total = employeeMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setData(data);
        bean.setTotal(total);
        return bean;
    }
}

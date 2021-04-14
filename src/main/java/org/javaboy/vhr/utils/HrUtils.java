package org.javaboy.vhr.utils;

import org.javaboy.vhr.model.Hr;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/8/9 11:47
 */
public class HrUtils {
    public static Hr getCurrentHr(){
        //获取当前登录的用户对象
        return (Hr) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

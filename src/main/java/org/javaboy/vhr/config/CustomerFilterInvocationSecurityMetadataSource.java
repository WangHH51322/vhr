package org.javaboy.vhr.config;

import org.javaboy.vhr.model.Menu;
import org.javaboy.vhr.model.Role;
import org.javaboy.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/7/30 22:36
 *
 * FilterInvocationSecurityMetadataSource 的主要作用是根据用户传来的请求地址,分析出请求需要的角色
 */
@Component
public class CustomerFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    MenuService menuService;
    AntPathMatcher antPathMatcher = new AntPathMatcher();//比对用的工具

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException { //当前请求需要的角色
        String requestUrl = ((FilterInvocation) o).getRequestUrl(); //获取当前请求的地址
        List<Menu> menus = menuService.getAllMenusWithRole(); //获取当前角色对应的menu里面的url
        for (Menu menu : menus) {
            if (antPathMatcher.match(menu.getUrl(),requestUrl)){ // 对比当前请求的地址和menu里面的url
                List<Role> roles = menu.getRoles();
                String[] str = new String[roles.size()];
                for (int i = 0; i < roles.size(); i++) {
                    str[i] = roles.get(i).getName();
                }
                return SecurityConfig.createList(str); // 返回url对的上的menu里面的角role
            }
        }
        return SecurityConfig.createList("ROLE_LOGIN"); //如果没有匹配上,登陆之后都可以访问
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

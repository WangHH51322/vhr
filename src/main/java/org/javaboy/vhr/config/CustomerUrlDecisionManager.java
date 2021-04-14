package org.javaboy.vhr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/7/30 22:59
 *
 * 在MyFilter对比发过来的url可以匹配到相应的role之后,看数据库里的这个role与登录的role是不是一致
 */
@Component
public class CustomerUrlDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        //登陆成功的用户的信息都保存在authentication里面
        //collection就是MyFilter的返回值
        for (ConfigAttribute configAttribute : collection) {
            String needRole = configAttribute.getAttribute(); //数据库里有的role
            if ("ROLE_LOGIN".equals(needRole)){
                if (authentication instanceof AnonymousAuthenticationToken){ // 判断用户是否登录,已经登陆的用户可以直接访问
                    throw new AccessDeniedException("尚未登录,请登录!");
                }else {
                    return;
                }
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();//获取当前登录用户的role
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(needRole)){ // 登录用户的role跟数据库里的role匹配上,可以访问
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足,请联系管理员!!!"); // 登录的role更数据库里的role皮配不上,说明权限不够
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

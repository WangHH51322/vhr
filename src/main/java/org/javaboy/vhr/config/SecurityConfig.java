package org.javaboy.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaboy.vhr.model.Hr;
import org.javaboy.vhr.model.RespBean;
import org.javaboy.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Qing
 * @version 1.0
 * @date 2020/7/17 16:54
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    HrService hrService;
    @Autowired
    CustomerFilterInvocationSecurityMetadataSource customerFilterInvocationSecurityMetadataSource;
    @Autowired
    CustomerUrlDecisionManager customerUrlDecisionManager;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login");
    }

    //如果登录成功,给前端返回一个登陆成功的json
    //由于没有登录成功的返回页面(即.loginProcessingUrl("/doLogin")是不存在的),所以直接返回前端一个json
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
//                .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setAccessDecisionManager(customerUrlDecisionManager);
                        o.setSecurityMetadataSource(customerFilterInvocationSecurityMetadataSource);
                        return o;
                    }
                })
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                .loginPage("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        //登陆成功以后,用户的信息是保存在authentication里面的
                        resp.setContentType("application/json;charset=utf-8");//登陆成功以后返回的json数据格式
                        PrintWriter out = resp.getWriter();//将用户信息往出写
                        Hr hr = (Hr)authentication.getPrincipal();//获取登陆成功的用户对象
                        RespBean ok = RespBean.ok("登陆成功", hr);//表示登陆成功
                        String s = new ObjectMapper().writeValueAsString(ok);//将获取的用户写成字符串
                        out.write(s);//将字符串写出去
                        out.flush();
                        out.close();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                        //登陆失败以后,登陆失败的信息是保存在e里面的
                        resp.setContentType("application/json;charset=utf-8");//登陆成失败以后返回的json数据格式
                        PrintWriter out = resp.getWriter();//将失败的异常往出写
                        RespBean respBean = RespBean.error("登陆失败");//表示登陆失败
                        //判断登陆失败的原因
                        if (e instanceof LockedException){
                            respBean.setMsg("账户被锁定,请联系管理员!");
                        }else if (e instanceof CredentialsExpiredException){
                            respBean.setMsg("密码过期,请联系管理员!");
                        }else if (e instanceof AccountExpiredException){
                            respBean.setMsg("账户过期,请联系管理员!");
                        }else if (e instanceof DisabledException){
                            respBean.setMsg("账户被禁用,请联系管理员!");
                        }else if (e instanceof BadCredentialsException){
                            respBean.setMsg("用户名或密码输入错误,请重新输入!");
                        }
                        String s = new ObjectMapper().writeValueAsString(respBean);//将错误信息写成字符串
                        out.write(s);//将字符串写出去
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        //注销成功以后,用户的信息是保存在authentication里面的
                        resp.setContentType("application/json;charset=utf-8");//注销成功以后返回的json数据格式
                        PrintWriter out = resp.getWriter();//将注销成功后的信息往出写
                        String s = new ObjectMapper().writeValueAsString("注销成功!");//将获取的用户写成字符串
                        out.write(s);//将字符串写出去
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()
                .csrf().disable().exceptionHandling()
                //权限不足,没有认证时,在这里处理结果,不要重定向
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                resp.setContentType("application/json;charset=utf-8");//登陆成失败以后返回的json数据格式
                resp.setStatus(401);
                PrintWriter out = resp.getWriter();//将失败的异常往出写
                RespBean respBean = RespBean.error("访问失败");//表示登陆失败
                //判断登陆失败的原因
                if (e instanceof InsufficientAuthenticationException){
                    respBean.setMsg("请求失败,请联系管理员!");
                }
                String s = new ObjectMapper().writeValueAsString(respBean);//将错误信息写成字符串
                out.write(s);//将字符串写出去
                out.flush();
                out.close();
            }
        });
    }
}

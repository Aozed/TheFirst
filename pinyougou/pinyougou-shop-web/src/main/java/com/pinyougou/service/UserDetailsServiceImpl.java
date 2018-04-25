package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义认证类
 * 结合dubbo
 */
public class UserDetailsServiceImpl implements UserDetailsService{

    //调用dubbo的服务接口
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //测试是否生效
        System.out.println("security登录认证");
        //创建role权限集合
        List<GrantedAuthority> authorities = new ArrayList<>();
        //添加role权限
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //获取对应商家
        TbSeller seller = sellerService.findOne(username);

        //判断返回条件
        if(seller!=null){
            if(seller.getStatus().equals("1")){
                //设置登录的条件
                return new User(username,seller.getPassword(),authorities);
            }else {
                return null;
            }
        }else{
            return null;
        }
    }
}

package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.TestUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<TestUser> {

    @Select("select * from user where username = #{username} and password = #{password}")
    TestUser selectByUsernameAndPassword(String username, String password);

    @Select("select * from user where id=#{user.id}")
    Page<TestUser> selectAll(Page page, @Param("user") TestUser testUser);
}

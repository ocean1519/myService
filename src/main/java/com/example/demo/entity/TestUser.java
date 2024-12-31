package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
@Builder
@TableName("user")
public class TestUser {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;

//    @TableField(exist = false)
//    private Integer status;
}

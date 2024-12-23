package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String name;
    private String description;
    private String status;
    private Integer age;
    public User() {}
}
package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@Builder
public class EsUser {
    @Id
    private String id;
    private String name;
    private String description;
    private Integer age;
    public EsUser() {}
}
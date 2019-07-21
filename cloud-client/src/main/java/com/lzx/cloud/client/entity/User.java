package com.lzx.cloud.client.entity;

import lombok.Data;

/**
 * @ClassName
 * @Description TODO
 * @Date 2019-07-18 09:52
 **/
@Data
public class User {
    private Long id;
    private String username;
    private String name;
    private Integer age;
    private Double balance;
    private String requestId;
}

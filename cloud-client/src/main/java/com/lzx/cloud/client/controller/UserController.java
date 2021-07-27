package com.lzx.cloud.client.controller;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lzx.cloud.client.entity.PersonTestProtos;
import com.lzx.cloud.client.entity.User;
import com.lzx.cloud.client.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping("/user/{id}")
    public User findById(@PathVariable Long id) {
        User user = this.userFeignClient.findById(id);
        return user;
    }

    @GetMapping("/user/all")
    public List<User> saveUser(){
        return this.userFeignClient.saveUser();
    }


    @GetMapping(value = "/user/getOne")
    public ByteString getOne(){
        try {
            /** Step1：生成 personTest 对象 */
            PersonTestProtos.PersonTest.Builder builder = PersonTestProtos.PersonTest.newBuilder();

            builder.setName("Mrzhang")
                    .setAge(18)
                    .setSex(true)
                    .setBirthday(System.currentTimeMillis())
                    .setAddress("军事基地")
                    .addCars(0, PersonTestProtos.Car.newBuilder().setName("兰博基尼").setColor("Red").build())
                    .putOther("描述", "暂无");
            PersonTestProtos.PersonTest personTest = builder.build();

            /** Step2：序列化和反序列化 */
            // 方式一 byte[]：
            // 序列化
//            byte[] bytes = personTest.toByteArray();
            // 反序列化
//            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseFrom(bytes);
//            System.out.println(String.format("反序列化得到的信息，姓名：%s，性别：%d，手机号：%s", personTestResult.getName(), personTest.getSexValue(), personTest.getPhone(0).getNumber()));



            // 方式二 ByteString：
            // 序列化
            ByteString byteString = personTest.toByteString();
            System.out.println(byteString.toString());
            System.out.println(personTest.toBuilder().toString());
            System.out.println(Arrays.toString(personTest.toByteArray()));
            System.out.println(personTest);
            // 反序列化
            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseFrom(byteString);
            System.out.println(personTestResult);
            return byteString;



            // 方式三 InputStream
            // 粘包,将一个或者多个protobuf 对象字节写入 stream
            // 序列化
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            personTest.writeDelimitedTo(byteArrayOutputStream);
            // 反序列化，从 steam 中读取一个或者多个 protobuf 字节对象
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseDelimitedFrom(byteArrayInputStream);
//            System.out.println(String.format("反序列化得到的信息，姓名：%s，性别：%d，手机号：%s", personTestResult.getName(), personTest.getSexValue(), personTest.getPhone(0).getNumber()));

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

}
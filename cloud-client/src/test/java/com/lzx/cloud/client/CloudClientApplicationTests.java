package com.lzx.cloud.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Consumer;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudClientApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSteam(){
        Consumer consumer = i -> System.out.println(i);
        consumer.accept(2);
    }

    @Test
    public void testSteam1(){
        Consumer consumer = new Consumer() {
            @Override
            public void accept(Object o) {
                System.out.println(o);
            }
        };
        consumer.accept(2);
    }

    @Test
    public void testSteam2(){
        Consumer consumer = i -> {
            String s = "我是第" + i + "个；";
            System.out.println(s);
        };
        pr(consumer);
    }

    public void pr(Consumer<Integer> consumer){
        for (int i = 0; i < 10; i++) {
             consumer.accept(i);
        }
    }
}

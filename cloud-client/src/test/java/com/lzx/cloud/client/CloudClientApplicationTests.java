package com.lzx.cloud.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudClientApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSteam() {
        Consumer consumer = i -> System.out.println(i);
        consumer.accept(2);
    }

    @Test
    public void testSteam1() {
        Consumer consumer = new Consumer() {
            @Override
            public void accept(Object o) {
                System.out.println(o);
            }
        };
        consumer.accept(2);
    }

    @Test
    public void testSteam2() {
        Consumer consumer = i -> {
            String s = "我是第" + i + "个；";
            System.out.println(s);
        };
        pr(consumer);
    }

    public void pr(Consumer<Integer> consumer) {
        for (int i = 0; i < 10; i++) {
            consumer.accept(i);
        }
    }


    @FunctionalInterface
    public interface MessageBuilder {
        String buildMessage();
    }

    private void log(int level, MessageBuilder builder) {
        if (level == 1) {
            System.out.println(builder.buildMessage());// 实际上利用内部类 延迟的原理,代码不相关 无需进入到启动代理执行
        }
    }

    @Test
    public void testLambda1() {
        String msgA = "Hello ";
        String msgB = "World ";
        String msgC = "Java";
        log(1, () -> {
            System.out.println("lambda 是否执行了");
            return msgA + msgB + msgC;
        });
    }


    public void generateX(Consumer<String> consumer) {
        consumer.accept("hello consumer");
    }

    @Test
    public void testConsumer() {
        generateX(s -> System.out.println(s));
    }

    // 判断字符串是否存在o  即使生产者 又是消费者接口
    private void method_test(Predicate<String> predicate) {
        boolean b = predicate.test("OOM SOF");
        System.out.println(b);
    }

    // 判断字符串是否同时存在o h 同时
    private void method_and(Predicate<String> predicate1, Predicate<String> predicate2) {
        boolean b = predicate1.and(predicate2).test("OOM SOF");
        System.out.println(b);
    }

    //判断字符串是否一方存在o h
    private void method_or(Predicate<String> predicate1, Predicate<String> predicate2) {
        boolean b = predicate1.or(predicate2).test("OOM SOF");
        System.out.println(b);
    }

    // 判断字符串不存在o 为真   相反结果
    private void method_negate(Predicate<String> predicate) {
        boolean b = predicate.negate().test("OOM SOF");
        System.out.println(b);
    }

    @Test
    public void testPredicate() {
        method_test((s) -> s.contains("O"));
        method_and(s -> s.contains("O"), s -> s.contains("h"));
        method_or(s -> s.contains("O"), s -> s.contains("h"));
        method_negate(s -> s.contains("O"));
    }

}

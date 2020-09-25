package com.lzx.cloud.client;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
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

    @Test
    public void testTobData(){
        Map<String, Object> map = new HashMap<>(10);
        map.put("accesskey", "lizhixinvpn");
        map.put("accessid", "VMS");
        map.put("time", Instant.now().toEpochMilli());
        map.put("channelId", "EPGC1386744804340101");
        map.put("channelName", "CCTV");
        String sign = createSign(map,"");
        map.put("sign", sign);
        String url = mapToUrlParams(map);
        System.out.println(url);
    }

    /**
     * 签名生成
     *
     * @param params    参数map
     * @param secretKey 加密key
     */
    public String createSign(Map<String, Object> params, String secretKey) {
        StringBuilder urlStr = new StringBuilder();
        // 将参数以参数名的字典升序排序
        Map<String, Object> sortParams = new TreeMap<>(params);
        // 遍历排序的字典,并拼接"key=value"格式
        for (Map.Entry<String, Object> entry : sortParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString().trim();
            //过滤签名字段，签名字段不进行生成签名
            if (!StringUtils.isEmpty(value) && !"sign".equals(key)) {
                urlStr.append("&").append(key).append("=").append(value);
            }
        }
        //url最后拼接secretKey
        urlStr.append("&").append(secretKey);
        return sha1Hex(urlStr.toString().replaceFirst("&", ""));
    }

    /**
     * sha1Hex加密
     */
    public String sha1Hex(String str) {
        try {
            if(StringUtils.isNotBlank(str)){
                return DigestUtils.sha1Hex(str).toUpperCase();
            }else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将map转换成url
     *
     * @param map
     */
    public String mapToUrlParams(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

}

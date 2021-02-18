package com.lzx.cloud.client;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.cloud.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.management.ObjectName;
import javax.validation.constraints.NotNull;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudClientApplicationTests {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

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
    public void testTobData() {
        Map<String, Object> map = new HashMap<>(10);
        map.put("accesskey", "lizhixinvpn");
        map.put("accessid", "VMS");
        map.put("time", Instant.now().toEpochMilli());
        map.put("channelId", "EPGC1386744804340101");
        map.put("channelName", "CCTV");
        String sign = createSign(map, "");
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
            if (StringUtils.isNotBlank(str)) {
                return DigestUtils.sha1Hex(str).toUpperCase();
            } else {
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

    @Test
    public void TestReflect() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setAge(12);
        user.setUsername("test");
        user.setRequestId("12121212");
        user.setBalance(12.0);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println("-------------------------------------");

//        Class clazz = user.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        Arrays.stream(fields).map(Field::getName).forEach(x -> System.out.println(x));
//        System.out.println("-------------------------------------");
//        Method[] methods = clazz.getMethods();
//        Arrays.stream(methods).map(Method::getName).forEach(x -> System.out.println(x));
//        System.out.println("-------------------------------------");
        String value = reflectGetMethod(user, "name");
        System.out.println("reflectGetMethod----外------" + value);
//        reflectGetMethod1(user,"name");
    }

    /**
     * @Description 反射获取value
     * @Date 17:25 2020/10/9
     **/
    private String reflectGetMethod(User user, String fieldName) {
        try {
            Map<String, PropertyDescriptor> map = new LinkedHashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(user.getClass());
            Arrays.stream(beanInfo.getPropertyDescriptors()).filter(p -> {
                String name = p.getName();
                //过滤掉不需要修改的属性
                return !"class".equals(name);
            }).collect(Collectors.toList()).forEach(x -> map.put(x.getName(), x));
            if(map.get(fieldName) != null){
                PropertyDescriptor descriptor = map.get(fieldName);
                Method readMethod = descriptor.getReadMethod();
                Object o = readMethod.invoke(user);
                System.out.println(descriptor.getName() + ":" + o);
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "12121";
    }

    /**
     * @Description 反射获取value
     * @Date 17:25 2020/10/9
     **/
    private String reflectGetMethod1(User user, String fieldName) {
        try {
            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(user.getClass(), fieldName);
            Method readMethod = descriptor.getReadMethod();
            Object o = readMethod.invoke(user);
            System.out.println(descriptor.getName() + ":" + o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "12121";
    }

    @Test
    public void testSwitch() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        map.put("4", "4");

        for (String s : map.keySet()) {
            switch (s) {
                case "1":
                case "2":
                case "3":
                case "4":
                    System.out.println(map.get("4"));
                    break;
            }
        }
    }

    @Test
    public void testListSub(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(i);
        }

        for (int i = 0; i < 10; i++) {
            List<Integer> a = list.subList(i * 5, (i + 1) * 5);
            a.forEach(x -> System.out.println(x));
            System.out.println("---------------------------");
        }
    }

    @Test
    public void testJSON(){
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setAge(12);
//        user.setUsername("test");
        user.setRequestId("12121212");
        user.setBalance(12.0);
//        String jsonn = JSO
        String b = null;
        String a = Optional.ofNullable(b).orElse("");
        System.out.println(a);
    }

    @Test
    public void testList() {
        Map<Integer, Integer> map = new LinkedHashMap<>();

        for (Integer a : map.keySet()) {
            System.out.println(a);
        }
    }

    @Test
    public void testList1(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add(i);
        }
        System.out.println(list.size());
        System.out.println(list.subList(0,15));
    }

    @Test
    public void testRedisTemplate(){
        Set<String> keys = redisTemplate.keys("navigation#*");
        System.out.println(keys);
//        Long delete = stringRedisTemplate.delete(keys);
//        System.out.println(delete);
    }

}

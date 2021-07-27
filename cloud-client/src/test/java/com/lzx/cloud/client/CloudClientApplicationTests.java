package com.lzx.cloud.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.lzx.cloud.client.entity.User;
import com.lzx.cloud.client.util.HttpUtils;
import com.lzx.cloud.protoc.PersonTestProtos;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
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

    @Test
    public void testException(){
        try {
            int a = 1 / 0;
//            List<Integer> b = new ArrayList();
//            int c = b.get(10);
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println(e.toString());
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Test
    public void testEq(){
        String a = "{\"trackId\":1,\"title\":\"太阳系外射线会影响“土卫六”大气成分\",\"brief\":\"日本东京大学饭野孝浩特任准教授领导的一个研究小组，利用ALMA望远镜对土星卫星“土卫六”的大气进行观测，检测出了微量气体释放的电波。\",\"subTitle\":null,\"vsetType\":null,\"showStyle\":\"0\",\"img1\":\"https://p4.img.cctvpic.com/photoworkspace/2020/02/17/2020021713470783989.jpg\",\"img2\":\"https://p4.img.cctvpic.com/photoworkspace/2020/02/17/2020021713470783989.jpg\",\"img3\":\"\",\"vtype\":\"31\",\"jumpUrl\":\"cntvcbox%3A%2F%2Fapp.cntv.cn%2FimageText%3FitemId%3DARTIv5qu9yIbMudW4Z4MGOjE200217%26title%3D%25E5%25A4%25AA%25E9%2598%25B3%25E7%25B3%25BB%25E5%25A4%2596%25E5%25B0%2584%25E7%25BA%25BF%25E4%25BC%259A%25E5%25BD%25B1%25E5%2593%258D%25E2%2580%259C%25E5%259C%259F%25E5%258D%25AB%25E5%2585%25AD%25E2%2580%259D%25E5%25A4%25A7%25E6%25B0%2594%25E6%2588%2590%25E5%2588%2586\",\"playid\":null,\"itemId\":\"ARTIv5qu9yIbMudW4Z4MGOjE200217\",\"itemUrl\":\"http://news.cctv.com/2020/02/17/ARTIv5qu9yIbMudW4Z4MGOjE200217.shtml\",\"mid\":\"ARTIv5qu9yIbMudW4Z4MGOjE200217\",\"adid\":null,\"videoLength\":null,\"cornerStr\":null,\"cornerStrRb\":null,\"cornerColour\":null,\"startTime\":0,\"endTime\":null,\"isVip\":0,\"hoverText\":null,\"source\":\"土卫六 大气成分\",\"imgRGB\":null,\"isTop\":0,\"recordNumber\":0,\"epgId\":null,\"epgName\":null,\"epgType\":null,\"epgChnlChar\":null,\"epgDarkDiamondPic\":null,\"epgDarkHorizontalPic\":null,\"epgHorizontalPic\":null,\"epgDiamondPic\":null,\"videoSetId\":null,\"homologous\":0,\"is_media_account\":null,\"media_account_id\":null,\"copyright\":null,\"copyrightStart\":null,\"copyrightPeriod\":null,\"skinColor\":null,\"isPublish\":1,\"isDraft\":0}";
//        String b = "{\"trackId\":2,\"title\":\"太阳系外射线会影响“土卫六”大气成分\",\"brief\":\"日本东京大学饭野孝浩特任准教授领导的一个研究小组，利用ALMA望远镜对土星卫星“土卫六”的大气进行观测，检测出了微量气体释放的电波。\",\"subTitle\":null,\"vsetType\":null,\"showStyle\":\"0\",\"img1\":\"https://p4.img.cctvpic.com/photoworkspace/2020/02/17/2020021713470783989.jpg\",\"img2\":\"https://p4.img.cctvpic.com/photoworkspace/2020/02/17/2020021713470783989.jpg\",\"img3\":\"\",\"vtype\":\"31\",\"jumpUrl\":\"cntvcbox%3A%2F%2Fapp.cntv.cn%2FimageText%3FitemId%3DARTIv5qu9yIbMudW4Z4MGOjE200217%26title%3D%25E5%25A4%25AA%25E9%2598%25B3%25E7%25B3%25BB%25E5%25A4%2596%25E5%25B0%2584%25E7%25BA%25BF%25E4%25BC%259A%25E5%25BD%25B1%25E5%2593%258D%25E2%2580%259C%25E5%259C%259F%25E5%258D%25AB%25E5%2585%25AD%25E2%2580%259D%25E5%25A4%25A7%25E6%25B0%2594%25E6%2588%2590%25E5%2588%2586\",\"playid\":null,\"itemId\":\"ARTIv5qu9yIbMudW4Z4MGOjE200217\",\"itemUrl\":\"http://news.cctv.com/2020/02/17/ARTIv5qu9yIbMudW4Z4MGOjE200217.shtml\",\"mid\":\"ARTIv5qu9yIbMudW4Z4MGOjE200217\",\"adid\":null,\"videoLength\":null,\"cornerStr\":null,\"cornerStrRb\":null,\"cornerColour\":null,\"startTime\":0,\"endTime\":null,\"isVip\":0,\"hoverText\":null,\"source\":\"土卫六 大气成分\",\"imgRGB\":null,\"isTop\":0,\"recordNumber\":0,\"epgId\":null,\"epgName\":null,\"epgType\":null,\"epgChnlChar\":null,\"epgDarkDiamondPic\":null,\"epgDarkHorizontalPic\":null,\"epgHorizontalPic\":null,\"epgDiamondPic\":null,\"videoSetId\":null,\"homologous\":0,\"is_media_account\":null,\"media_account_id\":null,\"copyright\":null,\"copyrightStart\":null,\"copyrightPeriod\":null,\"skinColor\":null,\"isPublish\":1,\"isDraft\":0}";
        System.out.println(a.equals(null));
    }

    @Test
    public void testSplit(){
//        String a = "null#cboxoms";
//        String[] split = a.split("#");
//        Arrays.stream(split).forEach(System.out::println);
//        Integer s = Integer.valueOf(split[0]);
//        System.out.println(s);

        String a = "http://10.52.29.45:8080/cboxoms/profile/jsonFile/dominatingScreen/2021/06/03/rhzm3_1921500882290794496.json";
        System.out.println("/Users/lizhixin/uploadPath" + a.split("/profile")[1]);

    }


    @Test
    public void testRandom(){
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            System.out.println(random.nextInt(10));
        }
    }

    @Test
    public void testHttp(){
        String url = "https://app.cctv.com/oms/api/promote/ouzhoubei";
        String s = HttpUtils.sendGet(url, "");
        System.out.println(s);
    }

    @Test
    public void testFastJson() throws IOException {
        System.out.println(JSON.toJSONString(1));

        Map<String, String> data = new LinkedHashMap<>();
        data.put("code", "12121");
        System.out.println(JSON.toJSONString(data));

        Map<String, Object> json = new LinkedHashMap<>();
        File file = new File("/Users/lizhixin/uploadPath/cboxDominatingScreen/sportsEventsDominatingScreen.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        json = JSON.parseObject(fileInputStream, Map.class, Feature.AutoCloseSource);
        System.out.println(JSON.toJSONString(json));

        JSONObject.toJSONString(new HashMap<>());
    }

    @Test
    public void testProtoc(){
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
//            System.out.println(byteString.toString());
//            System.out.println(personTest.toBuilder().toString());
//            System.out.println(Arrays.toString(personTest.toByteArray()));
//            System.out.println(personTest);
            // 反序列化
            PersonTestProtos.PersonTest personTestResult = PersonTestProtos.PersonTest.parseFrom(byteString);
//            System.out.println(personTestResult);


            //to Json
            JsonFormat.Printer printer = JsonFormat.printer();
            String print = "";
            try {
                print = printer.includingDefaultValueFields().print(personTest);
                System.out.println(print);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            Person myPerson = gson.fromJson(print, Person.class);
            System.out.println(myPerson);
            print = gson.toJson(myPerson);
            System.out.println(print);

            //to Object
            JsonFormat.Parser parser = JsonFormat.parser();
            try {
                PersonTestProtos.PersonTest.Builder newBuilder = PersonTestProtos.PersonTest.newBuilder();
                parser.ignoringUnknownFields().merge(print, newBuilder);
                System.out.println(newBuilder.build());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }



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
    }

    //添加java bean 此类对性数据库的字段，同时与proto类属性名相同
    @Data
    public class Person implements Serializable {
        private String name;
        private Integer age;
        private Boolean sex;
        private Date dirthday;//此处注意这里是时间类型而非proto类中的long类型
        private String address;
        private List<Car> cars = new ArrayList<Car>();
        private Map<String, String> other = new HashMap<String, String>();

        public class Car implements Serializable {
            private String name;
            private String color;
        }
    }

}

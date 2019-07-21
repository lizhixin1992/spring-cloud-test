package com.lzx.cloud.client.feign;

import com.lzx.cloud.client.entity.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName
 * @Description TODO
 * @Date 2019-07-18 09:53
 **/
@FeignClient(name = "cloud-provider", fallbackFactory = FeignClientFallbackFactory.class)
public interface UserFeignClient {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable("id") Long id);

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<User> saveUser();
}

/**
 * UserFeignClient的fallbackFactory类，该类需实现FallbackFactory接口，并覆写create方法 ---------断路器实现
 */
@Slf4j
@Component
class FeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public User findById(Long id) {
                FeignClientFallbackFactory.log.info("fallback; reason was:", cause);
                User user = new User();
                user.setId(-1L);
                user.setUsername("默认用户");
                user.setAge(0);
                user.setBalance((double) 0);
                return user;
            }

            @Override
            public List<User> saveUser() {
                FeignClientFallbackFactory.log.info("fallback; reason was:", cause);
                return new ArrayList<>();
            }
        };
    }
}
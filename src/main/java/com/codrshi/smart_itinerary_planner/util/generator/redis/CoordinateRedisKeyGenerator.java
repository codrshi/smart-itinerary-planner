package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component(Constant.COORDINATE_KEY_GENERATOR)
@Slf4j
public class CoordinateRedisKeyGenerator extends RedisKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if(params.length == 1 && params[0] instanceof ILocationDTO locationDTO) {
            return generateKeyWithoutContext(Constant.COORDINATE_KEY, locationDTO.getCity(),
                                             locationDTO.getCountryCode());
        }
        return generateRandomKey();
    }
}

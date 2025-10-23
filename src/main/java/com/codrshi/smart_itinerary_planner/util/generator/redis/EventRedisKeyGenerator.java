package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.dto.ILocationDTO;
import com.codrshi.smart_itinerary_planner.dto.ITimePeriodDTO;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component(Constant.EVENT_KEY_GENERATOR)
public class EventRedisKeyGenerator extends RedisKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if(params.length == 2 && params[0] instanceof ILocationDTO locationDTO && params[1] instanceof ITimePeriodDTO timePeriodDTO) {
            return generateKeyWithoutContext(Constant.EVENT_KEY, locationDTO.getCity(), locationDTO.getCountryCode(),
                                             String.format("{}_{}", timePeriodDTO.getStartDate(), timePeriodDTO.getEndDate()));
        }

        return generateRandomKey();
    }
}

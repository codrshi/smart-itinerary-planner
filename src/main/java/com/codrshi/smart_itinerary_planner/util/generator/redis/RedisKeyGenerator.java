package com.codrshi.smart_itinerary_planner.util.generator.redis;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public abstract class RedisKeyGenerator {

    protected static String generateKeyWithoutContext(Object... params) {

        StringBuilder sb = new StringBuilder(Constant.ITIX_KEY_PREFIX);

        for(Object param:params) {
            sb.append(":").append(param.toString());
        }

        return sb.toString();
    }

    protected static String generateKeyWithContext(Object... params) {

        String username = RequestContext.getCurrentContext().getUsername();

        if(Constant.SYSTEM_USER.equals(username)){
            log.warn("Skipping cache for invalid user context");
            return UUID.randomUUID().toString(); // force cache miss for anonymous user
        }

        StringBuilder sb = new StringBuilder(Constant.ITIX_KEY_PREFIX);
        sb.append(":").append(username);

        for(Object param:params) {
            sb.append(":").append(param.toString());
        }

        return sb.toString();
    }

    protected static String generateKeyMissingContext(String username, Object... params) {
        return generateKeyWithoutContext(username, params);
    }

    protected static String generateRandomKey() {
        return UUID.randomUUID().toString();
    }
}

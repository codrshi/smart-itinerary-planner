package com.codrshi.smart_itinerary_planner.common.aspect;

import com.codrshi.smart_itinerary_planner.config.ItineraryProperties;
import com.codrshi.smart_itinerary_planner.exception.GenerateMailException;
import com.codrshi.smart_itinerary_planner.service.IMailService;
import com.codrshi.smart_itinerary_planner.service.implementation.AuxiliaryService;
import com.codrshi.smart_itinerary_planner.util.RequestContext;
import com.codrshi.smart_itinerary_planner.util.generator.redis.AuxiliaryRedisKeyGenerator;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@Slf4j
public class AuxiliaryCacheAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IMailService mailService;

    @Autowired
    private ItineraryProperties itineraryProperties;

    @Around(value = "execution(* com.codrshi.smart_itinerary_planner.service.implementation.AuxiliaryService.mailItinerary(..))",
            argNames = "itineraryId")
    public String checkCacheBeforeGeneratingMail(ProceedingJoinPoint joinPoint, String itineraryId)
            throws MessagingException {

        String email = RequestContext.getCurrentContext().getEmail();
        String blacklistedMailKey = AuxiliaryRedisKeyGenerator.generateBlacklistedMail();

        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(blacklistedMailKey, email))){
            log.warn("Skipping mail generation for {} as mail is either invalid or facing temporary issue.", email);
            throw new GenerateMailException();
        }

        String mailedItineraryKey = AuxiliaryRedisKeyGenerator.generateMailedItinerary(itineraryId);
        String mailBody = fromCache(mailedItineraryKey);

        try {
            if(mailBody != null) {
                log.debug("CACHE HIT: mail body found for itineraryId = {}: {}", itineraryId, mailBody);
                mailService.sendMail(AuxiliaryService.getSubjectContent(itineraryId), mailBody);
            }
            else {
                log.debug("CACHE MISS: mail body not found for itineraryId = {}", itineraryId);
                mailBody = (String) joinPoint.proceed();
                redisTemplate.opsForValue().set(mailedItineraryKey, mailBody);
            }
        }
        catch (MessagingException | MailException e) {
            log.error("Failed to send mail for itineraryId = {}, blacklisting email = {}", itineraryId, email, e);

            if(Boolean.FALSE.equals(redisTemplate.hasKey(blacklistedMailKey))) {
                redisTemplate.opsForSet().add(blacklistedMailKey, email);
                redisTemplate.expire(blacklistedMailKey, Duration.ofMinutes(itineraryProperties.getRedis().getBlacklistedMailsTtl()));
            } else {
                redisTemplate.opsForSet().add(blacklistedMailKey, email);
            }

            throw e;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return mailBody;
    }

    private String fromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}

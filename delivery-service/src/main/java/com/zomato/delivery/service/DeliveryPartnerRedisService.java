package com.zomato.delivery.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class DeliveryPartnerRedisService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String AVAILABLE_PARTNERS_KEY = "available_partners";
    private static final String PARTNER_LOCATIONS_KEY = "partner_locations";

    public void markAvailable(Long partnerId){
        String partnerId_inString = String.valueOf(partnerId);
        redisTemplate.opsForSet().add(AVAILABLE_PARTNERS_KEY,partnerId_inString);
    }

    public void markUnavailable(Long partnerId){
        String partnerId_inString = String.valueOf(partnerId);
        redisTemplate.opsForSet().remove(AVAILABLE_PARTNERS_KEY,partnerId_inString);
    }

    public void updateLocation(Long partnerId,Double lat, Double lng){
        
        String partnerId_inString = String.valueOf(partnerId);
        redisTemplate.opsForGeo().add(PARTNER_LOCATIONS_KEY, new Point(lng,lat),partnerId_inString);
    }
    
    public List<Long> findNearestAvailablePartner(Double lat, Double lng,Double radiusKm){
        
        Circle area = new Circle(new Point(lng,lat), new Distance(radiusKm, Metrics.KILOMETERS));

        List<Long> responseList = new ArrayList<>();

        GeoResults<RedisGeoCommands.GeoLocation<String>> result =  redisTemplate.opsForGeo().radius(PARTNER_LOCATIONS_KEY, area,RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().sortAscending());
        if(result==null) return responseList;
        for(GeoResult<RedisGeoCommands.GeoLocation<String>> partner: result){
            String partnerIDString = partner.getContent().getName();
            if(partnerIDString==null) continue;
            if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(AVAILABLE_PARTNERS_KEY, partnerIDString))){
                responseList.add(Long.valueOf( partner.getContent().getName()));
            }
        }

        return responseList;
    }

    public Point getLocation(Long partnerId) {
        String partnerId_inString = String.valueOf(partnerId);
        List<Point> cordinates = redisTemplate.opsForGeo().position(PARTNER_LOCATIONS_KEY,partnerId_inString);
        if(cordinates!=null && !cordinates.isEmpty() && cordinates.get(0)!=null){
            return cordinates.get(0);
        }
        return null;
    }
}

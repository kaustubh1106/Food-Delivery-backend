package com.zomato.controller;

import com.zomato.dto.request.DeliveryPartnerRequest;
import com.zomato.dto.request.LocationRequest;
import com.zomato.dto.response.DeliveryPartnerResponse;
import com.zomato.entity.Location;
import com.zomato.service.DeliveryPartnerRedisService;
import com.zomato.service.DeliveryPartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/delivery-partners")
@RequiredArgsConstructor
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;
    @Autowired
    DeliveryPartnerRedisService redisService;
    @Autowired
    SimpMessagingTemplate messageTemplate;

    @PostMapping
    public ResponseEntity<DeliveryPartnerResponse> addDeliveryPartner(
            @Valid @RequestBody DeliveryPartnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryPartnerService.addDeliveryPartner(request));
    }

    @PutMapping("/{id}/available")
    public ResponseEntity<String> markAvailable(@PathVariable Long id) {
        redisService.markAvailable(id);
        return ResponseEntity.ok("status set as available");
    }

    @PutMapping("/{id}/unavailable")
    public ResponseEntity<String> markUnavailable(@PathVariable Long id) {
        redisService.markUnavailable(id);
        return ResponseEntity.ok("status set as unavailable");
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<String> updateLocation(@PathVariable Long id,@RequestBody LocationRequest location) {
        messageTemplate.convertAndSend("/topic/location/"+id,location);
        redisService.updateLocation(id,location.getLatitude(),location.getLongitude());
        return ResponseEntity.ok("location updated!");
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<Location> getLocation(@PathVariable Long id) {
        Point coordinates = redisService.getLocation(id);
        if(coordinates==null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new Location(coordinates.getY(),coordinates.getX()));
    }


    @GetMapping("/nearest")
    public ResponseEntity<List<Long>> getMethodName(@RequestParam Double latitude,@RequestParam Double longitude, @RequestParam Double radiusKm) {
        List<Long> res = redisService.findNearestAvailablePartner(latitude,longitude, radiusKm);
        return ResponseEntity.ok(res);
    }
    

}

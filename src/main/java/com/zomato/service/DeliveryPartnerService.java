package com.zomato.service;

import com.zomato.dto.request.DeliveryPartnerRequest;
import com.zomato.dto.response.DeliveryPartnerResponse;
import com.zomato.entity.DeliveryPartner;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zomato.repository.DeliveryPartnerRepository;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public DeliveryPartnerResponse addDeliveryPartner(DeliveryPartnerRequest request) {
        DeliveryPartner partner = DeliveryPartner.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .vehicleType(request.getVehicleType())
                .availabilityStatus(DeliveryPartner.AvailabilityStatus.AVAILABLE)
                .build();

        DeliveryPartner saved = deliveryPartnerRepository.save(partner);
        return modelMapper.map(saved, DeliveryPartnerResponse.class);
    }
}

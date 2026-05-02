package com.zomato.delivery.service;

import com.zomato.delivery.dto.request.DeliveryPartnerRequest;
import com.zomato.delivery.dto.response.DeliveryPartnerResponse;
import com.zomato.delivery.entity.DeliveryPartner;
import com.zomato.delivery.exception.ResourceNotFoundException;
import com.zomato.delivery.repository.DeliveryPartnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public DeliveryPartnerResponse getDeliveryPartnerById(Long id) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found with id: " + id));
        return modelMapper.map(partner, DeliveryPartnerResponse.class);
    }
}

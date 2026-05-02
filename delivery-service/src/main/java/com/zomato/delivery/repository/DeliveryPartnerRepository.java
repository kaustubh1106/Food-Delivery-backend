package com.zomato.delivery.repository;

import com.zomato.delivery.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
}

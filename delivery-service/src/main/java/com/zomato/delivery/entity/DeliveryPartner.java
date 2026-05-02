package com.zomato.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;



    private Double rating;

    public enum AvailabilityStatus {
        AVAILABLE, BUSY, OFFLINE
    }

   
}

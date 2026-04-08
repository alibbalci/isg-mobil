package com.alibalci.isgmobil.isg.isgbackend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id  ;

    private String name ;

    private String address ;

    private String hazardClass;

    private String phone ;

    private String occupationalPhysician ;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")

    private User user ;

}

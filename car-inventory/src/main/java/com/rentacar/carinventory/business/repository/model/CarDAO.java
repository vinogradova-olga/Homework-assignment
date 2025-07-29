package com.rentacar.carinventory.business.repository.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "car")
public class CarDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id;

    @Column(name = "make")
    private String make;

    @Column(name="model")
    private String model;

    @Column(name="gearbox")
    private String gearbox;

    @Column(name="fuel")
    private String fuel;

    @Column(name="year")
    private int year;

    @Column(name="price")
    private double price;

    @Column(name="available")
    private boolean available;
}

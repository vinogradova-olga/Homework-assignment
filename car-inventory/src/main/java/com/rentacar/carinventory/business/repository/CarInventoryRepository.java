package com.rentacar.carinventory.business.repository;

import com.rentacar.carinventory.business.repository.model.CarDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarInventoryRepository extends JpaRepository<CarDAO, Long> {
}

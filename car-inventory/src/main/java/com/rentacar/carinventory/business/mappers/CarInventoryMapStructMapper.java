package com.rentacar.carinventory.business.mappers;

import com.rentacar.carinventory.business.repository.model.CarDAO;
import com.rentacar.carinventory.model.Car;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface CarInventoryMapStructMapper {

    CarDAO carToCarDAO (Car car);

    Car carDAOToCar (CarDAO carDAO);
}

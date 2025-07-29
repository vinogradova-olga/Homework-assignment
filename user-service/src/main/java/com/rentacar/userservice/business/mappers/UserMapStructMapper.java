package com.rentacar.userservice.business.mappers;

import com.rentacar.userservice.business.repository.model.UserDAO;
import com.rentacar.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapStructMapper {
    UserDAO userToUserDAO (User user);

    User userDAOToUser (UserDAO userDAO);
}

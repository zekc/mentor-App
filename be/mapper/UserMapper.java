package com.obss.mentorapp.mapper;

import com.obss.mentorapp.dto.UserDTO;
import com.obss.mentorapp.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        // Eğer diğer alanlar eklenirse onları da burada map edebilirsiniz

        return userDTO;
    }

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        // Eğer diğer alanlar eklenirse onları da burada map edebilirsiniz

        return user;
    }
}

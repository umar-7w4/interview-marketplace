package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper class that converts Data Transfer Object to user entity object.
 * 
 * @author Umar Mohammad
 */

public class UserMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Map User Entity to UserDto.
     * 
     * @param user
     * @return
     */

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPasswordHash(user.getPassword());
        userDto.setRole(user.getRole() != null ? user.getRole().name() : null);
        userDto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setProfilePictureUrl(user.getProfilePictureUrl());
        userDto.setPreferredLanguage(user.getPreferredLanguage());
        userDto.setTimezone(user.getTimezone());
        userDto.setFullName(user.getFullName());
        userDto.setFirebaseUid(user.getFirebaseUid());
        userDto.setWorkEmail(user.getWorkEmail());
        userDto.setWorkEmailVerified(user.isWorkEmailVerified());
        userDto.setEmailVerified(user.isEmailVerified());

        if (user.getCreatedAt() != null) {
            userDto.setCreatedAt(FORMATTER.format(user.getCreatedAt()));
        }
        if (user.getLastLogin() != null) {
            userDto.setLastLogin(FORMATTER.format(user.getLastLogin()));
        }

        return userDto;
    }
    
    /**
     * Map UserDto to User Entity
     * 
     * @param userDto
     * @return
     */

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPasswordHash());
        user.setRole(userDto.getRole() != null ? User.Role.valueOf(userDto.getRole()) : null);
        user.setStatus(userDto.getStatus() != null ? User.Status.valueOf(userDto.getStatus()) : null);
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setProfilePictureUrl(userDto.getProfilePictureUrl());
        user.setPreferredLanguage(userDto.getPreferredLanguage());
        user.setTimezone(userDto.getTimezone());
        user.setFirebaseUid(userDto.getFirebaseUid());
        user.setWorkEmail(userDto.getWorkEmail());
        user.setWorkEmailVerified(userDto.isWorkEmailVerified());
        user.setEmailVerified(userDto.isEmailVerified());

        if (userDto.getCreatedAt() != null) {
            user.setCreatedAt(LocalDateTime.parse(userDto.getCreatedAt(), FORMATTER));
        }
        if (userDto.getLastLogin() != null) {
            user.setLastLogin(LocalDateTime.parse(userDto.getLastLogin(), FORMATTER));
        }

        return user;
    }
}

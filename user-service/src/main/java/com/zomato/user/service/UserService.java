package com.zomato.user.service;

import com.zomato.user.dto.request.UserRequest;
import com.zomato.user.dto.response.UserResponse;
import com.zomato.user.entity.User;
import com.zomato.user.exception.ResourceNotFoundException;
import com.zomato.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public UserResponse registerUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = modelMapper.map(request, User.class);
        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponse.class);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponse.class);
    }
}

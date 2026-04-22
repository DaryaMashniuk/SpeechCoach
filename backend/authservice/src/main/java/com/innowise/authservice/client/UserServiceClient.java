package com.innowise.authservice.client;

import com.innowise.authservice.model.dto.request.UserRequestDto;
import com.innowise.authservice.model.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "userService",
        url = "${userservice.url}",
        path = "/"
)
public interface UserServiceClient {

  @PostMapping("/api/v1/users")
  UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto);

  @DeleteMapping("/api/v1/users/{id}")
  Void deleteUser(@PathVariable("id") Long id);
}

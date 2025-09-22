package com.example.ComputerStore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải có từ 2-100 ký tự")
    private String fullName;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 1, max = 255, message = "Địa chỉ phải có từ 1-255 ký tự")
    private String address;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[0-9]{8,11}$", message = "Số điện thoại không hợp lệ (8-11 chữ số)")
    private String phone;
}

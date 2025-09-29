package com.example.ComputerStore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contact creation request")
public class CreateContactDto {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 255, message = "Họ và tên phải từ 2-255 ký tự")
    @Schema(description = "Customer's full name", example = "Nguyễn Văn A")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 số")
    @Schema(description = "Customer's phone number", example = "0901234567")
    private String phone;

    @Schema(description = "Customer's email (optional)", example = "customer@email.com")
    private String email;

    @Schema(description = "Additional message or notes (optional)", example = "Cần tư vấn thêm về tản nhiệt")
    private String message;

    @NotBlank(message = "Nhu cầu mua PC không được để trống")
    @Schema(description = "Purchase intention", 
            example = "Có", 
            allowableValues = {"Có", "Không", "Tham khảo (mua sau)", "Muốn được tư vấn cấu hình rồi mua"})
    private String option1;

    @NotBlank(message = "Mục đích sử dụng không được để trống")
    @Schema(description = "Usage purpose", 
            example = "Chơi Game nặng + Livestream: GTA 5, Game AAA, Game Offline nặng")
    private String option2;

    @NotBlank(message = "Ngân sách không được để trống")
    @Schema(description = "Budget range", 
            example = "PC 25 triệu: Chơi game 2K Maxseting + Thoải mái làm việc đồ họa")
    private String option3;

    @NotBlank(message = "Phương thức nhận hàng không được để trống")
    @Schema(description = "Delivery/Payment method", 
            example = "Cọc 10% giá trị đơn hàng - Nhận hàng kiểm tra rùi thanh toán số tiền còn lại")
    private String option4;
}
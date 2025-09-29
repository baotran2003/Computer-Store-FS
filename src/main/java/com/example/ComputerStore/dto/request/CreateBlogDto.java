package com.example.ComputerStore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Blog creation/update request")
public class CreateBlogDto {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 500, message = "Tiêu đề phải từ 5-500 ký tự")
    @Schema(description = "Blog title", example = "Hướng dẫn chọn CPU phù hợp cho gaming")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 50, message = "Nội dung phải ít nhất 50 ký tự")
    @Schema(description = "Blog content", example = "CPU là tim của máy tính gaming...")
    private String content;

    @NotBlank(message = "Hình ảnh không được để trống")
    @Schema(description = "Blog featured image URL", example = "https://example.com/images/cpu-guide.jpg")
    private String image;
}
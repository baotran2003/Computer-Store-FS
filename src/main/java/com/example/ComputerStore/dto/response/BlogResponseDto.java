package com.example.ComputerStore.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Blog response")
public class BlogResponseDto {

    @Schema(description = "Blog ID")
    private UUID id;

    @Schema(description = "Blog title", example = "Hướng dẫn chọn CPU phù hợp cho gaming")
    private String title;

    @Schema(description = "Blog content", example = "CPU là tim của máy tính gaming...")
    private String content;

    @Schema(description = "Blog featured image URL", example = "https://example.com/images/cpu-guide.jpg")
    private String image;

    @Schema(description = "Blog creation time", example = "2025-09-29 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createdAt;

    @Schema(description = "Blog last update time", example = "2025-09-29 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updatedAt;
}
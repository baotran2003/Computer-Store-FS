package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@Slf4j
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Value("${file.upload-dir:src/main/java/com/example/ComputerStore/uploads/images}")
    private String uploadDir;

    /**
     * Upload single image - POST /api/uploads/image
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("File không được để trống")
                        .build());
            }

            // Validate file type
            String contentType = file.getContentType();
            if (!isImageFile(contentType)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Chỉ chấp nhận file hình ảnh (jpg, jpeg, png, gif, webp)")
                        .build());
            }

            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = System.currentTimeMillis() + fileExtension;
            
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative URL
            String imageUrl = "/api/uploads/images/" + uniqueFilename;

            return ResponseEntity.ok(
                ApiResponse.<String>builder()
                    .success(true)
                    .message("Upload hình ảnh thành công")
                    .data(imageUrl)
                    .build()
            );

        } catch (IOException e) {
            log.error("Error uploading image", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Lỗi server khi upload hình ảnh")
                    .build());
        }
    }

    /**
     * Upload multiple images - POST /api/uploads/images
     */
    @PostMapping("/images")
    public ResponseEntity<ApiResponse<List<String>>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files) {
        
        List<String> imageUrls = new ArrayList<>();
        
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // Validate file type
                String contentType = file.getContentType();
                if (!isImageFile(contentType)) {
                    continue; // Skip invalid files
                }

                // Create upload directory if not exists
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
                
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Add to result list
                String imageUrl = "/api/uploads/images/" + uniqueFilename;
                imageUrls.add(imageUrl);
            }

            if (imageUrls.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.<List<String>>builder()
                        .success(false)
                        .message("Không có file hình ảnh hợp lệ nào được upload")
                        .build());
            }

            return ResponseEntity.ok(
                ApiResponse.<List<String>>builder()
                    .success(true)
                    .message("Upload " + imageUrls.size() + " hình ảnh thành công")
                    .data(imageUrls)
                    .build()
            );

        } catch (IOException e) {
            log.error("Error uploading multiple images", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.<List<String>>builder()
                    .success(false)
                    .message("Lỗi server khi upload hình ảnh")
                    .build());
        }
    }

    /**
     * Serve uploaded images - GET /api/uploads/images/{filename}
     */
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Determine content type
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("Error serving image: {}", filename, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error reading image: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get list of existing images - GET /api/uploads/images
     */
    @GetMapping("/images")
    public ResponseEntity<ApiResponse<List<String>>> getExistingImages() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return ResponseEntity.ok(
                    ApiResponse.<List<String>>builder()
                        .success(true)
                        .message("Thư mục upload chưa tồn tại")
                        .data(new ArrayList<>())
                        .build()
                );
            }

            List<String> imageUrls = new ArrayList<>();
            Files.walk(uploadPath, 1)
                .filter(Files::isRegularFile)
                .filter(path -> isImageFile(path.toString()))
                .forEach(path -> {
                    String filename = path.getFileName().toString();
                    String imageUrl = "/api/uploads/images/" + filename;
                    imageUrls.add(imageUrl);
                });

            return ResponseEntity.ok(
                ApiResponse.<List<String>>builder()
                    .success(true)
                    .message("Lấy danh sách hình ảnh thành công")
                    .data(imageUrls)
                    .build()
            );

        } catch (IOException e) {
            log.error("Error getting existing images", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.<List<String>>builder()
                    .success(false)
                    .message("Lỗi server khi lấy danh sách hình ảnh")
                    .build());
        }
    }

    /**
     * Delete image - DELETE /api/uploads/images/{filename}
     */
    @DeleteMapping("/images/{filename}")
    public ResponseEntity<ApiResponse<String>> deleteImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return ResponseEntity.ok(
                    ApiResponse.<String>builder()
                        .success(true)
                        .message("Xóa hình ảnh thành công")
                        .data(filename)
                        .build()
                );
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Error deleting image: {}", filename, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Lỗi server khi xóa hình ảnh")
                    .build());
        }
    }

    // Helper methods
    private boolean isImageFile(String contentTypeOrFilename) {
        if (contentTypeOrFilename == null) return false;
        
        String lower = contentTypeOrFilename.toLowerCase();
        return lower.contains("image/") || 
               lower.endsWith(".jpg") || 
               lower.endsWith(".jpeg") || 
               lower.endsWith(".png") || 
               lower.endsWith(".gif") || 
               lower.endsWith(".webp") ||
               lower.endsWith(".bmp");
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Default extension
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
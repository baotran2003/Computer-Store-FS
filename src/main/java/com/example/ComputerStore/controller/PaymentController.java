package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.CancelOrderDto;
import com.example.ComputerStore.dto.request.CreatePaymentDto;
import com.example.ComputerStore.dto.request.UpdatePaymentStatusDto;
import com.example.ComputerStore.dto.response.*;
import com.example.ComputerStore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Payment Management Controller
 * 
 * REST API endpoints for:
 * - Creating payments (COD/Gateway)
 * - User order management
 * - Admin order management
 * - Payment gateway callbacks
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    //Tạo đơn hàng mới từ cart
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentUrlResponseDto>> createPayment(
            @Valid @RequestBody CreatePaymentDto createPaymentDto) {
        
        log.info("Creating payment for user: {}, type: {}", 
                createPaymentDto.getUserId(), createPaymentDto.getTypePayment());
        
        try {
            PaymentUrlResponseDto response = paymentService.createPayment(createPaymentDto);
            
            return ResponseEntity.ok(ApiResponse.<PaymentUrlResponseDto>builder()
                    .success(true)
                    .message("Payment created successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error creating payment: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.<PaymentUrlResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Lấy danh sách đơn hàng của user
    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<ApiResponse<List<OrderListResponseDto>>> getUserOrders(
            @PathVariable UUID userId) {
        
        log.info("Getting orders for user: {}", userId);
        
        try {
            List<OrderListResponseDto> orders = paymentService.getUserOrders(userId);
            
            return ResponseEntity.ok(ApiResponse.<List<OrderListResponseDto>>builder()
                    .success(true)
                    .message("Orders retrieved successfully")
                    .data(orders)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting user orders: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<OrderListResponseDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/user/{userId}/order/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponseDto>> getOrderDetail(
            @PathVariable UUID userId,
            @PathVariable String orderId) {
        
        log.info("Getting order detail: {} for user: {}", orderId, userId);
        
        try {
            OrderDetailResponseDto orderDetail = paymentService.getOrderDetail(orderId, userId);
            
            return ResponseEntity.ok(ApiResponse.<OrderDetailResponseDto>builder()
                    .success(true)
                    .message("Order detail retrieved successfully")
                    .data(orderDetail)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting order detail: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.<OrderDetailResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/user/{userId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable UUID userId,
            @Valid @RequestBody CancelOrderDto cancelOrderDto) {
        
        log.info("Cancelling order: {} for user: {}", cancelOrderDto.getOrderId(), userId);
        
        try {
            paymentService.cancelOrder(userId, cancelOrderDto);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Order cancelled successfully")
                    .data("Order " + cancelOrderDto.getOrderId() + " has been cancelled")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error cancelling order: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getAllOrders() {
        
        log.info("Admin getting all orders");
        
        try {
            List<PaymentResponseDto> orders = paymentService.getAllOrders();
            
            return ResponseEntity.ok(ApiResponse.<List<PaymentResponseDto>>builder()
                    .success(true)
                    .message("All orders retrieved successfully")
                    .data(orders)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting all orders: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<PaymentResponseDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/admin/update-status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @Valid @RequestBody UpdatePaymentStatusDto updatePaymentStatusDto) {
        
        log.info("Admin updating order status: {} to {}", 
                updatePaymentStatusDto.getIdPayment(), updatePaymentStatusDto.getStatus());
        
        try {
            paymentService.updateOrderStatus(updatePaymentStatusDto);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Order status updated successfully")
                    .data("Order " + updatePaymentStatusDto.getIdPayment() + 
                          " status updated to " + updatePaymentStatusDto.getStatus())
                    .build());
                    
        } catch (Exception e) {
            log.error("Error updating order status: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // ==================== PAYMENT GATEWAY CALLBACKS ====================

    /**
     * MOMO Payment Gateway Callback Handler
     * Processes form-data callback from MOMO payment system
     */
    @PostMapping("/callback/momo")
    public ResponseEntity<ApiResponse<String>> momoCallback(
            @RequestParam String orderId,
            @RequestParam String resultCode,
            @RequestParam(required = false) String message) {
        
        log.info("MOMO callback received - OrderId: {}, ResultCode: {}, Message: {}", 
                orderId, resultCode, message);
        
        try {
            // Determine payment status based on MOMO result code (0 = success)
            String status = "0".equals(resultCode) ? "SUCCESS" : "FAILED";
            
            paymentService.handlePaymentCallback(orderId, status, "MOMO");
            
            log.info("MOMO payment {} processed successfully with status: {}", orderId, status);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("MOMO callback processed successfully")
                    .data(String.format("Payment %s processed with status: %s", orderId, status))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error processing MOMO callback for order {}: ", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Failed to process MOMO callback")
                            .build());
        }
    }

    /**
     * VNPAY Payment Gateway Callback Handler
     * Processes query parameter callback from VNPAY payment system
     */
    @GetMapping("/callback/vnpay")
    public ResponseEntity<ApiResponse<String>> vnpayCallback(
            @RequestParam String vnp_TxnRef,
            @RequestParam String vnp_ResponseCode,
            @RequestParam(required = false) String vnp_TransactionStatus) {
        
        log.info("VNPAY callback received - TxnRef: {}, ResponseCode: {}, TransactionStatus: {}", 
                vnp_TxnRef, vnp_ResponseCode, vnp_TransactionStatus);
        
        try {
            // Determine payment status based on VNPAY response code (00 = success)
            String status = "00".equals(vnp_ResponseCode) ? "SUCCESS" : "FAILED";
            
            paymentService.handlePaymentCallback(vnp_TxnRef, status, "VNPAY");
            
            log.info("VNPAY payment {} processed successfully with status: {}", vnp_TxnRef, status);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("VNPAY callback processed successfully")
                    .data(String.format("Payment %s processed with status: %s", vnp_TxnRef, status))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error processing VNPAY callback for transaction {}: ", vnp_TxnRef, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Failed to process VNPAY callback")
                            .build());
        }
    }

    // ==================== UTILITY ENDPOINTS ====================
    /**
     * Check payment status by payment ID
     * Optimized method that searches across all payments efficiently
     */
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<ApiResponse<String>> checkPaymentStatus(@PathVariable String paymentId) {
        
        log.info("Checking payment status for ID: {}", paymentId);
        
        try {
            // Use stream to find payment efficiently
            Optional<PaymentResponseDto> paymentOpt = paymentService.getAllOrders()
                    .stream()
                    .filter(payment -> paymentId.equals(payment.getIdPayment()))
                    .findFirst();
            
            if (paymentOpt.isPresent()) {
                PaymentResponseDto payment = paymentOpt.get();
                String statusInfo = String.format(
                    "Payment ID: %s, Status: %s, Amount: %.2f VND, Type: %s", 
                    payment.getIdPayment(),
                    payment.getStatus(), 
                    payment.getTotalPrice(), 
                    payment.getTypePayment()
                );
                
                return ResponseEntity.ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Payment status retrieved successfully")
                        .data(statusInfo)
                        .build());
            }
            
            log.warn("Payment not found: {}", paymentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Payment not found: " + paymentId)
                            .build());
                    
        } catch (Exception e) {
            log.error("Error checking payment status for {}: ", paymentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Internal server error while checking payment status")
                            .build());
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Payment service is running")
                .data("OK")
                .build());
    }
}
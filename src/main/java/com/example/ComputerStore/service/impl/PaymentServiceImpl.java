package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.CancelOrderDto;
import com.example.ComputerStore.dto.request.CreatePaymentDto;
import com.example.ComputerStore.dto.request.UpdatePaymentStatusDto;
import com.example.ComputerStore.dto.response.*;
import com.example.ComputerStore.entity.Cart;
import com.example.ComputerStore.entity.Payment;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.enumeric.PaymentStatus;
import com.example.ComputerStore.enumeric.PaymentType;
import com.example.ComputerStore.repository.CartRepository;
import com.example.ComputerStore.repository.PaymentRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.PaymentService;
import com.example.ComputerStore.service.gateway.MomoGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Payment Service Implementation
 * 
 * LEARNING APPROACH:
 * - Mỗi method có step-by-step comments
 * - Business logic được explain rõ ràng
 * - Error handling patterns
 * - Transaction management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final MomoGatewayService momoGatewayService;

    @Override
    @Transactional
    public PaymentUrlResponseDto createPayment(CreatePaymentDto createPaymentDto) {
        log.info("Creating payment for user: {}, type: {}", 
                createPaymentDto.getUserId(), createPaymentDto.getTypePayment());

        // Validate user exists
        User user = userRepository.findById(createPaymentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user's cart items and validate
        List<Cart> cartItems = cartRepository.findByUserId(createPaymentDto.getUserId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = cartItems.stream()
                .map(Cart::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Generate unique payment ID
        String paymentId = generatePaymentId();
        
        // Create payment records for each cart item
        List<Payment> payments = cartItems.stream()
                .map(cartItem -> Payment.builder()
                        .idPayment(paymentId)
                        .user(user)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .totalPrice(cartItem.getTotalPrice())
                        .typePayment(createPaymentDto.getTypePayment().name())
                        .status(PaymentStatus.PENDING)
                        .fullName(createPaymentDto.getFullName())
                        .phone(createPaymentDto.getPhone())
                        .address(createPaymentDto.getAddress())
                        .build())
                .collect(Collectors.toList());
        
        // save db
        paymentRepository.saveAll(payments);
        
        // Handle payment type
        if (createPaymentDto.getTypePayment() == PaymentType.COD) {
            // COD: Update status and clear cart immediately
            payments.forEach(payment -> payment.setStatus(PaymentStatus.COMPLETED));
            paymentRepository.saveAll(payments);
            cartRepository.deleteAll(cartItems);
            
            return PaymentUrlResponseDto.builder()
                    .paymentId(paymentId)
                    .message("Order placed successfully with COD")
                    .isDirectPayment(true)
                    .totalAmount(totalAmount)
                    .build();
        } else {
            // Gateway payment: Generate real payment URL
            String paymentUrl = null;
            
            if (createPaymentDto.getTypePayment() == PaymentType.MOMO) {
                paymentUrl = momoGatewayService.createPaymentUrl(paymentId, totalAmount, user.getId().toString());
            } else if (createPaymentDto.getTypePayment() == PaymentType.VNPAY) {
                // TODO: Implement VNPAY gateway service
                paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?orderId=" + paymentId;
            }
            
            return PaymentUrlResponseDto.builder()
                    .paymentId(paymentId)
                    .message("Payment gateway URL generated")
                    .isDirectPayment(false)
                    .totalAmount(totalAmount)
                    .paymentUrl(paymentUrl != null ? paymentUrl : "https://mock-gateway.example.com/payment/" + paymentId)
                    .build();
        }
    }

    @Override
    public void handlePaymentCallback(String paymentId, String status, String gatewayType) {
        log.info("Handling payment callback: {}, status: {}, gateway: {}", 
                paymentId, status, gatewayType);

        // Find all payments with same idPayment
        List<Payment> payments = paymentRepository.findByIdPayment(paymentId);
        if (payments.isEmpty()) {
            throw new RuntimeException("Payment not found: " + paymentId);
        }
        
        // Update payment status based on gateway response
        PaymentStatus newStatus = "SUCCESS".equals(status) ? PaymentStatus.COMPLETED : PaymentStatus.CANCELLED;
        
        payments.forEach(payment -> payment.setStatus(newStatus));
        paymentRepository.saveAll(payments);

        // Clear user cart if payment successful
        if (newStatus == PaymentStatus.COMPLETED) {
            UUID userId = payments.get(0).getUser().getId();
            List<Cart> cartItems = cartRepository.findByUserId(userId);
            cartRepository.deleteAll(cartItems);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDto> getUserOrders(UUID userId) {
        log.info("Getting orders for user: {}", userId);

        // Get all payments by userId
        List<Payment> payments = paymentRepository.findByUserId(userId);
        
        Map<String, List<Payment>> groupedPayments = payments.stream()
                .collect(Collectors.groupingBy(Payment::getIdPayment));

        // Convert to OrderListResponseDto
        return groupedPayments.entrySet().stream()
                .map(entry -> {
                    List<Payment> orderPayments = entry.getValue();
                    Payment firstPayment = orderPayments.get(0);
                    
                    BigDecimal totalAmount = orderPayments.stream()
                            .map(Payment::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return OrderListResponseDto.builder()
                            .orderId(entry.getKey())
                            .totalAmount(totalAmount)
                            .status(firstPayment.getStatus().name())
                            .typePayment(firstPayment.getTypePayment())
                            .orderDate(firstPayment.getCreatedAt().toString())
                            .build();
                })
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(String idPayment, UUID userId) {
        log.info("Getting order detail: {} for user: {}", idPayment, userId);

        // Find payments by idPayment  
        List<Payment> payments = paymentRepository.findByUserId(userId).stream()
                .filter(p -> p.getIdPayment().equals(idPayment))
                .collect(Collectors.toList());
                
        if (payments.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Payment firstPayment = payments.get(0);
        
        return OrderDetailResponseDto.builder()
                .fullName(firstPayment.getFullName())
                .phone(firstPayment.getPhone())
                .address(firstPayment.getAddress())
                .typePayment(firstPayment.getTypePayment())
                .totalPrice(firstPayment.getTotalPrice())
                .status(firstPayment.getStatus().name())
                .createdAt(firstPayment.getCreatedAt())
                .products(payments.stream()
                        .map(payment -> OrderDetailResponseDto.ProductInOrderDetailDto.builder()
                                .productId(payment.getProduct().getId())
                                .name(payment.getProduct().getName())
                                .price(payment.getProduct().getPrice())
                                .quantity(payment.getQuantity())
                                .images(payment.getProduct().getImages())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void cancelOrder(UUID userId, CancelOrderDto cancelOrderDto) {
        log.info("Cancelling order: {} for user: {}", cancelOrderDto.getOrderId(), userId);

        // Find user's payments for this order
        List<Payment> payments = paymentRepository.findByUserId(userId).stream()
                .filter(p -> p.getIdPayment().equals(cancelOrderDto.getOrderId()))
                .collect(Collectors.toList());
                
        if (payments.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Payment payment = payments.get(0);
        
        // Only PENDING orders can be cancelled
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Can only cancel PENDING orders");
        }

        // Update status to CANCELLED
        payments.forEach(p -> p.setStatus(PaymentStatus.CANCELLED));
        paymentRepository.saveAll(payments);
    }

    @Override
    @Transactional(readOnly = true) 
    public List<PaymentResponseDto> getAllOrders() {
        log.info("Getting all orders for admin");

        // Get all payments and group by idPayment
        List<Payment> allPayments = paymentRepository.findAll();
        
        Map<String, List<Payment>> groupedPayments = allPayments.stream()
                .collect(Collectors.groupingBy(Payment::getIdPayment));

        // Convert to PaymentResponseDto
        return groupedPayments.entrySet().stream()
                .map(entry -> {
                    List<Payment> orderPayments = entry.getValue();
                    Payment firstPayment = orderPayments.get(0);
                    
                    BigDecimal totalAmount = orderPayments.stream()
                            .map(Payment::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return PaymentResponseDto.builder()
                            .idPayment(entry.getKey())
                            .fullName(firstPayment.getFullName())
                            .phone(firstPayment.getPhone()) 
                            .address(firstPayment.getAddress())
                            .totalPrice(totalAmount)
                            .status(firstPayment.getStatus())
                            .typePayment(PaymentType.valueOf(firstPayment.getTypePayment()))
                            .createdAt(firstPayment.getCreatedAt())
                            .user(PaymentResponseDto.UserInPaymentDto.builder()
                                    .userId(firstPayment.getUser().getId())
                                    .email(firstPayment.getUser().getEmail())
                                    .fullName(firstPayment.getUser().getFullName())
                                    .phone(firstPayment.getUser().getPhone())
                                    .build())
                            .build();
                })
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrderStatus(UpdatePaymentStatusDto updatePaymentStatusDto) {
        log.info("Updating order status: {} to {}", 
                updatePaymentStatusDto.getIdPayment(), updatePaymentStatusDto.getStatus());

        // Find all payments with same idPayment
        List<Payment> payments = paymentRepository.findByIdPayment(updatePaymentStatusDto.getIdPayment());
        if (payments.isEmpty()) {
            throw new RuntimeException("Payment not found: " + updatePaymentStatusDto.getIdPayment());
        }

        Payment firstPayment = payments.get(0);
        
        // Validate status transition
        if (!isValidStatusTransition(firstPayment.getStatus(), updatePaymentStatusDto.getStatus())) {
            throw new RuntimeException("Invalid status transition from " + 
                    firstPayment.getStatus() + " to " + updatePaymentStatusDto.getStatus());
        }

        // Update status for all payments with same idPayment
        payments.forEach(payment -> payment.setStatus(updatePaymentStatusDto.getStatus()));
        paymentRepository.saveAll(payments);
        
        log.info("Order {} status updated to {}", updatePaymentStatusDto.getIdPayment(), 
                updatePaymentStatusDto.getStatus());
    }

    @Override
    public String generatePaymentId() {
            // Generate payment ID format: PAY + YYYYMMDDHHMMSS + random 4 digits
        
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "PAY" + timestamp + random;
    }

    @Override
    public boolean isValidStatusTransition(PaymentStatus from, PaymentStatus to) {
        // E-commerce Business Logic - Realistic Rules
        
        // Allow same status (idempotent operation) 
        if (from == to) {
            return true;
        }
        
        switch (from) {
            case PENDING:
                // From pending: can complete payment or cancel
                return to == PaymentStatus.COMPLETED || to == PaymentStatus.CANCELLED;
                
            case COMPLETED: 
                // From completed: can deliver OR rollback in exceptional cases
                return to == PaymentStatus.DELIVERED || to == PaymentStatus.CANCELLED;
                // Note: COMPLETED → CANCELLED for refunds, chargebacks, admin corrections
                
            case DELIVERED:
                // Delivered is final - no transitions allowed
                return false;
                
            case CANCELLED:
                // Cancelled is final - no transitions allowed  
                return false;
                
            default:
                return false;
        }
    }

}
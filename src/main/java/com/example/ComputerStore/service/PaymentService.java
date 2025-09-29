package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.CancelOrderDto;
import com.example.ComputerStore.dto.request.CreatePaymentDto;
import com.example.ComputerStore.dto.request.UpdatePaymentStatusDto;
import com.example.ComputerStore.dto.response.*;
import com.example.ComputerStore.enumeric.PaymentStatus;

import java.util.List;
import java.util.UUID;

/**
 * 
 * LEARNING OBJECTIVES:
 * - Payment flow từ cart → payment → order completion
 * - Integration với payment gateways (MOMO, VNPAY)
 * - Order management lifecycle
 */
public interface PaymentService {

    // ==================== CORE PAYMENT OPERATIONS ====================
    
    /**
     * TODO: Implement method này
     * NHIỆM VỤ: Tạo thanh toán từ cart của user
     * INPUT: CreatePaymentDto (userId, paymentType, delivery info)
     * OUTPUT: PaymentUrlResponseDto (có thể chứa URL redirect cho gateway)
     * 
     * LOGIC CẦN IMPLEMENT:
     * 1. Validate user cart không rỗng
     * 2. Tính tổng tiền cart
     * 3. Tạo payment records cho mỗi cart item
     * 4. Xử lý theo payment type (COD/MOMO/VNPAY)
     * 5. Clear cart nếu COD, giữ cart nếu gateway payment
     */
    PaymentUrlResponseDto createPayment(CreatePaymentDto createPaymentDto);

    /**
     * TODO: Implement method này  
     * NHIỆM VỤ: Xử lý callback từ payment gateway
     * INPUT: Payment gateway response parameters
     * OUTPUT: Success/failure status
     * 
     * LOGIC CẦN IMPLEMENT:
     * 1. Verify gateway signature/response
     * 2. Update payment status thành COMPLETED
     * 3. Clear user cart
     * 4. Send confirmation notification
     */
    void handlePaymentCallback(String paymentId, String status, String gatewayType);

    // ==================== ORDER MANAGEMENT ====================
    
    /**
     * TODO: Implement method này
     * NHIỆM VỤ: Lấy danh sách đơn hàng của user
     * OUTPUT: List orders được group theo idPayment
     */
    List<OrderListResponseDto> getUserOrders(UUID userId);

    OrderDetailResponseDto getOrderDetail(String idPayment, UUID userId);

    /**
     * TODO: Implement method này
     * NHIỆM VỤ: User hủy đơn hàng (chỉ được hủy khi status = PENDING)
     */
    void cancelOrder(UUID userId, CancelOrderDto cancelOrderDto);

    // ==================== ADMIN OPERATIONS ====================
    
    /**
     * TODO: Implement method này
     * NHIỆM VỤ: Admin lấy tất cả đơn hàng
     */
    List<PaymentResponseDto> getAllOrders();

    /**
     * TODO: Implement method này  
     * NHIỆM VỤ: Admin cập nhật trạng thái đơn hàng
     * BUSINESS RULES:
     * - PENDING → COMPLETED hoặc CANCELLED
     * - COMPLETED → DELIVERED
     * - DELIVERED/CANCELLED là final states
     */
    void updateOrderStatus(UpdatePaymentStatusDto updatePaymentStatusDto);

    // ==================== HELPER METHODS ====================
    
    /**
     * TODO: Implement method này
     * NHIỆM VỤ: Generate unique payment ID
     * FORMAT: PAY + timestamp + random
     */
    String generatePaymentId();

    /**
     * TODO: Implement method này
     * NHIỆM VỤ: Validate status transition hợp lệ
     */
    boolean isValidStatusTransition(PaymentStatus from, PaymentStatus to);
}

/*
LEARNING NOTES:
1. Interface chỉ định nghĩa contract, không implement
2. Mỗi method có clear responsibility (Single Responsibility Principle)
3. Method names self-documenting (getUserOrders, cancelOrder, etc.)
4. Return types phù hợp với frontend needs
5. Separation giữa user operations và admin operations
*/

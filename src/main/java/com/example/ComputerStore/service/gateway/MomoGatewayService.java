package com.example.ComputerStore.service.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * MOMO Payment Gateway Integration Service
 * Real implementation based on MOMO API documentation
 */
@Service
@Slf4j
public class MomoGatewayService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${payment.momo.partner-code:MOMO}")
    private String partnerCode;

    @Value("${payment.momo.access-key:F8BBA842ECF85}")
    private String accessKey;

    @Value("${payment.momo.secret-key:K951B6PE1waDMi640xX08PD3vg6EkVlz}")
    private String secretKey;

    @Value("${payment.momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String momoEndpoint;

    @Value("${payment.momo.redirect-url:http://localhost:8090/api/payments/callback/momo}")
    private String redirectUrl;

    @Value("${payment.momo.ipn-url:http://localhost:8090/api/payments/callback/momo}")
    private String ipnUrl;

    public String createPaymentUrl(String paymentId, BigDecimal amount, String userId) {
        try {
            // Generate request parameters
            String requestId = partnerCode + System.currentTimeMillis();
            String orderId = paymentId;
            String orderInfo = "thanh toan " + userId;
            String requestType = "captureWallet";
            String extraData = "";

            // Create raw signature string
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount.longValue() +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            // Generate signature
            String signature = generateHmacSHA256(rawSignature, secretKey);

            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("accessKey", accessKey);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount.longValue());
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", redirectUrl);
            requestBody.put("ipnUrl", ipnUrl);
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", requestType);
            requestBody.put("signature", signature);
            requestBody.put("lang", "en");

            // Send request to MOMO
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(momoEndpoint, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("payUrl")) {
                return (String) response.getBody().get("payUrl");
            }

            log.error("MOMO API error: {}", response.getBody());
            return null;

        } catch (Exception e) {
            log.error("Error creating MOMO payment URL: ", e);
            return null;
        }
    }

    private String generateHmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
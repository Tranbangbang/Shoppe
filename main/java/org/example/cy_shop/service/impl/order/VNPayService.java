package org.example.cy_shop.service.impl.order;

import org.example.cy_shop.configuration.vnpay.VNPayConfigure;
import org.example.cy_shop.service.order.IVNPayService;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VNPayService implements IVNPayService {
    @Override
    public String createOrder(int total, String orderInfo, String urlReturn) throws UnsupportedEncodingException {
        System.out.println("total = " + total);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";

        String vnp_TxnRef = VNPayConfigure.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfigure.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total * 100));
//        vnp_Params.put("vnp_Amount", String.valueOf(1000.5));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

//        String locate = "vn";
        vnp_Params.put("vnp_Locale", "vn");

//        vnp_Params.put("vnp_ReturnUrl", VNPayConfigure.vnp_ReturnUrl);
        vnp_Params.put("vnp_ReturnUrl", 	urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        LocalDateTime cld = UtilsFunction.getVietNameTimeNow();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", UtilsFunction.convertLocalDateTimeToStr(cld));

//        cld.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", UtilsFunction.convertLocalDateTimeToStr(cld.plusMinutes(15)));

//        System.out.println("Now time: " + cld);
//        System.out.println("start date = " + UtilsFunction.convertLocalDateTimeToStr(cld));
//        System.out.println("end date = " + UtilsFunction.convertLocalDateTimeToStr(cld.plusMinutes(15)));

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfigure.hmacSHA512(VNPayConfigure.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VNPayConfigure.vnp_PayUrl + "?" + queryUrl;

        return  paymentUrl;
    }
}

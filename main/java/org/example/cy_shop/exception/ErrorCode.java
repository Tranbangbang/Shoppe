package org.example.cy_shop.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(111, "UNCATEGORIZED ERROR"),
    ADDRESS_NOT_FOUND(119,"ADDRESS_NOT_FOUND"),
    INVALID_SHOP_NAME(120,"INVALID_SHOP_NAME"),
    ACCESS_DENIED(333,"ACCESS_DENIED"),
    INVALID_IMAGE_FORMAT(334,"INVALID_IMAGE_FORMAT"),
    DUPLICATE_ORDER_NUMBER(335,"Order number already exists"),
    INVALID_INPUT(336,"Banner ID and update data cannot be null"),
    BANNER_NOT_FOUND(337,"BANNER_NOT_FOUND"),
    INVALID_PASSWORD(338,"INVALID_PASSWORD"),
    INVALID_TOKEN(339,"INVALID_TOKEN"),
    ORDER_DETAILS_EMPTY(340,"ORDER_DETAILS_EMPTY"),
    INVALID_REQUEST(341,"INVALID_REQUEST"),
    PRODUCT_NOT_FOUND(342,"PRODUCT_NOT_FOUND"),
    Discount_value_cannot_be_negative(343,"Discount value cannot be negative"),
    Minimum_order_value_cannot_be_negative(344,"Minimum order value cannot be negative"),
    Max_usage_cannot_be_negative(345,"Max usage cannot be negative"),
    INVALID_PHONE_NUMBER(121,"INVALID_PHONE_NUMBER"),
    INVALID_FULL_NAME(122,"INVALID_FULL_NAME"),
    INVALID_DETAIL_ADDRESS(123,"INVALID_DETAIL_ADDRESS"),
    email_already_exists(199,"email already exists"),
    decodedDataNull(198,"decodedDataNull"),
    REPORT_NOT_FOUND(183,"REPORT_NOT_FOUND"),
    SHOP_ALREADY_APPROVED(195,"SHOP_ALREADY_APPROVED"),
    INVALID_USER_PROFILE(112,"INVALID_USER_PROFILE"),
    INVALID_OAUTH_TOKEN(113,"INVALID_OAUTH_TOKEN"),
    DUPLICATE_REPORT(193,"DUPLICATE_REPORT"),
    CURRENT_PASSWORD_INCORRECT(101,"CURRENT_PASSWORD_INCORRECT"),
    PASSWORD_MUST_BE_DIFFERENT(102,"PASSWORD_MUST_BE_DIFFERENT"),
    PASSWORD_CANNOT_BE_OLD_PASSWORD(103,"PASSWORD_CANNOT_BE_OLD_PASSWORD"),
    Invalid_product_or_user(105,"Invalid product or user"),
    Reset_PassWord_Failed(104,"ResetPassWord failed"),
    FORBIDDEN_ROLE(107,"FORBIDDEN_ROLE"),
    VOUCHER_NOT_FOUND(108,"VOUCHER_NOT_FOUND"),
    VOUCHER_INACTIVE(181,"VOUCHER_INACTIVE"),
    VOUCHER_EXHAUSTED(182,"VOUCHER_EXHAUSTED"),
    VOUCHER_NOT_STARTED_YET(183,"VOUCHER_NOT_STARTED_YET"),
    VOUCHER_EXPIRED(184,"VOUCHER_EXPIRED"),
    VOUCHER_ALREADY_USED(184,"VOUCHER_ALREADY_USED"),
    FILE_UPLOAD_FAILED(345,"FILE_UPLOAD_FAILED"),
    SHOP_NOT_FOUND(303,"SHOP_NOT_FOUND"),
    IDENTITY_ALREADY_EXISTS(989,"IDENTITY_ALREADY_EXISTS"),
    ACCOUNT_INACTIVE(211,"ACCOUNTINACTIVE"),
    INTERNAL_SERVER_ERROR(876,"INTERNAL_SERVER_ERROR"),
    EMAIL_SENDING_FAILED(222,"EMAIL_SENDING_FAILED"),
    INVALID_EMAIL(224,"INVALID_EMAIL"),
    TOKEN_GENERATION_FAILED(223,"TOKEN_GENERATION_FAILED"),
    INVALID_KEY(1001, "INVALID MESSAGE KEY"),
    SHOP_NAME_ALREADY_EXISTS(2099,"SHOP_NAME_ALREADY_EXISTS"),
    USER_USERNAME_EXISTED(2002, "USER USERNAME ALREADY EXISTS"),
    USER_NOT_FOUND(2003, "USER NOT FOUND"),
    USER_CANT_CREATE_USER(2004, "USER CANT CREATE USER"),
    ACCOUNT_EMAIL_EXISTED(2003, "ACCOUNT EMAIL EXISTS"),
    ACCOUNT_NOT_FOUND(2004, "ACCOUNT NOT FOUND"),
    INVALID_MISSING_TOKEN(2001, "Invalid or missing Authorization token"),
    TOKEN_RESET_PASSWORD_INVALID(2005, "TOKEN RESET PASSWORD INVALID"),
    TOKEN_RESET_PASSWORD_EXPIRED(2006, "TOKEN RESET PASSWORD EXPIRED"),
    TOKEN_RESET_NOT_MATH(2007, "TOKEN RESET PASSWORD NOT MATCH"),
    INVALID_CREDENTIAL(1002, "INVALID CREDENTIAL"),
    TOKEN_EXPIRED(1003, "TOKEN EXPIRED"),
    ACCOUNT_INVALID(1004, "ACCOUNT INVALID"),
    LOGIN_FAILED(1005, "LOGIN FAILED"),
    REFRESH_TOKEN_NOT_FOUND(1006, "REFRESH TOKEN NOT FOUND"),
    LOGOUT_FAILED(1007, "LOGOUT FAILED"),
    ERROR_(1009, "An error occurred while retrieving account details"),
    ROLE_NOT_FOUND(2001, "ROLE NOT FOUND"),
    REGISTER_ACCOUNT_FAILED(2001, "REGISTER FAILED"),


    //--------------user exception
    EMAIL_NOT_VALID(3000, "Email không hợp lệ"),
    USER_NOT_LOGIN(3001, "Hãy đăng nhập để tiếp tục"),
    USER_NOT_HAS_ROLE_SHOP(3002, "Bạn chưa đăng ký tính năng bán hàng"),
    USER_NOT_FINDING(3003, "Tài khoản không tồn tại"),
    LOGIN_NOT_VALID(3004, "Thông tin đăng nhập không hợp lệ!"),
    ROLE_NOT_VALID(3005, "Không tìm thấy role này"),
    EMAIL_WAS_REGISTER(3006, "Email này đã được sử dụng"),
    PASS_WORD_NULL(3007, "Không được để mật khẩu trống"),
    USER_NOT_HAS_ROLE_BUY(3008, "Bạn không có quyền mua hàng"),
    USER_CAN_NOT_FIND_SHOP(3009, "Không tìm thấy cửa hàng của bạn"),
    CANNOT_UPDATE_ACCOUNT(3010, "Không thể update account"),
    TOKEN_INVALID(3011, "Token không hợp lệ"),

    //---------------------accoint
    ACCOUNT_OF_USER_NOT_FOUND(3020, "Tài khoản người dùng không tồn tại"),

    //---------------category exception
    CATEGORY_EXITS(3100, "Danh mục này đã tồn tại"),
    CATEGORY_NOT_FOUND(3101, "Danh mục này không tồn tại"),
    CATEGORY_OF_PRODUCT_EMPTY(3102, "Hãy thêm danh mục cho sản phẩm"),
    CATEGORY_NOT_ALLOW(3103, "Không thể tham chiếu danh mục này"),


    //------------------product exception-------------------------------------------------
    CAN_NOT_SAVE_PRODUCT(3200, "Không thể thêm sản phẩm"),
    PRODUCT_REQUEST_NULL(3201, "Sản phẩm thiếu thông tin"),
    PRODUCT_OF_MEDIA_NULL(3202, "Thiếu tham chiếu đến sản phẩm cho media"),
    PRODUCT_OF_MEDIA_INVALID(3203, "Sản phẩm tham chiếu của media không hợp lệ"),
    PRODUCT_NOT_FOUNT(3204, "Không tìm thấy sản phẩm này"),
    PRODUCT_OPTION_NOT_FOUNT(3205, "Không tìm thấy sản phẩm có biến thể này"),
    PRODUCT_AND_ID_SHOP_NOT_MATCH(3206, "Thông tin shop không phù hợp"),
    PRODUCT_IS_NOT_YOUR_SHOP(3207, "Sản phẩm này không phải của shop của bạn"),
    PRODUCT_NOT_HAS_MEDIA(3208, "Thông tin sản phẩm thiếu media"),
    CANNOT_FIND_SHOP_USER(3209, "Không tìm thấy shop của user"),
                            //stock
    STOCK_INVALID(3210, "Stock entity thiếu thông tin"),
    PRODUCT_OF_STOCK_REQUEST_NULL(3211, "Thông tin sản phẩm của stock null"),
    PRODUCT_OF_STOCK_REQUEST_NOT_FOUND(3212, "Không tìm thấy thông tin sản phẩm của stock"),
    STOCK_VALUE_INVALID(3213, "Số lượng hoặc giá không hợp lệ"),
    CAN_NOT_SAVE_STOCK(3214, "Không thể lưu stock"),
    STOCK_OF_PRODUCT_WAS_CREATED(3215, "Stock của sản phẩm này đã được tạo"),
    STOCK_MISSING_INFO(3216, "Sản phẩm thiếu thông tin số lượng và giá"),

                            //option
    PRODUCT_OF_OPTION_NOT_FOUND(3220, "Không tìm thấy sản phẩm của option"),
    PRODUCT_OF_OPTION_NULL(3221, "Sản phẩm tham chiếu của option null"),

                            //value
    VALUE_OF_PRODUCT_NOT_VALID(3230, "Thông tin value request không hợp lệ"),
    OPTION_OF_VALUE_NOT_FOUND(3231, "Option tham chiếu của value không tồn tại"),
    STOCK_OF_VALUE_NOT_FOUND(3232, "Stock tham chiếu của value không tồn tại"),

    //*******************end - product exception*********************************************************

    //----------------------media exception
    VIDEO_PRODUCT_LARGE(3301, "Kích thước video quá lớn"),
    CANNOT_SAVE_VIDEO(3302, "Không thể lưu video"),

    //-----------------------shop exception
    SHOP_OF_PRODUCT_EMPTY(3401, "Hãy cửa hàng cho sản phẩm"),
    SHOP_CAN_NOT_FOUND(3402, "Cửa hàng này không tồn tại"),
    SHOP_NOT_ACTIVE(3403, "Shop không khả dụng"),


    //-------------------client product exception--------------------------------------
    PRICE_NOT_VALID(3500, "Giá tiền không hợp lệ"),
    //****************end ofclient product exception***********************************

    //-------------------shop exception--------------------------------------
    SHOP_IS_NOT_ACTIVE(3600, "Shop không hoạt động"),
    //****************end of shop exception***********************************


    //-------------------cart exception--------------------------------------
    CART_REQUEST_INVALID(3700, "Thông tin giỏ hàng không hợp lệ"),
    ACCOUT_OF_CART_NOT_FOUND(3701, "Không tìm thấy người dùng này"),
    PRODUCT_OF_CART_NOT_FOUND(3702, "Sản phẩm này không còn tồn tại"),
    PRODUCT_NOT_HAS_OPTION(3703, "Sản phẩm không có tùy chọn này"),
    SELECT_OPTION_OF_PRODUCT_NOT_VALID(3704, "Chưa chọn đầy đủ các tùy chọn của sản phẩm"),
    STOCK_OF_PRODUCT_NOT_FOUND(3705, "Không tìm thấy thông tin số lượng của sản phẩm"),
    QUANTITY_OF_CART_NOT_VALID(3706, "Số lượng sản phẩm trong giỏ hàng không hợp lý"),
    OPTION_OF_CART_REQUEST_NOT_VALID(3707, "Tùy chọn của sản phẩm không hợp lệ"),
    OPTION_OF_CART_REQUEST_NOT_FULL(3708, "Hãy chọn tùy chọn hợp lệ"),


    //*********quantity
    QUANTITY_BIGGER_THAN_STOCK(3800, "Số lượng lớn hơn trong kho"),
    QUANTITY_OF_STOCK_NOT_HAS(3801, "Số lượng hàng tồn kho không còn"),

    //****************end of cart exception***********************************

    //-------------------order exception--------------------------------------
    //---order
    CANNOT_SAVE_ORDER(3900, "Không thể lưu đơn hàng"),
    CANNOT_SAVE_ORDER_DETAILS(3901, "Không thể lưu đơn hàng chi tiết"),
    ORDER_MUST_HAVE_1PRODUCT(3902, "Cần 1 sản phẩm để mua hàng"),
    OPTION_OF_ORDER_NO_VALID(3903, "Thông tin option của order không hợp lệ"),
    QUANTITY_OF_ORDER_NO_VALID(3904, "Thông tin số luượng của order không hợp lệ"),
    ORDER_NOT_FOUND(3905, "Không tìm thấy thông tin order"),
    ORDER_IS_NOT_U(3906, "Đơn hàng này không phải của bạn"),
    ORDER_IS_NOT_YOUR_SHOP(3907, "Đơn hàng này không phải của shop bạn"),
    CAN_CANCEL_PENDING(3908, "Chỉ hủy được đơn hàng đang pending"),
    ORDER_TIME_OUT(3909, "Thời gian chờ mua hàng quá lâu"),
    PRODUCT_WAS_BUY_BY_ANOTHER(3910, "Sản phẩm đang bị mua bởi người khác"),

    //*** order status
    ORDER_NOT_ACCPET_LAST(3920, "Trạng thái trước đó không phải là accept"),
    ORDER_NOT_RECEIVED_LAST(3921, "Trạng thái trước đó không phải là received"),
    ORDER_NOT_PENDING_LAST(3922, "Trạng thái trước đó không phải là pending"),
    CANNOT_BUY_MY_SHOP(3923, "Không thể mua sản phẩm của shop"),
    ORDER_VN_PAY_NOT_PAID(3924, "Phương thức thanh toán chưa hoàn tất"),

    USER_NOT_ORDER_LAST(3930, "Phải mua hàng mới có thể feed back"),
    TIME_FEEDBACK_EXPRIED(3931, "Ch có thể bình luận trong khoảng 1 ngày sau khi mua"),
    ORDER_DETAIL_NOT_FOUND(3932, "Không tìm thấy orderdetail"),
    STATUS_ORDER_NOT_RRECEIVE(3933, "Chỉ feed back nếu nhận được hàng"),
    FEEDBACK_INVALID(3934, "Cần vote sản phẩm (1-5)"),
    YOU_WAS_FEEDBACK(3935, "Bạn đã feed back cho đơn hàng này rồi"),
    FEEDBACK_REQUEST_NULL(3936, "Feed back request null"),

    //***cart

    CART_NOT_FOUND(3950, "Không tìm thấy thông tin giỏ hàng"),
    CANNOT_SAVE_CART(3951, "Không thể lưu giỏ hàng"),
    //****************end of order exception***********************************

    //-------------------voucher exception--------------------------------------
    VOUCHER_ORDER_NOT_FOUND(4000, "Voucher này không tồn tại"),
    VOUCHER_ORDER_NOT_AVAILABLE(4001, "Voucher không khả dụng"),
    VOUCHER_ORDER_EXPIRED(4002, "Voucher đã hết hạn"),
    VOUCHER_ORDER_NOT_NOW(4003, "Voucher chưa thể sử dụng"),
    //****************end of order exception***********************************

    //-------------------tracking exception--------------------------------------
    CANNOT_SAVE_TRACKING(4100, "Không thể lưu tracking"),
    //****************end of tracking exception***********************************

    //-------------------chat exception--------------------------------------
    CANNOT_SAVE_CHAT(4500, "Không thể tạo phòng chat"),
    CHAT_ROOM_NOT_FOUND(4501, "Không tìm thấy phòng chat này"),

    //***message
    CANNOT_SAVE_MESSAGE(4600, "Không tìm lưu tin nhắn"),
    MESSAGE_REQUEST_INVALID(4601, "Thông tin tin nhắn request không hợp lệ"),


    //feed back
    CANNOT_SAVE_FEEDBACK(4700, "Không thể feedback"),
    CANNOT_FIND_FEEDBACK(4701, "Không tìm thấy feedback"),
    FEEDBACK_REQUEST_INVALID(4702, "Thông tin feedback không hợp lệ")
    //****************end of chat exception***********************************


    ;

    private int code;
    private String message;
}

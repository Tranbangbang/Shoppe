package org.example.cy_shop.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsFunction {
    //-------truyền vào file name và trả ra exstension của file đó
    public static String getExtension(String fileName){
        int lastDot = fileName.lastIndexOf('.');
        if(lastDot == -1)
            return "";
        return fileName.substring(lastDot + 1);
    }

    public static boolean isImage(String imageName){
        if(Arrays.asList(Const.ALLOWED_IMAGE_EXTENSIONS).contains(getExtension(imageName)))
            return true;
        return false;
    }

    public static boolean isVideo(String videoName){
        if(Arrays.asList(Const.ALLOWED_VIDEO_EXTENSIONS).contains(getExtension(videoName)))
            return true;
        return false;
    }

    public static boolean isEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Kiểm tra chuỗi email với biểu thức chính quy
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches(); // Trả về true nếu email hợp lệ, false nếu không hợp lệ
    }

    public static Double getMbSizeOfFile(MultipartFile file){
        return file.getSize()/(1024 * 1024.0);
    }

    public static boolean createFolder(String folederName) {
        try {
            Path folderPath = Paths.get(folederName);

            if(!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
                System.out.println("Da tao " + folederName + " thanh cong");
            }
            else {
                System.out.println("Folder " + folederName + " da ton tai");
            }
            return true;
        }catch (Exception e){
            System.out.println("Loi tao file " + folederName + ": " + e);
            return false;
        }
    }

    //----nhận vào 1 file và vị trí lưu thư mục
    //-----trả ra tên file sau khi lưu
    public static String saveMediaFile(MultipartFile multipartFile, String source){
        try {
//            createFolder(source);
            String fileName = multipartFile.getOriginalFilename();
            String fileNameRandom = UUID.randomUUID() + "_" + fileName;

            Path filePath = Paths.get(source, fileNameRandom);
            Files.write(filePath, multipartFile.getBytes());

//            System.out.println("file name: " + fileNameRandom);
//            System.out.println("file path: " + fileName);
            System.out.println("Da ghi file thanh cong: " + filePath);
            return fileNameRandom;
        }catch (Exception e){
            System.out.println("Loi khi luu file phuong tien (util function): " + e);
            return null;
        }
    }

    public static LocalDateTime getVietNameTimeNow(){
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamTime = ZonedDateTime.now(vietnamZone);

        // Chuyển đổi thành LocalDateTime
        return vietnamTime.toLocalDateTime();
    }

    public  static String convertParamToString(String param){
        if(param == null)
            return null;
        param = param.trim();
        if(param.isEmpty())
            return null;
        return param;
    }

    public static Double convertParamToDouble(String param){
        if(param == null)
            return null;
        param = param.trim();
        if(param.isEmpty())
            return null;

        return Double.parseDouble(param);
    }

    public static Boolean notNull(Object object) {
        // Kiểm tra nếu đối tượng là null
        if (object == null) {
            return false;  // Nếu đối tượng là null, trả về false
        }

        // Kiểm tra nếu đối tượng là một List và danh sách có size bằng 0
        if (object instanceof List) {
            return !((List<?>) object).isEmpty();  // Trả về false nếu List rỗng
        }

        // Kiểm tra nếu đối tượng là mảng và mảng có rỗng không
        if (object.getClass().isArray()) {
            return Array.getLength(object) > 0;  // Trả về true nếu mảng không rỗng
        }

        // Nếu không phải mảng hay List, trả về true (đối tượng không null và không rỗng)
        return true;
    }

    public static String convertLocalDateTimeToStr(LocalDateTime lcd){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Chuyển đổi LocalDateTime thành String với định dạng trên
        return lcd.format(formatter);
    }


}

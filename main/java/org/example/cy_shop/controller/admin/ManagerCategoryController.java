package org.example.cy_shop.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.CategoryRequest;
import org.example.cy_shop.dto.response.product.CategoryResponse;
import org.example.cy_shop.entity.product.CategoryEntity;
import org.example.cy_shop.service.product.ICategoryService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "ADMIN_01. MANAGER_CATEGORY")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/manager_category")
public class ManagerCategoryController {
    @Autowired
    ICategoryService categoryService;

    @GetMapping("/get_list")
    public List<CategoryResponse> findAllListCategory(){
        return categoryService.findAll();
    }

    @PostMapping("/add")
    public String add(){
        String parentImageFashionMen = "https://down-vn.img.susercontent.com/file/687f3967b7c2fe6a134a2c11894eea4b@resize_w320_nl.webp";
        String parentImagePhoneAccessories = "https://down-vn.img.susercontent.com/file/31234a27876fb89cd522d7e3db1ba5ca@resize_w320_nl.webp";
        String parentImageElectronics = "https://down-vn.img.susercontent.com/file/978b9e4cb61c611aaaf58664fae133c5@resize_w320_nl.webp";
        String parentImageLaptops = "https://down-vn.img.susercontent.com/file/c3f3edfaa9f6dafc4825b77d8449999d@resize_w320_nl.webp";
        String parentImageCameras = "https://down-vn.img.susercontent.com/file/ec14dd4fc238e676e43be2a911414d4d@resize_w320_nl.webp";
        String parentImageWatches = "https://down-vn.img.susercontent.com/file/86c294aae72ca1db5f541790f7796260@resize_w320_nl.webp";
        String parentImageShoesMen = "https://down-vn.img.susercontent.com/file/74ca517e1fa74dc4d974e5d03c3139de@resize_w320_nl.webp";
        String parentImageHomeAppliances = "https://down-vn.img.susercontent.com/file/7abfbfee3c4844652b4a8245e473d857@resize_w320_nl.webp";
        String parentImageSportsAndTravel = "https://down-vn.img.susercontent.com/file/6cb7e633f8b63757463b676bd19a50e4@resize_w320_nl.webp";
        String parentImageCarsBikes = "https://down-vn.img.susercontent.com/file/3fb459e3449905545701b418e8220334@resize_w320_nl.webp";
        String parentImageFashionWomen = "https://down-vn.img.susercontent.com/file/75ea42f9eca124e9cb3cde744c060e4d@resize_w320_nl.webp";
        String parentImageBabyAndMom = "https://down-vn.img.susercontent.com/file/099edde1ab31df35bc255912bab54a5e@resize_w320_nl.webp";
        String parentImageHomeLiving = "https://down-vn.img.susercontent.com/file/24b194a695ea59d384768b7b471d563f@resize_w320_nl.webp";
        String parentImageBeauty = "https://down-vn.img.susercontent.com/file/ef1f336ecc6f97b790d5aae9916dcb72@resize_w320_nl.webp";
        String parentImageHealth = "https://down-vn.img.susercontent.com/file/49119e891a44fa135f5f6f5fd4cfc747@resize_w320_nl.webp";
        String parentImageShoesWomen = "https://down-vn.img.susercontent.com/file/48630b7c76a7b62bc070c9e227097847@resize_w320_nl.webp";
        String parentImageBagsWomen = "https://down-vn.img.susercontent.com/file/fa6ada2555e8e51f369718bbc92ccc52@resize_w320_nl.webp";
        String parentImageJewelryWomen = "https://down-vn.img.susercontent.com/file/8e71245b9659ea72c1b4e737be5cf42e@resize_w320_nl.webp";
        String parentImageGrocery = "https://down-vn.img.susercontent.com/file/c432168ee788f903f1ea024487f2c889@resize_w320_nl.webp";
        String parentImageBookStore = "https://down-vn.img.susercontent.com/file/36013311815c55d303b0e6c62d6a8139@resize_w320_nl.webp";

        List<CategoryRequest> categories = Arrays.asList(
                // Thời trang nam (cha) - Có ảnh
                new CategoryRequest(1L, "Thời Trang Nam", 1, parentImageFashionMen, null),

                new CategoryRequest(2L, "Quần", 2, null, 1L),
                new CategoryRequest(3L, "Áo", 2, null, 1L),
                new CategoryRequest(4L, "Mũ", 2, null, 1L),
                new CategoryRequest(5L, "Giày", 2, null, 1L),

                // Điện thoại và phụ kiện (cha) - Có ảnh
                new CategoryRequest(6L, "Điện thoại và phụ kiện", 1, parentImagePhoneAccessories, null),
                // Các category con của Điện thoại và phụ kiện - Không có ảnh
                new CategoryRequest(7L, "Điện thoại", 2, null, 6L),
                new CategoryRequest(8L, "Máy tính bảng", 2, null, 6L),
                new CategoryRequest(9L, "Pin dự phòng", 2, null, 6L),
                new CategoryRequest(10L, "Ốp lưng", 2, null, 6L),
                new CategoryRequest(11L, "Bảo vệ màn hình", 2, null, 6L),
                new CategoryRequest(12L, "Khác - điện thoại và phụ kiện", 2, null, 6L),

                // Thiết bị điện tử (cha) - Có ảnh
                new CategoryRequest(13L, "Thiết bị điện tử", 1, parentImageElectronics, null),
                // Các category con của Thiết bị điện tử - Không có ảnh
                new CategoryRequest(14L, "Thiết bị đeo thông minh", 2, null, 13L),
                new CategoryRequest(15L, "Phụ kiện tivi", 2, null, 13L),
                new CategoryRequest(16L, "Máy game console", 2, null, 13L),
                new CategoryRequest(17L, "Phụ kiện console", 2, null, 13L),
                new CategoryRequest(18L, "Khác - Thiết bị điện tử", 2, null, 13L),

                // Máy tính và Laptop (cha) - Có ảnh
                new CategoryRequest(19L, "Máy tính và Laptop", 1, parentImageLaptops, null),
                // Các category con của Máy tính và Laptop - Không có ảnh
                new CategoryRequest(20L, "Laptop", 2, null, 19L),
                new CategoryRequest(21L, "PC gaming", 2, null, 19L),
                new CategoryRequest(22L, "Linh kiện máy tính", 2, null, 19L),
                new CategoryRequest(23L, "Phụ kiện máy tính", 2, null, 19L),
                new CategoryRequest(24L, "Màn hình", 2, null, 19L),
                new CategoryRequest(25L, "Chuột và bàn phím", 2, null, 19L),
                new CategoryRequest(26L, "Thiết bị lưu trữ", 2, null, 19L),

                // Máy ảnh và máy quay phim (cha) - Có ảnh
                new CategoryRequest(27L, "Máy ảnh và máy quay phim", 1, parentImageCameras, null),
                // Các category con của Máy ảnh và máy quay phim - Không có ảnh
                new CategoryRequest(28L, "Máy ảnh DSLR", 2, null, 27L),
                new CategoryRequest(29L, "Máy ảnh Mirrorless", 2, null, 27L),
                new CategoryRequest(30L, "Camera hành trình", 2, null, 27L),
                new CategoryRequest(31L, "Phụ kiện máy ảnh", 2, null, 27L),
                new CategoryRequest(32L, "Ống kính", 2, null, 27L),
                new CategoryRequest(33L, "Máy quay phim", 2, null, 27L),
                new CategoryRequest(34L, "Tripod và Gimbal", 2, null, 27L),

                // Đồng hồ (cha) - Có ảnh
                new CategoryRequest(35L, "Đồng hồ", 1, parentImageWatches, null),
                // Các category con của Đồng hồ - Không có ảnh
                new CategoryRequest(36L, "Đồng hồ nam", 2, null, 35L),
                new CategoryRequest(37L, "Đồng hồ nữ", 2, null, 35L),
                new CategoryRequest(38L, "Đồng hồ trẻ em", 2, null, 35L),
                new CategoryRequest(39L, "Đồng hồ thông minh", 2, null, 35L),
                new CategoryRequest(40L, "Phụ kiện đồng hồ", 2, null, 35L),
                new CategoryRequest(41L, "Bộ sưu tập đồng hồ", 2, null, 35L),

                // Giày dép nam (cha) - Có ảnh
                new CategoryRequest(42L, "Giày dép nam", 1, parentImageShoesMen, null),
                // Các category con của Giày dép nam - Không có ảnh
                new CategoryRequest(43L, "Giày thể thao", 2, null, 42L),
                new CategoryRequest(44L, "Giày tây", 2, null, 42L),
                new CategoryRequest(45L, "Giày lười", 2, null, 42L),
                new CategoryRequest(46L, "Dép nam", 2, null, 42L),
                new CategoryRequest(47L, "Giày boot", 2, null, 42L),
                new CategoryRequest(48L, "Phụ kiện giày dép", 2, null, 42L),

                // Thiết bị điện gia dụng (cha) - Có ảnh
                new CategoryRequest(49L, "Thiết bị điện gia dụng", 1, parentImageHomeAppliances, null),
                // Các category con của Thiết bị điện gia dụng - Không có ảnh
                new CategoryRequest(50L, "Máy giặt", 2, null, 49L),
                new CategoryRequest(51L, "Tủ lạnh", 2, null, 49L),
                new CategoryRequest(52L, "Lò vi sóng", 2, null, 49L),
                new CategoryRequest(53L, "Máy hút bụi", 2, null, 49L),
                new CategoryRequest(54L, "Máy điều hòa", 2, null, 49L),
                new CategoryRequest(55L, "Quạt điện", 2, null, 49L),
                new CategoryRequest(56L, "Nồi chiên không dầu", 2, null, 49L),
                new CategoryRequest(57L, "Khác - Thiết bị gia dụng", 2, null, 49L),

                // Thể thao và du lịch (cha) - Có ảnh
                new CategoryRequest(58L, "Thể thao và du lịch", 1, parentImageSportsAndTravel, null),
                // Các category con của Thể thao và du lịch - Không có ảnh
                new CategoryRequest(59L, "Dụng cụ thể thao", 2, null, 58L),
                new CategoryRequest(60L, "Trang phục thể thao", 2, null, 58L),
                new CategoryRequest(61L, "Phụ kiện du lịch", 2, null, 58L),
                new CategoryRequest(62L, "Ba lô và túi xách", 2, null, 58L),
                new CategoryRequest(63L, "Lều và dụng cụ cắm trại", 2, null, 58L),
                new CategoryRequest(64L, "Giày thể thao leo núi", 2, null, 58L),
                new CategoryRequest(65L, "Khác - Thể thao và du lịch", 2, null, 58L),

                // Ô tô và xe máy và xe đạp (cha) - Có ảnh
                new CategoryRequest(66L, "Ô tô và xe máy và xe đạp", 1, parentImageCarsBikes, null),
                // Các category con của Ô tô và xe máy và xe đạp - Không có ảnh
                new CategoryRequest(67L, "Ô tô", 2, null, 66L),
                new CategoryRequest(68L, "Xe máy", 2, null, 66L),
                new CategoryRequest(69L, "Xe đạp", 2, null, 66L),
                new CategoryRequest(70L, "Phụ tùng ô tô", 2, null, 66L),
                new CategoryRequest(71L, "Phụ kiện xe máy", 2, null, 66L),
                new CategoryRequest(72L, "Dầu nhớt và bảo dưỡng", 2, null, 66L),
                new CategoryRequest(73L, "Đồ bảo hộ", 2, null, 66L),

                // Thời trang nữ (cha) - Có ảnh
                new CategoryRequest(74L, "Thời trang nữ", 1, parentImageFashionWomen, null),
                // Các category con của Thời trang nữ - Không có ảnh
                new CategoryRequest(75L, "Đầm", 2, null, 74L),
                new CategoryRequest(76L, "Áo nữ", 2, null, 74L),
                new CategoryRequest(77L, "Quần nữ", 2, null, 74L),
                new CategoryRequest(78L, "Chân váy", 2, null, 74L),
                new CategoryRequest(79L, "Áo khoác", 2, null, 74L),

                // Mẹ và bé (cha) - Có ảnh
                new CategoryRequest(80L, "Mẹ và bé", 1, parentImageBabyAndMom, null),
                // Các category con của Mẹ và bé - Không có ảnh
                new CategoryRequest(81L, "Đồ cho mẹ", 2, null, 80L),
                new CategoryRequest(82L, "Đồ chơi trẻ em", 2, null, 80L),
                new CategoryRequest(83L, "Tã và bỉm", 2, null, 80L),
                new CategoryRequest(84L, "Thức ăn trẻ em", 2, null, 80L),
                new CategoryRequest(85L, "Đồ dùng học tập", 2, null, 80L),
                new CategoryRequest(86L, "Xe đẩy và ghế ăn", 2, null, 80L),
                new CategoryRequest(87L, "Khác - mẹ và bé", 2, null, 80L),

                // Nhà cửa và đời sống (cha) - Có ảnh
                new CategoryRequest(88L, "Nhà cửa và đời sống", 1, parentImageHomeLiving, null),
                // Các category con của Nhà cửa và đời sống - Không có ảnh
                new CategoryRequest(89L, "Đồ nội thất", 2, null, 88L),
                new CategoryRequest(90L, "Dụng cụ làm bếp", 2, null, 88L),
                new CategoryRequest(91L, "Trang trí nhà cửa", 2, null, 88L),
                new CategoryRequest(92L, "Vệ sinh nhà cửa", 2, null, 88L),
                new CategoryRequest(93L, "Đồ gia dụng", 2, null, 88L),
                new CategoryRequest(94L, "Khác - Nhà của và đời sống", 2, null, 88L),

                // Sắc đẹp (cha) - Có ảnh
                new CategoryRequest(95L, "Sắc đẹp", 1, parentImageBeauty, null),
                // Các category con của Sắc đẹp - Không có ảnh
                new CategoryRequest(96L, "Mỹ phẩm", 2, null, 95L),
                new CategoryRequest(97L, "Chăm sóc da", 2, null, 95L),
                new CategoryRequest(98L, "Trang điểm", 2, null, 95L),
                new CategoryRequest(99L, "Dụng cụ làm đẹp", 2, null, 95L),
                new CategoryRequest(100L, "Nước hoa", 2, null, 95L),
                new CategoryRequest(101L, "Chăm sóc tóc", 2, null, 95L),
                new CategoryRequest(102L, "Khác - Sắc đẹp", 2, null, 95L),

                // Sức khỏe (cha) - Có ảnh
                new CategoryRequest(103L, "Sức khỏe", 1, parentImageHealth, null),
                // Các category con của Sức khỏe - Không có ảnh
                new CategoryRequest(104L, "Thực phẩm chức năng", 2, null, 103L),
                new CategoryRequest(105L, "Dụng cụ y tế", 2, null, 103L),
                new CategoryRequest(106L, "Chăm sóc sức khỏe", 2, null, 103L),
                new CategoryRequest(107L, "Thiết bị massage", 2, null, 103L),
                new CategoryRequest(108L, "Thể dục tại nhà", 2, null, 103L),
                new CategoryRequest(109L, "Khác - Sức khỏe", 2, null, 103L),

                // Giày dép nữ (cha) - Có ảnh
                new CategoryRequest(110L, "Giày dép nữ", 1, parentImageShoesWomen, null),
                // Các category con của Giày dép nữ - Không có ảnh
                new CategoryRequest(111L, "Giày cao gót", 2, null, 110L),
                new CategoryRequest(112L, "Giày thể thao nữ", 2, null, 110L),
                new CategoryRequest(113L, "Giày bệt", 2, null, 110L),
                new CategoryRequest(114L, "Dép nữ", 2, null, 110L),
                new CategoryRequest(115L, "Giày boot nữ", 2, null, 110L),
                new CategoryRequest(116L, "Phụ kiện giày", 2, null, 110L),

                // Túi ví nữ (cha) - Có ảnh
                new CategoryRequest(117L, "Túi ví nữ", 1, parentImageBagsWomen, null),
                // Các category con của Túi ví nữ - Không có ảnh
                new CategoryRequest(118L, "Túi xách", 2, null, 117L),
                new CategoryRequest(119L, "Ví nữ", 2, null, 117L),
                new CategoryRequest(120L, "Túi đeo chéo", 2, null, 117L),
                new CategoryRequest(121L, "Ba lô nữ", 2, null, 117L),
                new CategoryRequest(122L, "Túi du lịch", 2, null, 117L),
                new CategoryRequest(123L, "Khác -Túi ví nữ", 2, null, 117L),

                // Phụ kiện và trang sức nữ (cha) - Có ảnh
                new CategoryRequest(124L, "Phụ kiện và trang sức nữ", 1, parentImageJewelryWomen, null),
                // Các category con của Phụ kiện và trang sức nữ - Không có ảnh
                new CategoryRequest(125L, "Trang sức", 2, null, 124L),
                new CategoryRequest(126L, "Phụ kiện tóc", 2, null, 124L),
                new CategoryRequest(127L, "Kính mắt", 2, null, 124L),
                new CategoryRequest(128L, "Đồng hồ nữ thời trang", 2, null, 124L),
                new CategoryRequest(129L, "Phụ kiện thời trang", 2, null, 124L),
                new CategoryRequest(130L, "Khác - Phụ kiện và trang sức", 2, null, 124L),

                // Bách hóa online (cha) - Có ảnh
                new CategoryRequest(131L, "Bách hóa online", 1, parentImageGrocery, null),
                // Các category con của Bách hóa online - Không có ảnh
                new CategoryRequest(132L, "Bách Hóa xanh Online", 2, null, 131L),
                new CategoryRequest(133L, "Đồ ăn vặt", 2, null, 131L),
                new CategoryRequest(134L, "Đồ chế biến sẵn", 2, null, 131L),
                new CategoryRequest(135L, "Nhu yếu phẩm", 2, null, 131L),
                new CategoryRequest(136L, "Nguyên liệu nấu ăn", 2, null, 131L),
                new CategoryRequest(137L, "Đồ làm bánh(bách hóa)", 2, null, 131L),

                // Nhà sách online (cha) - Có ảnh
                new CategoryRequest(138L, "Nhà sách online", 1, parentImageBookStore, null),
                // Các category con của Nhà sách online - Không có ảnh
                new CategoryRequest(139L, "Nhà Sách Bách khoa Online", 2, null, 138L),
                new CategoryRequest(140L, "Sách Tiếng Việt", 2, null, 138L),
                new CategoryRequest(141L, "Sách ngoại văn", 2, null, 138L),
                new CategoryRequest(142L, "Gói Quà", 2, null, 138L),
                new CategoryRequest(143L, "Bút viết", 2, null, 138L),
                new CategoryRequest(144L, "Dụng cụ học sinh & văn phòng", 2, null, 138L)
        );
        categories.forEach(categoryRequest -> categoryService.save(categoryRequest));
        //ok
        return "ok";
    }

    @GetMapping("/get_list_parent")
    public List<CategoryResponse> findAllParentCategory(){
        return categoryService.findAllParent();
    }

    @GetMapping("/get_list_children")
    public List<CategoryResponse>findAllChildren(@RequestParam(value = "id_parent", required = false)Long idParent){
        if(idParent == null)
            return null;
        return categoryService.findAllChildren(idParent);
    }

    @GetMapping("/category_by_id")
    public CategoryResponse findById(@RequestParam(value = "id") Long id){
        return categoryService.findById(id);
    }

    @GetMapping("/category_parent_by_id_sub")
    public CategoryResponse findParent(@RequestParam(value = "id") Long id){
        return categoryService.findParentBySubId(id);
    }

    @PostMapping("/edit")
    public ApiResponse<CategoryEntity> edit(@RequestBody CategoryRequest categoryRequest){
        return categoryService.edit(categoryRequest);
    }
}

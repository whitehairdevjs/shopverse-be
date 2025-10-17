package org.biz.shopverse.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.dto.product.response.CategoryResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.exception.GlobalExceptionHandler;
import org.biz.shopverse.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ==================== 카테고리별 상품 조회 테스트 ====================
    @Test
    @DisplayName("카테고리별 상품 조회 성공 - 정상적인 카테고리 ID")
    void getProductsByCategoryId_Success() throws Exception {
        // Given
        Long categoryId = 1L;
        
        List<ProductResponse> mockProducts = Arrays.asList(
                ProductResponse.builder()
                        .id(1L)
                        .name("프리미엄 무선 이어폰")
                        .slug("premium-wireless-earphones")
                        .subtitle("고품질 음질과 편안한 착용감")
                        .description("<p>최고급 음질과 편안한 착용감을 제공하는 무선 이어폰입니다.</p>")
                        .shortDescription("고품질 음질과 편안한 착용감을 제공하는 프리미엄 무선 이어폰")
                        .price(new BigDecimal("199000"))
                        .comparePrice(new BigDecimal("249000"))
                        .costPrice(new BigDecimal("120000"))
                        .categoryId(categoryId)
                        .discountPercent(20.1)
                        .brandId(3L)
                        .sku("SKU-001-ABC")
                        .barcode("1234567890123")
                        .weight(new BigDecimal("0.150"))
                        .dimensions("{\"width\":10, \"height\":20, \"depth\":5}")
                        .status("active")
                        .visibility("visible")
                        .isDigital(false)
                        .isSubscription(false)
                        .isCustomizable(true)
                        .isPreorder(false)
                        .trackInventory(true)
                        .allowBackorder(false)
                        .minOrderQuantity(1)
                        .maxOrderQuantity(10)
                        .metaTitle("프리미엄 무선 이어폰 - 최고의 음질")
                        .metaDescription("고품질 음질과 편안한 착용감을 제공하는 프리미엄 무선 이어폰을 만나보세요.")
                        .searchKeywords("이어폰,무선,블루투스,음질")
                        .averageRating(4.5)
                        .reviewCount(127)
                        .viewCount(1543)
                        .salesCount(89)
                        .wishlistCount(23)
                        .createdAt("2024-01-15T10:30:00")
                        .updatedAt("2024-01-20T14:25:00")
                        .publishedAt("2024-01-16T09:00:00")
                        .build(),
                ProductResponse.builder()
                        .id(2L)
                        .name("스마트워치 시리즈 5")
                        .slug("smartwatch-series-5")
                        .subtitle("건강 관리와 스타일을 동시에")
                        .description("<p>건강 관리와 스타일을 동시에 만족시키는 스마트워치입니다.</p>")
                        .shortDescription("건강 관리와 스타일을 동시에 만족시키는 스마트워치")
                        .price(new BigDecimal("299000"))
                        .comparePrice(new BigDecimal("349000"))
                        .costPrice(new BigDecimal("180000"))
                        .categoryId(categoryId)
                        .discountPercent(14.3)
                        .brandId(2L)
                        .sku("SKU-002-DEF")
                        .barcode("1234567890124")
                        .weight(new BigDecimal("0.080"))
                        .dimensions("{\"width\":4, \"height\":4, \"depth\":1}")
                        .status("active")
                        .visibility("visible")
                        .isDigital(false)
                        .isSubscription(false)
                        .isCustomizable(false)
                        .isPreorder(false)
                        .trackInventory(true)
                        .allowBackorder(false)
                        .minOrderQuantity(1)
                        .maxOrderQuantity(5)
                        .metaTitle("스마트워치 시리즈 5 - 건강 관리")
                        .metaDescription("건강 관리와 스타일을 동시에 만족시키는 스마트워치를 만나보세요.")
                        .searchKeywords("스마트워치,건강,피트니스,시계")
                        .averageRating(4.2)
                        .reviewCount(89)
                        .viewCount(987)
                        .salesCount(45)
                        .wishlistCount(12)
                        .createdAt("2024-01-10T09:15:00")
                        .updatedAt("2024-01-18T16:30:00")
                        .publishedAt("2024-01-12T08:00:00")
                        .build()
        );

        when(productService.getProductsByCategoryId(categoryId)).thenReturn(mockProducts);

        // When & Then
        mockMvc.perform(get("/product/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("카테고리별 상품 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("프리미엄 무선 이어폰"))
                .andExpect(jsonPath("$.data[0].categoryId").value(categoryId))
                .andExpect(jsonPath("$.data[0].price").value(199000))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("스마트워치 시리즈 5"))
                .andExpect(jsonPath("$.data[1].categoryId").value(categoryId))
                .andExpect(jsonPath("$.data[1].price").value(299000));
    }

    @Test
    @DisplayName("카테고리별 상품 조회 성공 - 빈 상품 목록")
    void getProductsByCategoryId_Success_EmptyList() throws Exception {
        // Given
        Long categoryId = 999L;
        List<ProductResponse> emptyProducts = Collections.emptyList();

        when(productService.getProductsByCategoryId(categoryId)).thenReturn(emptyProducts);

        // When & Then
        mockMvc.perform(get("/product/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("카테고리별 상품 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - 존재하지 않는 카테고리")
    void getProductsByCategoryId_Failure_CategoryNotFound() throws Exception {
        // Given
        Long categoryId = 999L;

        when(productService.getProductsByCategoryId(categoryId))
                .thenThrow(new CustomBusinessException("존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/product/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 카테고리입니다."));
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - 잘못된 카테고리 ID 형식")
    void getProductsByCategoryId_Failure_InvalidCategoryId() throws Exception {
        // Given
        String invalidCategoryId = "invalid";

        // When & Then
        mockMvc.perform(get("/product/category/{categoryId}", invalidCategoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - 서비스 예외 발생")
    void getProductsByCategoryId_Failure_ServiceException() throws Exception {
        // Given
        Long categoryId = 1L;

        when(productService.getProductsByCategoryId(categoryId))
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        mockMvc.perform(get("/product/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("데이터베이스 연결 오류"));
    }

    // ==================== 전체 카테고리 조회 테스트 ====================
    @Test
    @DisplayName("전체 카테고리 조회 성공")
    void getAllCategories_Success() throws Exception {
        // Given
        List<CategoryResponse> mockCategories = Arrays.asList(
                CategoryResponse.builder()
                        .id(1L)
                        .code("ELECTRONICS")
                        .name("전자제품")
                        .slug("electronics")
                        .description("다양한 전자제품을 만나보세요")
                        .parentId(null)
                        .iconUrl("/icons/electronics.png")
                        .bannerImage("/banners/electronics.jpg")
                        .sortOrder(1)
                        .isActive(true)
                        .seoTitle("전자제품 카테고리")
                        .seoDescription("최신 전자제품을 만나보세요")
                        .createdAt("2024-01-01T00:00:00")
                        .updatedAt("2024-01-01T00:00:00")
                        .build(),
                CategoryResponse.builder()
                        .id(2L)
                        .code("CLOTHING")
                        .name("의류")
                        .slug("clothing")
                        .description("트렌디한 의류를 만나보세요")
                        .parentId(null)
                        .iconUrl("/icons/clothing.png")
                        .bannerImage("/banners/clothing.jpg")
                        .sortOrder(2)
                        .isActive(true)
                        .seoTitle("의류 카테고리")
                        .seoDescription("트렌디한 의류를 만나보세요")
                        .createdAt("2024-01-01T00:00:00")
                        .updatedAt("2024-01-01T00:00:00")
                        .build()
        );

        when(productService.getAllCategories()).thenReturn(mockCategories);

        // When & Then
        mockMvc.perform(get("/product/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("카테고리 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("전자제품"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("의류"));
    }

    // ==================== 활성화된 카테고리 조회 테스트 ====================
    @Test
    @DisplayName("활성화된 카테고리 조회 성공")
    void getActiveCategories_Success() throws Exception {
        // Given
        List<CategoryResponse> mockActiveCategories = Arrays.asList(
                CategoryResponse.builder()
                        .id(1L)
                        .code("ELECTRONICS")
                        .name("전자제품")
                        .slug("electronics")
                        .description("다양한 전자제품을 만나보세요")
                        .parentId(null)
                        .iconUrl("/icons/electronics.png")
                        .bannerImage("/banners/electronics.jpg")
                        .sortOrder(1)
                        .isActive(true)
                        .seoTitle("전자제품 카테고리")
                        .seoDescription("최신 전자제품을 만나보세요")
                        .createdAt("2024-01-01T00:00:00")
                        .updatedAt("2024-01-01T00:00:00")
                        .build()
        );

        when(productService.getActiveCategories()).thenReturn(mockActiveCategories);

        // When & Then
        mockMvc.perform(get("/product/categories/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("활성화된 카테고리 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("전자제품"))
                .andExpect(jsonPath("$.data[0].isActive").value(true));
    }

    @Test
    @DisplayName("카테고리별 상품 조회 - Content-Type이 없는 경우")
    void getProductsByCategoryId_NoContentType() throws Exception {
        // Given
        Long categoryId = 1L;
        List<ProductResponse> mockProducts = Collections.emptyList();

        when(productService.getProductsByCategoryId(categoryId)).thenReturn(mockProducts);

        // When & Then
        mockMvc.perform(get("/product/category/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== 엔드포인트 경로 테스트 ====================
    @Test
    @DisplayName("카테고리별 상품 조회 - 잘못된 URL 경로")
    void getProductsByCategoryId_WrongUrlPath() throws Exception {
        // When & Then
        mockMvc.perform(get("/wrong/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

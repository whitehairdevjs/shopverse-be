package org.biz.shopverse.service.product;

import org.biz.shopverse.dto.product.response.CategoryResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.mapper.product.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // 테스트 전 초기화 작업이 필요한 경우 여기에 작성
    }

    // ==================== 카테고리별 상품 조회 테스트 ====================
    @Test
    @DisplayName("카테고리별 상품 조회 성공 - 정상적인 카테고리 ID")
    void getProductsByCategoryId_Success() {
        // Given
        Long categoryId = 1L;
        List<ProductResponse> expectedProducts = Arrays.asList(
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

        when(productMapper.selectProductsByCategoryId(categoryId)).thenReturn(expectedProducts);

        // When
        List<ProductResponse> actualProducts = productService.getProductsByCategoryId(categoryId);

        // Then
        assertThat(actualProducts).isNotNull();
        assertThat(actualProducts).hasSize(2);
        assertThat(actualProducts.get(0).getId()).isEqualTo(1L);
        assertThat(actualProducts.get(0).getName()).isEqualTo("프리미엄 무선 이어폰");
        assertThat(actualProducts.get(0).getCategoryId()).isEqualTo(categoryId);
        assertThat(actualProducts.get(0).getPrice()).isEqualTo(new BigDecimal("199000"));
        assertThat(actualProducts.get(1).getId()).isEqualTo(2L);
        assertThat(actualProducts.get(1).getName()).isEqualTo("스마트워치 시리즈 5");
        assertThat(actualProducts.get(1).getCategoryId()).isEqualTo(categoryId);
        assertThat(actualProducts.get(1).getPrice()).isEqualTo(new BigDecimal("299000"));

        verify(productMapper, times(1)).selectProductsByCategoryId(categoryId);
    }

    @Test
    @DisplayName("카테고리별 상품 조회 성공 - 빈 상품 목록")
    void getProductsByCategoryId_Success_EmptyList() {
        // Given
        Long categoryId = 999L;
        List<ProductResponse> emptyProducts = Collections.emptyList();

        when(productMapper.selectProductsByCategoryId(categoryId)).thenReturn(emptyProducts);

        // When
        List<ProductResponse> actualProducts = productService.getProductsByCategoryId(categoryId);

        // Then
        assertThat(actualProducts).isNotNull();
        assertThat(actualProducts).isEmpty();

        verify(productMapper, times(1)).selectProductsByCategoryId(categoryId);
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - null 카테고리 ID")
    void getProductsByCategoryId_Failure_NullCategoryId() {
        // Given
        Long categoryId = null;

        // When & Then
        assertThatThrownBy(() -> productService.getProductsByCategoryId(categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리 ID는 필수입니다.");

        verify(productMapper, never()).selectProductsByCategoryId(any());
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - 음수 카테고리 ID")
    void getProductsByCategoryId_Failure_NegativeCategoryId() {
        // Given
        Long categoryId = -1L;

        // When & Then
        assertThatThrownBy(() -> productService.getProductsByCategoryId(categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리 ID는 양수여야 합니다.");

        verify(productMapper, never()).selectProductsByCategoryId(any());
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - 0 카테고리 ID")
    void getProductsByCategoryId_Failure_ZeroCategoryId() {
        // Given
        Long categoryId = 0L;

        // When & Then
        assertThatThrownBy(() -> productService.getProductsByCategoryId(categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리 ID는 양수여야 합니다.");

        verify(productMapper, never()).selectProductsByCategoryId(any());
    }

    @Test
    @DisplayName("카테고리별 상품 조회 실패 - 데이터베이스 오류")
    void getProductsByCategoryId_Failure_DatabaseError() {
        // Given
        Long categoryId = 1L;

        when(productMapper.selectProductsByCategoryId(categoryId))
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        assertThatThrownBy(() -> productService.getProductsByCategoryId(categoryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(productMapper, times(1)).selectProductsByCategoryId(categoryId);
    }

    // ==================== 전체 카테고리 조회 테스트 ====================
    @Test
    @DisplayName("전체 카테고리 조회 성공")
    void getAllCategories_Success() {
        // Given
        List<CategoryResponse> expectedCategories = Arrays.asList(
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
                        .build(),
                CategoryResponse.builder()
                        .id(3L)
                        .code("BOOKS")
                        .name("도서")
                        .slug("books")
                        .description("다양한 도서를 만나보세요")
                        .parentId(null)
                        .iconUrl("/icons/books.png")
                        .bannerImage("/banners/books.jpg")
                        .sortOrder(3)
                        .isActive(false)
                        .seoTitle("도서 카테고리")
                        .seoDescription("다양한 도서를 만나보세요")
                        .createdAt("2024-01-01T00:00:00")
                        .updatedAt("2024-01-01T00:00:00")
                        .build()
        );

        when(productMapper.findAllCategories()).thenReturn(expectedCategories);

        // When
        List<CategoryResponse> actualCategories = productService.getAllCategories();

        // Then
        assertThat(actualCategories).isNotNull();
        assertThat(actualCategories).hasSize(3);
        assertThat(actualCategories.get(0).getId()).isEqualTo(1L);
        assertThat(actualCategories.get(0).getName()).isEqualTo("전자제품");
        assertThat(actualCategories.get(0).getIsActive()).isTrue();
        assertThat(actualCategories.get(1).getId()).isEqualTo(2L);
        assertThat(actualCategories.get(1).getName()).isEqualTo("의류");
        assertThat(actualCategories.get(1).getIsActive()).isTrue();
        assertThat(actualCategories.get(2).getId()).isEqualTo(3L);
        assertThat(actualCategories.get(2).getName()).isEqualTo("도서");
        assertThat(actualCategories.get(2).getIsActive()).isFalse();

        verify(productMapper, times(1)).findAllCategories();
    }

    @Test
    @DisplayName("전체 카테고리 조회 성공 - 빈 카테고리 목록")
    void getAllCategories_Success_EmptyList() {
        // Given
        List<CategoryResponse> emptyCategories = Collections.emptyList();

        when(productMapper.findAllCategories()).thenReturn(emptyCategories);

        // When
        List<CategoryResponse> actualCategories = productService.getAllCategories();

        // Then
        assertThat(actualCategories).isNotNull();
        assertThat(actualCategories).isEmpty();

        verify(productMapper, times(1)).findAllCategories();
    }

    @Test
    @DisplayName("전체 카테고리 조회 실패 - 데이터베이스 오류")
    void getAllCategories_Failure_DatabaseError() {
        // Given
        when(productMapper.findAllCategories())
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        assertThatThrownBy(() -> productService.getAllCategories())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(productMapper, times(1)).findAllCategories();
    }

    // ==================== 활성화된 카테고리 조회 테스트 ====================
    @Test
    @DisplayName("활성화된 카테고리 조회 성공")
    void getActiveCategories_Success() {
        // Given
        List<CategoryResponse> expectedActiveCategories = Arrays.asList(
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

        when(productMapper.findActiveCategories()).thenReturn(expectedActiveCategories);

        // When
        List<CategoryResponse> actualActiveCategories = productService.getActiveCategories();

        // Then
        assertThat(actualActiveCategories).isNotNull();
        assertThat(actualActiveCategories).hasSize(2);
        assertThat(actualActiveCategories.get(0).getId()).isEqualTo(1L);
        assertThat(actualActiveCategories.get(0).getName()).isEqualTo("전자제품");
        assertThat(actualActiveCategories.get(0).getIsActive()).isTrue();
        assertThat(actualActiveCategories.get(1).getId()).isEqualTo(2L);
        assertThat(actualActiveCategories.get(1).getName()).isEqualTo("의류");
        assertThat(actualActiveCategories.get(1).getIsActive()).isTrue();

        // 모든 카테고리가 활성화 상태인지 확인
        assertThat(actualActiveCategories).allMatch(CategoryResponse::getIsActive);

        verify(productMapper, times(1)).findActiveCategories();
    }

    @Test
    @DisplayName("활성화된 카테고리 조회 성공 - 빈 활성화된 카테고리 목록")
    void getActiveCategories_Success_EmptyList() {
        // Given
        List<CategoryResponse> emptyActiveCategories = Collections.emptyList();

        when(productMapper.findActiveCategories()).thenReturn(emptyActiveCategories);

        // When
        List<CategoryResponse> actualActiveCategories = productService.getActiveCategories();

        // Then
        assertThat(actualActiveCategories).isNotNull();
        assertThat(actualActiveCategories).isEmpty();

        verify(productMapper, times(1)).findActiveCategories();
    }

    @Test
    @DisplayName("활성화된 카테고리 조회 실패 - 데이터베이스 오류")
    void getActiveCategories_Failure_DatabaseError() {
        // Given
        when(productMapper.findActiveCategories())
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        assertThatThrownBy(() -> productService.getActiveCategories())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 연결 오류");

        verify(productMapper, times(1)).findActiveCategories();
    }

    // ==================== 경계값 테스트 ====================
    @Test
    @DisplayName("카테고리별 상품 조회 - 최대 Long 값")
    void getProductsByCategoryId_BoundaryValue_MaxLong() {
        // Given
        Long categoryId = Long.MAX_VALUE;
        List<ProductResponse> emptyProducts = Collections.emptyList();

        when(productMapper.selectProductsByCategoryId(categoryId)).thenReturn(emptyProducts);

        // When
        List<ProductResponse> actualProducts = productService.getProductsByCategoryId(categoryId);

        // Then
        assertThat(actualProducts).isNotNull();
        assertThat(actualProducts).isEmpty();

        verify(productMapper, times(1)).selectProductsByCategoryId(categoryId);
    }

    @Test
    @DisplayName("카테고리별 상품 조회 - 0 카테고리 ID")
    void getProductsByCategoryId_BoundaryValue_Zero() {
        // Given
        Long categoryId = 0L;

        // When & Then
        assertThatThrownBy(() -> productService.getProductsByCategoryId(categoryId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productMapper, never()).selectProductsByCategoryId(any());
    }

    // ==================== 성능 테스트 ====================
    @Test
    @DisplayName("카테고리별 상품 조회 - 대량 데이터 처리")
    void getProductsByCategoryId_Performance_LargeDataset() {
        // Given
        Long categoryId = 1L;
        List<ProductResponse> largeProductList = Arrays.asList(
                ProductResponse.builder().id(1L).name("상품1").categoryId(categoryId).build(),
                ProductResponse.builder().id(2L).name("상품2").categoryId(categoryId).build(),
                ProductResponse.builder().id(3L).name("상품3").categoryId(categoryId).build(),
                ProductResponse.builder().id(4L).name("상품4").categoryId(categoryId).build(),
                ProductResponse.builder().id(5L).name("상품5").categoryId(categoryId).build()
        );

        when(productMapper.selectProductsByCategoryId(categoryId)).thenReturn(largeProductList);

        // When
        long startTime = System.currentTimeMillis();
        List<ProductResponse> actualProducts = productService.getProductsByCategoryId(categoryId);
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(actualProducts).isNotNull();
        assertThat(actualProducts).hasSize(5);
        assertThat(endTime - startTime).isLessThan(1000); // 1초 이내에 처리되어야 함

        verify(productMapper, times(1)).selectProductsByCategoryId(categoryId);
    }
}

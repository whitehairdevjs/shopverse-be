package org.biz.shopverse.service.product;

import org.biz.shopverse.dto.product.request.ProductListRequest;
import org.biz.shopverse.dto.product.response.ProductListPageResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.biz.shopverse.mapper.product.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 페이지네이션 조회 - 기본값 페이지/사이즈")
    void getProductsPaged_Defaults() {
        // Given
        ProductListRequest req = ProductListRequest.builder().build();
        when(productMapper.countProducts(null, null)).thenReturn(0L);

        // When
        ProductListPageResponse resp = productService.getProductsPaged(req);

        // Then
        assertThat(resp.getProducts()).isEmpty();
        assertThat(resp.getTotalCount()).isEqualTo(0);
        assertThat(resp.getCurrentPage()).isEqualTo(1);
        assertThat(resp.getTotalPages()).isEqualTo(1);
        assertThat(resp.isHasNext()).isFalse();
        assertThat(resp.isHasPrevious()).isFalse();
    }

    @Test
    @DisplayName("상품 목록 페이지네이션 조회 - 데이터 존재")
    void getProductsPaged_WithData() {
        // Given
        ProductListRequest req = ProductListRequest.builder()
                .categoryId(1L)
                .page(2)
                .size(2)
                .sort("latest")
                .build();

        List<ProductResponse> pageData = Arrays.asList(
                ProductResponse.builder().id(3L).name("상품3").categoryId(1L).price(new BigDecimal("1000")).build(),
                ProductResponse.builder().id(4L).name("상품4").categoryId(1L).price(new BigDecimal("2000")).build()
        );

        when(productMapper.countProducts(eq(1L), isNull())).thenReturn(5L);
        when(productMapper.selectProductsPaged(eq(1L), eq("latest"), eq(2), eq(2), isNull())).thenReturn(pageData);

        // When
        ProductListPageResponse resp = productService.getProductsPaged(req);

        // Then
        assertThat(resp.getProducts()).hasSize(2);
        assertThat(resp.getTotalCount()).isEqualTo(5);
        assertThat(resp.getCurrentPage()).isEqualTo(2);
        assertThat(resp.getTotalPages()).isEqualTo(3);
        assertThat(resp.isHasNext()).isTrue();
        assertThat(resp.isHasPrevious()).isTrue();
    }
}



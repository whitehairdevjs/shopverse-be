package org.biz.shopverse.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.biz.shopverse.dto.product.response.ProductListPageResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.biz.shopverse.exception.GlobalExceptionHandler;
import org.biz.shopverse.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;

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

    @Test
    @DisplayName("상품 목록 페이징 조회 성공")
    void getAllProducts_Paged_Success() throws Exception {
        // Given
        ProductListPageResponse response = ProductListPageResponse.builder()
                .products(Arrays.asList(
                        ProductResponse.builder().id(1L).name("상품1").categoryId(1L).price(new BigDecimal("1000")).build(),
                        ProductResponse.builder().id(2L).name("상품2").categoryId(1L).price(new BigDecimal("2000")).build()
                ))
                .totalCount(12)
                .currentPage(1)
                .totalPages(6)
                .hasNext(true)
                .hasPrevious(false)
                .build();

        when(productService.getProductsPaged(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/product/products")
                        .queryParam("categoryId", "1")
                        .queryParam("page", "1")
                        .queryParam("size", "2")
                        .queryParam("sort", "latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("상품 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.totalCount").value(12))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(6))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.hasPrevious").value(false));
    }
}



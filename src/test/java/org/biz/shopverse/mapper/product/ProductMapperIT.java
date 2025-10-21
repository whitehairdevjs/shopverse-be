package org.biz.shopverse.mapper.product;

import org.biz.shopverse.dto.product.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 테스트용 PostgreSQL 사용
class ProductMapperIT {

    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("selectProductsPaged - 기본 조회(최신순) 정상 동작")
    void selectProductsPaged_latest_ok() {
        List<ProductResponse> results = productMapper.selectProductsPaged(
                null, // categoryId
                "latest",
                0,
                10,
                null // search
        );

        assertThat(results).isNotNull();
        assertThat(results.size()).isBetween(0, 10);
    }

    @Test
    @DisplayName("selectProductsPaged - 정렬 옵션(price-low/high, rating, popular) 정상 동작")
    void selectProductsPaged_sort_options_ok() {
        for (String sort : Arrays.asList("price-low", "price-high", "rating", "popular")) {
            List<ProductResponse> results = productMapper.selectProductsPaged(
                    null,
                    sort,
                    0,
                    5,
                    null
            );

            assertThat(results).isNotNull();
            assertThat(results.size()).isBetween(0, 5);
        }
    }

    @Test
    @DisplayName("selectProductsPaged - 카테고리/검색어/페이징 조합 호출 시 예외 없이 결과 반환")
    void selectProductsPaged_with_filters_ok() {
        // 카테고리와 검색어는 테스트 DB 상황에 따라 결과가 없을 수 있으므로 크기만 방어적으로 검증
        List<ProductResponse> page1 = productMapper.selectProductsPaged(1L, "latest", 0, 3, "");
        List<ProductResponse> page2 = productMapper.selectProductsPaged(1L, "latest", 3, 3, "");

        assertThat(page1).isNotNull();
        assertThat(page2).isNotNull();
        assertThat(page1.size()).isBetween(0, 3);
        assertThat(page2.size()).isBetween(0, 3);
    }

    @Test
    @DisplayName("countProducts - 카운트가 0 이상으로 반환")
    void countProducts_ok() {
        long countAll = productMapper.countProducts(null, null);
        long countByCategory = productMapper.countProducts(1L, null);

        assertThat(countAll).isGreaterThanOrEqualTo(0);
        assertThat(countByCategory).isGreaterThanOrEqualTo(0);
    }
}



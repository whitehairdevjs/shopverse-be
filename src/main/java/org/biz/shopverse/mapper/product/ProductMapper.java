package org.biz.shopverse.mapper.product;

import org.biz.shopverse.dto.product.response.CategoryResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    List<CategoryResponse> findAllCategories();
    
    List<CategoryResponse> findActiveCategories();
    
    List<ProductResponse> selectProductsPaged(
            @Param("categoryId") Long categoryId,
            @Param("sort") String sort,
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("search") String search
    );

    long countProducts(
            @Param("categoryId") Long categoryId,
            @Param("search") String search
    );
}
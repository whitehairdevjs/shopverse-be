package org.biz.shopverse.mapper.product;

import org.biz.shopverse.domain.product.Category;
import org.biz.shopverse.dto.product.response.CategoryResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;

import java.util.List;

public interface ProductMapper {
    List<CategoryResponse> findAllCategories();
    
    List<CategoryResponse> findActiveCategories();
    
    List<ProductResponse> selectProductsByCategoryId(Long categoryId);

    List<ProductResponse> selectAllProducts();
}

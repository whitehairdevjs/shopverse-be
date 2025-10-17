package org.biz.shopverse.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biz.shopverse.domain.product.Category;
import org.biz.shopverse.dto.product.response.CategoryResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.biz.shopverse.mapper.product.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;

    public List<CategoryResponse> getAllCategories() {
        return productMapper.findAllCategories();
    }

    public List<CategoryResponse> getActiveCategories() {
        return productMapper.findActiveCategories();
    }

    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("카테고리 ID는 양수여야 합니다.");
        }
        
        return productMapper.selectProductsByCategoryId(categoryId);
    }

    public List<ProductResponse> getAllProducts() {
        return productMapper.selectAllProducts();
    }
}

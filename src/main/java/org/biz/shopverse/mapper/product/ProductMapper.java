package org.biz.shopverse.mapper.product;

import org.biz.shopverse.domain.product.Category;

import java.util.List;

public interface ProductMapper {
    List<Category> findAllCategories();
    
    List<Category> findActiveCategories();
}

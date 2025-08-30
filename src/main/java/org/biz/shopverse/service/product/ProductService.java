package org.biz.shopverse.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biz.shopverse.domain.product.Category;
import org.biz.shopverse.dto.product.response.CategoryResponse;
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
        List<Category> categories = productMapper.findAllCategories();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getActiveCategories() {
        List<Category> categories = productMapper.findActiveCategories();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse convertToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .iconUrl(category.getIconUrl())
                .bannerImage(category.getBannerImage())
                .sortOrder(category.getSortOrder())
                .isActive(category.getIsActive())
                .seoTitle(category.getSeoTitle())
                .seoDescription(category.getSeoDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

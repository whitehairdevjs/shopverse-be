package org.biz.shopverse.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biz.shopverse.domain.product.Category;
import org.biz.shopverse.dto.product.request.ProductListRequest;
import org.biz.shopverse.dto.product.response.ProductListPageResponse;
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

    public ProductListPageResponse getProductsPaged(ProductListRequest request) {
        int requestedPage = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 10;

        Long categoryId = request.getCategoryId();
        String sort = request.getSort();
        String search = request.getSearch();

        long totalCount = productMapper.countProducts(categoryId, search);
        int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) size));
        int currentPage = Math.max(1, Math.min(requestedPage, totalPages));
        int offset = (currentPage - 1) * size;

        List<ProductResponse> products = totalCount > 0
                ? productMapper.selectProductsPaged(categoryId, sort, offset, size, search)
                : List.of();

        boolean hasNext = currentPage < totalPages;
        boolean hasPrevious = currentPage > 1;

        return ProductListPageResponse.builder()
                .products(products)
                .totalCount(totalCount)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }
}

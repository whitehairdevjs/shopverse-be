package org.biz.shopverse.controller.product;

import lombok.RequiredArgsConstructor;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.dto.product.request.ProductListRequest;
import org.biz.shopverse.dto.product.response.CategoryResponse;
import org.biz.shopverse.dto.product.response.ProductListPageResponse;
import org.biz.shopverse.dto.product.response.ProductResponse;
import org.biz.shopverse.service.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "카테고리 목록을 조회했습니다."));
    }

    @GetMapping("/categories/active")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActiveCategories() {
        List<CategoryResponse> categories = productService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "활성화된 카테고리 목록을 조회했습니다."));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<ProductListPageResponse>> getAllProducts(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "search", required = false) String search
    ) {
        ProductListRequest req = ProductListRequest.builder()
                .categoryId(categoryId)
                .sort(sort)
                .page(page)
                .size(size)
                .search(search)
                .build();

        ProductListPageResponse response = productService.getProductsPaged(req);
        return ResponseEntity.ok(ApiResponse.success(response, "상품 목록을 조회했습니다."));
    }
}

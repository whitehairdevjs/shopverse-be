package org.biz.shopverse.dto.product.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListRequest {
    private Long categoryId; // 가장 구체적으로 선택된 카테고리 ID (소분류 우선)

    @Pattern(regexp = "^(latest|price-low|price-high|popular|rating)?$", message = "잘못된 정렬 값입니다.")
    private String sort;

    @Min(value = 1, message = "페이지는 1 이상이어야 합니다.")
    private Integer page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    private Integer size;

    private String search;
}
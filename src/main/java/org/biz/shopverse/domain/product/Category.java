package org.biz.shopverse.domain.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private String code;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private String iconUrl;
    private String bannerImage;
    private Integer sortOrder;
    private Boolean isActive;
    private String seoTitle;
    private String seoDescription;
    private String createdAt;
    private String updatedAt;
}

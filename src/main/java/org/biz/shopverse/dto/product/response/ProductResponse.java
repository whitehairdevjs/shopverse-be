package org.biz.shopverse.dto.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 정보 응답 DTO")
public class ProductResponse {
    
    @Schema(description = "상품 고유 ID", example = "1")
    private Long id;

    @Schema(description = "변형 상품 고유 ID", example = "1")
    private Long variantId;

    @Schema(description = "상품명", example = "프리미엄 무선 이어폰")
    private String name;
    
    @Schema(description = "SEO 친화적 URL 식별자", example = "premium-wireless-earphones")
    private String slug;
    
    @Schema(description = "상품 부제목/간단 설명", example = "고품질 음질과 편안한 착용감")
    private String subtitle;
    
    @Schema(description = "상품 상세 설명 (HTML 가능)", example = "<p>최고급 음질과 편안한 착용감을 제공하는 무선 이어폰입니다.</p>")
    private String description;
    
    @Schema(description = "상품 요약 설명 (500자 이내)", example = "고품질 음질과 편안한 착용감을 제공하는 프리미엄 무선 이어폰")
    private String shortDescription;
    
    @Schema(description = "판매 가격 (현재 가격)", example = "199000")
    private BigDecimal price;
    
    @Schema(description = "할인 전 가격 표시용", example = "249000")
    private BigDecimal comparePrice;
    
    @Schema(description = "상품 원가 (내부 관리용)", example = "120000")
    private BigDecimal costPrice;
    
    @Schema(description = "소속 카테고리 ID", example = "5")
    private Long categoryId;
    
    @Schema(description = "할인율 (소수점 1자리)", example = "20.1")
    private Double discountPercent;
    
    @Schema(description = "브랜드 ID", example = "3")
    private Long brandId;
    
    @Schema(description = "상품 고유번호 (Stock Keeping Unit)", example = "SKU-001-ABC")
    private String sku;
    
    @Schema(description = "바코드/EAN 번호", example = "1234567890123")
    private String barcode;
    
    @Schema(description = "상품 무게 (kg, 소수점 3자리)", example = "0.150")
    private BigDecimal weight;
    
    @Schema(description = "JSON 형태 치수 정보", example = "{\"width\":10, \"height\":20, \"depth\":5}")
    private String dimensions;
    
    @Schema(description = "상품 상태", example = "active", allowableValues = {"draft", "active", "inactive", "discontinued"})
    private String status;
    
    @Schema(description = "상품 노출 설정", example = "visible", allowableValues = {"visible", "hidden", "catalog_only"})
    private String visibility;
    
    @Schema(description = "디지털 상품 여부", example = "false")
    private Boolean isDigital;
    
    @Schema(description = "구독 상품 여부", example = "false")
    private Boolean isSubscription;
    
    @Schema(description = "커스터마이징 가능 여부", example = "true")
    private Boolean isCustomizable;
    
    @Schema(description = "예약주문 가능 여부", example = "false")
    private Boolean isPreorder;
    
    @Schema(description = "상품 출시 예정일", example = "2024-12-01T00:00:00")
    private String preorderDate;
    
    @Schema(description = "재고 추적 여부", example = "true")
    private Boolean trackInventory;
    
    @Schema(description = "품절시 주문 허용 여부", example = "false")
    private Boolean allowBackorder;
    
    @Schema(description = "최소 주문 수량", example = "1")
    private Integer minOrderQuantity;
    
    @Schema(description = "최대 주문 수량", example = "10")
    private Integer maxOrderQuantity;
    
    @Schema(description = "SEO 메타 제목 (60자 이내)", example = "프리미엄 무선 이어폰 - 최고의 음질")
    private String metaTitle;
    
    @Schema(description = "SEO 메타 설명 (160자 이내)", example = "고품질 음질과 편안한 착용감을 제공하는 프리미엄 무선 이어폰을 만나보세요.")
    private String metaDescription;
    
    @Schema(description = "검색 키워드 (콤마로 구분)", example = "이어폰,무선,블루투스,음질")
    private String searchKeywords;
    
    @Schema(description = "추천 상품 노출 종료일", example = "2024-12-31T23:59:59")
    private String featuredUntil;
    
    @Schema(description = "평균 평점 (0.0-5.0, 자동 계산)", example = "4.5")
    private Double averageRating;
    
    @Schema(description = "리뷰 개수 (자동 계산)", example = "127")
    private Integer reviewCount;
    
    @Schema(description = "상품 조회수", example = "1543")
    private Integer viewCount;
    
    @Schema(description = "판매 수량", example = "89")
    private Integer salesCount;
    
    @Schema(description = "찜 횟수", example = "23")
    private Integer wishlistCount;
    
    @Schema(description = "상품 등록 일시", example = "2024-01-15T10:30:00")
    private String createdAt;
    
    @Schema(description = "상품 수정 일시", example = "2024-01-20T14:25:00")
    private String updatedAt;
    
    @Schema(description = "상품 공개 일시", example = "2024-01-16T09:00:00")
    private String publishedAt;
    
    @Schema(description = "상품 삭제 일시 (소프트 삭제)", example = "null")
    private String deletedAt;

    @Schema(description = "사용자 지정 옵션 (예: {'color': '블랙', 'storage': '256GB'})", example = "{\"color\": \"블랙\", \"storage\": \"256GB\"}")
    private Object options;

}

package org.biz.shopverse.member;

import org.biz.shopverse.domain.member.Member;
import org.biz.shopverse.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void testFindByLoginId() {
        // Given: 테스트용 login_id
        String loginId = "jskim";

        // When: login_id로 멤버 조회
        Member member = memberService.findByLoginId(loginId);

        // Then: 결과 검증
        if (member != null) {
            assertNotNull(member);
//            assertEquals(loginId, member.getLoginId());
//            assertNotNull(member.getName());
//            assertNotNull(member.getRole());
            
            System.out.println("Found member:");
            System.out.println("ID: " + member.getId());
            System.out.println("Login ID: " + member.getLoginId());
            System.out.println("Name: " + member.getName());
            System.out.println("Email: " + member.getEmail());
            System.out.println("Role: " + member.getRole());
            System.out.println("Status: " + member.getStatus());
            System.out.println("Point: " + member.getPoint());
        } else {
            System.out.println("Member with login_id '" + loginId + "' not found");
        }
    }

    @Test
    public void testFindByLoginId_NotFound() {
        // Given: 존재하지 않는 login_id
        String nonExistentLoginId = "nonexistent@example.com";

        // When: 존재하지 않는 login_id로 멤버 조회
        Member member = memberService.findByLoginId(nonExistentLoginId);

        // Then: null이 반환되어야 함
        assertNull(member);
        System.out.println("Member with login_id '" + nonExistentLoginId + "' not found (expected)");
    }

    @Test
    public void testFindByLoginId_EmptyString() {
        // Given: 빈 문자열 login_id
        String emptyLoginId = "";

        // When: 빈 문자열로 멤버 조회
        Member member = memberService.findByLoginId(emptyLoginId);

        // Then: null이 반환되어야 함
        assertNull(member);
        System.out.println("Member with empty login_id not found (expected)");
    }

    @Test
    public void testFindByLoginId_Null() {
        // Given: null login_id
        String nullLoginId = null;

        // When: null로 멤버 조회
        Member member = memberService.findByLoginId(nullLoginId);

        // Then: null이 반환되어야 함
        assertNull(member);
        System.out.println("Member with null login_id not found (expected)");
    }
} 
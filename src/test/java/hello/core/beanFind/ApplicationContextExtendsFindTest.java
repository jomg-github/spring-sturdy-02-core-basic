package hello.core.beanFind;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationContextExtendsFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
    @Configuration
    static class TestConfig {
        @Bean
        public DiscountPolicy rateDiscountPolicy() {
            return new RateDiscountPolicy();
        }

        @Bean
        public DiscountPolicy fixDiscountPolicy() {
            return new FixDiscountPolicy();
        }
    }

    @Test
    @DisplayName("부모 타입으로 조회 시 자식이 둘 이상 있으면 중복 오류 발생")
    void findBeanByParentTypeDuplicate() {
        assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(DiscountPolicy.class));
    }

    @Test
    @DisplayName("부모 타입으로 조회 시 자식이 둘 이상 있으면 빈 이름을 지정해서 조회")
    void findBeanByParentTypeByBeanName() {
        DiscountPolicy rateDiscountPolicy = ac.getBean("rateDiscountPolicy", DiscountPolicy.class);
        assertThat(rateDiscountPolicy).isInstanceOf(DiscountPolicy.class);
    }

    @Test
    @DisplayName("특정 하위 타입(구현 클래스)으로 조회 -> DIP에는 위반")
    void findBeanBySubType() {
        DiscountPolicy rateDiscountPolicy = ac.getBean(RateDiscountPolicy.class);
        assertThat(rateDiscountPolicy).isInstanceOf(DiscountPolicy.class);
        assertThat(rateDiscountPolicy).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("부모타입으로 하위 타입 모두 조회")
    void findAllBeansByParentType() {
        Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);

        for (String key : beansOfType.keySet()) {
            DiscountPolicy discountPolicy = beansOfType.get(key);
            System.out.println("key = " + key + " value = " + discountPolicy);
            assertThat(discountPolicy).isInstanceOf(DiscountPolicy.class);
        }
    }

}

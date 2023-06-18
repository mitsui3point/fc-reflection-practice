package org.example;

import org.assertj.core.api.Assertions;
import org.example.annotation.Controller;
import org.example.annotation.Service;
import org.example.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Controller 애노테이션이 설정돼 있는 모든 클래스를 찾아서 출력한다.
 * <p>
 * Reflection
 * 힙 영역에 로드돼 있는 클래스 타입의 객체를 통해 필드/메소드/생성자를 접근 제어자와 상관 없이 사용할 수 있도록 지원하는 API
 * 컴파일 시점이 아닌 런타임 시점에 동적으로 특정 클래스의 정보를 추출해낼 수 있는 프로그래밍 기법
 * 주로 프레임워크 또는 라이브러리 개발 시 사용됨
 * https://www.baeldung.com/reflections-library
 */
public class ReflectionTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionTest.class);

    @DisplayName("Controller 애노테이션이 설정돼 있는 모든 클래스를 찾아서 출력한다.")
    @Test
    void controllerScan() {
        //given
        Set<Class<?>> beans = getTypesAnnotatedWith(List.of(Controller.class, Service.class));

        log.debug("beans: [{}]", beans);
    }

    @DisplayName("리플렉션으로 클래스의 대한 필드, 생성자, 메서드 메타정보를 확인한다.")
    @Test
    void showClass() {
        Class<User> clazz = User.class;
        log.debug("user: [{}]", clazz.getName());

        log.debug(
                "User all declared fields: [{}]",
                Arrays.stream(clazz.getDeclaredFields())
                        .collect(Collectors.toList()));
        log.debug(
                "User all declared constructors: [{}]",
                Arrays.stream(clazz.getDeclaredConstructors())
                        .collect(Collectors.toList()));
        log.debug(
                "User all declared methods: [{}]",
                Arrays.stream(clazz.getDeclaredMethods())
                        .collect(Collectors.toList()));
    }

    @DisplayName("힙 영역에 로드되어있는 클래스 타입의 객체를 호출을 세가지 방법으로 구현한다.")
    @Test
    void load() throws ClassNotFoundException {
        // 첫번쨰 방법
        Class<User> clazz1 = User.class;

        // 두번째 방법
        User user = new User("userId", "김윤호");
        Class<? extends User> clazz2 = user.getClass();

        // 세번째 방법
        Class<?> clazz3 = Class.forName("org.example.model.User");

        log.debug("clazz1: [{}]", clazz1);
        log.debug("clazz2: [{}]", clazz2);
        log.debug("clazz3: [{}]", clazz3);

        assertThat(clazz1.equals(clazz2));
        assertThat(clazz2.equals(clazz3));
        assertThat(clazz3.equals(clazz1));
    }

    private Set<Class<?>> getTypesAnnotatedWith(List<Class<? extends Annotation>> annotations) {
        String targetPackage = "org.example";
        Reflections reflections = new Reflections(targetPackage);
        Set<Class<?>> beans = new HashSet<>();

        //when
        annotations.forEach(annotation -> {
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(annotation);
            beans.addAll(typesAnnotatedWith);
        });
        return beans;
    }

}

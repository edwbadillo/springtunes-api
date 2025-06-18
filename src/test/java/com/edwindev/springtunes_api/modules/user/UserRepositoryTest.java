package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.config.jpa.AuditorAwareImpl;
import com.edwindev.springtunes_api.config.jpa.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class})
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldCreateUser() {
        User user = User.builder()
                .id("XYZ")
                .displayName("DisplayName")
                .email("a@b.com")
                .profilePictureUrl("https://google.com")
                .role(User.Role.USER)
                .build();
        repository.save(user);

        User savedUser = repository.findById("XYZ").orElse(null);
        assertThat(savedUser).isNotNull();
    }

    @Test
    void shouldExistByEmail() {
        User user = User.builder()
                .id("XYZ")
                .displayName("DisplayName")
                .email("a@b.com")
                .profilePictureUrl("https://google.com")
                .role(User.Role.USER)
                .build();
        repository.save(user);

        assertThat(repository.existsByEmailIgnoreCase("a@B.com")).isTrue();
        assertThat(repository.existsByEmailIgnoreCase("xyz@b.com")).isFalse();
    }

    @Test
    void shouldExistByEmailAndIdNot() {
        User user = User.builder()
                .id("XYZ")
                .displayName("DisplayName")
                .email("a@b.com")
                .profilePictureUrl("https://google.com")
                .role(User.Role.USER)
                .build();
        repository.save(user);

        assertThat(repository.existsByEmailIgnoreCaseAndIdNot("a@B.com", "XYZ")).isFalse();
        assertThat(repository.existsByEmailIgnoreCaseAndIdNot("a@B.com", "abc")).isTrue();
    }
}

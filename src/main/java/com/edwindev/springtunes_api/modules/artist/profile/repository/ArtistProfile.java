package com.edwindev.springtunes_api.modules.artist.profile.repository;

import com.edwindev.springtunes_api.modules.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The artist profile entity.
 */
@Entity
@Table(name = "artist_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ArtistProfile {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_artist_user"))
    private User user;

    private String artistName;

    private String bio;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Check if the artist profile has a user.
     *
     * @return true if the artist profile has a user, false otherwise.
     */
    public boolean hasUser() {
        return Objects.nonNull(user) && Objects.nonNull(user.getId());
    }

    /**
     * The status of the artist profile.
     */
    public enum Status {
        /**
         * The artist profile is pending review.
         */
        PENDING,

        /**
         * The artist profile is approved and currently active.
         */
        APPROVED,

        /**
         * The artist profile is rejected
         */
        REJECTED,

        /**
         * The artist profile is disabled or banned
         */
        DISABLED
    }
}

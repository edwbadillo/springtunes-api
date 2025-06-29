package com.edwindev.springtunes_api.modules.music.track;

import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.music.album.Album;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The track entity.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Track {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "album_id", foreignKey = @ForeignKey(name = "fk_track_album"))
    private Album album;

    private String title;

    private Integer duration;

    private String audioUrl;

    private String coverUrl;

    private LocalDateTime releaseDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_track_artist"))
    private ArtistProfile artist;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Status of the track
     */
    public enum Status {
        /**
         * Track is pending review by an admin
         */
        PENDING_REVIEW,

        /**
         * Track is approved by an admin and can be launched
         */
        APPROVED,

        /**
         * Track has been banned by an admin
         */
        BANNED,

        /**
         * Track has been rejected by an admin in the review process
         */
        REJECTED
    }
}

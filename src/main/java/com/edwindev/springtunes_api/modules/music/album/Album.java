package com.edwindev.springtunes_api.modules.music.album;

import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The album entity, any track belongs to an album even if it's a single.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String coverUrl;

    @ManyToOne
    @JoinColumn(name = "artist_profile_id", foreignKey = @ForeignKey(name = "fk_album_artist"))
    private ArtistProfile artist;

    /**
     * The album type.
     */
    public enum Type {
        /**
         * The album has a single track.
         */
        SINGLE,

        /**
         * The album contains multiple different tracks.
         */
        ALBUM
    }
}

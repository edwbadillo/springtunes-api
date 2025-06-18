package com.edwindev.springtunes_api.modules.music.track;

import com.edwindev.springtunes_api.modules.music.genre.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(TrackGenre.PK.class)
public class TrackGenre {

    @Id
    @ManyToOne
    @JoinColumn(name = "track_id", foreignKey = @ForeignKey(name = "fk_sg_track"))
    private Track track;

    @Id
    @ManyToOne
    @JoinColumn(name = "genre_id", foreignKey = @ForeignKey(name = "fk_sg_genre"))
    private Genre genre;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private UUID track;
        private Integer genre;
    }
}

package com.edwindev.springtunes_api.modules.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * The user that uses the application, can be a normal user, artist or an admin.
 */
@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id; // UID de Firebase

    private String displayName;

    private String email;

    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }


    public boolean isArtist() {
        return Role.ARTIST.equals(role);
    }

    public boolean isUser() {
        return Role.USER.equals(role);
    }

    public enum Role {
        USER,
        ARTIST,
        ADMIN
    }
}

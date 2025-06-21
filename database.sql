
-- 🔄 Reset de todas las tablas en orden inverso de dependencia
DROP TABLE IF EXISTS playlist_track CASCADE;
DROP TABLE IF EXISTS favorite_track CASCADE;
DROP TABLE IF EXISTS playlist CASCADE;
DROP TABLE IF EXISTS track_genre CASCADE;
DROP TABLE IF EXISTS track CASCADE;
DROP TABLE IF EXISTS album CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS artist_claim_request CASCADE;
DROP TABLE IF EXISTS artist_profile CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;

-- ✅ Tabla: user
CREATE TABLE "user" (
    id VARCHAR PRIMARY KEY, -- Firebase UID
    display_name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    profile_picture_url TEXT,
    role VARCHAR NOT NULL CHECK (role IN ('USER', 'ADMIN', 'ARTIST'))
);

-- ✅ Tabla: artist_profile
CREATE TABLE artist_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR UNIQUE,
    artist_name VARCHAR NOT NULL UNIQUE,
    bio TEXT,
    status VARCHAR NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'DISABLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_artist_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE SET NULL
);

-- ✅ Tabla: artist_claim_request
CREATE TABLE artist_claim_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR NOT NULL,
    artist_profile_id UUID NOT NULL,
    status VARCHAR NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    message TEXT,
    message_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_claim_artist FOREIGN KEY (artist_profile_id) REFERENCES artist_profile(id) ON DELETE CASCADE
);

-- ✅ Tabla: genre
CREATE TABLE genre (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE,
    description TEXT
);

-- ✅ Tabla: album
CREATE TABLE album (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR NOT NULL,
    release_date DATE,
    type VARCHAR NOT NULL CHECK (type IN ('SINGLE', 'ALBUM')),
    cover_url TEXT,
    artist_profile_id UUID NOT NULL,
    CONSTRAINT fk_album_artist FOREIGN KEY (artist_profile_id) REFERENCES artist_profile(id) ON DELETE CASCADE
);

-- ✅ Tabla: track
CREATE TABLE track (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      album_id UUID,
      title VARCHAR NOT NULL,
      duration INTEGER NOT NULL,
      audio_url TEXT NOT NULL,
      cover_url TEXT,
      release_date TIMESTAMP,
      artist_profile_id UUID NOT NULL,
      status VARCHAR NOT NULL CHECK (status IN ('PENDING_REVIEW', 'APPROVED', 'BANNED', 'REJECTED')),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_track_album FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE SET NULL,
      CONSTRAINT fk_track_artist FOREIGN KEY (artist_profile_id) REFERENCES artist_profile(id) ON DELETE CASCADE
  );

  -- ✅ Tabla: track_genre
  CREATE TABLE track_genre (
      track_id UUID NOT NULL,
      genre_id INTEGER NOT NULL,
      PRIMARY KEY (track_id, genre_id),
      CONSTRAINT fk_sg_track FOREIGN KEY (track_id) REFERENCES track(id) ON DELETE CASCADE,
      CONSTRAINT fk_sg_genre FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
  );

  -- ✅ Tabla: playlist
  CREATE TABLE playlist (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      name VARCHAR NOT NULL,
      description TEXT,
      is_public BOOLEAN DEFAULT FALSE,
      user_id VARCHAR NOT NULL,
      CONSTRAINT fk_playlist_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
  );

  -- ✅ Tabla: playlist_track
  CREATE TABLE playlist_track (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      playlist_id UUID NOT NULL,
      track_id UUID NOT NULL,
      order_index INTEGER NOT NULL,
      CONSTRAINT fk_pl_track_playlist FOREIGN KEY (playlist_id) REFERENCES playlist(id) ON DELETE CASCADE,
      CONSTRAINT fk_pl_track_track FOREIGN KEY (track_id) REFERENCES track(id) ON DELETE CASCADE
  );

  -- ✅ Tabla: favorite_track
  CREATE TABLE favorite_track (
      user_id VARCHAR NOT NULL,
      track_id UUID NOT NULL,
      PRIMARY KEY (user_id, track_id),
      CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
      CONSTRAINT fk_fav_track FOREIGN KEY (track_id) REFERENCES track(id) ON DELETE CASCADE
  );

-- 🔧 Activar extensión pgcrypto si no está activa (para gen_random_uuid)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

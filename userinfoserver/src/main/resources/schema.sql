CREATE TABLE IF NOT EXISTS customer (
    user_id    VARCHAR(50) PRIMARY KEY,
    `password` VARCHAR(255),
    name       VARCHAR(100),
    email      VARCHAR(255),
    created_at TIMESTAMP NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS consumed_user_event (
    event_id    VARCHAR(100) PRIMARY KEY,
    consumed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS category (
    category_id        BIGINT PRIMARY KEY,
    category_name      VARCHAR(100),
    parent_category_id BIGINT,
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_category_id) REFERENCES category (category_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS video (
    video_id   BIGINT PRIMARY KEY,
    video_name VARCHAR(255),
    started_at TIMESTAMP NULL,
    ended_at   TIMESTAMP NULL,
    category_id BIGINT,
    CONSTRAINT fk_video_category
        FOREIGN KEY (category_id) REFERENCES category (category_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS follows (
    following_user_id VARCHAR(50),
    followed_user_id  VARCHAR(50),
    followed_at       TIMESTAMP NULL,
    CONSTRAINT pk_follows PRIMARY KEY (following_user_id, followed_user_id),
    CONSTRAINT fk_follows_following FOREIGN KEY (following_user_id) REFERENCES customer (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_followed  FOREIGN KEY (followed_user_id)  REFERENCES customer (user_id) ON DELETE CASCADE,
    INDEX idx_follows_following_user_id (following_user_id, followed_user_id),
    INDEX idx_follows_followed_user_id  (followed_user_id,  following_user_id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS watch_history (
    user_id    VARCHAR(50),
    video_id   BIGINT,
    started_at TIMESTAMP NULL,
    ended_at   TIMESTAMP NULL,
    CONSTRAINT fk_watch_user  FOREIGN KEY (user_id)  REFERENCES customer (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_watch_video FOREIGN KEY (video_id) REFERENCES video    (video_id) ON DELETE CASCADE,
    INDEX idx_watch_user_id  (user_id,  started_at),
    INDEX idx_watch_video_id (video_id, started_at)
) ENGINE = InnoDB;

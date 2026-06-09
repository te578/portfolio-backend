CREATE TABLE IF NOT EXISTS users (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    pass       VARCHAR(255) NOT NULL,
    update_date TIMESTAMP,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    sort_order  INTEGER,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP,
    deleted_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id           SERIAL PRIMARY KEY,
    product_code VARCHAR(50)  NOT NULL UNIQUE,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    price        INTEGER      NOT NULL,
    unit         VARCHAR(20)  NOT NULL,
    min_stock    INTEGER      NOT NULL DEFAULT 0,
    category_id  INTEGER      NOT NULL REFERENCES categories(id),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP,
    deleted_at   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock (
    product_id INTEGER   NOT NULL PRIMARY KEY REFERENCES products(id),
    quantity   INTEGER   NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS transactions (
    id         SERIAL PRIMARY KEY,
    product_id INTEGER     NOT NULL REFERENCES products(id),
    user_id    INTEGER     NOT NULL REFERENCES users(id),
    type       VARCHAR(10) NOT NULL,
    quantity   INTEGER     NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

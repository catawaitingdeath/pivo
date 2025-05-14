CREATE TABLE storage
(
    id    BIGINT PRIMARY KEY,
    beer  VARCHAR(25) NOT NULL,
    store VARCHAR(25) NOT NULL,
    count BIGINT      NOT NULL,
    FOREIGN KEY (beer) REFERENCES beer (id),
    FOREIGN KEY (store) REFERENCES store (id)
);
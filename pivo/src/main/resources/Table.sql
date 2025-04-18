CREATE TABLE type
(
    id BIGINT NOT NULL,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE beer
(
    id VARCHAR(25) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    producer VARCHAR(50),
    price BIGINT NOT NULL,
    alcohol BIGINT NOT NULL,
    type BIGINT NOT NULL,
    FOREIGN KEY (type) REFERENCES type(id)
);

CREATE TABLE store
(
    id VARCHAR(50) PRIMARY KEY,
    address VARCHAR(50) NOT NULL,
    phone VARCHAR(25) NOT NULL
);

CREATE TABLE storage
(
    id VARCHAR(50) PRIMARY KEY,
    beer BIGINT NOT NULL,
    FOREIGN KEY (beer) REFERENCES beer(id),
    store BIGINT NOT NULL,
    FOREIGN KEY (store) REFERENCES store(id),
    count BIGINT NOT NULL
);
CREATE TABLE employee
(
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50),
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(50),
    position VARCHAR(50) NOT NULL,
    salary BIGINT NOT NULL,
    store BIGINT NOT NULL,
    FOREIGN KEY (store) REFERENCES store(id)
);




INSERT INTO store
VALUES
(DEFAULT, 'ул. Лескова, 8', '+7(123)456-78-90'),
(DEFAULT, 'Печатников пер., 3', '+7(543)765-34-21');

INSERT INTO beer
VALUES
(DEFAULT, 'Жигули Барное светлое фильтрованное', 'Московская пивоваренная компания', 70, 4.9, 1),
(DEFAULT, 'Troll Brew IPA светлое нефильтрованное', 'Арвиай', 120, 7.8, 2),
(DEFAULT, 'Волковская Пивоварня Chocolate Stout тёмный нефильтрованный', 'Московская пивоваренная компания', 115, 6.5, 3);

INSERT INTO storage
VALUES
(DEFAULT, 1, 1, 100),
(DEFAULT, 2, 1, 50),
(DEFAULT, 3, 1, 1000),
(DEFAULT, 1, 2, 5),
(DEFAULT, 3, 2, 0);

INSERT INTO employee
VALUES
(DEFAULT, 'Иван', 'Иванов', '+7(968)234-44-44', null, 'продавец', 50000, 1),
(DEFAULT, 'Мария', 'Смирнова', '+7(263)153-48-27', 'smirno_m@mail.ru', 'кладовщик', 30000, 2),
(DEFAULT, 'Спидвагон', null, '+7(836)173-88-44', null, 'уборщик', 20000, 1)





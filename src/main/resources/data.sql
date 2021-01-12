DROP TABLE IF EXISTS items, price_reductions, suppliers, users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    UNIQUE KEY username_const(username)
);

CREATE TABLE price_reductions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount_deducted DOUBLE NOT NULL,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    end_date TIMESTAMP NOT NULL
);
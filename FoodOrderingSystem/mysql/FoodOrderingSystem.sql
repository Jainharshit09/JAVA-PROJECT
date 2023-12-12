use FoodOrderingSystem;
CREATE TABLE Customers (
    UserId VARCHAR(50) PRIMARY KEY,
    Name VARCHAR(255),
    Address VARCHAR(255),
    Contact VARCHAR(20),
    Premium BOOLEAN
);
CREATE TABLE Login (
    UserId INT,
    Username VARCHAR(50) UNIQUE,
    Password VARCHAR(50),
    CONSTRAINT fk_customer FOREIGN KEY (UserId) REFERENCES Customers(UserId)
);
CREATE TABLE Reviews (
    ReviewId INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT,
    Review TEXT,
    Rating FLOAT,
    FOREIGN KEY (UserId) REFERENCES Customers(UserId)
);
ALTER TABLE Customers
MODIFY COLUMN UserId INT AUTO_INCREMENT;
CREATE TABLE Orders (
    OrderId INT PRIMARY KEY AUTO_INCREMENT,
    UserId INT,
    OrderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DOUBLE,
    FOREIGN KEY (UserId) REFERENCES Customers(UserId)
);
CREATE TABLE MenuItems (
    ItemId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Price DOUBLE NOT NULL
);
DELETE FROM orders;
DELETE FROM reviews;
DELETE FROM login;
DELETE FROM customers;
DELETE FROM MenuItems;
select*from customers;
select*from login;
select *from orders;
select*from reviews;
select*from MenuItems;
INSERT INTO MenuItems (Name, Price) VALUES ('Pizza', 199.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Burger', 50.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Cold Coffee', 100.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Tea', 40.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Coke', 40.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Pepsi', 40.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Orange Juice', 110.00);
INSERT INTO MenuItems (Name, Price) VALUES ('Apple Juice', 110.00);


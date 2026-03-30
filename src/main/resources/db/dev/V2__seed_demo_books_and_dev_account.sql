INSERT INTO customers (id, email, password_hash, full_name, phone, status, version)
VALUES (
    1,
    '${demoEmail}',
    '${demoPasswordHash}',
    'Dev Reader',
    '0901234567',
    'ACTIVE',
    0
);

SELECT setval(pg_get_serial_sequence('customers', 'id'), COALESCE((SELECT MAX(id) FROM customers), 1), true);

INSERT INTO books (id, isbn, title, author, description, img_url, price, stock_quantity, active, version)
VALUES
    (1, '9780132350884', 'Clean Code', 'Robert C. Martin', 'Huong dan viet ma de doc, de bao tri va giam no ky thuat.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80', 12.50, 8, TRUE, 0),
    (2, '9780321125217', 'Domain-Driven Design', 'Eric Evans', 'Kham pha cach mo hinh hoa domain phuc tap va giu kien truc nhat quan.', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=900&q=80', 18.90, 5, TRUE, 0),
    (3, '9780134494166', 'Clean Architecture', 'Robert C. Martin', 'Tong hop nguyen ly kien truc giup ung dung tien hoa ma van de test.', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=900&q=80', 16.40, 9, TRUE, 0),
    (4, '9781617294549', 'Spring in Action', 'Craig Walls', 'Mot tua sach backend pho bien de demo kho sach nhieu the loai.', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=900&q=80', 15.75, 0, TRUE, 0),
    (5, '9781491950357', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Sach ve du lieu, consistency va he thong phan tan voi goc nhin thuc chien.', 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=900&q=80', 21.30, 4, TRUE, 0),
    (6, '9780134685991', 'Effective Java', 'Joshua Bloch', 'Tap hop cac best practice quan trong cho lap trinh Java hien dai.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=900&q=80', 17.20, 7, TRUE, 0);

SELECT setval(pg_get_serial_sequence('books', 'id'), COALESCE((SELECT MAX(id) FROM books), 1), true);

insert into users(id, username, password) values(0, 'user', '$2y$12$D60xEptjzpebptOYTrKMPetO/.f0IKABOXRAxfiVH/mvCjtvZ1fca');
insert into items(id, code, price, state, user_id) values(0, 1234567890, 34.34, 'ACTIVE', 0);
insert into price_reductions(id, code, amount_deducted, start_date, end_date, item_id) values(0, 1234567890, 10.0, current_timestamp(), current_timestamp(), 0);
insert into suppliers(id, country, name) values(0, 'ES', 'Supplier 1');
insert into item_supplier(supplier_id, item_id) values (0,0);
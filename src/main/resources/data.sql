insert into users(id, username, password) values(nextval('users_id_seq'), 'user', '$2y$12$D60xEptjzpebptOYTrKMPetO/.f0IKABOXRAxfiVH/mvCjtvZ1fca');
insert into items(id, code, price, state, user_id) values(nextval('items_id_seq'), 1234567890, 34.34, 'ACTIVE', currval('users_id_seq'));
insert into price_reductions(id, code, amount_deducted, start_date, end_date, item_id) values(nextval('price_reductions_id_seq'), 1234567890, 10.0, current_timestamp(), current_timestamp(), currval('items_id_seq'));
insert into suppliers(id, country, name) values(nextval('suppliers_id_seq'), 'ES', 'Supplier 1');

insert into item_supplier(supplier_id, item_id) values (currval('suppliers_id_seq'), currval('items_id_seq'));

insert into users(id, username, password, role) values(nextval('users_id_seq'), 'admin', '$2y$12$P/Q2wezFN6XbCUIgixbO4OXmkzSAKLwWRsqFN7vQ/vN3gmrKKCKma', 'ADMIN');
insert into items(id, code, price, state, user_id) values(nextval('items_id_seq'), 1234567895, 34.34, 'ACTIVE', currval('users_id_seq'));

INSERT INTO "ingredients" ("name") VALUES
('vanilla dough'),
('chocolate dough'),
('vegan dough'),
('cream'),
('strawberry jam'),
('chocolate'),
('caramel glaze'),
('sugar glaze'),
('chocolate glaze');

INSERT INTO "donuts" ("description", "name", "price") VALUES
('Creamy donut with a magnificent caramel glaze',	'Caramel Delight',	'2.50'),
('A chocolate explosion with a delicate sugary topping',	'Chocolate Bomb',	'3.00'),
('Celestial vegan donut filled with artisanal strawberry jam',	'Heavenly Spring',	'2.00');

INSERT INTO "donuts_ingredients" ("donut_id", "ingredient_id") VALUES
(1,	1),
(1,	7),
(1,	4),
(2,	6),
(2,	8),
(2,	2),
(3,	3),
(3,	8),
(3,	5);

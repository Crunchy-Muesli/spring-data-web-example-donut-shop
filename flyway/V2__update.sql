INSERT INTO "ingredients" ("ingredient_id", "name") VALUES
(1,	'vanilla dough'),
(2,	'chocolate dough'),
(3,	'vegan dough'),
(4,	'cream'),
(5,	'strawberry jam'),
(6,	'chocolate'),
(7,	'caramel glaze'),
(8,	'sugar glaze'),
(9,	'chocolate glaze');

INSERT INTO "donuts" ("donut_id", "description", "name", "price") VALUES
(1,	'Creamy donut with a magnificent caramel glaze',	'Caramel Delight',	'2.50'),
(2,	'A chocolate explosion with a delicate sugary topping',	'Chocolate Bomb',	'3.00'),
(3,	'Celestial vegan donut filled with artisanal strawberry jam',	'Heavenly Spring',	'2.00');

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

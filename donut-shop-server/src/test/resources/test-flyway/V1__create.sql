CREATE TABLE IF NOT EXISTS "public"."donuts" (
    "donut_id" integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    "description" character varying(255),
    "name" character varying(255),
    "price" character varying(255),
    CONSTRAINT "donuts_pkey" PRIMARY KEY ("donut_id"),
    CONSTRAINT "ukfqrvwh92amkxwpasxuw2ws89u" UNIQUE ("name", "description")
) WITH (oids = false);

CREATE TABLE IF NOT EXISTS "public"."ingredients" (
    "ingredient_id" integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    "name" character varying(255),
    CONSTRAINT "ingredients_pkey" PRIMARY KEY ("ingredient_id"),
    CONSTRAINT "ukj6tsl15xx76y4kv41yxr4uxab" UNIQUE ("name")
) WITH (oids = false);

CREATE TABLE IF NOT EXISTS "public"."donuts_ingredients" (
    "donut_id" integer NOT NULL,
    "ingredient_id" integer NOT NULL,
    CONSTRAINT "donuts_ingredients_pkey" PRIMARY KEY ("donut_id", "ingredient_id"),
    CONSTRAINT "fkfo6hhkxrj454mgi61l7tylj18" FOREIGN KEY (donut_id) REFERENCES donuts(donut_id) NOT DEFERRABLE,
    CONSTRAINT "fkhxd0p1281u45i2dwd5aixmlbm" FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id) NOT DEFERRABLE
) WITH (oids = false);

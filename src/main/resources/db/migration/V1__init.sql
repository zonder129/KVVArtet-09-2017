CREATE TABLE public.user (
  id serial PRIMARY KEY NOT NULL,
  username character varying(45) NOT NULL,
  email character varying(45) NOT NULL,
  password CHARACTER(60) NOT NULL
);
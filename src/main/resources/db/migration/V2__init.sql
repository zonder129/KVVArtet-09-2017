CREATE TABLE IF NOT EXISTS public.scoreboard (
  id serial PRIMARY KEY NOT NULL,
  userid integer NOT NULL,
  gold integer NOT NULL,
  frags integer NOT NULL
);
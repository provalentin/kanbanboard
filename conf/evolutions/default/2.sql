# --- !Ups

INSERT INTO "stories" VALUES (1, 'add db schema first', 'deploy');
INSERT INTO "stories" VALUES (2, 'add some data for db', 'test');
INSERT INTO "stories" VALUES (3, 'create repository for db access', 'dev');
INSERT INTO "stories" VALUES (4, 'create form for add story', 'ready');
INSERT INTO "stories" VALUES (5, 'create view for board of stories', 'dev');
INSERT INTO "stories" VALUES (8, 'add css styles for board', 'dev');
INSERT INTO "stories" VALUES (6, 'create post for story update', 'test');
INSERT INTO "stories" VALUES (7, 'create ui element on click handler for each card', 'deploy');

# --- !Downs

drop table "people" if exists;
drop table "stories" if exists;

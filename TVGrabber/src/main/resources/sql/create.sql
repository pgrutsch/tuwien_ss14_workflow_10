DROP TABLE TVUser;
DROP TABLE Comment;
DROP TABLE TVProgram;

CREATE TABLE TVProgram (
  id INTEGER IDENTITY,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(200) NOT NULL,
  startTime DATETIME NOT NULL,
  endTime DATETIME NOT NULL,
  channel VARCHAR(200) NOT NULL
);

CREATE TABLE Comment (
  id INTEGER IDENTITY,
  email VARCHAR(200) NOT NULL,
  comment VARCHAR(200) NOT NULL,
  tvProgram INTEGER NOT NULL
); 

CREATE INDEX idx_comment__tvprogram ON Comment (tvProgram);

ALTER TABLE Comment ADD CONSTRAINT fk_comment__tvprogram FOREIGN KEY (tvProgram) REFERENCES TVProgram (id);

CREATE TABLE TVUser (
  id INTEGER IDENTITY,
  email VARCHAR(200) NOT NULL,
  subscribed INTEGER NOT NULL,
  searchTerm VARCHAR(200) NOT NULL
)
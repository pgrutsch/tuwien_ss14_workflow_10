insert into TVUser
(email, subscribed, searchTerm) values ('bla', FALSE , '');
insert into TVUser
(email, subscribed, searchTerm) values ('usermailbiz', TRUE , 'subscribed');

insert into TVProgram
(id, title, description, startTime, endTime, channel) values (1, 'The Simpsons', 'blub blu blu', '2014-09-01 10:00:00', '2014-09-01 11:10:00', 'ORF eins');

insert into Comment
(email, comment, tvProgram_id) values ('a@b.com', 'nice series', 1);
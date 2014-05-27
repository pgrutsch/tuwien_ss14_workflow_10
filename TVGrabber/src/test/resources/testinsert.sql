insert into TVUser
(email, subscribed, searchTerm) values ('bla', FALSE , '');
insert into TVUser
(email, subscribed, searchTerm) values ('usermailbiz', TRUE , 'subscribed');

insert into TVProgram
(id, title, description, startTime, endTime, channel) values (1, 'The Simpsons', 'the yellows', '2014-09-01 10:00:00', '2014-09-01 11:10:00', 'ORF eins');
insert into TVProgram
(id, title, description, startTime, endTime, channel) values (2, 'Two and a half man', 'Charly in action', '2014-09-01 22:00:00', '2014-09-01 13:11:00', 'Pro Sieben');


insert into Comment
(email, comment, tvProgram_id) values ('a@b.com', 'nice series', 1);
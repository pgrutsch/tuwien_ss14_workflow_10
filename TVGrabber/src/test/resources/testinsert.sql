insert into TVUser
(email, subscribed, searchTerm) values ('bla', FALSE , '');
insert into TVUser
(email, subscribed, searchTerm) values ('usermailbiz', TRUE , 'subscribed');
insert into TVUser
(email, subscribed, searchTerm) values ('usermailbiz', TRUE , 'subscribed');

insert into TVProgram
(title, description, startTime, endTime, channel)
values ('The Simpsons', 'the yellows', '2014-09-01 10:00:00.010000', '2014-09-01 11:10:00.020000', 'ORF eins');
insert into TVProgram
(title, description, startTime, endTime, channel)
values ('Two and a half man', 'Charly in action', '2014-09-01 22:00:00.010000', '2014-09-01 13:11:00.020000', 'Pro Sieben');
insert into TVProgram
(title, description, startTime, endTime, channel)
values ('test1', 'just a sample', '1990-03-02 08:30:00.010000','1990-03-02 08:30:00.020000', 'blachannel');
insert into TVProgram
(title, description, startTime, endTime, channel)
values ('test2', 'just a sample', '1990-03-02 08:30:00.010000','1990-03-02 08:30:00.020000', 'blachannel');
insert into TVProgram
(title, description, startTime, endTime, channel)
values ('test2blabla', 'just a sample', '1990-03-02 08:30:00.010000','1990-03-02 08:30:00.020000', 'blachannel');

insert into Comment
(email, comment, tvProgram_id) values ('a@b.com', 'nice series', 1);


create database if not exists server_b;

create table if not exists server_b.t_test(
id int primary key auto_increment,
name varchar(50) not null default "",
content varchar(50) not null default "",
create_time datetime not null default now(),
update_time datetime not null default now()
);
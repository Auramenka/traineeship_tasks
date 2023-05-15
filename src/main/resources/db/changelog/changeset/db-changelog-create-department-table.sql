create table if not exists departments
(
    id bigint not null auto_increment,
    name varchar(255),
    primary key (id)
);
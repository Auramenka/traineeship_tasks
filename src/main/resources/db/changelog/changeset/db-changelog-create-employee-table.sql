create table if not exists employees
(
    id bigint auto_increment not null primary key,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    gender int not null,
    date_birth date not null,
    is_fired int not null,
    department_id bigint not null,
    position_id bigint not null,
    boss_id bigint,
    foreign key (department_id) references departments(id),
    foreign key (position_id) references positions(id),
    foreign key (boss_id) references employees(id)
);
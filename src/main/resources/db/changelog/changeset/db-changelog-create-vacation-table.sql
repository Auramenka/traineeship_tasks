create table if not exists vacations
(
    id bigint auto_increment not null primary key,
    date_start date not null,
    date_end date not null,
    employee_id bigint not null,
    status_of_vacation varchar(255),
    foreign key (employee_id) references employees(id)
);
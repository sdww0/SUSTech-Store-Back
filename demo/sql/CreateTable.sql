
-- postgresql

drop table if exists store.address;
drop table if exists store.user_role;
drop table if exists store.users;
drop table if exists store.role;


create table if not exists store.role(
    role_id serial primary key ,
    name varchar not null
);

create table if not exists store.users(

    user_id serial primary key,
    name varchar not null,
    password varchar not null ,
    gender char not null,
    birthday time,
    credit int not null ,
    phone BIGINT,
    email varchar unique not null ,
    id_card varchar ,
    money float not null,
    picture_path varchar not null,
    slat varchar,
    token varchar


);

create table if not exists store.user_role(
    user_id int not null ,
    role_id int not null ,
    primary key (user_id,role_id),
    constraint user_id_fkey foreign key (user_id) references store.users(user_id),
    constraint role_id_fkey foreign key (role_id) references store.role(role_id)
);


create table if not exists store.address(

    address_id serial primary key ,
    belong_user_id int not null ,
    address_name varchar not null ,
    recipient_name varchar not null,
    phone bigint not null ,
    constraint belong_user_id_fkey foreign key (belong_user_id) references store.users(user_id)
);

insert into store.role (name) values ('user');

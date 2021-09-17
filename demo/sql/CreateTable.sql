
-- postgresql

drop table if exists store.address;
drop table if exists store."user";


create table if not exists store.user(

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
    picture_path varchar not null

);

create table if not exists store.address(

    address_id serial primary key ,
    belong_user_id int not null ,
    address_name varchar not null ,
    phone bigint not null ,
    constraint belong_user_id_fkey foreign key (belong_user_id) references store.user(user_id)
);


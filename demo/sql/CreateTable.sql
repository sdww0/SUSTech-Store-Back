
-- postgresql

drop table if exists store.address;
drop table if exists store.user_role;
drop table if exists store.users;
drop table if exists store.role;

create table if not exists store.users(

    user_id serial primary key,
    sign varchar,
    email varchar not null ,
    userName varchar not null ,
    password varchar not null ,
    gender int not null ,
    birthday date ,
    credit int not null ,
    id_card varchar,
    money float not null ,
    picturePath varchar not null


);

create table if not exists store.address
(

    address_id     serial primary key,
    belong_user_id int     not null,
    address_name   varchar not null,
    recipient_name varchar not null,
    phone          bigint  not null,
    constraint belong_user_id_fkey foreign key (belong_user_id) references store.users (user_id)
);



create table if not exists store.goods(
    goods_id serial primary key ,
    price float not null ,
    introduce varchar not null ,
    announcer_id int not null ,
    want_amount int not null ,
    announce_time date not null,
    constraint announcer_id_fkey foreign key (announcer_id) references store.users(user_id)

);

create table if not exists store.comment
(

    content         varchar not null,
    comment_user_id int     not null,
    comment_date    date    not null,
    belong_goods_id int     not null,
    constraint comment_user_id_fkey foreign key (comment_user_id) references store.users (user_id),
    constraint belong_goods_id_fkey foreign key (belong_goods_id) references store.goods (goods_id)
);




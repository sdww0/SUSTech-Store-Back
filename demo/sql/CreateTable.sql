
-- postgresql

drop table if exists store.address cascade;
drop table if exists store.users cascade ;
drop table if exists store.goods cascade ;
drop table if exists store.label cascade ;
drop table if exists store.chat_content cascade ;
drop table if exists store.goods_label cascade ;
drop table if exists store.comment cascade ;
drop table if exists store.deal cascade;
--主要的大类

create table if not exists store.users(

    user_id serial primary key,
    sign varchar,
    email varchar not null ,
    user_name varchar not null ,
    password varchar not null ,
    gender int not null ,
    birthday date ,
    credit int not null ,
    id_card varchar,
    money float not null ,
    phone bigint  ,
    picture_path varchar not null,
    check_code int, --验证码
    is_activate bool not null, --用户状态，未激活，激活等
    activate_code varchar not null unique --激活码


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
    title varchar not null,
    introduce varchar not null ,
    announcer_id int not null ,
    want_amount int not null ,
    announce_time date not null,
    goods_state int not null ,
    picture_amount int not null ,
    constraint announcer_id_fkey foreign key (announcer_id) references store.users(user_id)
);
--
-- create table if not exists store.goods_picture(
--     goods_id int not null ,
--     picture_path varchar not null ,
--     constraint goods_id_fkey foreign key (goods_id) references store.goods(goods_id),
--     primary key (goods_id,picture_path)
-- );


create table if not exists store.deal(
    deal_id serial primary key ,
    stage int not null ,
    goods_id int not null ,
    buyer_id int not null ,
    seller_id int not null ,
    mailing_number varchar,
    shipping_address_id int,
    constraint goods_id_fkey foreign key (goods_id) references store.goods(goods_id),
    constraint buyer_id_fkey foreign key (buyer_id) references store.users(user_id),
    constraint seller_id_fkey foreign key (seller_id) references store.users(user_id),
    constraint shipping_address_id_fkey foreign key (shipping_address_id) references store.address(address_id)
);
create table if not exists store.chat_content(
     chat_content_id serial primary key ,
     belong_to_deal_id int not null ,
     is_seller_speak bool not null ,
     speak_date date not null,
     constraint belong_to_deal_id_fkey foreign key (belong_to_deal_id) references store.deal (deal_id)
);

-- 一些小类
create table if not exists store.label(
    label_id serial primary key ,
    content varchar not null
);

create table if not exists store.comment
(
    comment_id serial primary key ,
    content         varchar not null,
    comment_user_id int     not null,
    comment_date    date    not null,
    belong_goods_id int     not null,
    constraint comment_user_id_fkey foreign key (comment_user_id) references store.users (user_id),
    constraint belong_goods_id_fkey foreign key (belong_goods_id) references store.goods (goods_id)
);

-- 关系表

create table if not exists store.goods_label(
    goods_id int not null ,
    label_id int not null,
    constraint goods_id_fkey foreign key (goods_id) references store.goods (goods_id),
    constraint label_id_fkey foreign key (label_id) references store.label (label_id)
);

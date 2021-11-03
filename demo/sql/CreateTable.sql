
-- postgresql

drop table if exists store.address cascade;
drop table if exists store.users cascade ;
drop table if exists store.goods cascade ;
drop table if exists store.label cascade ;
drop table if exists store.chat_content cascade ;
drop table if exists store.goods_label cascade ;
drop table if exists store.goods_comment cascade ;
drop table if exists store.deal cascade;
drop table if exists store.users_comment cascade ;
drop table if exists store.goods_picture cascade ;
drop table if exists store.complain_goods cascade ;
drop table if exists store.complain_users cascade ;
drop table if exists store.chat cascade ;
drop table if exists store.user_role cascade ;
drop table if exists store.users_collection cascade ;
drop table if exists store.complain_deal cascade ;
--主要的大类

create table if not exists store.users(

    user_id serial primary key,
    sign varchar,
    email varchar not null ,
    user_name varchar not null ,
    password varchar not null ,
    gender int not null ,
    birthday date  ,
    credit int not null ,
    id_card varchar,
    money float not null ,
    phone bigint  ,
    picture_path varchar not null,
    check_code int, --验证码
    is_activate bool not null, --用户状态，未激活，激活等
    is_ban bool not null ,
    activate_code varchar not null unique--激活码


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
    want int not null ,
    announce_time timestamp  not null,
    goods_state int not null ,
    is_sell bool not null ,
    postage float not null ,
    view int not null ,
    constraint announcer_id_fkey foreign key (announcer_id) references store.users(user_id)
);
--
create table if not exists store.goods_picture(
    goods_id int not null ,
    picture_path varchar not null ,
    is_activate bool not null ,
    is_default_picture bool not null ,
    constraint goods_id_fkey foreign key (goods_id) references store.goods(goods_id),
    primary key (goods_id,picture_path)
);


create table if not exists store.deal(
    deal_id serial primary key ,
    stage int not null ,
    goods_id int not null ,
    buyer_id int not null ,
    seller_id int not null ,
    mailing_number varchar,
    shipping_address_id int,
    need_mailing bool,
    constraint goods_id_fkey foreign key (goods_id) references store.goods(goods_id),
    constraint buyer_id_fkey foreign key (buyer_id) references store.users(user_id),
    constraint seller_id_fkey foreign key (seller_id) references store.users(user_id),
    constraint shipping_address_id_fkey foreign key (shipping_address_id) references store.address(address_id)
);

create table if not exists store.chat(
    chat_id serial primary key ,
    goods_id int not null ,
    initiator_id int not null ,
    constraint goods_id_fkey foreign key (goods_id) references store.goods(goods_id),
    constraint initiator_id_fkey foreign key (initiator_id) references store.users(user_id)

);

create table if not exists store.user_role(
    user_id int not null ,
    role int not null ,
    constraint user_id_fkey foreign key (user_id) references store.users(user_id),
    primary key(user_id,role)
);


create table if not exists store.users_comment(
      belong_deal_id int not null ,
      belong_user_id int not null ,
      comment_user_id int not null ,
      comment_date timestamp  not null ,
      content varchar not null ,
      is_good boolean not null ,
      constraint belong_user_id_fkey1 foreign key (belong_user_id) references store.users (user_id),
      constraint belong_user_id_fkey2 foreign key (comment_user_id) references store.users (user_id),
      constraint belong_deal_id_fkey foreign key (belong_deal_id) references store.deal (deal_id),
      primary key (belong_deal_id,belong_user_id,comment_user_id)
);

create table if not exists store.chat_content(
     chat_content_id serial primary key ,
     chat_id int not null ,
     is_initiator_speak bool not null ,
     speak_date timestamp  not null,
     content varchar not null ,
     constraint chat_id_fkey foreign key (chat_id) references store.chat (chat_id)
);

-- 一些小类

create table if not exists store.users_collection(
    user_id int not null ,
    goods_id int not null ,
    constraint user_id_fkey foreign key (user_id) references store.users (user_id),
    constraint goods_id_fkey foreign key (goods_id) references store.goods (goods_id),
    primary key (user_id,goods_id)

);

create table if not exists store.label(
    label_id serial primary key ,
    content varchar not null
);

create table if not exists store.goods_comment
(
    comment_id serial primary key ,
    content         varchar not null,
    comment_user_id int     not null,
    comment_date    timestamp     not null,
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

create table if not exists store.complain_goods(
    record_id serial primary key,
    goods_id int not null,
    complainer_id int not null,
    content varchar not null ,
    picturePath varchar,
    is_process bool not null ,
    constraint goods_id_fkey foreign key (goods_id) references store.goods(goods_id),
    constraint complain_id_fkey foreign key (complainer_id) references store.users(user_id)
);

create table if not exists store.complain_users(
   record_id serial primary key,
   users_id int not null,
   complainer_id int not null,
   content varchar not null ,
   picturePath varchar,
   is_process bool not null ,
   constraint goods_id_fkey foreign key (users_id) references store.users(user_id),
   constraint complainer_id_fkey foreign key (complainer_id) references store.users(user_id)
);

create table if not exists store.appealing_deal(
    record_id serial primary key ,
    deal_id int not null ,
    content varchar not null ,
    picturePath varchar,
    is_process bool not null ,
    constraint deal_id_fkey foreign key (deal_id) references store.deal(deal_id)

);
--务必运行一次下面的
create rule  r_insert_label_ginore as on insert to store.label where exists(select 1 from store.label where content=new.content) do instead nothing ;

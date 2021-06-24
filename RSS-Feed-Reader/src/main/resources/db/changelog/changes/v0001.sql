create table category
(
    id   bigserial not null
        constraint category_pkey
            primary key,
    name varchar(255)
        constraint unique_category_name
            unique
);

create table "user"
(
    id                      bigserial not null
        constraint user_pkey
            primary key,
    account_non_expired     boolean,
    account_non_locked      boolean,
    age                     integer,
    country                 text,
    credentials_non_expired boolean,
    email                   text      not null
        constraint uniqueemailconstraint
            unique,
    enabled                 boolean,
    first_name              text      not null,
    job                     text,
    last_name               text      not null,
    password                text,
    username                text      not null
        constraint uniqueusernameconstraint
            unique
);


create table channel
(
    id           bigserial not null
        constraint channel_pkey
            primary key,
    channel_url  text      not null
        constraint unique_url_channel
            unique,
    description  text,
    title        text      not null,
    website_link text,
    language     text,
    favicon_link text
);

create table channel_user
(
    channel_id  bigint  not null
        constraint fkicqx3hc23lmqofh36eeyi29nn
            references channel,
    user_id     bigint  not null
        constraint fknnyehpprkxthae5himae4e90n
            references "user",
    favorite    boolean not null,
    category_id bigint
        constraint fkd4ng0wqni1j53bfpwm72heciy
            references category,
    constraint channel_user_pkey
        primary key (channel_id, user_id)
);

create table feed_item
(
    id                 bigserial not null
        constraint feed_item_pkey
            primary key,
    author             text,
    content            text,
    description        text,
    link               text      not null,
    publish_date       timestamp ,
    title              text      not null,
    channel_id         bigint
        constraint fks8ye9q5s6dw12cnb10iyoo7f1
            references channel,
    language           text,
    publish_local_date date,
    image_url          text
);

create table feed_item_x_category
(
    category_id  bigint not null
        constraint fkbpmfgk61nbb1q7ep932kocqap
            references category,
    feed_item_id bigint not null
        constraint fknkp5ic49dgtgpuhyyddwebguf
            references feed_item
);

create table feed_item_x_user
(
    clicks     integer default 0,
    liked      boolean not null,
    read       boolean not null,
    read_later boolean not null,
    feed_item  bigint  not null
        constraint fkdccevj71twxrldjhs39l5ryy
            references feed_item,
    "user"     bigint  not null
        constraint fk2yf00x9bru8ldd2mix5un4eog
            references "user",
    constraint feed_item_x_user_pkey
        primary key (feed_item, "user")
);

create table user_x_interest
(
    user_id       bigint not null
        constraint fk2iuq0lc7i47l4586x29b379t9
            references "user",
    user_interest varchar(255)
);

CREATE MATERIALIZED VIEW search_index_feed_item AS
SELECT id,
       setweight(to_tsvector(language::regconfig, title), 'A')||
       setweight(to_tsvector(language::regconfig, description), 'B') AS document
From feed_item
GROUP BY id;

Create INDEX idx_fts_search_feed_item ON search_index_feed_item USING gist(document);

Create FUNCTION refresh_mat_view_feed_item()
    RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY search_index_feed_item;
END $$;

Create MATERIALIZED VIEW search_index_channel AS
SELECT id,
       setweight(to_tsvector(language::regconfig, title), 'A')||
       setweight(to_tsvector(language::regconfig, description), 'B') AS document
From channel
Group BY id;

CREATE INDEX idx_fts_search ON search_index_channel USING gist(document);

Create FUNCTION refresh_mat_view_channel()
    RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY search_index_channel;
END $$;
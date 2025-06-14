create table users (
    id          bigserial primary key,
    external_id varchar(20)                not null unique,
    name        varchar(100),
    email       varchar(256),
    nickname    varchar(50)                not null,
    is_deleted  boolean      default false not null,
    created_at  timestamp(2) default current_timestamp(2),
    updated_at  timestamp(2) default current_timestamp(2),
    deleted_at  timestamp(2)
);

comment on column users.external_id is '회원 번호(USR + yyyyMMdd + 난수 9자리)';

comment on column users.name is '이름';

comment on column users.email is '이메일';

comment on column users.nickname is '닉네임';

alter table users
    owner to commerce;

create table oauth2_providers (
    id         bigserial primary key,
    name       varchar(50)               not null unique,
    is_active  boolean      default true not null,
    created_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column oauth2_providers.name is 'OAuth2 제공 회사명';

comment on column oauth2_providers.is_active is '활성상태 여부';

alter table oauth2_providers
    owner to commerce;

create table user_oauth2_connections (
    id          bigserial primary key,
    user_id     bigint       not null,
    provider_id bigint       not null,
    oauth2_id   varchar(255) not null,
    created_at  timestamp(2) default current_timestamp(2),
    deleted_at  timestamp(2)
);

comment on column user_oauth2_connections.user_id is '회원 아이디';

comment on column user_oauth2_connections.provider_id is 'OAuth2 제공자 아이디';

comment on column user_oauth2_connections.oauth2_id is 'OAuth2 제공자가 발급한 사용자 식별자';

alter table user_oauth2_connections
    owner to commerce;

create table roles (
    id         bigserial primary key,
    code       varchar(20) not null,
    created_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column roles.code is '권한 코드(ROLE_USER/ROLE_ADMIN/ROLE_SUPER_ADMIN)';

alter table roles
    owner to commerce;

create table user_role_connections (
    id         bigserial primary key,
    user_id    bigint not null,
    role_id    bigint not null,
    created_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column user_role_connections.user_id is '회원 아이디';

comment on column user_role_connections.role_id is '권한 아이디';

alter table user_role_connections
    owner to commerce;

create table user_addresses (
    id              bigserial primary key,
    user_id         bigint                     not null,
    alias           varchar(20)                not null,
    recipient_name  varchar(100)               not null,
    recipient_phone varchar(20)                not null,
    zip_code        varchar(10)                not null,
    address1        varchar(255)               not null,
    address2        varchar(255),
    is_default      boolean      default false not null,
    created_at      timestamp(2) default current_timestamp(2),
    updated_at      timestamp(2) default current_timestamp(2),
    deleted_at      timestamp(2)
);

comment on column user_addresses.user_id is '회원 아이디';

comment on column user_addresses.alias is '배송지 별칭';

comment on column user_addresses.recipient_name is '받는사람 이름';

comment on column user_addresses.recipient_phone is '받는사람 연락처';

comment on column user_addresses.zip_code is '송지 우편번호';

comment on column user_addresses.address1 is '배송지 주소 ';

comment on column user_addresses.address2 is '배송지 상세주소';

alter table user_addresses
    owner to commerce;

create table products (
    id           bigserial primary key,
    name         varchar(255)                   not null,
    status       varchar(20)  default 'ON_SALE' not null,
    price        integer                        not null,
    thumbnail    varchar(255)                   not null,
    detail_image varchar(255)                   not null,
    is_deleted   boolean      default false     not null,
    created_at   timestamp(2) default current_timestamp(2),
    updated_at   timestamp(2) default current_timestamp(2),
    deleted_at   timestamp(2)
);

comment on column products.name is '상품명';

comment on column products.status is '판매 상태(ON_SALE/STOPPED/HIDDEN)';

comment on column products.price is '상품 가격';

comment on column products.thumbnail is '상품 썸네일';

comment on column products.detail_image is '상품 상세 이미지';

alter table products
    owner to commerce;

create table inventory (
    id         bigserial primary key,
    product_id bigint not null,
    quantity   bigint not null,
    created_at timestamp(2) default current_timestamp(2),
    updated_at timestamp(2) default current_timestamp(2)
);

comment on column inventory.product_id is '상품 아이디';

comment on column inventory.quantity is '수량';

alter table inventory
    owner to commerce;

create table storage_context_key (
    id         uuid   not null primary key,
    admin_id   bigint not null,
    created_at timestamp(2) default current_timestamp(2)
);

comment on column storage_context_key.admin_id is '관리자 아이디';

alter table storage_context_key
    owner to commerce;

create table file_metadata (
    id                 bigserial primary key,
    storage_context_key_id   uuid         not null,
    stored_path        varchar(255) not null,
    stored_file_name   varchar(255) not null,
    original_file_name varchar(255) not null,
    content_type       varchar(20)  not null,
    file_size          integer      not null,
    status             varchar(20)  not null default 'PENDING',
    created_at         timestamp(2) default current_timestamp(2)
);

comment on column file_metadata.stored_path is '저장 경로';

comment on column file_metadata.stored_file_name is '저장된 파일명';

comment on column file_metadata.original_file_name is '원본 파일명';

comment on column file_metadata.content_type is '파일타입';

comment on column file_metadata.file_size is '파일크기';

comment on column file_metadata.status is '파일업로드 상태(PENDING/SUCCESS)';

alter table file_metadata
    owner to commerce;

create table category_groups (
    id         bigserial primary key,
    title      varchar(50)                not null,
    is_deleted boolean      default false not null,
    created_at timestamp(2) default current_timestamp(2),
    updated_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column category_groups.title is '카테고리 그룹명';

alter table category_groups
    owner to commerce;

create table categories (
    id         bigserial primary key,
    group_id   bigint                     not null,
    name       varchar(50)                not null,
    sort_order integer      default 0,
    is_deleted boolean      default false not null,
    created_at timestamp(2) default current_timestamp(2),
    updated_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column categories.group_id is '카테고리 그룹 아이디';

comment on column categories.name is '카테고리명';

comment on column categories.sort_order is '정렬순서';

alter table categories
    owner to commerce;

create table product_categories (
    id          bigserial primary key,
    product_id  bigint                     not null,
    category_id bigint                     not null,
    is_deleted  boolean      default false not null,
    created_at  timestamp(2) default current_timestamp(2),
    deleted_at  timestamp(2)
);

comment on column product_categories.product_id is '상품 아이디';

comment on column product_categories.category_id is '카테고리 아이디';

alter table product_categories
    owner to commerce;

create table reviews (
    id         bigserial primary key,
    user_id    bigint                     not null,
    product_id bigint                     not null,
    order_item_id   bigint                     not null,
    rating     int                   not null,
    content    varchar(1000),
    is_deleted boolean      default false not null,
    created_at timestamp(2) default current_timestamp(2),
    updated_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column reviews.user_id is '회원 아이디';

comment on column reviews.product_id is '상품 아이디';

comment on column reviews.order_item_id is '주문 아이템 아이디';

comment on column reviews.rating is '별점';

comment on column reviews.content is '리뷰 내용';

alter table reviews
    owner to commerce;

create table review_replies (
    id               bigserial primary key,
    review_id        bigint not null,
    replier_id bigint not null,
    content          text   not null,
    created_at       timestamp(2) default current_timestamp(2),
    updated_at       timestamp(2) default current_timestamp(2),
    deleted_at       timestamp(2)
);

comment on column review_replies.review_id is '리뷰 아이디';

comment on column review_replies.replier_id is '답글 작성한 관리자 아이디';

comment on column review_replies.content is '답글 내용';

alter table review_replies
    owner to commerce;

create table cart_items (
    id         bigserial primary key,
    user_id    bigint  not null,
    product_id bigint  not null,
    quantity   integer not null,
    created_at timestamp(2) default current_timestamp(2),
    updated_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column cart_items.user_id is '사용자 아이디';

comment on column cart_items.product_id is '상품 아이디';

comment on column cart_items.quantity is '장바구니에 담은 수량';

alter table cart_items
    owner to commerce;

create table orders (
    id                     bigserial primary key,
    order_number           varchar(20)                    not null unique,
    user_id                bigint                         not null,
    total_amount           integer                        not null,
    recipient_name         varchar(100)                   not null,
    recipient_phone        varchar(20)                    not null,
    zip_code               varchar(10)                    not null,
    address1               varchar(255)                   not null,
    address2               varchar(255),
    delivery_message       varchar(200),
    status                 varchar(20)  default 'PENDING' not null,
    paid_at                timestamp(2),
    shipped_at             timestamp(2),
    delivered_at           timestamp(2),
    cancelled_at           timestamp(2),
    cancelled_reason       varchar(200),
    refund_requested_at    timestamp(2),
    refunded_at            timestamp(2),
    refund_reason        varchar(200),
    refund_rejected_at   timestamp(2),
    refund_reject_reason varchar(200),
    is_deleted             boolean      default false     not null,
    ordered_at             timestamp(2) default current_timestamp(2),
    created_at             timestamp(2) default current_timestamp(2),
    updated_at             timestamp(2) default current_timestamp(2),
    deleted_at             timestamp(2)
);

comment on column orders.order_number is '주문 번호(ORD + yyyyMMdd + 난수 9자리)';

comment on column orders.user_id is '회원 아이디';

comment on column orders.total_amount is '총 주문 금액';

comment on column orders.recipient_name is '받는사람 이름';

comment on column orders.recipient_phone is '받는사람 연락처';

comment on column orders.zip_code is '배송지 우편번호';

comment on column orders.address1 is '배송지 주소';

comment on column orders.address2 is '배송지 상세주소';

comment on column orders.delivery_message is '배송 메시지';

comment on column orders.status is '주문 상태(PENDING/PAID/PREPARING_SHIPMENT/SHIPPED/DELIVERED/CANCELED/RETURN_REQUESTED/RETURNED/RETURNED_REJECT)';

comment on column orders.paid_at is '결제 시각';

comment on column orders.shipped_at is '배송출발 시각';

comment on column orders.delivered_at is '배송완료 시각';

comment on column orders.cancelled_at is '주문취소 시각';

comment on column orders.cancelled_reason is '주문취소 사유';

comment on column orders.return_requested_at is '환불요청 시각';

comment on column orders.returned_at is '환불완료 시각';

comment on column orders.returned_reason is '환불 사유';

comment on column orders.returned_reject_reason is '환불거절 사유';

comment on column orders.returned_rejected_at is '환불거절 시각';

alter table orders
    owner to commerce;

create table order_items (
    id                  bigserial primary key,
    order_id            bigint  not null,
    product_snapshot_id bigint  not null,
    quantity            integer not null,
    unit_price            integer not null,
    created_at          timestamp(2) default current_timestamp(2),
    updated_at          timestamp(2) default current_timestamp(2),
    deleted_at          timestamp(2)
);

comment on column order_items.order_id is '주문 아이디';

comment on column order_items.product_snapshot_id is '주문 당시 상품 스냅샷 아이디';

comment on column order_items.quantity is '주문 수량';

comment on column order_items.unit_price is '주문 당시 가격';

alter table order_items
    owner to commerce;

create table product_snapshots (
    id                  bigserial primary key,
    product_id bigint       not null,
    name                varchar(255) not null,
    price               integer      not null,
    thumbnail           varchar(255) not null,
    created_at          timestamp(2) default current_timestamp(2)
);

comment on column product_snapshots.product_id is '상품 아이디';

comment on column product_snapshots.name is '상품명';

comment on column product_snapshots.price is '상품 가격';

comment on column product_snapshots.thumbnail is '상품 썸네일';

alter table product_snapshots
    owner to commerce;

create table payments (
    id             bigserial primary key,
    payment_number varchar(20)                    not null unique,
    order_id       bigint                         not null,
    user_id        bigint                         not null,
    amount         integer                        not null,
    status         varchar(20)  default 'PENDING' not null,
    payment_method varchar(20)                    not null,
    transaction_id varchar(255),
    failed_reason  varchar(255),
    created_at     timestamp(2) default current_timestamp(2),
    updated_at     timestamp(2) default current_timestamp(2),
    deleted_at     timestamp(2)
);

comment on column payments.payment_number is '결제 번호(PAY + yyyyMMdd + 난수 9자리)';

comment on column payments.order_id is '주문 아이디';

comment on column payments.user_id is '회원 아이디';

comment on column payments.amount is '결제 금액';

comment on column payments.status is '결제 상태(PENDING/COMPLETED/CANCELLED/REFUNDED)';

comment on column payments.payment_method is '결제 방식(MOCK/TOSS_PAY)';

comment on column payments.transaction_id is 'PG사에서 발급한 결제 거래 식별자';

comment on column payments.failed_reason is '결제실패 사유';

alter table payments
    owner to commerce;

create table chat_rooms (
    id         bigserial primary key,
    guest_id   varchar(100),
    user_id    bigint,
    admin_id   bigint,
    product_id bigint,
    status     varchar(20)  default 'REQUESTED' not null,
    created_at timestamp(2) default current_timestamp(2),
    updated_at timestamp(2) default current_timestamp(2),
    deleted_at timestamp(2)
);

comment on column chat_rooms.guest_id is '비회원 식별자';

comment on column chat_rooms.user_id is '회원 아이디';

comment on column chat_rooms.admin_id is '관리자 아이디';

comment on column chat_rooms.product_id is '상품 아이디';

comment on column chat_rooms.status is '채팅방 상태(REQUESTED/ON_CHAT/AWAITING/END)';

alter table chat_rooms
    owner to commerce;

create table chat_messages (
    id           bigserial primary key,
    chat_room_id bigint        not null,
    sender_type  varchar(20)   not null,
    content      varchar(2000) not null,
    created_at   timestamp(2) default current_timestamp(2),
    deleted_at   timestamp(2)
);

comment on column chat_messages.chat_room_id is '채팅룸 아이디';

comment on column chat_messages.sender_type is '발신자 타입(GUEST/USER/ADMIN)';

comment on column chat_messages.content is '메시지 내용';

alter table chat_messages
    owner to commerce;


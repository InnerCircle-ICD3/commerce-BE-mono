```text
Table "users" {
  "id" bigserial [pk, increment]
  "external_id" varchar(20) [unique, not null, note: '회원 번호(USR + yyyyMMdd + 난수 9자리)']
  "name" varchar(100) [note: '이름']
  "email" varchar(256) [note: '이메일']
  "nickname" varchar(50) [not null, note: '닉네임']
  "is_deleted" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "oauth2_providers" {
  "id" bigserial [pk, increment]
  "name" varchar(50) [unique, not null, note: 'OAuth2 제공 회사명']
  "is_active" boolean [not null, default: true, note: '활성상태 여부']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "user_oauth2_connections" {
  "id" bigserial [pk, increment]
  "user_id" bigint [not null, note: '회원 아이디']
  "provider_id" bigint [not null, note: 'OAuth2 제공자 아이디']
  "oauth2_id" varchar(255) [not null, note: 'OAuth2 제공자가 발급한 사용자 식별자']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "roles" {
  "id" bigserial [pk, increment]
  "code" varchar(20) [not null, note: '권한 코드(ROLE_USER/ROLE_ADMIN/ROLE_SUPER_ADMIN)']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "user_role_connections" {
  "id" bigserial [pk, increment]
  "user_id" bigint [not null, note: '회원 아이디']
  "role_id" bigint [not null, note: '권한 아이디']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "user_addresses" {
  "id" bigserial [pk, increment]
  "user_id" bigint [not null, note: '회원 아이디']
  "alias" varchar(20) [not null, note: '배송지 별칭']
  "recipient_name" varchar(100) [not null, note: '받는사람 이름']
  "recipient_phone" varchar(20) [not null, note: '받는사람 연락처']
  "zip_code" varchar(10) [not null, note: '송지 우편번호']
  "address1" varchar(255) [not null, note: '배송지 주소 ']
  "address2" varchar(255) [note: '배송지 상세주소']
  "is_default" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "products" {
  "id" bigserial [pk, increment]
  "name" varchar(255) [not null, note: '상품명']
  "status" varchar(20) [not null, default: 'ON_SALE', note: '판매 상태(ON_SALE/STOPPED/HIDDEN)']
  "price" integer [not null, note: '상품 가격']
  "thumbnail" varchar(255) [not null, note: '상품 썸네일']
  "detail_image" varchar(255) [not null, note: '상품 상세 이미지']
  "is_deleted" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "inventory" {
  "id" bigserial [pk, increment]
  "product_id" bigint [not null, note: '상품 아이디']
  "quantity" bigint [not null, note: '수량']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
}

Table "storage_context_key" {
  "id" uuid [pk, not null]
  "admin_id" bigint [not null, note: '관리자 아이디']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
}

Table "file_metadata" {
  "id" bigserial [pk, increment]
  "storage_context_key_id" uuid [not null]
  "stored_path" varchar(255) [not null, note: '저장 경로']
  "stored_file_name" varchar(255) [not null, note: '저장된 파일명']
  "original_file_name" varchar(255) [not null, note: '원본 파일명']
  "content_type" varchar(20) [not null, note: '파일타입']
  "file_size" integer [not null, note: '파일크기']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
}

Table "category_groups" {
  "id" bigserial [pk, increment]
  "title" varchar(50) [not null, note: '카테고리 그룹명']
  "is_deleted" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "categories" {
  "id" bigserial [pk, increment]
  "group_id" bigint [not null, note: '카테고리 그룹 아이디']
  "name" varchar(50) [not null, note: '카테고리명']
  "sort_order" integer [default: 0, note: '정렬순서']
  "is_deleted" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "product_categories" {
  "id" bigserial [pk, increment]
  "product_id" bigint [not null, note: '상품 아이디']
  "category_id" bigint [not null, note: '카테고리 아이디']
  "is_deleted" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "reviews" {
  "id" bigserial [pk, increment]
  "user_id" bigint [not null, note: '회원 아이디']
  "product_id" bigint [not null, note: '상품 아이디']
  "order_item_id" bigint [not null, note: '주문 아이템 아이디']
  "rating" int [not null, note: '별점']
  "content" varchar(1000) [note: '리뷰 내용']
  "is_deleted" boolean [not null, default: false]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "review_replies" {
  "id" bigserial [pk, increment]
  "review_id" bigint [not null, note: '리뷰 아이디']
  "replier_id" bigint [not null, note: '답글 작성한 관리자 아이디']
  "content" text [not null, note: '답글 내용']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "cart_items" {
  "id" bigserial [pk, increment]
  "user_id" bigint [not null, note: '사용자 아이디']
  "product_id" bigint [not null, note: '상품 아이디']
  "quantity" integer [not null, note: '장바구니에 담은 수량']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "orders" {
  "id" bigserial [pk, increment]
  "order_number" varchar(20) [unique, not null, note: '주문 번호(ORD + yyyyMMdd + 난수 9자리)']
  "user_id" bigint [not null, note: '회원 아이디']
  "total_amount" integer [not null, note: '총 주문 금액']
  "recipient_name" varchar(100) [not null, note: '받는사람 이름']
  "recipient_phone" varchar(20) [not null, note: '받는사람 연락처']
  "zip_code" varchar(10) [not null, note: '배송지 우편번호']
  "address1" varchar(255) [not null, note: '배송지 주소']
  "address2" varchar(255) [note: '배송지 상세주소']
  "delivery_message" varchar(200) [note: '배송 메시지']
  "status" varchar(20) [not null, default: 'PENDING', note: '주문 상태(PENDING/PAID/PREPARING_SHIPMENT/SHIPPED/DELIVERED/CANCELED/RETURN_REQUESTED/RETURNED/RETURNED_REJECT)']
  "paid_at" timestamp(2) [note: '결제 시각']
  "shipped_at" timestamp(2) [note: '배송출발 시각']
  "delivered_at" timestamp(2) [note: '배송완료 시각']
  "cancelled_at" timestamp(2) [note: '주문취소 시각']
  "cancelled_reason" varchar(200) [note: '주문취소 사유']
  "return_requested_at" timestamp(2) [note: '환불요청 시각']
  "returned_at" timestamp(2) [note: '환불완료 시각']
  "returned_reason" varchar(200) [note: '환불 사유']
  "returned_rejected_at" timestamp(2) [note: '환불거절 시각']
  "returned_reject_reason" varchar(200) [note: '환불거절 사유']
  "is_deleted" boolean [not null, default: false]
  "ordered_at" timestamp(2) [default: `current_timestamp(2)`]
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "order_items" {
  "id" bigserial [pk, increment]
  "order_id" bigint [not null, note: '주문 아이디']
  "product_snapshot_id" bigint [not null, note: '주문 당시 상품 스냅샷 아이디']
  "quantity" integer [not null, note: '주문 수량']
  "unit_price" integer [not null, note: '주문 당시 가격']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "product_snapshots" {
  "id" bigserial [pk, increment]
  "product_id" bigint [not null, note: '상품 아이디']
  "name" varchar(255) [not null, note: '상품명']
  "price" integer [not null, note: '상품 가격']
  "thumbnail" varchar(255) [not null, note: '상품 썸네일']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
}

Table "payments" {
  "id" bigserial [pk, increment]
  "payment_number" varchar(20) [unique, not null, note: '결제 번호(PAY + yyyyMMdd + 난수 9자리)']
  "order_id" bigint [not null, note: '주문 아이디']
  "user_id" bigint [not null, note: '회원 아이디']
  "amount" integer [not null, note: '결제 금액']
  "status" varchar(20) [not null, default: 'PENDING', note: '결제 상태(PENDING/COMPLETED/CANCELLED/REFUNDED)']
  "payment_method" varchar(20) [not null, note: '결제 방식(MOCK/TOSS_PAY)']
  "transaction_id" varchar(255) [note: 'PG사에서 발급한 결제 거래 식별자']
  "failed_reason" varchar(255) [note: '결제실패 사유']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "chat_rooms" {
  "id" bigserial [pk, increment]
  "guest_id" varchar(100) [note: '비회원 식별자']
  "user_id" bigint [note: '회원 아이디']
  "admin_id" bigint [note: '관리자 아이디']
  "product_id" bigint [note: '상품 아이디']
  "status" varchar(20) [not null, default: 'REQUESTED', note: '채팅방 상태(REQUESTED/ON_CHAT/AWAITING/END)']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "updated_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Table "chat_messages" {
  "id" bigserial [pk, increment]
  "chat_room_id" bigint [not null, note: '채팅룸 아이디']
  "sender_type" varchar(20) [not null, note: '발신자 타입(GUEST/USER/ADMIN)']
  "content" varchar(2000) [not null, note: '메시지 내용']
  "created_at" timestamp(2) [default: `current_timestamp(2)`]
  "deleted_at" timestamp(2)
}

Ref "fk_user_oauth2_connections_user":"users"."id" < "user_oauth2_connections"."user_id"

Ref "fk_user_oauth2_connections_provider":"oauth2_providers"."id" < "user_oauth2_connections"."provider_id"

Ref "fk_user_role_connections_user":"users"."id" < "user_role_connections"."user_id"

Ref "fk_user_role_connections_role":"roles"."id" < "user_role_connections"."role_id"

Ref "fk_user_addresses_user":"users"."id" < "user_addresses"."user_id"

Ref "fk_reviews_user":"users"."id" < "reviews"."user_id"

Ref "fk_reviews_product":"products"."id" < "reviews"."product_id"

Ref "fk_review_replies_review":"reviews"."id" < "review_replies"."review_id"

Ref "fk_review_replies_replier":"users"."id" < "review_replies"."replier_id"

Ref "fk_cart_items_user":"users"."id" < "cart_items"."user_id"

Ref "fk_cart_items_product":"products"."id" < "cart_items"."product_id"

Ref "fk_orders_user":"users"."id" < "orders"."user_id"

Ref "fk_order_items_order":"orders"."id" < "order_items"."order_id"

Ref "fk_order_items_product_snapshot":"product_snapshots"."id" < "order_items"."product_snapshot_id"

Ref "fk_product_snapshots_product":"products"."id" < "product_snapshots"."product_id"

Ref "fk_payments_user":"users"."id" < "payments"."user_id"

Ref "fk_payments_order":"orders"."id" < "payments"."order_id"

Ref "fk_inventory_product":"products"."id" < "inventory"."product_id"

Ref "fk_product_categories_product":"products"."id" < "product_categories"."product_id"

Ref "fk_product_categories_category":"categories"."id" < "product_categories"."category_id"

Ref "fk_categories_group":"category_groups"."id" < "categories"."group_id"

Ref "fk_storage_context_key_admin":"users"."id" < "storage_context_key"."admin_id"

Ref "fk_file_metadata_context_key":"storage_context_key"."id" < "file_metadata"."storage_context_key_id"

Ref "fk_chat_rooms_user":"users"."id" < "chat_rooms"."user_id"

Ref "fk_chat_rooms_admin":"users"."id" < "chat_rooms"."admin_id"

Ref "fk_chat_rooms_product":"products"."id" < "chat_rooms"."product_id"

Ref "fk_chat_messages_chat_room":"chat_rooms"."id" < "chat_messages"."chat_room_id"
```

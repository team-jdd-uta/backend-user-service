# backend-user-service

사용자 조회/마이페이지 API와 회원가입 이벤트 projection을 담당하는 Spring Boot 서비스입니다. `backend-login-service`가 발행한 `UserRegistered` outbox 이벤트를 Kafka에서 소비해 target MariaDB의 `customer` 테이블에 반영합니다. 채팅 댓글 조회 API는 MongoDB의 `comment` collection을 읽습니다.

## 역할

- 사용자 정보 조회와 프로필 수정 API를 제공합니다.
- 팔로우/팔로워 목록과 시청 기록 조회 API를 제공합니다.
- MongoDB에 저장된 채팅 댓글을 조회합니다.
- Kafka topic `outbox.event.user`에서 `UserRegistered` 이벤트를 소비합니다.
- 이벤트 중복 처리를 위해 `consumed_user_event` 테이블을 사용합니다.

## 기술 스택

- Java 21
- Spring Boot
- Spring Web
- Spring JDBC / MyBatis
- Spring Kafka
- Spring Data MongoDB
- MariaDB
- MongoDB

## HTTP API

### 사용자 / 마이페이지

| Method | Path | 설명 |
| --- | --- | --- |
| `POST` | `/users/{userId}/follow` | 사용자 follow |
| `PUT` | `/users/{userId}/profile` | 사용자 이름/email 수정 |
| `GET` | `/users/info/{userId}` | 사용자 기본 정보 조회 |
| `GET` | `/users/{userId}/Ifollowing/{page}/{size}` | 내가 follow 중인 사용자 목록 |
| `GET` | `/users/{userId}/followingI/{page}/{size}` | 나를 follow 중인 사용자 목록 |
| `GET` | `/users/{userId}/watch_history/{offset}/{limit}` | 시청 기록 조회 |

프로필 수정 예:

```bash
curl -X PUT http://localhost:8084/users/{userId}/profile \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com"}'
```

### 댓글 조회

| Method | Path | 설명 |
| --- | --- | --- |
| `GET` | `/api/comments/user/{userId}` | 사용자별 댓글 조회 |
| `GET` | `/api/comments/user/{userId}/room/{roomId}` | 사용자 + 방 기준 댓글 조회 |
| `GET` | `/api/comments/room/{roomId}` | 방 기준 댓글 조회 |
| `GET` | `/api/comments/room/{roomId}/date/{startDate}/{endDate}` | 방 + 기간 기준 댓글 조회 |

## Kafka Consumer

Consumer class:

```text
userinfoserver/src/main/java/com/teamuta/userinfoserver/messaging/UserRegisteredEventConsumer.java
```

처리 흐름:

1. `outbox.event.user` topic 메시지 수신
2. Kafka header `event_type` 또는 `eventType`이 `UserRegistered`인지 확인
3. Kafka header `aggregate_type` 또는 `aggregateType`이 있으면 `user`인지 확인
4. JSON value를 `UserRegisteredEvent`로 파싱
5. `eventVersion=1`, 필수 필드 검증
6. `consumed_user_event`에 `eventId` insert
7. 중복이 아니면 `customer` upsert

Event payload:

```json
{
  "eventId": "<uuid>",
  "userId": "<uuid>",
  "email": "alice",
  "name": "alice",
  "occurredAt": 1714000000000,
  "eventVersion": 1
}
```

Target mapping:

| Event field | Target column |
| --- | --- |
| `userId` | `customer.user_id` |
| `name` | `customer.name` |
| `email` | `customer.email` |
| `occurredAt` | `customer.created_at` |

`customer.password`는 login-service 소유 정보라 projection에서 `NULL`로 둡니다.

## DB

MariaDB 초기 schema:

```text
userinfoserver/docker/mariadb/init-user.sql
```

주요 테이블:

- `customer`
- `consumed_user_event`
- `category`
- `video`
- `watch_history`
- `follows`

MongoDB:

- database: `commentdb`
- collection: `comment`

## 환경변수

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `SERVER_PORT` | `8084` | HTTP 서버 포트 |
| `MONGO_URI` | `mongodb://localhost:27017/commentdb` | MongoDB URI |
| `DB_URL` | `jdbc:mariadb://localhost:3307/app_target?...` | target MariaDB JDBC URL |
| `DB_USER` | `app_target` | target DB username |
| `DB_PASSWORD` | `app_target1234` | target DB password |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:29092` | Kafka bootstrap servers |
| `KAFKA_CONSUMER_GROUP` | `user-service` | Kafka consumer group |
| `KAFKA_AUTO_OFFSET_RESET` | `earliest` | consumer offset reset policy |
| `KAFKA_CONSUMER_ENABLED` | `true` | Kafka consumer 활성화 여부 |
| `USER_REGISTERED_TOPIC` | `outbox.event.user` | 회원가입 이벤트 topic |

## 로컬 실행

MariaDB, MongoDB, Kafka가 먼저 떠 있어야 합니다.

```bash
cd userinfoserver
./gradlew bootRun
```

Docker 이미지 빌드:

```bash
cd userinfoserver
docker build -t team9-user-service:local .
```

## Kubernetes 기준

- Service port는 `8084`입니다.
- Ingress에서는 `/api/users`를 `/users`로, `/api/comments`는 그대로 이 서비스로 라우팅합니다.
- Kafka와 target MariaDB가 준비된 뒤 배포해야 consumer가 정상 기동합니다.

## 주의점

- Kafka/Debezium은 최소 1회 전달이므로 `consumed_user_event` 기반 idempotency가 필요합니다.
- `UserRegistered` 외 이벤트는 skip합니다.
- MongoDB가 없으면 댓글 조회 API는 실패할 수 있습니다.
- 로그인 인증은 login-service에서 추후 구현 예정입니다.

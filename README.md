# 이커머스 - 801베이스

## Push 전에 ktlint 자동 실행

1. `.githooks/pre-push`에 pre-push 훅이 정의되어 있습니다.
2. 아래 명령어로 로컬 훅을 설치해주세요.

```bash
./scripts/install-hooks.sh
```

## Swagger

도커 실행 후 http://localhost:8082로 접속해주세요.
```shell
# 실행
docker-compose -f docker/docker-compose.yml up -d swagger-ui swagger-api
```
```shell
# 종료
docker-compose -f docker/docker-compose.yml stop swagger-ui swagger-api
```
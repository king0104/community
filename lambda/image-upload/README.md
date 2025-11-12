# Image Upload Lambda Function

Lambda + API Gateway를 통한 이미지 업로드 함수

## 환경 변수

Lambda 함수에 다음 환경 변수를 설정해야 합니다:

- `S3_BUCKET`: S3 버킷 이름
- `AWS_REGION`: AWS 리전 (기본값: ap-northeast-2)
- `SPRING_BOOT_API_URL`: Spring Boot 메타데이터 저장 API URL
  - 예: `http://your-domain.com/api/v1/images/metadata`

## 동작 흐름

1. 클라이언트 → API Gateway → Lambda: 이미지 파일 업로드
2. Lambda: 파일 유효성 검증
3. Lambda → S3: 파일 저장
4. Lambda → Spring Boot: 메타데이터 저장 API 호출
5. Lambda → 클라이언트: 업로드 결과 반환

## 파일 제한

- 최대 파일 크기: 5MB
- 허용 확장자: jpg, jpeg, png, gif, webp
- MIME 타입: image/jpeg, image/png, image/gif, image/webp

## 배포 방법

다음 단계에서 배포 스크립트 제공 예정
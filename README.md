# 인사관리 프로그램 (Spring Boot)

Spring Boot 3.3 / Java 17 / Spring Data JPA / H2 / Thymeleaf / Lombok

## 실행
IntelliJ 등에서 프로젝트를 Gradle 프로젝트로 열고 HrApplication을 실행하거나, 터미널에서 `gradle bootRun`

- 직원: http://localhost:8080/employees
- 부서: http://localhost:8080/departments
- H2 콘솔: http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:hrdb, 사용자: sa, 비밀번호 없음)

## 기능
- 직원: 목록 / 이름·부서명 검색 / 등록 / 수정 / 삭제 / 프로필 사진 업로드
- 목록: 페이징(5명씩), 번호·이름·급여·입사일 컬럼 클릭 정렬 (검색어·정렬 상태 유지)
- 부서: 별도 엔티티(Department)로 분리, 직원과 다대일(@ManyToOne) 연관관계, 부서 등록/삭제
  - 소속 직원이 있는 부서는 삭제 불가 (BusinessException)
- 예외 처리: NotFoundException(404), BusinessException(400), 잘못된 id 형식(400), DB 제약 위반(409) -> error.html

## 사진 저장 위치
프로젝트 실행 폴더 아래 `uploads/` (application.properties의 `app.upload-dir`로 변경 가능)

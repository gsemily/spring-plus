# SPRING PLUS

## 💻프로젝트 소개
코드 개선 및 테스트 코드 작성, 성능 최적화

### ⏰개발 기간
25.06.23 ~ 25.07.04

### ⚙️개발 환경
* Java 17
* JDK 1.8.0
* IDE : IntelliJ IDEA
* Spring Boot 3.5.0


## 📌주요 변경 사항
### 코드 개선
1. @Transactional
* @Transactional(readOnly = true) 인 상태에서 save 호출 → Connection is read-only 예외 발생 
* 쓰기 메서드에는 생략
2. JWT
* User 엔티티에 nickname 추가
* JwtUtil에서 nickname 포함하여 토큰 생성 및 파싱
3. JPA
* TodoController, TodoService에 조건 파라미터 추가
* Repository에 JPQL 메서드 작성 & if문으로 분리 처리
```
@Query("SELECT t FROM Todo t WHERE t.weather = :weather AND t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findByWeatherAndModifiedAtBetween(String weather, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.weather = :weather")
    Page<Todo> findByWeather(String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findByModifiedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
```
4. AOP
* @After → @Before로 수정
* execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))로 수정
---
### 테스트코드 수정
todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다()
* 예외 발생 시에도 상태 코드 200으로 응답하려 함 → 400 반환됨 
* 테스트를 400 BAD_REQUEST 기준으로 수정
```
// then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Todo not found"));
```
---
### JPA Cascade
* @OneToMany(mappedBy = "todo")에 cascade = CascadeType.PERSIST 추가
---
### N+1
* 댓글 조회 시 댓글마다 user 조회 쿼리 발생 (N+1)
* JOIN FETCH 사용하여 한 번에 user 정보 가져오기
---
### QueryDSL
1. QueryDSL 적용
* JPQL로 작성된 findByIdWithUser를 QueryDSL로 변경
* fetchJoin() 사용하여 user 함께 조회

2. 검색 기능 만들기
* QueryDSL + Projections 사용 
* 필요한 필드(title, manager count, comment count)만 반환하는 TodoSearchResponse 작성
* 동적 조건 검색, 페이징 처리를 적용한 searchTodos 메서드 작성 → Service, Controller에 적용
---
### SpringSecurity
* 기존 : JWT를 직접 파싱해서 request에 setAttribute, ArgumentResolver로 주입 
* JwtFilter → SecurityContextHolder 사용으로 변경 
* SecurityFilterChain을 적용한 SecurityConfig 작성
* @AuthenticationPrincipal로 인증 정보 주입 
* AuthUser → User



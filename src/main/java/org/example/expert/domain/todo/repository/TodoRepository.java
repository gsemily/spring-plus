package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t WHERE t.weather = :weather AND t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findByWeatherAndModifiedAtBetween(String weather, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.weather = :weather")
    Page<Todo> findByWeather(String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findByModifiedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}

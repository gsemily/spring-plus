package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(String titleKeyword, String nicknameKeyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> content = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(
                        titleKeyword != null ? todo.title.contains(titleKeyword) : null,
                        nicknameKeyword != null ? manager.user.nickname.contains(nicknameKeyword) : null,
                        startDate != null && endDate != null ? todo.createdAt.between(startDate, endDate) : null
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(
                        titleKeyword != null ? todo.title.contains(titleKeyword) : null,
                        nicknameKeyword != null ? manager.user.nickname.contains(nicknameKeyword) : null,
                        startDate != null && endDate != null ? todo.createdAt.between(startDate, endDate) : null
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}

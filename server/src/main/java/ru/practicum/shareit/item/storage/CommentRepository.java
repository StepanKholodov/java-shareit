package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;

import java.util.List;

/**
 * JPA-репозиторий отзывов на вещи. Жадно подгружает {@code author} через
 * {@link EntityGraph}, чтобы {@code CommentMapper.toCommentDto} не делал
 * по лишнему запросу на каждый отзыв при обращении к {@code author.getName()}.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "author")
    List<Comment> findAllByItemId(Long itemId);

    @EntityGraph(attributePaths = "author")
    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}

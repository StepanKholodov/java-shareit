package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Преобразование между моделью {@link Comment} и {@link CommentDto}.
 */
public final class CommentMapper {

    private CommentMapper() {
    }

    /**
     * @param comment сущность отзыва
     * @return DTO для отдачи через REST API
     */
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    /**
     * @param commentDto DTO, полученный из запроса
     * @param item       вещь, к которой относится отзыв
     * @param author     автор отзыва
     * @return новая сущность отзыва с текущей отметкой времени
     */
    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}

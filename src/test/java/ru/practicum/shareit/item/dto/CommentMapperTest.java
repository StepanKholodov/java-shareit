package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toCommentDto_mapsAllFields() {
        User author = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(10L, "Дрель", "desc", true, author);
        Comment comment = new Comment();
        comment.setId(5L);
        comment.setText("Отличная вещь");
        comment.setItem(item);
        comment.setAuthor(author);
        LocalDateTime created = LocalDateTime.now();
        comment.setCreated(created);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь");
        assertThat(dto.getAuthorName()).isEqualTo("Ivan");
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void toComment_setsTextItemAuthorAndCreated() {
        User author = new User(1L, "Ivan", "ivan@mail.ru");
        Item item = new Item(10L, "Дрель", "desc", true, author);
        CommentDto dto = new CommentDto(null, "Отличная вещь", null, null);

        Comment comment = CommentMapper.toComment(dto, item, author);

        assertThat(comment.getText()).isEqualTo("Отличная вещь");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<CommentMapper> constructor = CommentMapper.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}

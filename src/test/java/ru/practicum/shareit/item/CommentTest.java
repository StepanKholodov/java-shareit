package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void equals_sameReference_returnsTrue() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertThat(comment.equals(comment)).isTrue();
    }

    @Test
    void equals_null_returnsFalse() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertThat(comment.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertThat(comment.equals("not a comment")).isFalse();
    }

    @Test
    void equals_sameId_returnsTrue() {
        Comment first = new Comment();
        first.setId(1L);
        Comment second = new Comment();
        second.setId(1L);
        second.setText("Другой текст");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_returnsFalse() {
        Comment first = new Comment();
        first.setId(1L);
        Comment second = new Comment();
        second.setId(2L);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_withNullId_returnsFalse() {
        Comment first = new Comment();
        Comment second = new Comment();

        assertThat(first).isNotEqualTo(second);
    }
}

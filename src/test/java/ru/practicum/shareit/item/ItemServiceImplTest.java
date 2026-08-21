package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Ivan", "ivan@mail.ru");
        item = new Item(1L, "Дрель", "Простая дрель", true, owner);
    }

    @Test
    void create_whenOwnerExists_savesItem() {
        ItemDto inputDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemStorage.create(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(1L, inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void create_whenOwnerMissing_propagatesNotFoundAndDoesNotSave() {
        ItemDto inputDto = new ItemDto(null, "Дрель", "Простая дрель", true);
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemService.create(99L, inputDto)).isInstanceOf(NotFoundException.class);

        verify(itemStorage, never()).create(any());
    }

    @Test
    void update_whenOwnerMatchesAndAllFieldsProvided_updatesAllFields() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(itemStorage.update(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto patch = new ItemDto(null, "Новая дрель", "Новое описание", false);

        ItemDto result = itemService.update(1L, 1L, patch);

        assertThat(result.getName()).isEqualTo("Новая дрель");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getAvailable()).isFalse();
        verify(userService).getUserById(1L);
    }

    @Test
    void update_withBlankAndNullFields_keepsOriginalValues() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(itemStorage.update(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto patch = new ItemDto(null, "  ", null, null);

        ItemDto result = itemService.update(1L, 1L, patch);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Простая дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void update_withNullNameAndBlankDescription_keepsOriginalValues() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(itemStorage.update(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto patch = new ItemDto(null, null, "   ", null);

        ItemDto result = itemService.update(1L, 1L, patch);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Простая дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void update_whenItemNotFound_throwsNotFoundException() {
        when(itemStorage.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(1L, 404L, new ItemDto(null, "X", "Y", true)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenRequesterIsNotOwner_throwsForbiddenAndDoesNotSave() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(2L, 1L, new ItemDto(null, "Hack", null, null)))
                .isInstanceOf(ForbiddenException.class);

        verify(itemStorage, never()).update(any());
    }

    @Test
    void findById_whenFound_returnsMappedDto() {
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));

        ItemDto result = itemService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
    }

    @Test
    void findById_whenMissing_throwsNotFoundException() {
        when(itemStorage.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById(404L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByOwner_whenOwnerExists_returnsMappedList() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemStorage.findAllByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemDto> result = List.copyOf(itemService.findAllByOwner(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void findAllByOwner_whenOwnerMissing_propagatesNotFound() {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemService.findAllByOwner(99L)).isInstanceOf(NotFoundException.class);

        verify(itemStorage, never()).findAllByOwnerId(any());
    }

    @Test
    void search_returnsMappedResults() {
        when(itemStorage.search("дрель")).thenReturn(List.of(item));

        List<ItemDto> result = List.copyOf(itemService.search("дрель"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void search_whenNoMatches_returnsEmptyList() {
        when(itemStorage.search("отвертка")).thenReturn(List.of());

        assertThat(itemService.search("отвертка")).isEmpty();
    }
}

package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(ErrorHandler.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto sampleBookingDto() {
        return new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, new BookingItemDto(10L, "Дрель"), new BookingBookerDto(2L));
    }

    @Test
    void create_withValidBody_returns201() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                10L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingService.create(eq(2L), any(BookItemRequestDto.class))).thenReturn(sampleBookingDto());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(10))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    void create_withPastStart_returns400() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                10L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenItemUnavailable_returns400WithErrorBody() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                10L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingService.create(eq(2L), any(BookItemRequestDto.class)))
                .thenThrow(new ValidationException("Вещь с id 10 недоступна для бронирования"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Вещь с id 10 недоступна для бронирования"));
    }

    @Test
    void approve_withApprovedTrue_returns200() throws Exception {
        BookingDto approved = sampleBookingDto();
        approved.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(1L, 1L, true)).thenReturn(approved);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approve_whenNotOwner_returns403() throws Exception {
        when(bookingService.approve(2L, 1L, true))
                .thenThrow(new ForbiddenException("Пользователь с id 2 не является владельцем вещи из бронирования с id 1"));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById_returns200() throws Exception {
        when(bookingService.findById(2L, 1L)).thenReturn(sampleBookingDto());

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findById_whenNotFoundOrStranger_returns404() throws Exception {
        when(bookingService.findById(99L, 1L))
                .thenThrow(new NotFoundException("Бронирование с id 1 не найдено"));

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByBooker_withDefaultState_returns200() throws Exception {
        when(bookingService.findAllByBooker(2L, BookingState.ALL)).thenReturn(List.of(sampleBookingDto()));

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllByBooker_withExplicitState_returns200() throws Exception {
        when(bookingService.findAllByBooker(2L, BookingState.WAITING)).thenReturn(List.of(sampleBookingDto()));

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 2L).param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllByBooker_withUnknownState_returns400WithExactMessage() throws Exception {
        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 2L).param("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void findAllByOwner_withDefaultState_returns200() throws Exception {
        when(bookingService.findAllByOwner(1L, BookingState.ALL)).thenReturn(List.of(sampleBookingDto()));

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllByOwner_withUnknownState_returns400WithExactMessage() throws Exception {
        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L).param("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: UNSUPPORTED_STATUS"));
    }
}

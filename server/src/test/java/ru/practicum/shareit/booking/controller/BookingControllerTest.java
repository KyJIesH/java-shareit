package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.CheckPage;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private CheckPage checkPage;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void saveTest() throws Exception {
        BookingDto request = new BookingDto();
        request.setId(1L);
        request.setBookerId(1L);

        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(request);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.bookerId", is(1L), Long.class));
    }

    @Test
    void approveTest() throws Exception {
        BookingDto request = new BookingDto();
        request.setId(1L);

        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(request);

        mockMvc.perform(patch("/bookings/{bookingId}", request.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdTest() throws Exception {
        BookingDto request = new BookingDto();
        request.setId(1L);

        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(request);

        mockMvc.perform(get("/bookings/{bookingId}", request.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByUserTest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        doNothing().when(checkPage).checkPage(1, 1);
        verify(bookingService, times(1))
                .getAllByBooker(1L, "ALL", PageRequest.of(1, 1));
    }

    @Test
    void getAllForOwnerTest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        doNothing().when(checkPage).checkPage(1, 1);
        verify(bookingService, times(1))
                .getAllByOwner(1L, "ALL", PageRequest.of(1, 1));
    }
}
package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CheckPage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CheckPage checkPage;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createItemTest() throws Exception {
        ItemDto request = new ItemDto(1L, "name", "description", new User(), true,
                1L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(request);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void createItemTestIncorrectId() throws Exception {
        ItemDto request = new ItemDto(1L, "name", "description", new User(), true,
                1L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(request);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void createItemTestIncorrectDto() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void createItemCommentTest() throws Exception {
        CommentDto request = new CommentDto();
        Long itemId = 1L;

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(request);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getItemTest() throws Exception {
        ItemDto request = new ItemDto(1L, "name", "description",
                new User(1L, "test", "test"),
                true, 1L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(request);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAllItemsByUserIdTest() throws Exception {
        mockMvc.perform(get("/items")
                        .param("from", "1")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        doNothing().when(checkPage).checkPage(1, 1);
        verify(itemService, times(1))
                .getAllItemsByUserId(1L, PageRequest.of(1, 1));
    }

    @Test
    void updateTest() throws Exception {
        ItemDto inDB = new ItemDto(1L, "name", "description", new User(), true,
                1L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        ItemDto requestUpdate = new ItemDto(2L, "nameUp", "descriptionUp", new User(), true,
                2L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        when(itemService.update(any(ItemDto.class), anyLong(), anyLong())).thenReturn(requestUpdate);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .content(objectMapper.writeValueAsString(requestUpdate))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void updateTestIncorrectId() throws Exception {
        ItemDto inDB = new ItemDto(1L, "name", "description", new User(), true,
                1L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        ItemDto requestUpdate = new ItemDto(2L, "nameUp", "descriptionUp", new User(), true,
                2L, new ItemRequest(), new BookingDto(), new BookingDto(), new ArrayList<>());

        when(itemService.update(any(ItemDto.class), anyLong(), anyLong())).thenReturn(requestUpdate);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .content(objectMapper.writeValueAsString(requestUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void updateTestIncorrectDto() throws Exception {
        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void searchByTextTest() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "1")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        doNothing().when(checkPage).checkPage(1, 1);
        verify(itemService, times(1))
                .searchByText("text", 1L, PageRequest.of(1, 1));
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete("/items/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService, times(1)).delete(1L);
    }
}
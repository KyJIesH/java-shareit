package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long userId, PageRequest pageRequest);

    @Query("select r from ItemRequest r " +
            "where r.requester.id != ?1")
    List<ItemRequest> findAll(Long userId, PageRequest pageRequest);
}

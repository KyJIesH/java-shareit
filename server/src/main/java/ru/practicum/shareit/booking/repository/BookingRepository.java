package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Методы для getAllByBooker
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentBookerBookings(Long bookerId, LocalDateTime now, Pageable pageable);

    //Методы для getAllByOwner
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, StatusBooking status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentOwnerBookings(Long ownerId, LocalDateTime now, Pageable pageable);

    //Методы для createComment
    Long countAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    //Методы для getItem
    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start < ?3 and b.status != 'REJECTED' " +
            "order by b.end desc")
    List<Booking> findPastOwnerBookings(Long itemId, Long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start > ?3 and b.status != 'REJECTED' " +
            "order by b.start asc")
    List<Booking> findFutureOwnerBookings(Long itemId, Long ownerId, LocalDateTime now);

    //Метод для getItems
    List<Booking> findByItemIdInAndStatusNot(List<Long> itemIds, StatusBooking status);
}

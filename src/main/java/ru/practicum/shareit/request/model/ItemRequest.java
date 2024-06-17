package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 15, max = 500, message = "Длина описания должна быть от 15 до 500 символов")
    @Column(name = "description")
    private String description;

    @NotBlank(message = "Пользователь, который создаёт запрос должен существовать")
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    private User requestor;

    @NotBlank
    @FutureOrPresent(message = "Значение должно быть настоящим временем либо будущим")
    @Column(name = "creation_date")
    private Date created;
}

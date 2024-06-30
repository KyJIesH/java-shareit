package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import ru.practicum.shareit.item.validation.ValidationItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = {ValidationItem.Create.class})
    @Size(min = 1, max = 30, message = "Длина названия должна быть от 5 до 30 символов")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = {ValidationItem.Create.class})
    @Size(min = 5, max = 2000, message = "Длина описания должна быть от 15 до 2000 символов")
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @NotNull(message = "Статус не может быть пустым", groups = {ValidationItem.Create.class})
    @Column(name = "available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "request_id")
    @JsonBackReference
    private ItemRequest request;
}

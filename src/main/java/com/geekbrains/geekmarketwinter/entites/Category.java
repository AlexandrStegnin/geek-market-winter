package com.geekbrains.geekmarketwinter.entites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@EqualsAndHashCode(exclude = {"title", "description"})
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    @Size(min = 3, message = "Название категории должно быть более 2 символов")
    private String title;

    @Column(name = "description")
    private String description;
}

package com.geekbrains.geekmarketwinter.entites;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class Role implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	@Size(min = 3, message = "Название роли не может быть менее 3 символов")
	private String name;

	@Column(name = "humanized")
	@Size(min = 3, message = "Описание не может быть менее 3 символов")
	private String humanized;

}

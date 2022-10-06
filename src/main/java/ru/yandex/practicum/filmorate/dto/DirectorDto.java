package ru.yandex.practicum.filmorate.dto;

import javax.validation.constraints.NotBlank;

public class DirectorDto {
    private Long id;
    @NotBlank
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DirectorDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

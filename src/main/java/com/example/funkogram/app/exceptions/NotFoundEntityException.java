package com.example.funkogram.app.exceptions;

import lombok.Getter;

@Getter
public class NotFoundEntityException extends RuntimeException {

    private final String objectName;

    private Long id = 0L;

    public NotFoundEntityException(Class<?> clazz) {
        objectName = clazz.getName();
    }

    public NotFoundEntityException(Class<?> clazz, Long id) {
        objectName = clazz.getName();
        this.id = id;
    }
}

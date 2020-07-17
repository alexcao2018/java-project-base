package com.project.base.model.validation;

@FunctionalInterface
public interface IValidation<T> {
    String validate(T object);
}

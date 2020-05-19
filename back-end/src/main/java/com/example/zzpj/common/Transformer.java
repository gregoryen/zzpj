package com.example.zzpj.common;

@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T object);
}


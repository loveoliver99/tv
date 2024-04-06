package com.evangelsoft.econnect.util;

public interface ObjectFactory<E> {
    E getObjectInstance() throws Exception;
}
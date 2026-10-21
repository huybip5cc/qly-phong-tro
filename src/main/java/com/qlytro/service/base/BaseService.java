package com.qlytro.service.base;

import java.util.List;

/**
 * Interface chung cho cac thao tac CRUD co ban.
 * Moi Service cu the phai ke thua interface nay (hoac extend them).
 *
 * @param <T>  Kieu DTO response
 * @param <ID> Kieu khoa chinh
 */
public interface BaseService<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}

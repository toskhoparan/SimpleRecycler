package com.mt.simplerecycler;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by m_toskhoparan on 09/21/17.
 */

public interface AdapterActions<E> {

    void add(E item);

    void addAll(List<E> items);

    void remove(E item);

    void removeAll();

    @Nullable E get(int position);

    @Nullable List<E> getAll();
}

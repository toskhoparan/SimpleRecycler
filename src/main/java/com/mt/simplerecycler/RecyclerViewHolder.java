package com.mt.simplerecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by m_toskhoparan on 09/21/17.
 */

public abstract class RecyclerViewHolder<E> extends RecyclerView.ViewHolder {

    public RecyclerViewHolder(View view) {
        super(view);
    }

    protected abstract void bind(E item);

    public interface Factory<E> {

        RecyclerViewHolder<E> get(ViewGroup parent, int viewType);

        int getViewType(E item, int position);
    }
}

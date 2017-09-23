package com.mt.simplerecycler;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by m_toskhoparan on 09/21/17.
 */

public abstract class RecyclerAdapter<E extends AdapterItem>
        extends RecyclerView.Adapter<RecyclerViewHolder<E>> implements AdapterActions<E> {

    @Nullable
    private List<E> items;

    private final RecyclerViewHolder.Factory<E> factory = getFactory();

    // each time data is set, we update this variable so that if DiffUtil calculation returns
    // after repetitive updates, we can ignore the old calculation
    private int dataVersion = 0;

    @Override
    public RecyclerViewHolder<E> onCreateViewHolder(ViewGroup parent, int viewType) {
        //noinspection ConstantConditions
        if (factory == null) throw new IllegalStateException("Factory should be initialized");
        return factory.get(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder<E> holder, int position) {
        if (items != null) holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        //noinspection ConstantConditions
        if (factory == null) throw new IllegalStateException("Factory should be initialized");
        return factory.getViewType(get(position), position);
    }

    @Override
    public void add(E item) {
        dataVersion++;
        if (items == null) {
            items = Collections.singletonList(item);
            notifyDataSetChanged();
        } else {
            new DiffTask<>(this, dataVersion, items, Collections.singletonList(item)).execute();
        }
    }

    @Override
    public void addAll(List<E> items) {
        dataVersion++;
        if (this.items == null) {
            this.items = items;
            notifyDataSetChanged();
        } else {
            new DiffTask<>(this, dataVersion, this.items, items).execute();
        }
    }

    @Override
    public void remove(E item) {
        dataVersion++;
        if (items != null) {
            int position = items.indexOf(item);
            items.remove(position);
            if (items.isEmpty()) items = null;
            notifyItemRemoved(position);
        }
    }

    @Override
    public void removeAll() {
        dataVersion++;
        if (items != null) {
            int oldSize = items.size();
            items = null;
            notifyItemRangeRemoved(0, oldSize);
        }
    }

    @Override
    public E get(int position) {
        return items != null ? items.get(position) : null;
    }

    @Override
    public List<E> getAll() {
        return items;
    }

    @NonNull
    protected abstract RecyclerViewHolder.Factory<E> getFactory();

    protected abstract boolean areItemsTheSame(E oldItem, E newItem);

    protected abstract boolean areContentsTheSame(E oldItem, E newItem);

    private static class DiffTask<E extends AdapterItem> extends AsyncTask<Void, Void, DiffUtil.DiffResult> {

        private final RecyclerAdapter<E> adapter;
        private final List<E> oldItems;
        private final List<E> newItems;
        private final int currentDataVersion;

        DiffTask(RecyclerAdapter<E> adapter, int currentDataVersion, List<E> oldItems, List<E> newItems) {
            this.adapter = adapter;
            this.currentDataVersion = currentDataVersion;
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        protected DiffUtil.DiffResult doInBackground(Void... voids) {
            return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldItems.size();
                }

                @Override
                public int getNewListSize() {
                    return newItems.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    E oldItem = oldItems.get(oldItemPosition);
                    E newItem = newItems.get(newItemPosition);
                    return adapter.areItemsTheSame(oldItem, newItem);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    E oldItem = oldItems.get(oldItemPosition);
                    E newItem = newItems.get(newItemPosition);
                    return adapter.areContentsTheSame(oldItem, newItem);
                }
            });
        }

        @Override
        protected void onPostExecute(DiffUtil.DiffResult diffResult) {
            if (adapter.dataVersion != currentDataVersion) return;
            adapter.items = newItems;
            diffResult.dispatchUpdatesTo(adapter);
        }
    }
}

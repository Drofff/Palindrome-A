package com.drofff.palindrome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.type.Displayable;
import com.drofff.palindrome.view.holder.UpdatableViewHolder;
import com.drofff.palindrome.view.holder.strategy.DisplayStrategy;

import java.util.ArrayList;
import java.util.List;

import static android.view.LayoutInflater.from;

public class ArrayViewAdapter<T extends Displayable> extends RecyclerView.Adapter<UpdatableViewHolder<T>> {

    private final int layoutId;
    private final Class<? extends DisplayStrategy> displayStrategyClass;

    private List<T> displayedElements = new ArrayList<>();

    public ArrayViewAdapter(int layoutId, Class<? extends DisplayStrategy> displayStrategyClass) {
        this.layoutId = layoutId;
        this.displayStrategyClass = displayStrategyClass;
    }

    @NonNull
    @Override
    public UpdatableViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = from(context).inflate(layoutId, parent, false);
        return new UpdatableViewHolder<>(view, displayStrategyClass);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdatableViewHolder<T> holder, int position) {
        T elem = displayedElements.get(position);
        holder.display(elem);
    }

    @Override
    public int getItemCount() {
        return displayedElements.size();
    }

    public void updateDisplayedElementsList(List<T> displayedElements) {
        this.displayedElements = displayedElements;
        notifyDataSetChanged();
    }

}

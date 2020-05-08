package com.drofff.palindrome.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.type.Displayable;
import com.drofff.palindrome.view.holder.strategy.DisplayStrategy;

import static com.drofff.palindrome.utils.ReflectionUtils.constructInstanceOfClass;

public class UpdatableViewHolder<T extends Displayable> extends RecyclerView.ViewHolder {

    private final DisplayStrategy displayStrategy;

    public UpdatableViewHolder(@NonNull View itemView, Class<? extends DisplayStrategy> displayStrategyClass) {
        super(itemView);
        displayStrategy = constructInstanceOfClass(displayStrategyClass);
        displayStrategy.setRootView(itemView);
    }

    public void display(T t) {
        displayStrategy.display(t);
    }

}

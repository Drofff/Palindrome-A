package com.drofff.palindrome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.ViolationType;

import java.util.List;
import java.util.Optional;

public class ViolationTypesSpinnerAdapter extends ArrayAdapter<ViolationType> {

    private final Context context;
    private final List<ViolationType> violationTypes;

    public ViolationTypesSpinnerAdapter(@NonNull Context context, @NonNull List<ViolationType> violationTypes) {
        super(context, R.layout.violation_type_item, violationTypes);
        this.context = context;
        this.violationTypes = violationTypes;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = Optional.ofNullable(convertView)
                .orElseGet(() -> inflateParentWithViolationTypeItem(parent));
        TextView textView = itemView.findViewById(R.id.violation_type_name);
        ViolationType violationType = violationTypes.get(position);
        String typeName = violationType.getName();
        textView.setText(typeName);
        return itemView;
    }

    private View inflateParentWithViolationTypeItem(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.violation_type_item, parent, false);
    }

}

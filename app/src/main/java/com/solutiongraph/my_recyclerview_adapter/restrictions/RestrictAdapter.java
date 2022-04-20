package com.solutiongraph.my_recyclerview_adapter.restrictions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.model.simplexdata.Restriction;
import com.solutiongraph.R;
import com.solutiongraph.my_recyclerview_adapter.BaseRecyclerViewAdapter;

import java.util.Arrays;

public class RestrictAdapter extends BaseRecyclerViewAdapter<Restriction, RestrictViewHolder> {
    public RestrictAdapter(Context context, Restriction[] data) {
        super(LayoutInflater.from(context), data);
        initErrorStatus();
    }

    @NonNull
    @Override
    public RestrictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View restrictViewItem =
                this.layoutInflater.inflate(R.layout.restrict_item, parent, false);

        return new RestrictViewHolder(restrictViewItem, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RestrictViewHolder holder, int position) {
        Restriction restrict = this.data[position];
        holder.setFreeCoeff(restrict.getDoubleFreeCoeff());
        holder.setResult(restrict.getResultAsDouble());
        holder.setCoeffs(restrict.getDoubleCoeffs());
        holder.signCheck(restrict.getSign());
        holder.updateHeader();
    }

    private void initErrorStatus() {
        this.errorsArray = new boolean[data.length];
        Arrays.fill(errorsArray , false);
    }
}

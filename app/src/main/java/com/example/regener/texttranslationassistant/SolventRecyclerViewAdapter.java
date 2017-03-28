package com.example.regener.texttranslationassistant;

/**
 * Created by Regener on 24.03.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.regener.texttranslationassistant.manager.SolventViewHolders;

import java.util.List;

public class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {

    private List<ItemObject> itemList;
    private Context context;

    public SolventRecyclerViewAdapter(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;

    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
        SolventViewHolders rcv = new SolventViewHolders(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(SolventViewHolders holder, int position) {
        holder.mFileTitle.setText(itemList.get(position).getTitle());
        holder.mFileText.setText(itemList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}

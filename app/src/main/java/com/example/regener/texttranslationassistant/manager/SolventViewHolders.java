package com.example.regener.texttranslationassistant.manager;

/**
 * Created by Regener on 24.03.2017.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regener.texttranslationassistant.R;
import com.example.regener.texttranslationassistant.TextViewActivity;
import com.example.regener.texttranslationassistant.logic.SolvenItemConteiner;

import static android.support.v4.content.ContextCompat.startActivity;


public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mFileTitle;
    public TextView mFileText;
    private Context mContext;

    public SolventViewHolders(View itemView, Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        mContext = context;
        mFileTitle = (TextView) itemView.findViewById(R.id.fileTitle);
        mFileText = (TextView) itemView.findViewById(R.id.fileText);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, TextViewActivity.class);
        intent.putExtra("path", SolvenItemConteiner.getItem(getPosition()).getPath());
        mContext.startActivity(intent);
    }
}
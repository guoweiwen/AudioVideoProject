package com.wyman.fakedouyin;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wyman.fakedouyin.photographalbum.ImagePiece;

import java.util.List;

/**
 * 封装数据适配器
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridHolder> {
    private Context mContext;
    private List<ImagePiece> mList;
    private LayoutInflater mLayoutInflater;


    public GridAdapter(Context context, List<ImagePiece> list) {
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.grid_item, viewGroup, false);
        GridHolder gridHolder = new GridHolder(view);
        return gridHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int i) {

        if (holder instanceof GridHolder) {
            final GridHolder gridHolder = (GridHolder) holder;
            gridHolder.coverView.setImageBitmap(mList.get(i).getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size()>0){
            return mList.size();
        }else {
            return 0;
        }

    }


    class GridHolder extends RecyclerView.ViewHolder {

        public ImageView coverView;

        public GridHolder(View v) {
            super(v);
            coverView = (ImageView) v.findViewById(R.id.grid_item_cover);
        }
    }
}

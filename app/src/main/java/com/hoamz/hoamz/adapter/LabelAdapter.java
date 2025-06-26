package com.hoamz.hoamz.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Label;

import java.util.ArrayList;
import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.ViewHodel> {
    private List<Label> listLabel;
    private int currPosition = 0;

    //tao interface giao tiep voi main
    public interface onClickLabel{
        void onClickLabel(String label);
    }

    private onClickLabel onMyClick;

    public void setOnMyClick(onClickLabel onMyClick) {
        this.onMyClick = onMyClick;
    }

    public LabelAdapter(){
        listLabel = new ArrayList<>();
    }

    public void setCurrPosition(int currPosition) {
        this.currPosition = currPosition;
        notifyDataSetChanged();
    }

    public void setListLabel(List<Label> listLabel) {
        this.listLabel = listLabel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_label,parent,false);
        return new ViewHodel(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHodel holder, int position) {
        holder.tvLabel.setText(listLabel.get(position).getLabel());

        if(currPosition == holder.getAdapterPosition()){
            holder.cardLabel.setBackground(
                   ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.custom_background_label));
            holder.tvLabel.setTextColor(Color.WHITE);
        }
        else{
            holder.cardLabel.setBackground(
                    ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.custom_bg_label_none));
            holder.tvLabel.setTextColor(Color.GRAY);
        }

        holder.cardLabel.setOnClickListener(v ->{
            int oldPos = currPosition;
            currPosition = holder.getAdapterPosition();
            if(oldPos != -1){
                notifyItemChanged(oldPos);//doi nen ve trang thai cu
            }
            notifyItemChanged(currPosition);//thay doi ve trang thai moi

            //bat su kien click cardview luon
            onMyClick.onClickLabel(listLabel.get(position).getLabel());
        });
    }

    @Override
    public int getItemCount() {
        return listLabel.size();
    }

    public static class ViewHodel extends RecyclerView.ViewHolder{
        private CardView cardLabel;
        private TextView tvLabel;
        public ViewHodel(@NonNull View itemView) {
            super(itemView);
            cardLabel = itemView.findViewById(R.id.cardLabel);
            tvLabel = itemView.findViewById(R.id.tvLabel);
        }
    }
}

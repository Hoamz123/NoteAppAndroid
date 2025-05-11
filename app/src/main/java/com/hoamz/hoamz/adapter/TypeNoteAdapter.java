package com.hoamz.hoamz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TypeNoteAdapter extends RecyclerView.Adapter<TypeNoteAdapter.viewHodel> {
    private List<LabelDetail> listLabelDetail;

    public TypeNoteAdapter(){
        listLabelDetail = new ArrayList<>();
    }

    public interface onClickTypeNote{
        void onClickTypeLabel(LabelDetail labelDetail);

        void onClickForShowMoreOption(LabelDetail labelDetail);
    }

    private onClickTypeNote onClickTypeNote;

    public void setOnClickTypeNote(TypeNoteAdapter.onClickTypeNote onClickTypeNote) {
        this.onClickTypeNote = onClickTypeNote;
    }

    public void setListLabelDetail(List<LabelDetail> listLabelDetail) {
        this.listLabelDetail = listLabelDetail;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_in_type_note,parent,false);
        return new viewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHodel holder, int position) {
        LabelDetail labelDetail = listLabelDetail.get(position);
        String content = labelDetail.getLabelName() + " (" + labelDetail.getNumberNote() + ")";
        holder.tvNameLabel.setText(content);

        //bat su kien click
        holder.tvNameLabel.setOnClickListener(v ->{
            onClickTypeNote.onClickTypeLabel(labelDetail);
        });


        holder.ivMore.setOnClickListener(v ->{
            onClickTypeNote.onClickForShowMoreOption(labelDetail);
        });

        if(Objects.equals(labelDetail.getLabelName(), Constants.labelAll)){
            holder.ivMore.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return listLabelDetail.size();
    }

    public static class viewHodel extends RecyclerView.ViewHolder {
        private final ConstraintLayout cardViewCt;
        private final TextView tvNameLabel;
        private ImageView ivMore;
        public viewHodel(@NonNull View itemView) {
            super(itemView);
            cardViewCt = itemView.findViewById(R.id.cardViewCt);
            tvNameLabel = itemView.findViewById(R.id.tvLabelInType);
            ivMore = itemView.findViewById(R.id.ivMoreSelect);
        }
    }
}

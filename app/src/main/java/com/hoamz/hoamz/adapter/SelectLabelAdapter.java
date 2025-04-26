package com.hoamz.hoamz.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.LabelDetail;
import java.util.ArrayList;
import java.util.List;

public class SelectLabelAdapter extends RecyclerView.Adapter<SelectLabelAdapter.ViewHodel>{
    private List<LabelDetail> labelDetailList;
    private int selectedPosition = -1;

    public SelectLabelAdapter(){
        labelDetailList = new ArrayList<>();
    }
    public interface onClickToSelectLabel{
        void onClick(LabelDetail labelDetail);
    }

    private onClickToSelectLabel onClickToSelectLabel;

    public void setOnClickToSelectLabel(SelectLabelAdapter.onClickToSelectLabel onClickToSelectLabel) {
        this.onClickToSelectLabel = onClickToSelectLabel;
    }

    public void setLabelDetailList(List<LabelDetail> labelDetailList) {
        this.labelDetailList = labelDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.custom_item_in_dialog,null);
        return new ViewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodel holder, int position) {
        LabelDetail labelDetail = labelDetailList.get(position);
        holder.bind(labelDetail);

        if(selectedPosition == holder.getAdapterPosition()){
            //logic thay background
            holder.cardView.setBackgroundResource(R.drawable.bg_multi_selec);
        }
        else{
            //logic tra background ve nhu cu
            holder.cardView.setBackgroundResource(R.drawable.bg_unselect);
        }

        //su kien click
        holder.cardView.setOnClickListener(v ->{

            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();//idex moi vau dc click
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            onClickToSelectLabel.onClick(labelDetail);
        });

    }

    @Override
    public int getItemCount() {
        return labelDetailList.size();
    }

    public static class ViewHodel extends RecyclerView.ViewHolder{
        private ImageView ivCategory;
        private TextView tvNameLabel,tvNumberNote;
        private ConstraintLayout cardView;
        public ViewHodel(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.iv_category_dialog);
            tvNameLabel = itemView.findViewById(R.id.tvNameLabel_dialog);
            tvNumberNote = itemView.findViewById(R.id.tvNumberNote_dialog);
            cardView = itemView.findViewById(R.id.viewLabelDetail);
        }

        @SuppressLint("SetTextI18n")
        public void bind(LabelDetail labelDetail){
            tvNameLabel.setText(labelDetail.getLabelName());
            tvNumberNote.setText(labelDetail.getNumberNote() + " Ghi chú");
            ivCategory.setImageResource(R.drawable.category);
        }
    }
}

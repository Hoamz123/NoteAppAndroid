package com.hoamz.hoamz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHodel> {
    private List<Note> noteList;

    public interface OnItemClickListener{
        void onItemClick(Note note);
        void onItemLongClick(Note note);
    }

    private OnItemClickListener listener;

    public void setOnClickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NoteAdapter(){
        noteList = new ArrayList<>();
    }
    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_note,parent,false);
        return new ViewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodel holder, int position) {
        Note note = noteList.get(position);
        Date date = new Date(note.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault());

        /* do la co hai kieu hien thi -> moi lan nhan icon type -> recycle se tai che view -> thay doi view */
        //reset trang thai cu
        holder.ivFavorite.setVisibility(View.GONE);
        holder.ivPin.setVisibility(View.GONE);

        holder.cardNote.setCardBackgroundColor(note.getColorBgID());

        //xem xet doi mau chu (mac dinh la chu den -> chi can doi nhung cai thuoc nen dark)
        int colorBackground = note.getColorBgID();

        boolean isDark = false;

         if(Constants.colorDarkPicker.contains(colorBackground)){
            //logic
            holder.tvTitle.setTextColor(Color.WHITE);
            holder.tvContent.setTextColor(Color.WHITE);
            holder.tvDate.setTextColor(Color.WHITE);
            isDark = true;
        }
        else{
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvContent.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.BLACK);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable iconCalender = holder.itemView.getContext().getDrawable(R.drawable.ic_calender);
            holder.tvDate.setCompoundDrawablesWithIntrinsicBounds(iconCalender,null,null,null);
        }

        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        holder.tvDate.setText(sdf.format(date));

        if(isDark){
            @SuppressLint("UseCompatLoadingForDrawables") Drawable iconCalender = holder.itemView.getContext().getDrawable(R.drawable.ic_calender_w);
            holder.tvDate.setCompoundDrawablesWithIntrinsicBounds(iconCalender,null,null,null);
        }

        // cap nhat trang thai moi
        if (note.isFavorite()) holder.ivFavorite.setVisibility(View.VISIBLE);
        if (note.isPin() == 1) holder.ivPin.setVisibility(View.VISIBLE);

        holder.cardNote.setOnClickListener(v ->{
            listener.onItemClick(note);
        });

        holder.cardNote.setOnLongClickListener(v -> {
            listener.onItemLongClick(note);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class ViewHodel extends RecyclerView.ViewHolder{
        private CardView cardNote;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvDate;
        private ImageView ivFavorite,ivPin;
        public ViewHodel(@NonNull View itemView) {
            super(itemView);
            cardNote = itemView.findViewById(R.id.cardNote);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            ivPin = itemView.findViewById(R.id.iv_pin);
        }
    }
}

package com.hoamz.hoamz.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHodel> {
    private List<Note> noteList;
    private boolean isMultiSelect = false;
    private final Set<Integer> listMultiSelected;
    private final Set<Note> listNoteSelected;
    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
        notifyDataSetChanged();
    }


    public void setCancelMultiSelect(){
        listNoteSelected.clear();
        listMultiSelected.clear();
        isMultiSelect = false;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener{
        void onItemClick(Note note);

        void onItemLongClick(Note note);
    }


    public interface OnMultiSelectItem{
        void onMultiSelect(Set<Note> listMultiSelected);
    }

    private OnMultiSelectItem onMultiSelectItem;
    private OnItemClickListener listener;

    public void setOnMultiSelectItem(OnMultiSelectItem onMultiSelectItem) {
        this.onMultiSelectItem = onMultiSelectItem;
    }

    public void setOnClickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NoteAdapter(){
        noteList = new ArrayList<>();
        listMultiSelected = new HashSet<>();
        listNoteSelected = new HashSet<>();
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

        holder.cardNote.setBackgroundResource(note.getColorBgID());

        //xem xet doi mau chu (mac dinh la chu den -> chi can doi nhung cai thuoc nen dark)
        int colorBackground = note.getColorBgID();

        boolean isDark = false;

        if (Constants.backGroundDark.contains(colorBackground)) {
            //logic
            holder.tvTitle.setTextColor(Color.WHITE);
            holder.tvContent.setTextColor(Color.WHITE);
            holder.tvDate.setTextColor(Color.WHITE);
            isDark = true;
        } else {
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvContent.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.BLACK);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable iconCalender = holder.itemView.getContext().getDrawable(R.drawable.ic_calender);
            holder.tvDate.setCompoundDrawablesWithIntrinsicBounds(iconCalender, null, null, null);
        }

        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        holder.tvDate.setText(sdf.format(date));

        if (isDark) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable iconCalender = holder.itemView.getContext().getDrawable(R.drawable.ic_calender_w);
            holder.tvDate.setCompoundDrawablesWithIntrinsicBounds(iconCalender, null, null, null);
        }

        // cap nhat trang thai moi
        if (note.isFavorite()) holder.ivFavorite.setVisibility(View.VISIBLE);
        if (note.isPin() == 1) holder.ivPin.setVisibility(View.VISIBLE);

        if(listMultiSelected.contains(position)){
            if(Constants.backGroundDark.contains(colorBackground)){
                holder.bgMultiSelected.setBackgroundResource(R.drawable.bg_select_mul_dark);
            }
            else holder.bgMultiSelected.setBackgroundResource(R.drawable.bg_multi_selec);
        }
        else{
            holder.bgMultiSelected.setBackgroundResource(R.drawable.bg_un_select);
        }

        holder.cardNote.setOnClickListener(v -> {
            if(isMultiSelect){
                toggleSelection(position);
            }
            else {
                listener.onItemClick(note);
            }
        });

        holder.cardNote.setOnLongClickListener(v -> {
            listener.onItemLongClick(note);
            return true;
        });
    }

    private void toggleSelection(int position) {
        if(listMultiSelected.contains(position)){
            listMultiSelected.remove(position);
            listNoteSelected.remove(noteList.get(position));
        }
        else{
            listMultiSelected.add(position);
            listNoteSelected.add(noteList.get(position));
        }
        notifyItemChanged(position);
        onMultiSelectItem.onMultiSelect(listNoteSelected);
    }

    public void setAllClick(){
        for(int i=0;i<noteList.size();i++){
            listMultiSelected.add(i);
            listNoteSelected.add(noteList.get(i));
        }
        notifyDataSetChanged();
        onMultiSelectItem.onMultiSelect(listNoteSelected);
    }

    public void setClearAllClick(){
        for(int i=0;i<noteList.size();i++){
            if(listMultiSelected.contains(i) && listNoteSelected.contains(noteList.get(i))){
                listMultiSelected.remove(i);
                listNoteSelected.remove(noteList.get(i));
            }
        }
        notifyDataSetChanged();
        onMultiSelectItem.onMultiSelect(listNoteSelected);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class ViewHodel extends RecyclerView.ViewHolder{
        private final ConstraintLayout cardNote;
        private final ConstraintLayout bgMultiSelected;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvDate;
        private final ImageView ivFavorite;
        private final ImageView ivPin;
        public ViewHodel(@NonNull View itemView) {
            super(itemView);
            cardNote = itemView.findViewById(R.id.cardNote);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            ivPin = itemView.findViewById(R.id.iv_pin);
            bgMultiSelected = itemView.findViewById(R.id.bgMultiSelected);
        }
    }
}

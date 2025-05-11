package com.hoamz.hoamz.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Label;
import java.util.List;
import java.util.Objects;

public class DialogShow {
    public static void showDialogLabel(Context context,String title,String hint, List<Label> listLabel, onDialogListener onDialogCreateLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_create_label,null);
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        TextView tvTitle = view.findViewById(R.id.tvTitleDialog);
        tvTitle.setText(title);
        EditText edtEnterLabel = view.findViewById(R.id.edtLabel);
        if(hint != null){
            edtEnterLabel.setText(hint);
        }
        TextView btnSave = view.findViewById(R.id.tvOk);
        TextView btnExit = view.findViewById(R.id.tvNotOk);
        ImageView ivCancelText = view.findViewById(R.id.ivCancelText);
        btnExit.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String labelNew = Objects.requireNonNull(edtEnterLabel.getText()).toString();
            if(checkLabelExits(labelNew,listLabel)){
                edtEnterLabel.setError("Nhãn đã tồn tại");
            }
            else{
                onDialogCreateLabel.onSaveNewLabel(edtEnterLabel.getText().toString());
                dialog.dismiss();
            }
        });

        ivCancelText.setOnClickListener(v -> edtEnterLabel.setText(""));//clear text

        edtEnterLabel.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(s.length() > 0){
                    edtEnterLabel.setError(null);
                    ivCancelText.setVisibility(View.VISIBLE);
                }
                else if(s.length() == 0){
                    ivCancelText.setVisibility(View.INVISIBLE);
                }
            }
        });
        dialog.show();
    }

    private static boolean checkLabelExits(String label,List<Label> listLabel){
        for(Label l : listLabel){
            if(Objects.equals(l.getLabel(), label)){
                return true;//da ton tai roi
            }
        }
        return false;
    }

    public interface onDialogListener{
        void onSaveNewLabel(String label);
    }

    //show dialog notify xoa du lieu

    public interface onDialogDeleteListener{
        void onDeleteLabel(boolean delete);
    }

    public static void showDialogNotifyDelete(Context context,onDialogDeleteListener onDeleteLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_notify_delete,null);
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvCancel = view.findViewById(R.id.tvCancelNotify);
        TextView tvDelete = view.findViewById(R.id.tvDeleteOk);
        tvCancel.setOnClickListener(v ->{
            onDeleteLabel.onDeleteLabel(false);
            dialog.dismiss();
        });
        tvDelete.setOnClickListener(v ->{
            onDeleteLabel.onDeleteLabel(true);
            dialog.dismiss();
        });
        dialog.show();
    }

}

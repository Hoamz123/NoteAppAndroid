package com.hoamz.hoamz.ui.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.SelectLabelAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.NoteDeleted;
import com.hoamz.hoamz.databinding.ActivityNoteDetailBinding;
import com.hoamz.hoamz.ui.fragment.BottomSheetColor;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.utils.TextToFileTxt;
import com.hoamz.hoamz.utils.TextViewUndoRedo;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NoteDetail extends AppCompatActivity {
    private EditText edtTitle,edtContent;
    private ImageView ivBackToMain,iv_More,iv_alarm,iv_addColor;
    private TextView tvDate,tvChooseLabel;
    private ConstraintLayout viewEdtTextContentDetail;
    private CoordinatorLayout viewMainDetail;
    private SimpleDateFormat sdf;
    private BottomSheetColor sheetColor;
    private boolean isReadingMode = false;
    private boolean isEditedContent = false;
    private int colorBackground;
    private Note noteEdit;
    private NoteViewModel noteViewModel;
    private String contentUpdate,titleUpdate,labelUpdate;
    private boolean isFavorite;
    private boolean isPin;
    private boolean isShowMoreOption = false;
    private LiveData<List<Label>> listLabel;
    private LabelViewModel labelViewModel;
    private long timeAlarm = 0;
    private long timePrevious = 0;
    private ImageButton ivb_undo,ivb_redo;
    private String dateChoose;
    private String timeChoose,dayChoose;
    private Calendar calendarAlarm;
    private String tmpTime,tmpDay;
    private boolean isRepeat = false;

    //att in dialog
    /********************************/
    private RecyclerView rcViewInDialog;
    private SelectLabelAdapter selectLabelAdapter;
    private Button btnCreateNewLabel;
    private TextView acSave,acCancel;
    private List<LabelDetail> labelDetailList;
    private EditText edtInputNewLabel;
    private TextView acCancelCreateNewLabel,acSaveNewLabel;
    private long timeRepeatTmp = 0;
    private long timeRepeat = 0;
    private String textPrevious = "";
    private ActivityNoteDetailBinding binding;
    /********************************/
    private TextViewUndoRedo helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        helper = new TextViewUndoRedo(binding.edtContentIndT);
        helper.setMaxHistorySize(200);
        initView();
        onLoadingData();
        onClickListener();
    }

    @SuppressLint({"NewApi", "ResourceAsColor", "SetTextI18n"})
    private void onClickListener() {
        ivBackToMain.setOnClickListener(v -> {
            //cap nhat lai noi dung moi
            contentUpdate = edtContent.getText().toString();
            titleUpdate = edtTitle.getText().toString();
            labelUpdate = tvChooseLabel.getText().toString();
            noteEdit.setContent(contentUpdate);
            noteEdit.setTitle(titleUpdate);
            noteEdit.setLabel(labelUpdate);
            noteEdit.setTimeAlarm(timeAlarm);
            String date = tvDate.getText().toString();
            try {
                Date date1 = sdf.parse(date);
                if(date1 != null) {
                    noteEdit.setDate(date1.getTime());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            //set lai mau -> save lai vao Room
            noteEdit.setColorBgID(colorBackground);
            noteViewModel.updateNote(noteEdit);

            if(isRepeat){
                SharePre.getInstance(this).saveTimeRepeat(noteEdit.getId(), timeRepeat);
            }
            else{
                //khong co nhac lai
                timeRepeat = 0;
                SharePre.getInstance(this).saveTimeRepeat(noteEdit.getId(), 0L);
            }
            finish();
        });//back ve act gan nhat (tren dinh stack)

        //show more option
        binding.ivMoreIndT.setOnClickListener(v->{
            if(isShowMoreOption){
                binding.constrainMoreOption.setVisibility(View.INVISIBLE);
            }
            else{
                binding.constrainMoreOption.setVisibility(View.VISIBLE);
            }
            isShowMoreOption = !isShowMoreOption;
            if (isPin) {
                binding.acShowMoreSetupEdit.tvPin.setText(Constants.UNPIN);
            } else {
                binding.acShowMoreSetupEdit.tvPin.setText(Constants.PIN);
            }
            if (isFavorite) {
                binding.acShowMoreSetupEdit.tvFavorite.setText(Constants.UN_FAVORITE);
            } else {
                binding.acShowMoreSetupEdit.tvFavorite.setText(Constants.FAVORITE);
            }
        });

        binding.constrainMoreOption.setOnClickListener(v ->{
            if(isShowMoreOption){
                binding.constrainMoreOption.setVisibility(View.INVISIBLE);
                isShowMoreOption = false;
            }
        });

        //PIN
        binding.acShowMoreSetupEdit.tvPin.setOnClickListener(v ->{
            isPin = !isPin;
            //trang thai sau
            if (isPin) {
                binding.acShowMoreSetupEdit.tvPin.setText(Constants.UNPIN);
                noteEdit.setPin(1);
            } else {
                binding.acShowMoreSetupEdit.tvPin.setText(Constants.PIN);
                noteEdit.setPin(0);
            }
        });

        //Favorite
        binding.acShowMoreSetupEdit.tvFavorite.setOnClickListener(v ->{
            isFavorite = !isFavorite;
            if (isFavorite) {
                binding.acShowMoreSetupEdit.tvFavorite.setText(Constants.UN_FAVORITE);
            } else {
                binding.acShowMoreSetupEdit.tvFavorite.setText(Constants.FAVORITE);
            }
            noteEdit.setFavorite(isFavorite);//khi click 2 lan se doi trang thai
        });

        //Delete
        binding.acShowMoreSetupEdit.tvDelete.setOnClickListener(v ->{
            //hien thi log thong bao xoa

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            View viewDialog = View.inflate(this,R.layout.dialog_delete,null);
            builder.setView(viewDialog);
            android.app.AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            TextView acCancel = viewDialog.findViewById(R.id.tvCancel);
            TextView acDelete = viewDialog.findViewById(R.id.tvDelete);
            acCancel.setOnClickListener(view -> dialog.dismiss());
            acDelete.setOnClickListener(view_ -> {
                NoteDeleted noteDeleted = new NoteDeleted(noteEdit.getTitle()
                        ,noteEdit.getContent(),noteEdit.getLabel(),
                        noteEdit.getColorBgID(),System.currentTimeMillis());
                noteViewModel.deleteNote(noteEdit);
                noteViewModel.insertNoteDeleted(noteDeleted);
                dialog.dismiss();
                finish();//xoa xong back ve main
            });

            WindowManager.LayoutParams layoutParams = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.y = 100;
            viewDialog.setLayoutParams(layoutParams);
            dialog.show();
        });

        //Reading/Edit mode
        binding.acShowMoreSetupEdit.tvReadingMode.setOnClickListener(v ->{
            if(isReadingMode){
                //hien thi text edit mode
                binding.acShowMoreSetupEdit.tvReadingMode.setText(Constants.EDIT_MODE);
                binding.viewEdtTextContentDetail.setEnabled(false);
                binding.edtContentIndT.setEnabled(false);
                binding.edtTitleIndT.setEnabled(false);
            }
            else{
                //hien thi text Reading mode
                binding.acShowMoreSetupEdit.tvReadingMode.setText(Constants.READING_MODE);
                binding.viewEdtTextContentDetail.setEnabled(true);
                binding.edtContentIndT.setEnabled(true);
                binding.edtTitleIndT.setEnabled(true);
            }
            isReadingMode = !isReadingMode;
        });

        //Share
        binding.acShowMoreSetupEdit.tvShare.setOnClickListener(v ->{
            //logic
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String subject = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            intent.putExtra(Intent.EXTRA_TEXT,content);
            startActivity(Intent.createChooser(intent,"Chia sẻ qua "));
        });

        //luu note dang text
        binding.acShowMoreSetupEdit.tvSaveTXT.setOnClickListener(v->{
            String sub = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            TextToFileTxt.writeToFile(this,content,sub);
        });

        binding.acShowMoreSetupEdit.tvSaveImage.setOnClickListener(v->{
            String sub = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),colorBackground);
            boolean isLight = Constants.backGroundLight.contains(colorBackground);
            TextToFileTxt.saveTextToImage(this,content,sub,bitmap,isLight);
        });

        sheetColor.setOnSelectedColor(color -> {
            colorBackground = color;
            viewMainDetail.setBackgroundResource(color);
            setColorDetail(colorBackground,!isEditedContent);
        });

        viewEdtTextContentDetail.setOnClickListener(v -> {
            edtContent.requestFocus();
            ShowKey();
        });

        iv_addColor.setOnClickListener(v -> {
            hideKey();
            sheetColor.show(getSupportFragmentManager(), "bottomSheetColor");
        });

        tvChooseLabel.setOnClickListener(v -> {
            //logic
            //an ban phim ao
            hideKey();
            //logic show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialog = View.inflate(this, R.layout.dialog_choose_label, null);
            builder.setView(viewDialog);
            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            rcViewInDialog = viewDialog.findViewById(R.id.rcView);
            btnCreateNewLabel = viewDialog.findViewById(R.id.btnCreateNewLabel);
            acSave = viewDialog.findViewById(R.id.acSaveInDialog);
            acCancel = viewDialog.findViewById(R.id.acCancelInDialog);
            rcViewInDialog.setLayoutManager(new GridLayoutManager(this, 1));
            final String[] labelSelected = new String[1];
            labelSelected[0] = noteEdit.getLabel();

            if (rcViewInDialog.getAdapter() == null) {
                rcViewInDialog.setAdapter(selectLabelAdapter);
            }

            rcViewInDialog.post(() -> rcViewInDialog.requestLayout());//yc rcView ve lai view

            selectLabelAdapter.setOnClickToSelectLabel(labelSelect -> {
                if (labelSelect != null) {
                    labelSelected[0] = labelSelect.getLabelName();
                }
            });

            acCancel.setOnClickListener(click -> {
                dialog.dismiss();
            });

            acSave.setOnClickListener(click -> {
                if(!Objects.equals(noteEdit.getLabel(), labelSelected[0])){
                    tvChooseLabel.setText(labelSelected[0]);
                }
                dialog.dismiss();
            });

            //them moi nhan
            btnCreateNewLabel.setOnClickListener(click -> {
                //logic an dialog ngoai di
                dialog.dismiss();
                //logic show dialog input
                AlertDialog.Builder builderCreateNewLabel = new AlertDialog.Builder(this);
                View viewDialogCreateNewLabel = View.inflate(this, R.layout.layout_add_label, null);
                builderCreateNewLabel.setView(viewDialogCreateNewLabel);
                AlertDialog dialogCreateNewLabel = builderCreateNewLabel.create();
                Objects.requireNonNull(dialogCreateNewLabel.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogCreateNewLabel.setCancelable(true);
                dialogCreateNewLabel.setCanceledOnTouchOutside(true);

                //anh xa att ben trong
                edtInputNewLabel = viewDialogCreateNewLabel.findViewById(R.id.edtInputLabel);
                acCancelCreateNewLabel = viewDialogCreateNewLabel.findViewById(R.id.tvCancelCreateLabel);
                acSaveNewLabel = viewDialogCreateNewLabel.findViewById(R.id.tvSaveNewLabel);

                acCancelCreateNewLabel.setOnClickListener(c -> dialogCreateNewLabel.dismiss());
                acSaveNewLabel.setOnClickListener(c -> {
                    labelViewModel.insertLabel(new Label(edtInputNewLabel.getText().toString()));
                    dialogCreateNewLabel.dismiss();
                });

                dialogCreateNewLabel.show();
            });
            dialog.show();
        });

        //undo redo
        //click undo
        ivb_undo.setOnClickListener(v -> {
            if(helper.getCanUndo()){
                helper.undo();
            }
        });
        //click redo
        ivb_redo.setOnClickListener(v -> {
            if(helper.getCanRedo()){
                helper.redo();
            }
        });

        edtContent.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(s.length() > 0){
                    isEditedContent = true;
                }
                if(textPrevious.equals(s.toString())){
                    isEditedContent = false;
                    ivb_undo.setImageResource(R.drawable.ic_undo_none);
                    ivb_undo.setEnabled(false);
                }
                else {
                    iconUndoRedo();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if(s.length() > 0){
                    isEditedContent = true;
                }
                if(textPrevious.equals(s.toString())){
                    isEditedContent = false;
                    ivb_undo.setImageResource(R.drawable.ic_undo_none);
                    ivb_undo.setEnabled(false);
                }
                else {
                    iconUndoRedo();
                }
            }
        });

        //edit ngay gio
        tvDate.setOnClickListener(v -> setDateEdit(tvDate));

        iv_alarm.setOnClickListener(v ->{
            //hien thi alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialog = View.inflate(this,R.layout.dialog_create_reminder,null);
            builder.setView(viewDialog);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            //truoc do chua tung set Alarm
            if(Objects.equals(tmpTime, "") && Objects.equals(tmpDay, "")){
                tmpTime = Constants.getCurrentTime();
                tmpDay = Constants.getCurrentDay();
            }

            timeChoose = tmpTime;
            dayChoose = tmpDay;

            TextView setDate = viewDialog.findViewById(R.id.acSetDay);
            setDate.setText(dayChoose);
            TextView setTime = viewDialog.findViewById(R.id.acSetTime);
            setTime.setText(timeChoose);
            TextView btnSave = viewDialog.findViewById(R.id.acSave);
            TextView btnCancel = viewDialog.findViewById(R.id.acCancel);
            TextView acSetRepeat = viewDialog.findViewById(R.id.acSetRepeat);
            setTime.setOnClickListener(vw -> showTimePickerAlarm(setTime));
            setDate.setOnClickListener(vw -> showDatePickerAlarm(setDate));

            btnCancel.setOnClickListener(click ->{
                dialog.dismiss();
            });
            //dung
            //neu nhu thoi gian set bayh ma <= thoi gian hien tai thi khong the set

            /*
            nguyen nhan : khi nhan hai lan save ma ko thay doi thoi gian -> gay ra reset time TimeAlarm ve 0
            -> cach khac phuc : thay vi gan truc tiep calendarAlarm.getTimeInMillis(); cho timeAlarm thi chi gan khi nao thoa man dk -> tranh case gan nham
             */

            if(timeRepeat > 0){
                //trc do da cai dat nhac lai
                acSetRepeat.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_click_btn));
                acSetRepeat.setTextColor(Color.WHITE);
                viewDialog.findViewById(R.id.scrollRepeat).setVisibility(View.VISIBLE);
                isRepeat = true;
                onSetUpBackgroundRepeat(viewDialog,timeRepeat);
            }

            //khi cai lai time Repeat
            acSetRepeat.setOnClickListener(rp->{
                //logic here
                isRepeat = !isRepeat;
                if(isRepeat){
                    viewDialog.findViewById(R.id.scrollRepeat).setVisibility(View.VISIBLE);
                    acSetRepeat.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_click_btn));
                    acSetRepeat.setTextColor(Color.WHITE);

                    viewDialog.findViewById(R.id.tv15min).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv15min),timeRepeatSelect->{
                            timeRepeatTmp = timeRepeatSelect;
                        });
                    });
                    viewDialog.findViewById(R.id.tv30min).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv30min),timeRepeatSelect->{
                            timeRepeatTmp = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1hour).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1hour),timeRepeatSelect->{
                            timeRepeatTmp = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1day).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1day),timeRepeatSelect->{
                            timeRepeatTmp = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1week).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1week),timeRepeatSelect->{
                            timeRepeatTmp = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1month).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1month),timeRepeatSelect->{
                            timeRepeatTmp = timeRepeatSelect;
                        });
                    });
                }
                else{
                    viewDialog.findViewById(R.id.scrollRepeat).setVisibility(View.GONE);
                    acSetRepeat.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_time_repeat));
                    acSetRepeat.setTextColor(Color.GRAY);
                }
            });

            btnSave.setOnClickListener(click ->{
                long timeTMP = calendarAlarm.getTimeInMillis();
                long timeNow = System.currentTimeMillis();
                if(!isRepeat){
                    timeRepeatTmp = 0;//khong co nhac lai
                }
                timeRepeat = timeRepeatTmp;
                if(timeTMP > timeNow){
                    if(timeTMP != timePrevious && timeTMP != System.currentTimeMillis()){
                        timeAlarm = timeTMP;
                        timePrevious = timeTMP;
                        //timeRepeat = timeRepeatTmp;
                        noteEdit.setTimeAlarm(timeAlarm);//set -> luc nhan back se cap nhat sau
                        Constants.setUpAlarm(this,noteEdit,timeAlarm);
                    }
                }
                else if(timePrevious < System.currentTimeMillis() && timeRepeat == 0){
                    Constants.setCancelAlarm(this,noteEdit);
                }
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    private void onSetUpBackgroundRepeat(View viewGroup,long timeRepeat) {
        if(timeRepeat == Constants.fifteenMin){
            TextView tv = viewGroup.findViewById(R.id.tv15min);
            tv.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_click_btn));
            tv.setTextColor(Color.WHITE);
        }
        else if(timeRepeat == Constants.thirtyMin){
            TextView tv = viewGroup.findViewById(R.id.tv30min);
            tv.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_click_btn));
            tv.setTextColor(Color.WHITE);
        }
        else if(timeRepeat == Constants.oneHour){
            TextView tv = viewGroup.findViewById(R.id.tv1hour);
            tv.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_click_btn));
            tv.setTextColor(Color.WHITE);
        }
        else if(timeRepeat == Constants.oneDay) {
            TextView tv = viewGroup.findViewById(R.id.tv1day);
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_bg_click_btn));
            tv.setTextColor(Color.WHITE);
        }
        else if(timeRepeat == Constants.oneWeek) {
            TextView tv = viewGroup.findViewById(R.id.tv1week);
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_bg_click_btn));
            tv.setTextColor(Color.WHITE);
        }
        else if(timeRepeat == Constants.oneMonth) {
            TextView tv = viewGroup.findViewById(R.id.tv1month);
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_bg_click_btn));
            tv.setTextColor(Color.WHITE);
        }
    }

    private void showTimePickerAlarm(TextView setTime){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setHour(hour)
                .setMinute(minute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Chọn thời gian")
                .setTheme(R.style.CustomTimePicker)
                .build();

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                int hour = picker.getHour();
                int minute = picker.getMinute();

                calendarAlarm.set(Calendar.HOUR_OF_DAY,hour);
                calendarAlarm.set(Calendar.MINUTE,minute);
                calendarAlarm.set(Calendar.SECOND,0);
                calendarAlarm.set(Calendar.MILLISECOND,0);

                timeChoose = String.format("%02d:%02d",hour,minute);
                tmpTime = timeChoose;
                setTime.setText(timeChoose);
            }
        });
        picker.show(getSupportFragmentManager(),"timePicker");
    }
    private void showDatePickerAlarm(TextView setDate){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Chọn ngày")
                .setTheme(R.style.CustomDatePicker);
        MaterialDatePicker<Long> picker = builder.build();

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                //set ngay thang vao calenderAlarm
                calendarAlarm = calendar;
                dayChoose = String.format("%02d/%02d/%04d", day, month, year);
                tmpDay = dayChoose;
                setDate.setText(dayChoose);
            }
        });
        picker.show(getSupportFragmentManager(),"datePicker");
    }

    //set color view
    private void setColorDetail(int colorBackground,boolean stateFirst){
         if(Constants.backGroundLight.contains(colorBackground)){
            //set mau den
            ivBackToMain.setImageResource(R.drawable.ic_save_edit_b);//nut back
            iv_alarm.setImageResource(R.drawable.ic_alarm);//icon nhac nho
            iv_addColor.setImageResource(R.drawable.ic_color_24);//icon dat mau
            tvDate.setTextColor(Color.BLACK);
            iv_More.setImageResource(R.drawable.ic_more);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender,null);
            tvDate.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);

            tvChooseLabel.setTextColor(Color.BLACK);

            edtContent.setTextColor(Color.BLACK);
            edtTitle.setTextColor(Color.BLACK);
            edtTitle.setHintTextColor(Color.BLACK);

            if(!stateFirst){
                //co the undo hay khong
                if(helper.getCanUndo()){
                    ivb_undo.setImageResource(R.drawable.ic_undo_b);
                    ivb_undo.setEnabled(true);//bat su kien click
                }
                else{
                    ivb_undo.setImageResource(R.drawable.ic_undo_none);
                    ivb_undo.setEnabled(false);//khong bat su kien click
                }
                //co the redo hay khong
                if(helper.getCanRedo()){
                    ivb_redo.setImageResource(R.drawable.ic_redo_b);
                    ivb_redo.setEnabled(true);//bat su kien click
                }
                else{
                    ivb_redo.setImageResource(R.drawable.ic_redo_none);
                    ivb_redo.setEnabled(false);//khong bat su kien click
                }
            }
             @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item,null);
             tvChooseLabel.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);
        }

         else if(Constants.backGroundDark.contains(colorBackground)){
            //set mau trang
            ivBackToMain.setImageResource(R.drawable.ic_save_edit_w);//nut back
            iv_alarm.setImageResource(R.drawable.ic_alarm_w);//icon nhac nho
            iv_addColor.setImageResource(R.drawable.ic_color_w);//icon dat mau
            tvDate.setTextColor(Color.WHITE);
            iv_More.setImageResource(R.drawable.ic_more_w);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender_w,null);
            tvDate.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);

            tvChooseLabel.setTextColor(Color.WHITE);

            edtContent.setTextColor(Color.WHITE);
            edtTitle.setTextColor(Color.WHITE);
            edtTitle.setHintTextColor(Color.WHITE);

            if(!stateFirst) {
                //co the undo hay khong
                if (helper.getCanUndo()) {
                    ivb_undo.setImageResource(R.drawable.ic_undo_w);
                    ivb_undo.setEnabled(true);//bat su kien click
                } else {
                    ivb_undo.setImageResource(R.drawable.ic_undo_none);
                    ivb_undo.setEnabled(false);//khong bat su kien click
                }
                //co the redo hay khong
                if (helper.getCanRedo()) {
                    ivb_redo.setImageResource(R.drawable.ic_redo_w);
                    ivb_redo.setEnabled(true);//bat su kien click
                } else {
                    ivb_redo.setImageResource(R.drawable.ic_redo_none);
                    ivb_redo.setEnabled(false);//khong bat su kien click
                }
            }

             @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item_w,null);
             tvChooseLabel.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);
        }
    }
    @SuppressLint("DefaultLocale")
    private void onLoadingData() {
        Intent intent = getIntent();
        if(intent != null){
            noteEdit = intent.getParcelableExtra(Constants.KEY_NOTE);
            if(noteEdit != null){
                Log.e("ID",noteEdit.getId() + "");
                tvChooseLabel.setText(noteEdit.getLabel());
                edtTitle.setText(noteEdit.getTitle());
                edtContent.setText(noteEdit.getContent());
                textPrevious = noteEdit.getContent();
                Date date = new Date(noteEdit.getDate());
                timeAlarm = noteEdit.getTrigger();
                isFavorite = noteEdit.isFavorite();
                isPin = (noteEdit.isPin() == 1);//bien nay true hay false se phu thuoc vao dk isPin() co = 1 hay khong
                tvDate.setText(sdf.format(date));
                colorBackground = noteEdit.getColorBgID();
                viewMainDetail.setBackgroundResource(colorBackground);
                timeRepeat = SharePre.getInstance(this).getTimeRepeat(noteEdit.getId());
                setColorDetail(colorBackground,true);
                timePrevious = timeAlarm;
            }

            if(timeAlarm > 0 && timeAlarm > System.currentTimeMillis()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timeAlarm);
                //luu lai du lieu ngay h da cai dat truoc do
                tmpTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                tmpDay = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendarAlarm.get(Calendar.YEAR));
            }
            else{
                tmpTime = "";tmpDay = "";
            }
            timeChoose = tmpTime;
            dayChoose = tmpDay;
        }

        //lay tat cac cac nhan dang co trong database
        if (listLabel != null) {
            listLabel.removeObservers(this);
        }

        selectLabelAdapter = new SelectLabelAdapter();
        selectLabelAdapter.setLabelDetailList(new ArrayList<>());
        listLabel = Transformations.distinctUntilChanged(labelViewModel.getListLabels());//chi quan sat khi co su thay doi du lieu
        listLabel.observe(this, labels -> {
            labelDetailList = new ArrayList<>();
            List<Label> labelListCopy = new ArrayList<>(labels); // Lưu trữ danh sách nhãn ban đầu
            Map<String, Integer> labelCountMap = new HashMap<>(); // Lưu trữ số lượng ghi chú cho từng nhãn
            AtomicInteger completedCount = new AtomicInteger(0); // Đếm số lượng callback hoàn tất(ban dau khoi tao la 0)
            //toi hon int thong thuong (phu hop cho xu ly bat dong bo)
            if (labels.isEmpty()) {
                selectLabelAdapter.setLabelDetailList(labelDetailList);
                return;
            }
            for (Label label : labels) {
                noteViewModel.getCountNotes(label.getLabel()).observe(NoteDetail.this, integer -> {
                    labelCountMap.put(label.getLabel(), integer); // Lưu số lượng ghi chú
                    if (completedCount.incrementAndGet() == labels.size()) {//moi lan xong 1 call back thi completedCount se tang nen 1
                        // khi nay se hoan tat tat ca cac call back O(2n)
                        for (Label l : labelListCopy) {
                            Integer count = labelCountMap.get(l.getLabel());
                            if (count != null) {
                                labelDetailList.add(new LabelDetail(l.getLabel(), count));
                            }
                        }
                        selectLabelAdapter.setLabelDetailList(labelDetailList);
                        for(int i=0;i<labelDetailList.size();i++){
                            if(Objects.equals(labelDetailList.get(i).getLabelName(), tvChooseLabel.getText().toString())){
                                selectLabelAdapter.setSelectedPosition(i);
                            }
                        }
                    }
                });
            }
        });
    }
    private void initView() {
        edtTitle = findViewById(R.id.edtTitleIndT);
        edtContent = findViewById(R.id.edtContentIndT);
        tvDate = findViewById(R.id.tvDateIndT);
        ivBackToMain = findViewById(R.id.icBackToMainIndT);
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());//chuan hoa thoi gian
        iv_More = findViewById(R.id.ivMoreIndT);
        iv_alarm = findViewById(R.id.iv_alarmIndT);
        iv_addColor = findViewById(R.id.iv_addColorIndT);
        viewEdtTextContentDetail = findViewById(R.id.viewEdtTextContentDetail);
        viewMainDetail = findViewById(R.id.main_detail_view);
        sheetColor = new BottomSheetColor();
        tvChooseLabel = findViewById(R.id.choose_labelIndT);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        //lay ra trang thai read/edit mode
        isReadingMode = SharePre.getInstance(this).checkReadingMode();
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        ivb_redo = findViewById(R.id.ivbRedoInEdit);
        ivb_undo = findViewById(R.id.ivbUndoInEdit);
        calendarAlarm = Calendar.getInstance();

        ivb_undo.setImageResource(R.drawable.ic_undo_none);
        ivb_undo.setEnabled(false);//khong bat su kien click
        ivb_redo.setImageResource(R.drawable.ic_redo_none);
        ivb_redo.setEnabled(false);//khong bat su kien click
    }
    private void ShowKey(){
        //hien thi ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(edtContent,InputMethodManager.SHOW_IMPLICIT);
    }
    private void hideKey() {
        //an ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean isOpen = manager.isAcceptingText();//true dang o trang thai mo
        if (isOpen) {
            manager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }
    }

    private void iconUndoRedo(){
        //icon undo
        if(!helper.getCanUndo()) {
            ivb_undo.setImageResource(R.drawable.ic_undo_none);
            ivb_undo.setEnabled(false);//khong bat su kien click
        }
        else{
            ivb_undo.setEnabled(true);
            setIconUndoByColorBackground();
        }
        //icon redo
        if(!helper.getCanRedo()){
            ivb_redo.setImageResource(R.drawable.ic_redo_none);
            ivb_redo.setEnabled(false);//khong bat su kien click
        }
        else{
            ivb_redo.setEnabled(true);
            setIconRedoByColorBackground();
        }
    }

    private void setIconUndoByColorBackground(){
        if(Constants.backGroundLight.contains(colorBackground)){
            ivb_undo.setImageResource(R.drawable.ic_undo_b);
        }
        else if(Constants.backGroundDark.contains(colorBackground)){
            ivb_undo.setImageResource(R.drawable.ic_undo_w);
        }
    }

    private void setIconRedoByColorBackground(){
        if(Constants.backGroundLight.contains(colorBackground)){
            ivb_redo.setImageResource(R.drawable.ic_redo_b);
        }
        else if(Constants.backGroundDark.contains(colorBackground)){
            ivb_redo.setImageResource(R.drawable.ic_redo_w);
        }
    }

    private void setDateEdit(TextView tvDate){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Chọn ngày")
                .setTheme(R.style.CustomDatePicker);
        MaterialDatePicker<Long> picker = builder.build();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,second);
                dateChoose = sdf.format(calendar.getTime());
                tvDate.setText(dateChoose);
            }
        });
        picker.show(getSupportFragmentManager(),"datePicker");
    }

    // TODO: 7/15/2025
}
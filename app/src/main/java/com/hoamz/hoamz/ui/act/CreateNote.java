package com.hoamz.hoamz.ui.act;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.hoamz.hoamz.databinding.ActivityCreateNoteBinding;
import com.hoamz.hoamz.ui.fragment.BottomSheetColor;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.utils.TextViewUndoRedo;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
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
import java.util.function.Consumer;

public class CreateNote extends AppCompatActivity {
    private ImageView iv_backToMain,ivSetAlarm,ivAddColor;
    private TextView tv_choose_label;
    private EditText edtTitle;
    private EditText edtContent;
    private TextView tv_date;
    private SimpleDateFormat sdf;
    private ConstraintLayout viewEdtContent;
    private CoordinatorLayout viewMain;
    private LiveData<List<Label>> listLabel;
    private LabelViewModel labelViewModel;
    private NoteViewModel noteViewModel;
    private String timeChoose,dayChoose;
    private String dateCalender;
    private String currentLabel;
    private int colorBackground = R.drawable.img_15;
    private String dateChoose;
    private BottomSheetColor sheetColor;

    private Calendar calendarAlarm;
    private ImageButton ivb_undo,ivb_redo;
    private long timeAlarm = 0;
    private String tmpDay,tmpTime;
    private long timeRepeat = 0;
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
    /********************************/

    //undo  redo
    private TextViewUndoRedo helper;

    private ActivityCreateNoteBinding binding;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        helper = new TextViewUndoRedo(binding.edtContent);
        helper.setMaxHistorySize(200);
        initViews();//set date luon khi vua chuyen sang man hinh create note
        onClickItems();
        onGetData();
    }

    private void onGetData() {
        //lay date tu calenderDetail gui sang
        dateCalender = getIntent().getStringExtra(Constants.DATE_SELECTED);
        if(dateCalender != null){
            //co du lieu moi gan tuc la khi gui moi gan
            tv_date.setText(dateCalender);
        }

        //lay nhan tu main gui sang
        currentLabel = getIntent().getStringExtra(Constants.LABEL_CURRENT);
        if(currentLabel != null){
            tv_choose_label.setText(currentLabel);
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
                noteViewModel.getCountNotes(label.getLabel()).observe(CreateNote.this, integer -> {
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
                    }
                });
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale", "NewApi", "SetTextI18n"})
    private void onClickItems() {
        //back to main
        //done
        iv_backToMain.setOnClickListener(v ->{
            hideKey();//an ban phim ao
            String titleCheck = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            String title = (titleCheck.isEmpty()) ? edtTitle.getHint().toString() : titleCheck;
            String date = tv_date.getText().toString();//thoi gian tao note
            String label = tv_choose_label.getText().toString();
            long timestamp = 0;
            try {
                Date date1 = sdf.parse(date);
                if(date1 != null){
                    timestamp = date1.getTime();
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Note note = new Note(title, content, timestamp, timeAlarm, 0,false, label, colorBackground);
            noteViewModel.insertNewNote(note, id -> {
                note.setId(Math.toIntExact(id));
                // set notify ở đây!
                if (timeAlarm > 0) {
                    Constants.setUpAlarm(this, note, timeAlarm);
                    if (isRepeat) {
                        SharePre.getInstance(this).saveTimeRepeat(note.getId(), timeRepeat);
                    }
                }
                // back về main ở đây luôn cho chắc
                finish();
            });
        });

        //bat focus khi user click
        viewEdtContent.setOnClickListener(v ->{
            edtContent.requestFocus();
            ShowKey();
        });

        //alarm
        ivSetAlarm.setOnClickListener(v ->{
            //hien thi alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialog = View.inflate(this,R.layout.dialog_create_reminder,null);
            builder.setView(viewDialog);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

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
            TextView tvAcRepeat = viewDialog.findViewById(R.id.acSetRepeat);
            setTime.setOnClickListener(vw -> showTimePickerAlarm(setTime));
            setDate.setOnClickListener(vw -> showDatePickerAlarm(setDate));

            btnCancel.setOnClickListener(click ->{
                dialog.dismiss();
            });


            //khi cai lai time Repeat
            tvAcRepeat.setOnClickListener(rp->{
                //logic here
                isRepeat = !isRepeat;
                if(isRepeat){
                    viewDialog.findViewById(R.id.scrollRepeat).setVisibility(View.VISIBLE);
                    tvAcRepeat.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_click_btn));
                    tvAcRepeat.setTextColor(Color.WHITE);

                    viewDialog.findViewById(R.id.tv15min).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv15min),timeRepeatSelect->{
                            timeRepeat = timeRepeatSelect;
                        });
                    });
                    viewDialog.findViewById(R.id.tv30min).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv30min),timeRepeatSelect->{
                            timeRepeat = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1hour).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1hour),timeRepeatSelect->{
                            timeRepeat = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1day).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1day),timeRepeatSelect->{
                            timeRepeat = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1week).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1week),timeRepeatSelect->{
                            timeRepeat = timeRepeatSelect;
                        });
                    });

                    viewDialog.findViewById(R.id.tv1month).setOnClickListener(click->{
                        Constants.onBackgroundSelectTimeRepeat(viewDialog,viewDialog.findViewById(R.id.tv1month),timeRepeatSelect->{
                            timeRepeat = timeRepeatSelect;
                        });
                    });
                }
                else{
                    viewDialog.findViewById(R.id.scrollRepeat).setVisibility(View.GONE);
                    tvAcRepeat.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_bg_time_repeat));
                    tvAcRepeat.setTextColor(Color.GRAY);
                }
            });

            btnSave.setOnClickListener(click ->{
                timeAlarm = calendarAlarm.getTimeInMillis();
                if(timeAlarm < System.currentTimeMillis()){
                    timeAlarm = 0;
                }
                Toast.makeText(this, "Dat nhac nho thanh cong", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            dialog.show();
        });

        //su kien chon mau
        ivAddColor.setOnClickListener(v ->{
            //an ban phim
            hideKey();
            sheetColor.show(getSupportFragmentManager(),"bottomSheetColor");
        });

        sheetColor.setOnSelectedColor(color -> {
            colorBackground = color;
            if(Constants.backGroundLight.contains(colorBackground)){
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            else if(Constants.backGroundDark.contains(colorBackground)){
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
            viewMain.setBackgroundResource(color);
            setColorDetail(color);
        });//bat mau

//        edtContent.addTextChangedListener(new CustomTextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(isUndoRedoMode) return;//neu dang o trang thai undo hay redo thi khong theo doi thay doi
//                String currText = s.toString();
//                redoSt.clear();
//                iconUndoRedo();
//            }
//        });

        ivb_undo.setOnClickListener(v->{
            if(helper.getCanUndo()){
                helper.undo();
            }
        });

        ivb_redo.setOnClickListener(v ->{
            if(helper.getCanRedo()){
                helper.redo();
            }
        });

        //bat su kien click chon nhan
        tv_choose_label.setOnClickListener(v ->{
            //logic
            //an ban phim ao
            hideKey();
            //logic show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialog = View.inflate(this,R.layout.dialog_choose_label,null);
            builder.setView(viewDialog);
            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            rcViewInDialog = viewDialog.findViewById(R.id.rcView);
            btnCreateNewLabel = viewDialog.findViewById(R.id.btnCreateNewLabel);
            acSave = viewDialog.findViewById(R.id.acSaveInDialog);
            acCancel = viewDialog.findViewById(R.id.acCancelInDialog);
            rcViewInDialog.setLayoutManager(new GridLayoutManager(this,1));
            final String[] labelSelected = new String[1];

            if(rcViewInDialog.getAdapter() == null){
                rcViewInDialog.setAdapter(selectLabelAdapter);
            }

            rcViewInDialog.post(() -> rcViewInDialog.requestLayout());//yc rcView ve lai view

            selectLabelAdapter.setOnClickToSelectLabel(labelSelect -> {
                if(labelSelect != null){
                    labelSelected[0] = labelSelect.getLabelName();
                }
            });

            acCancel.setOnClickListener(click ->{
                dialog.dismiss();
            });

            acSave.setOnClickListener(click ->{
                tv_choose_label.setText(labelSelected[0]);
                dialog.dismiss();
            });

            //them moi nhan
            btnCreateNewLabel.setOnClickListener(click ->{
                //logic an dialog ngoai di
                dialog.dismiss();
                //logic show dialog input
                AlertDialog.Builder builderCreateNewLabel = new AlertDialog.Builder(this);
                View viewDialogCreateNewLabel = View.inflate(this,R.layout.layout_add_label,null);
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
                acSaveNewLabel.setOnClickListener(c ->{
                    labelViewModel.insertLabel(new Label(edtInputNewLabel.getText().toString()));
                    dialogCreateNewLabel.dismiss();
                });

                dialogCreateNewLabel.show();
            });
            dialog.show();
        });

        tv_date.setOnClickListener(v ->{
            setDateEdit(tv_date);
        });

        edtContent.addTextChangedListener(new CustomTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                iconUndoRedo();
            }
        });
    }
    //set color view
    private void setColorDetail(int colorBackground){

         if(Constants.backGroundLight.contains(colorBackground)){
            //set mau den
            iv_backToMain.setImageResource(R.drawable.ic_save_edit_b);//nut back
            ivSetAlarm.setImageResource(R.drawable.ic_alarm);//icon nhac nho
            ivAddColor.setImageResource(R.drawable.ic_color_24);//icon dat mau
            tv_date.setTextColor(Color.BLACK);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender,null);
            tv_date.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);

            tv_choose_label.setTextColor(Color.BLACK);

            edtContent.setTextColor(Color.BLACK);
            edtTitle.setTextColor(Color.BLACK);
            edtTitle.setHintTextColor(Color.GRAY);

            ivb_undo.setImageResource(R.drawable.ic_undo_b);
            ivb_redo.setImageResource(R.drawable.ic_redo_b);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item,null);
            tv_choose_label.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);

        }

         else if(Constants.backGroundDark.contains(colorBackground)){
            //set mau trang
            iv_backToMain.setImageResource(R.drawable.ic_save_edit_w);//nut back
            ivSetAlarm.setImageResource(R.drawable.ic_alarm_w);//icon nhac nho
            ivAddColor.setImageResource(R.drawable.ic_color_w);//icon dat mau
            tv_date.setTextColor(Color.WHITE);

            edtContent.setHintTextColor(ContextCompat.getColor(this,R.color.hint));

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender_w,null);
            tv_date.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);

            tv_choose_label.setTextColor(Color.WHITE);

            edtContent.setTextColor(Color.WHITE);
            edtTitle.setTextColor(Color.WHITE);
            edtTitle.setHintTextColor(Color.GRAY);

            ivb_undo.setImageResource(R.drawable.ic_undo_w);
            ivb_redo.setImageResource(R.drawable.ic_redo_w);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item_w,null);
            tv_choose_label.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);
        }

         iconUndoRedo();

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
    private void setDateEdit(TextView tvDate){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Chọn ngày")
                .setTheme(R.style.CustomDatePicker);
        MaterialDatePicker<Long> picker = builder.build();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);


        picker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(selection);
                calendar1.setFirstDayOfWeek(Calendar.MONDAY);
                calendar1.set(Calendar.HOUR_OF_DAY,hour);
                calendar1.set(Calendar.MINUTE,minute);
                calendar1.set(Calendar.SECOND,second);
                dateChoose = sdf.format(calendar1.getTime());
                tvDate.setText(dateChoose);
            });
        picker.show(getSupportFragmentManager(),"datePicker");
    }
    private void initViews() {
        iv_backToMain = findViewById(R.id.icBackToMain);
        tv_date = findViewById(R.id.tvDate);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        ivAddColor = findViewById(R.id.iv_addColor);
        viewEdtContent = findViewById(R.id.viewEdtTextContent);
        viewMain = findViewById(R.id.main_create_note);
        tv_choose_label = findViewById(R.id.choose_label);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        ivSetAlarm = findViewById(R.id.iv_alarm);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        sheetColor = new BottomSheetColor();
        ivb_undo = findViewById(R.id.btnUndo);
        ivb_redo = findViewById(R.id.btnRedo);
        iconUndoRedo();
        //lay ngay hom nay lam ngay mac dinh khi chua sua
        Calendar calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tv_date.setText(sdf.format(calendar.getTime()));//(ok)
        //khoi tao calenderAlarm de luu time nhac nho
        calendarAlarm = Calendar.getInstance();
        tmpDay = "";
        tmpTime = "";
    }
    private void ShowKey(){
        //hien thi ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(edtContent,InputMethodManager.SHOW_IMPLICIT);
    }
    private void hideKey() {
        //an ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean isOpen = manager.isAcceptingText();
        if (isOpen) {
            manager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }
    }
    private void iconUndoRedo(){
        //icon undo
        if(!helper.getCanUndo()){
            ivb_undo.setImageResource(R.drawable.ic_undo_none);
            ivb_undo.setEnabled(false);//khong bat su kien click(khong the undo)
        }
        else{
            ivb_undo.setEnabled(true);
            setIconUndoByColorBackground();
        }
        //icon redo
        if(!helper.getCanRedo()){
            ivb_redo.setImageResource(R.drawable.ic_redo_none);
            ivb_redo.setEnabled(false);//khong bat su kien click(khong the redo)
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

}
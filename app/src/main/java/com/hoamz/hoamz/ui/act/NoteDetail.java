package com.hoamz.hoamz.ui.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
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
import com.hoamz.hoamz.ui.fragment.BottomSheetColor;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean isReadingMode;
    private int colorBackground;
    private Note noteEdit;
    private NoteViewModel noteViewModel;
    private String contentUpdate,titleUpdate,labelUpdate;
    private boolean isFavorite;
    private boolean isPin;
    private LiveData<List<Label>> listLabel;
    private LabelViewModel labelViewModel;
    private long timeAlarm = 0;
    private long timeRepeat = 0;
    private final Deque<String> undoSt = new ArrayDeque<>();
    private final Deque<String> redoSt = new ArrayDeque<>();
    private boolean isUndoRedoMode = false;
    private static final int MAX_UNDO = 100;
    private ImageButton ivb_undo,ivb_redo;
    private String dateChoose;
    private String timeChoose,dayChoose;
    private Calendar calendarAlarm;
    private String tmpTime,tmpDay;
    private String edtPrevious = "";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_detail);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        onLoadingData();
        addUndo(edtPrevious);
        onClickListener();
    }

    @SuppressLint("NewApi")
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
            noteEdit.setRepeat(timeRepeat);
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
            if(timeAlarm > System.currentTimeMillis()){
                Constants.setUpAlarm(this,noteEdit,timeAlarm);//set alarm
            }
            //tat trang thai reading mode (trang thai chi nen tac dong den 1 note dang hien thi)
            if (!isReadingMode) {
                isReadingMode = true;
                SharePre.getInstance(this).saveReadingMode(isReadingMode);
            }
            finish();
        });//back ve act gan nhat (tren dinh stack)

        //show menu
        iv_More.setOnClickListener(v -> {
            ContextWrapper contextWrapper = new ContextThemeWrapper(this, R.style.CustomViewPopupMenu);
            PopupMenu popupMenu = new PopupMenu(contextWrapper, iv_More);
            popupMenu.getMenuInflater().inflate(R.menu.menu_detail_in_act_create, popupMenu.getMenu());

            Menu menu = popupMenu.getMenu();
            MenuItem menuItem = menu.findItem(R.id.ac_onlyRead);

            //bat vao item chi doc
            if (!isReadingMode) {
                //->dat title la Reading mode
                menuItem.setTitle(Constants.EDIT_MODE);

            } else {
                //-> dat title la Edit mode
                menuItem.setTitle(Constants.READING_MODE);
            }
            //bat vao item pin
            menuItem = menu.findItem(R.id.ac_pin);
            if(isPin){
                menuItem.setTitle(Constants.UNPIN);
            }else{
                menuItem.setTitle(Constants.PIN);
            }

            //bat vao item favorite
            menuItem = menu.findItem(R.id.ac_addFavorite);
            if(isFavorite){
                menuItem.setTitle(Constants.UN_FAVORITE);
            }else{
                menuItem.setTitle(Constants.FAVORITE);
            }

            //bat su kien click cho tung item
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                //click read/edit mode
                if (id == R.id.ac_onlyRead) {
                    //che do doc
                    if (isReadingMode) {
                        // -> chuyen sang che do doc
                        item.setTitle(Constants.EDIT_MODE);
                        edtContent.setEnabled(false);
                        edtTitle.setEnabled(false);
                    } else {
                        // -> chuyen sang che do chinh sua
                        item.setTitle(Constants.READING_MODE);
                        edtContent.setEnabled(true);
                        edtTitle.setEnabled(true);
                    }
                    //neu click se thay doi o day
                    isReadingMode = !isReadingMode;
                    SharePre.getInstance(this).saveReadingMode(isReadingMode);
                }

                //click share
                else if (id == R.id.ac_share) {
                    //logic
                }

                //click ac download
                else if (id == R.id.ac_download) {
                    //tai xuong
                }

                //thong tin chi tiet ve (ngay tao,thoi gian nhac nho,trang thai(yeu thich,hay ko) so tu..)
                else if (id == R.id.acDetail) {
                    //archiver
                }

                //them vao muc yeu thich
                else if (id == R.id.ac_addFavorite) {
                    //trang thai truoc
                    if (isFavorite) {
                        item.setTitle(Constants.FAVORITE);
                    } else {
                        item.setTitle(Constants.UN_FAVORITE);
                    }
                    //trang thai sau
                    isFavorite = !isFavorite;
                    noteEdit.setFavorite(isFavorite);//khi click 2 lan se doi trang thai
                }

                //ac delete
                else if (id == R.id.ac_delete) {
                    //xoa
                    noteViewModel.deleteNote(noteEdit);
                    finish();//xoa xong back ve main
                }

                //ac pin nen dau
                else if (id == R.id.ac_pin) {
                    //trang thai truoc
                    if (isPin) {
                        item.setTitle(Constants.PIN);
                    } else {
                        item.setTitle(Constants.UNPIN);
                    }
                    isPin = !isPin;
                    //trang thai sau
                    if (isPin) {
                        noteEdit.setPin(1);
                    } else {
                        noteEdit.setPin(0);
                    }
                }
                return false;
            });

            popupMenu.show();
        });

        sheetColor.setOnSelectedColor(color -> {
            colorBackground = color;
            viewMainDetail.setBackgroundColor(color);
            setColorDetail(colorBackground);
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
        edtContent.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isUndoRedoMode)
                    return;//neu dang o trang thai undo hay redo thi khong theo doi thay doi
                String currText = s.toString();
                addUndo(currText);
                redoSt.clear();
                iconUndoRedo();
            }
        });

        //click undo
        ivb_undo.setOnClickListener(v -> undo());
        //click redo
        ivb_redo.setOnClickListener(v -> redo());

        //edit ngay gio
        tvDate.setOnClickListener(v -> setDateEdit(tvDate));

        iv_alarm.setOnClickListener(v ->{
            //hien thi alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialog = View.inflate(this,R.layout.dialog_notification,null);
            builder.setView(viewDialog);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            if(Objects.equals(tmpTime, "") && Objects.equals(tmpDay, "")){
                tmpTime = getCurrentTime();
                tmpDay = getCurrentDay();
            }

            timeChoose = tmpTime;
            dayChoose = tmpDay;

            TextView setDate = viewDialog.findViewById(R.id.acSetDay);
            setDate.setText(dayChoose);
            TextView setTime = viewDialog.findViewById(R.id.acSetTime);
            setTime.setText(timeChoose);
            TextView btnSave = viewDialog.findViewById(R.id.acSave);
            TextView btnCancel = viewDialog.findViewById(R.id.acCancel);

            setTime.setOnClickListener(vw -> showTimePickerAlarm(setTime));
            setDate.setOnClickListener(vw -> showDatePickerAlarm(setDate));

            btnCancel.setOnClickListener(click ->{
                dialog.dismiss();
            });

            btnSave.setOnClickListener(click ->{
                timeAlarm = calendarAlarm.getTimeInMillis();
                if(timeAlarm < System.currentTimeMillis()){
                    timeAlarm = 0;
                }
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    @SuppressLint("DefaultLocale")
    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d",hour,minutes);
    }
    @SuppressLint("DefaultLocale")
    private String getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return String.format("%02d/%02d/%04d",day,month,year);
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

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<>() {
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
    private void setColorDetail(int colorBackground){
         if(Constants.colorLightPicker.contains(colorBackground)){
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

             ivb_undo.setImageResource(R.drawable.ic_undo_b);
             ivb_redo.setImageResource(R.drawable.ic_redo_b);

             @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item,null);
             tvChooseLabel.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);
        }

         else if(Constants.colorDarkPicker.contains(colorBackground)){
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

             ivb_undo.setImageResource(R.drawable.ic_undo_w);
             ivb_redo.setImageResource(R.drawable.ic_redo_w);

             @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item_w,null);
             tvChooseLabel.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);
        }
         iconUndoRedo();
    }
    @SuppressLint("DefaultLocale")
    private void onLoadingData() {
        Intent intent = getIntent();
        if(intent != null){
            noteEdit = intent.getParcelableExtra(Constants.KEY_NOTE);
            if(noteEdit != null){
                tvChooseLabel.setText(noteEdit.getLabel());
                edtTitle.setText(noteEdit.getTitle());
                edtContent.setText(noteEdit.getContent());
                edtPrevious = noteEdit.getContent();
                Date date = new Date(noteEdit.getDate());
                timeAlarm = noteEdit.getTrigger();
                isFavorite = noteEdit.isFavorite();
                isPin = (noteEdit.isPin() == 1);//bien nay true hay false se phu thuoc vao dk isPin() co = 1 hay khong
                tvDate.setText(sdf.format(date));
                colorBackground = noteEdit.getColorBgID();
                timeRepeat = noteEdit.getTimeRepeat();
                viewMainDetail.setBackgroundColor(colorBackground);
                setColorDetail(colorBackground);
            }

            if(timeAlarm > 0) {
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
    private void undo(){
        if(undoSt.size() <= 1) return;
        String currentText = undoSt.removeLast();
        redoSt.addLast(currentText);
        String previousText = undoSt.peekLast();
        isUndoRedoMode = true;//bat dau mode undo
        edtContent.setText(previousText);
        assert previousText != null;
        edtContent.setSelection(previousText.length());//dua con tro ve cuoi cau
        isUndoRedoMode = false;//ket thuc mode undo
        iconUndoRedo();
    }
    //method redo
    private void redo(){
        if(redoSt.size() <= 1) return;
        String currentText = redoSt.removeLast();
        undoSt.addLast(currentText);

        String nextText = redoSt.peekLast();
        if(nextText != null) {
            isUndoRedoMode = true;//bat dau mode redo
            edtContent.setText(nextText);
            edtContent.setSelection(nextText.length());//dua con tro ve cuoi cau
            isUndoRedoMode = false;//ket thuc mode redo
        }
        iconUndoRedo();
    }

    //add vao undoSt -> chi cho undo 100 lan
    private void addUndo(String text){
        if(undoSt.size() >= MAX_UNDO){
            undoSt.removeFirst();//xoa day duoi -> xoa trang thai cu nhat
        }
        undoSt.addLast(text);
    }

    private void iconUndoRedo(){
        //icon undo
        if(undoSt.size() <= 1) {
            ivb_undo.setImageResource(R.drawable.ic_undo_none);
            ivb_undo.setEnabled(false);//khong bat su kien click
        }
        else{
            ivb_undo.setEnabled(true);
            setIconUndoByColorBackground();
        }
        //icon redo
        if(redoSt.size() <= 1){
            ivb_redo.setImageResource(R.drawable.ic_redo_none);
            ivb_redo.setEnabled(false);//khong bat su kien click
        }
        else{
            ivb_redo.setEnabled(true);
            setIconRedoByColorBackground();
        }
    }

    private void setIconUndoByColorBackground(){
        if(Constants.colorLightPicker.contains(colorBackground)){
            ivb_undo.setImageResource(R.drawable.ic_undo_b);
        }
        else if(Constants.colorDarkPicker.contains(colorBackground)){
            ivb_undo.setImageResource(R.drawable.ic_undo_w);
        }
    }

    private void setIconRedoByColorBackground(){
        if(Constants.colorLightPicker.contains(colorBackground)){
            ivb_redo.setImageResource(R.drawable.ic_redo_b);
        }
        else if(Constants.colorDarkPicker.contains(colorBackground)){
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

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<>() {
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

    // TODO: 4/12/2025 :
}
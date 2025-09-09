package com.hoamz.hoamz.ui.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.PhotoAdapter;
import com.hoamz.hoamz.adapter.SelectLabelAdapter;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.databinding.ActivityCreateNoteBinding;
import com.hoamz.hoamz.ui.fragment.BottomSheetColor;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.utils.TextViewUndoRedo;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.PhotoViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CreateNote extends AppCompatActivity {
    private ImageView iv_backToMain,ivSetAlarm;
    private TextView tv_choose_label;
    private EditText edtTitle;
    private EditText edtContent;
    private TextView tv_date;
    private SimpleDateFormat sdf;
    private ConstraintLayout viewEdtContent;
    private boolean haveReminders = false;
    private CoordinatorLayout viewMain;
    private LiveData<List<Label>> listLabel;
    private LabelViewModel labelViewModel;
    private NoteViewModel noteViewModel;
    private Note noteTemp;
    private int colorBackground = R.drawable.img_12;
    private String dateChoose;
    private BottomSheetColor sheetColor;
    private ImageButton ivb_undo,ivb_redo;

    //att in dialog
    /********************************/
    private RecyclerView rcViewInDialog;
    private SelectLabelAdapter selectLabelAdapter;
    private TextView btnCreateNewLabel;
    private TextView acSave,acCancel;
    private List<LabelDetail> labelDetailList;
    private EditText edtInputNewLabel;
    private TextView acCancelCreateNewLabel,acSaveNewLabel;
    /********************************/
    //undo  redo
    private TextViewUndoRedo helper;
    private ActivityCreateNoteBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> chooseImage;
    private PhotoAdapter adapterPhoto;
    private boolean hasObservePhoto = false;
    private int idNoteSaved = -1;
    private boolean isSaved = false;//neu da luu roi thi set true

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
        String dateCalender = getIntent().getStringExtra(Constants.DATE_SELECTED);
        if(dateCalender != null){
            //co du lieu moi gan tuc la khi gui moi gan
            tv_date.setText(dateCalender);
        }

        //lay nhan tu main gui sang
        String currentLabel = getIntent().getStringExtra(Constants.LABEL_CURRENT);
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

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if(result.getResultCode() == 111){
                Intent intentData = result.getData();
                assert intentData != null;
                Uri uri = Uri.parse(intentData.getStringExtra("PhotoUri"));
                if(uri != null) {
                    if(idNoteSaved == -1){
                        saveNoteBeforePickingImage(uri);
                    } else {
                        savePhoto(uri);
                    }
                }
            }
        });

        chooseImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if(result.getResultCode() == RESULT_OK){
                Intent intent = result.getData();
                if(intent != null){
                    Uri uri = intent.getData();
                    if(uri != null){
                        if(idNoteSaved == -1){
                            saveNoteBeforePickingImage(uri);
                        } else {
                            savePhoto(uri);
                        }
                    }
                }
            }
        });

        if(binding.rcPhotos.getAdapter() == null){
            binding.rcPhotos.setAdapter(adapterPhoto);
        }
    }

    private void savePhoto(Uri uri) {
        if (idNoteSaved == -1) return;
        String path = uri.toString();
        Photo photo = new Photo(path, idNoteSaved);
        getPhotoViewModel().insertPhoto(photo,id ->{
            photo.setIdPhoto(Math.toIntExact(id));
            getPhotoViewModel().updatePhoto(photo);
        });
    }

    private void saveNoteBeforePickingImage(Uri uri) {
        saveNote(() -> savePhoto(uri));
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale", "NewApi", "SetTextI18n"})
    private void onClickItems() {
        //back to main
        //done
        iv_backToMain.setOnClickListener(v ->{
            if(!isSaved && idNoteSaved == -1){
                saveNote(() ->{
                    //neu nhu luu o day chung to chua dat nhac nho -> reminder = false
                });
            }
            else{
                //update
                getReminderViewModel().getAllRemindersByIdNote(idNoteSaved).observe(this,list ->{
                    haveReminders = list != null && !list.isEmpty();
                });
                updateNote();
            }
            hideKey();
            finish();
        });

        //bat focus khi user click
        viewEdtContent.setOnClickListener(v ->{
            edtContent.requestFocus();
            ShowKey();
        });

        ivSetAlarm.setOnClickListener(v ->{
            //check permission
            if(ActivityCompat.checkSelfPermission(this, Arrays.toString(new String[]{Manifest.permission.POST_NOTIFICATIONS})) != PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requirePermissionNotify();
                }
            };
            ivSetAlarm.setEnabled(false);
            if(!isSaved) {
                saveNote(() -> {
                    LiveData<List<Reminder>> liveData = getReminderViewModel().getAllRemindersByIdNote(idNoteSaved);
                    liveData.observe(this,list ->{
                        DialogUtils.ActionManagerReminder2(this,getSupportFragmentManager(),noteTemp,list,getReminderViewModel(),() ->{
                            getReminderViewModel().getAllRemindersByIdNote(idNoteSaved).observe(this,listRmd ->{
                                haveReminders = listRmd != null && !listRmd.isEmpty();
                                noteTemp.setTimeAlarm(haveReminders);
                                updateNote();
                                getReminderViewModel().getAllRemindersByIdNote(idNoteSaved).removeObservers(this);
                            });
                        });
                        liveData.removeObservers(this);
                    });
                });
            }
            else{
                LiveData<List<Reminder>> liveData = getReminderViewModel().getAllRemindersByIdNote(idNoteSaved);
                liveData.observe(this,list ->{
                    DialogUtils.ActionManagerReminder2(this,getSupportFragmentManager(),noteTemp,list,getReminderViewModel(),()->{
                        getReminderViewModel().getAllRemindersByIdNote(idNoteSaved).observe(this,listRmd ->{
                            haveReminders = listRmd != null && !listRmd.isEmpty();
                            noteTemp.setTimeAlarm(haveReminders);
                            updateNote();
                            getReminderViewModel().getAllRemindersByIdNote(idNoteSaved).removeObservers(this);
                        });
                    });
                    liveData.removeObservers(this);
                });
            }
            new Handler(Looper.getMainLooper()).postDelayed(() ->{ivSetAlarm.setEnabled(true);},500);
        });

        binding.acShowMoreSetupBottomBar.ivSetColorBg.setOnClickListener(v ->{
            //an ban phim
            hideKey();
            if(!sheetColor.isAdded()){
                sheetColor.show(getSupportFragmentManager(),"bottomSheetColor");
            }
        });

        binding.acShowMoreSetupBottomBar.ivAddPhoto.setOnClickListener(v ->{
            DialogUtils.showAddImage(this, action ->{
                Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
                if(action.equals(Constants.TAKE_PHOTO)){
                    Intent intent = new Intent(CreateNote.this, TakePhoto.class);
                    launcher.launch(intent);
                }
                else if(action.equals(Constants.CHOOSE_IMAGE)){
                    //logic
                    //mo thu muc anh cua user
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    chooseImage.launch(intent);
                }
            });
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
                    labelViewModel.insertLabel(new Label(edtInputNewLabel.getText().toString()),state ->{
                        Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
                    });
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

        adapterPhoto.setOnClickPhoto(photo ->{
            Intent intent = new Intent(this, ShowImage.class);
            intent.putExtra("photo___",photo);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        if(isSaved){
            updateNote();
        }
        super.onBackPressed();
    }

    private void saveNote(Runnable onSavedCallback){
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
        Note note = new Note(title, content, timestamp,haveReminders, 0,false,false,false, label, colorBackground,-1);
        noteTemp = note;
        noteViewModel.insertNewNote(note, id -> {
            note.setId(Math.toIntExact(id));
            noteViewModel.updateNote(note,state ->{});
            isSaved = true;
            idNoteSaved = note.getId();
            runOnUiThread(() ->{
                if(!hasObservePhoto){
                    hasObservePhoto = true;
                    getPhotoViewModel().getAllPhotosByIdNote(idNoteSaved).observe(this,list ->{
                        if(list != null){
                            adapterPhoto.setPhotoList(list);
                        }
                    });
                    if (onSavedCallback != null) {
                        onSavedCallback.run();  // gọi callback sau khi lưu xong
                    }
                }
            });
        });
    }

    private void updateNote(){
        if(noteTemp == null) return;
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
        noteTemp.setTitle(title);
        noteTemp.setColorBgID(colorBackground);
        noteTemp.setContent(content);
        noteTemp.setLabel(label);
        noteTemp.setDate(timestamp);
        noteTemp.setTimeAlarm(haveReminders);
        noteViewModel.updateNote(noteTemp,state ->{});
    }

    //set color view
    private void setColorDetail(int colorBackground){

         if(Constants.backGroundLight.contains(colorBackground)){
            //set mau den
            iv_backToMain.setImageResource(R.drawable.ic_save_edit_b);//nut back
            ivSetAlarm.setImageResource(R.drawable.ic_alarm);//icon nhac nho
            tv_date.setTextColor(Color.BLACK);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender,null);
            tv_date.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            tv_choose_label.setTextColor(Color.BLACK);

            edtContent.setTextColor(Color.BLACK);
            edtTitle.setTextColor(Color.BLACK);
//            edtTitle.setHintTextColor(Color.GRAY);

            ivb_undo.setImageResource(R.drawable.ic_undo_b);
            ivb_redo.setImageResource(R.drawable.ic_redo_b);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item,null);
            tv_choose_label.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);

        }

         else if(Constants.backGroundDark.contains(colorBackground)){
            //set mau trang
            iv_backToMain.setImageResource(R.drawable.ic_save_edit_w);//nut back
            ivSetAlarm.setImageResource(R.drawable.ic_alarm_w);//icon nhac nho
            tv_date.setTextColor(Color.WHITE);

            edtContent.setHintTextColor(ContextCompat.getColor(this,R.color.hint));

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender_w,null);
            tv_date.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            tv_choose_label.setTextColor(Color.WHITE);

            edtContent.setTextColor(Color.WHITE);
            edtTitle.setTextColor(Color.WHITE);
            edtTitle.setHintTextColor(getResources().getColor(R.color.hint,null));

            ivb_undo.setImageResource(R.drawable.ic_undo_w);
            ivb_redo.setImageResource(R.drawable.ic_redo_w);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMoreItem = getResources().getDrawable(R.drawable.ic_more_item_w,null);
            tv_choose_label.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableMoreItem,null);
        }

         iconUndoRedo();

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
        viewEdtContent = findViewById(R.id.viewEdtTextContent);
        viewMain = findViewById(R.id.main_create_note);
        tv_choose_label = findViewById(R.id.choose_label);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        ivSetAlarm = findViewById(R.id.iv_alarm);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        ivb_undo = findViewById(R.id.btnUndo);
        ivb_redo = findViewById(R.id.btnRedo);
        iconUndoRedo();
        sheetColor = new BottomSheetColor();
        //lay ngay hom nay lam ngay mac dinh khi chua sua
        Calendar calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tv_date.setText(sdf.format(calendar.getTime()));//(ok)
        binding.rcPhotos.setLayoutManager(new GridLayoutManager(this, 2));//hai cot
        adapterPhoto = new PhotoAdapter();
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
    private ReminderViewModel getReminderViewModel(){
        return new ViewModelProvider(this).get(ReminderViewModel.class);
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
    private PhotoViewModel getPhotoViewModel(){
        return new ViewModelProvider(this).get(PhotoViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requirePermissionNotify() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},1);
    }

    private void ShowKey() {
        if (edtContent != null) {
            edtContent.requestFocus();
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.showSoftInput(edtContent, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
    private void hideKey() {
        //an ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null && manager.isAcceptingText()) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}
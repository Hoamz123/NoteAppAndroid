package com.hoamz.hoamz.ui.act;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.camera.core.processing.SurfaceProcessorNode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.PhotoAdapter;
import com.hoamz.hoamz.adapter.SelectLabelAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.databinding.ActivityNoteDetailBinding;
import com.hoamz.hoamz.ui.fragment.BottomSheetColor;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.CameraUtils;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.utils.FileUtils;
import com.hoamz.hoamz.utils.TextViewUndoRedo;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.PhotoViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NoteDetail extends AppCompatActivity {
    private EditText edtTitle,edtContent;
    private ImageView ivBackToMain,iv_More,iv_alarm;
    private TextView tvDate,tvChooseLabel;
    private ConstraintLayout viewEdtTextContentDetail;
    private CoordinatorLayout viewMainDetail;
    private SimpleDateFormat sdf;
    private BottomSheetColor sheetColor;
    private PhotoAdapter photoAdapter;
    private boolean isReadingMode = false;
    private boolean isEditedContent = false;
    private int colorBackground;
    private Note noteEdit;
    private List<Label> listAllLabel = new ArrayList<>();
    private NoteViewModel noteViewModel;
    private String contentUpdate,titleUpdate,labelUpdate;
    private boolean isFavorite;
    private boolean isPin;
    private boolean isShowMoreOption = false;
    private boolean isArchive = false;
    private LiveData<List<Label>> listLabel;
    private LabelViewModel labelViewModel;
    private ImageButton ivb_undo,ivb_redo;
    private String dateChoose;
    private RecyclerView rcViewInDialog;
    private SelectLabelAdapter selectLabelAdapter;
    private TextView btnCreateNewLabel;
    private TextView acSave,acCancel;
    private List<LabelDetail> labelDetailList;
    private int idNote = -1;
    private String textPrevious = "";
    private ActivityNoteDetailBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> chooseImage;
    private TextViewUndoRedo helper;
    private String action = "";
    private boolean isHaveReminder = false;//default la khong set nhac nho


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        onLoadingData();
        onClickListener();
    }

    private void updateNote(){
        //cap nhat lai noi dung moi
        contentUpdate = edtContent.getText().toString();
        titleUpdate = edtTitle.getText().toString();
        if(titleUpdate.isEmpty()){
            titleUpdate = "Title";
        }
        labelUpdate = tvChooseLabel.getText().toString();
        noteEdit.setContent(contentUpdate);
        noteEdit.setTitle(titleUpdate);
        noteEdit.setLabel(labelUpdate);
        noteEdit.setTimeAlarm(isHaveReminder);
        getReminderViewModel().getAllRemindersByIdNote(noteEdit.getId()).observe(this,list ->{
            isHaveReminder = list != null && !list.isEmpty();
            noteEdit.setTimeAlarm(isHaveReminder);
            getReminderViewModel().getAllRemindersByIdNote(noteEdit.getId()).removeObservers(this);
        });

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
        noteViewModel.updateNote(noteEdit,state ->{});
    }

    @SuppressLint({"NewApi", "ResourceAsColor", "SetTextI18n"})
    private void onClickListener() {
        ivBackToMain.setOnClickListener(v -> {
            //cap nhat lai noi dung moi
            contentUpdate = edtContent.getText().toString();
            titleUpdate = edtTitle.getText().toString();
            if(titleUpdate.isEmpty()){
                titleUpdate = "Title";
            }
            labelUpdate = tvChooseLabel.getText().toString();
            noteEdit.setContent(contentUpdate);
            noteEdit.setTitle(titleUpdate);
            noteEdit.setLabel(labelUpdate);
            noteEdit.setTimeAlarm(isHaveReminder);
            String date = tvDate.getText().toString();
            try {
                Date date1 = sdf.parse(date);
                if(date1 != null) {
                    noteEdit.setDate(date1.getTime());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            //update
            LiveData<List<Reminder>> listLiveData = getReminderViewModel().getAllRemindersByIdNote(idNote);
            listLiveData.observe(this,list ->{
                isHaveReminder = list != null && !list.isEmpty();
                noteEdit.setTimeAlarm(isHaveReminder);
                listLiveData.removeObservers(this);
            });
            //set lai mau -> save lai vao Room
            noteEdit.setColorBgID(colorBackground);
            noteViewModel.updateNote(noteEdit,state ->{});
            if(action != null && action.equals("notify")){
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else finish();
        });
        //back ve act gan nhat (tren dinh stack)
        //show more option
        binding.ivMoreIndT.setOnClickListener(v->{
            hideKey();//an ban phim ao neu dang bat
            if(isShowMoreOption){
                binding.constrainMoreOption.setVisibility(View.INVISIBLE);
            }
            else{
                binding.constrainMoreOption.setVisibility(View.VISIBLE);
            }
            isShowMoreOption = !isShowMoreOption;
            if (isPin) {
                binding.acShowMoreSetupEdit.llPin.setText(Constants.UNPIN);
            } else {
                binding.acShowMoreSetupEdit.llPin.setText(Constants.PIN);
            }
            if (isFavorite) {
                binding.acShowMoreSetupEdit.llFavorite.setText(Constants.UN_FAVORITE);
            } else {
                binding.acShowMoreSetupEdit.llFavorite.setText(Constants.FAVORITE);
            }
            if(isArchive){
                binding.acShowMoreSetupEdit.llArchive.setText(Constants.UN_ARCHIVE);
            }
            else{
                binding.acShowMoreSetupEdit.llArchive.setText(Constants.ARCHIVE);
            }
        });

        binding.constrainMoreOption.setOnClickListener(v ->{
            if(isShowMoreOption){
                binding.constrainMoreOption.setVisibility(View.INVISIBLE);
                isShowMoreOption = false;
            }
        });

        //PIN
        binding.acShowMoreSetupEdit.llPin.setOnClickListener(v ->{
            isPin = !isPin;
            //trang thai sau
            if (isPin) {
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_unpin_fn,null);
                binding.acShowMoreSetupEdit.llPin.setText(Constants.UNPIN);
                binding.acShowMoreSetupEdit.llPin.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                noteEdit.setPin(1);
                Toast.makeText(this, "Pinned successfully", Toast.LENGTH_SHORT).show();
            } else {
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_pin_fn,null);
                binding.acShowMoreSetupEdit.llPin.setText(Constants.PIN);
                binding.acShowMoreSetupEdit.llPin.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                noteEdit.setPin(0);
                Toast.makeText(this, "Unpinned successfully", Toast.LENGTH_SHORT).show();
            }
        });

        //Favorite
        binding.acShowMoreSetupEdit.llFavorite.setOnClickListener(v ->{
            isFavorite = !isFavorite;
            if (isFavorite) {
                binding.acShowMoreSetupEdit.llFavorite.setText(Constants.UN_FAVORITE);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                binding.acShowMoreSetupEdit.llFavorite.setText(Constants.FAVORITE);
                Toast.makeText(this, "Removed to favorites", Toast.LENGTH_SHORT).show();
            }
            noteEdit.setFavorite(isFavorite);//khi click 2 lan se doi trang thai
        });

        //Delete
        binding.acShowMoreSetupEdit.llDelete.setOnClickListener(v ->{
            //hien thi log thong bao xoa isDeleted = true
            DialogUtils.ActionOnLongClickNote(this,Constants.DELETE, isAccept -> {
                if(isAccept){
                    noteEdit.setDeleted(true);//coi nhu da xoa
                    noteEdit.setTimeDeleteNote(System.currentTimeMillis());
                    noteViewModel.updateNote(noteEdit,state ->{});
                    deleteAllReminder(noteEdit);
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();//back ve home
                }
            });
        });

        //Reading/Edit mode
        binding.acShowMoreSetupEdit.llReadingMode.setOnClickListener(v ->{
            isReadingMode = !isReadingMode;
            if(isReadingMode){
                //hien thi text edit mode
                binding.acShowMoreSetupEdit.llReadingMode.setText(Constants.EDIT_MODE);
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_edit_fn,null);
                binding.acShowMoreSetupEdit.llReadingMode.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.viewEdtTextContentDetail.setEnabled(false);
                binding.edtContentIndT.setEnabled(false);
                binding.edtTitleIndT.setEnabled(false);
                Toast.makeText(this, "Reading mode on", Toast.LENGTH_SHORT).show();
            }
            else{
                //hien thi text Reading mode
                binding.acShowMoreSetupEdit.llReadingMode.setText(Constants.READING_MODE);
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.book_svgrepo_com,null);
                binding.acShowMoreSetupEdit.llReadingMode.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                binding.viewEdtTextContentDetail.setEnabled(true);
                binding.edtContentIndT.setEnabled(true);
                binding.edtTitleIndT.setEnabled(true);
                Toast.makeText(this, "Editing mode on", Toast.LENGTH_SHORT).show();
            }
        });

        //Share
        binding.acShowMoreSetupEdit.llShare.setOnClickListener(v ->{
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
        binding.acShowMoreSetupEdit.llSaveTXT.setOnClickListener(v->{
            String sub = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            FileUtils.writeToFile(getApplicationContext(),content,sub);
        });

        //luu note dang image
        binding.acShowMoreSetupEdit.llSaveImage.setOnClickListener(v->{
            String sub = edtTitle.getText().toString();
            String content = edtContent.getText().toString();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),colorBackground);
            boolean isLight = Constants.backGroundLight.contains(colorBackground);
            FileUtils.saveTextToImage(getApplicationContext(),content,sub,bitmap,isLight);
        });

        //archive note
        binding.acShowMoreSetupEdit.llArchive.setOnClickListener(v ->{
            if(isArchive){
                binding.acShowMoreSetupEdit.llArchive.setText(Constants.ARCHIVE);
                isArchive = false;
            }
            else{
                DialogUtils.ActionOnLongClickNote(this,Constants.ARCHIVE, isAccept -> {
                    if(isAccept){
                        noteEdit.setArchived(true);
                        noteViewModel.updateNote(noteEdit,state ->{});
                        finish();
                        Toast.makeText(this, "Notes archived successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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

        binding.acShowMoreSetupBottomBarEdit.ivSetColorBg.setOnClickListener(v -> {
            hideKey();
            if(!sheetColor.isAdded()){
                sheetColor.show(getSupportFragmentManager(), "bottomSheetColor");
            }
        });

        binding.acShowMoreSetupBottomBarEdit.ivAddPhoto.setOnClickListener(v ->{
            DialogUtils.showAddImage(this, action ->{
                if(action.equals(Constants.TAKE_PHOTO)){
                    Intent intent = new Intent(this, TakePhoto.class);
                    launcher.launch(intent);
                }
                else{
                    //logic
                    //mo thu muc anh cua user
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    chooseImage.launch(intent);
                }
            });
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
            dialog.setCanceledOnTouchOutside(false);
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
                DialogUtils.showDialogLabel(this, Constants.TitleCreateNewLabel, null, listAllLabel, label -> {
                    labelViewModel.insertLabel(new Label(label),state ->{
                        Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
                    });
                });
            });
            dialog.show();
        });
        //undo redo
        //click undo
        ivb_undo.setOnClickListener(v -> {
            if(!v.isEnabled()) return;
            if(helper.getCanUndo()){
                helper.undo();
            }
        });
        //click redo
        ivb_redo.setOnClickListener(v -> {
            if(!v.isEnabled()) return;
            if(helper.getCanRedo()){
                helper.redo();
            }
        });

        edtContent.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(textPrevious.equals(s.toString())){
                    isEditedContent = false;
                    ivb_undo.setImageResource(R.drawable.ic_undo_none);
                    ivb_undo.setEnabled(false);
                    return;
                }
                if(!isEditedContent){
                    isEditedContent = true;
                    helper = new TextViewUndoRedo(binding.edtContentIndT);
                    helper.setMaxHistorySize(200);
                    iconUndoRedo();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
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
            iv_alarm.setEnabled(false);
            LiveData<List<Reminder>> listLiveData = getReminderViewModel().getAllRemindersByIdNote(idNote);
            listLiveData.observe(this,list ->{
                DialogUtils.ActionManagerReminder2(
                        NoteDetail.this,
                        getSupportFragmentManager(),
                        noteEdit,
                        list,
                        getReminderViewModel(),
                        this::updateNote
                );
                listLiveData.removeObservers(this);
            });
            new Handler().postDelayed(() ->{iv_alarm.setEnabled(true);},500);
        });

        photoAdapter.setOnClickPhoto(photo ->{
            Intent intent = new Intent(this, ShowImage.class);
            intent.putExtra("photo___",photo);
            startActivity(intent);
        });
    }


    private void deleteAllReminder(Note note){
        LiveData<List<Reminder>> reminderLiveData = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(note.getId()));
        reminderLiveData.observe(this, reminderList -> {
            if(reminderList != null){
                for(Reminder reminder : reminderList){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this,
                            reminder.getIdReminder(),
                            new Intent(this, MyBroadCastReminder.class),
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    getReminderViewModel().deleteReminder(reminder);
                    AlarmUtils.getInstance().setCancelAlarm(NoteDetail.this, pendingIntent);
                }
                reminderLiveData.removeObservers(this);
            }
        });
    }


    //set color view
    private void setColorDetail(int colorBackground,boolean stateFirst){
         if(Constants.backGroundLight.contains(colorBackground)){
            //set mau den
            ivBackToMain.setImageResource(R.drawable.ic_save_edit_b);//nut back
            iv_alarm.setImageResource(R.drawable.ic_alarm);//icon nhac nho
            tvDate.setTextColor(Color.BLACK);
            iv_More.setImageResource(R.drawable.ic_more);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender,null);
            tvDate.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            tvChooseLabel.setTextColor(Color.BLACK);

            edtContent.setTextColor(Color.BLACK);
            edtTitle.setTextColor(Color.BLACK);

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
            tvDate.setTextColor(Color.WHITE);
            iv_More.setImageResource(R.drawable.ic_more_w);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_calender_w,null);
            tvDate.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            tvChooseLabel.setTextColor(Color.WHITE);

            edtContent.setTextColor(Color.WHITE);
            edtTitle.setTextColor(Color.WHITE);
            edtTitle.setHintTextColor(getResources().getColor(R.color.hint,null));

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
            int id = intent.getIntExtra(Constants.KEY_NOTE,0);
            action = intent.getAction();
            idNote = id;
            LiveData<Note> observeNote = Transformations.distinctUntilChanged(noteViewModel.getNoteById(id));
            observeNote.observe(this,note ->{
                noteEdit = note;
                if(noteEdit != null){
                    tvChooseLabel.setText(noteEdit.getLabel());
                    edtTitle.setText(noteEdit.getTitle());
                    edtContent.setText(noteEdit.getContent());
                    textPrevious = noteEdit.getContent();
                    Date date = new Date(noteEdit.getDate());
                    isArchive = noteEdit.isArchived();
                    isHaveReminder = noteEdit.isHaveReminder();//xem trc do co dat nhac nho hay khong
                    isFavorite = noteEdit.isFavorite();
                    isPin = (noteEdit.isPin() == 1);//bien nay true hay false se phu thuoc vao dk isPin() co = 1 hay khong
                    tvDate.setText(sdf.format(date));
                    colorBackground = noteEdit.getColorBgID();
                    viewMainDetail.setBackgroundResource(colorBackground);
                    setColorDetail(colorBackground,true);
                    //lay danh sach cac reminder da set cho note nay
                    LiveData<List<Reminder>> listReminder = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(id));
                    listReminder.observe(this, reminderList -> {
                        isHaveReminder = reminderList != null && !reminderList.isEmpty();
                        listReminder.removeObservers(this);
                    });
                    observeNote.removeObservers(this);//huy observe
                }
            });
        }

        LiveData<List<Photo>> listPhotoLiveData = Transformations.distinctUntilChanged(getPhotoViewModel().getAllPhotosByIdNote(idNote));
        listPhotoLiveData.observe(this,listPhotos ->{
            if(listPhotos != null){
                binding.rcPhotosDetail.setVisibility(View.VISIBLE);
                photoAdapter.setPhotoList(listPhotos);
            }
            else {
                binding.rcPhotosDetail.setVisibility(View.INVISIBLE);
            }
        });

        //lay anh tu camera -> byte[]
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if(result.getResultCode() == 111){
                Intent intentData = result.getData();
                assert intentData != null;
                Uri uri = Uri.parse(intentData.getStringExtra("PhotoUri"));
                if(uri != null){
                    Photo photo = new Photo(uri.toString(),idNote);
                    getPhotoViewModel().insertPhoto(photo,id ->{
                        photo.setIdPhoto(Math.toIntExact(id));
                    });
                }
            }
        });


        chooseImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if(result.getResultCode() == RESULT_OK){
                Intent intent_ = result.getData();
                if(intent_ != null){
                    Uri uri = intent_.getData();
                    if(uri != null){
                        Photo photo = new Photo(uri.toString(),idNote);
                        getPhotoViewModel().insertPhoto(photo,id ->{
                            photo.setIdPhoto(Math.toIntExact(id));
                        });
                    }
                }
            }
        });

        if(binding.rcPhotosDetail.getAdapter() == null){
            binding.rcPhotosDetail.setAdapter(photoAdapter);
        }

        //lay tat cac cac nhan dang co trong database
        if (listLabel != null) {
            listLabel.removeObservers(this);
        }

        selectLabelAdapter = new SelectLabelAdapter();
        selectLabelAdapter.setLabelDetailList(new ArrayList<>());
        listLabel = Transformations.distinctUntilChanged(labelViewModel.getListLabels());//chi quan sat khi co su thay doi du lieu
        listLabel.observe(this, labels -> {
            listAllLabel = labels;
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
        sheetColor = new BottomSheetColor();
        viewEdtTextContentDetail = findViewById(R.id.viewEdtTextContentDetail);
        viewMainDetail = findViewById(R.id.main_detail_view);
        tvChooseLabel = findViewById(R.id.choose_labelIndT);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        //lay ra trang thai read/edit mode
        isReadingMode = SharePre.getInstance(this).checkReadingMode();
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        ivb_redo = findViewById(R.id.ivbRedoInEdit);
        ivb_undo = findViewById(R.id.ivbUndoInEdit);
        ivb_undo.setImageResource(R.drawable.ic_undo_none);
        ivb_undo.setEnabled(false);//khong bat su kien click
        ivb_redo.setImageResource(R.drawable.ic_redo_none);
        ivb_redo.setEnabled(false);//khong bat su kien click

        photoAdapter = new PhotoAdapter();
        binding.rcPhotosDetail.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private ReminderViewModel getReminderViewModel(){
        return new ViewModelProvider(this).get(ReminderViewModel.class);
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

    private PhotoViewModel getPhotoViewModel(){
        return new ViewModelProvider(this).get(PhotoViewModel.class);
    }

    @Override
    public void onBackPressed() {
        updateNote();
        if(action != null && action.equals("notify")) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        action = intent.getAction();
        if(action != null && action.equals("notify")){
            int id = intent.getIntExtra(Constants.KEY_NOTE,-1);
            if(id != -1){
                idNote = id;
                LiveData<Note> observeNote = Transformations.distinctUntilChanged(noteViewModel.getNoteById(id));
                observeNote.observe(this,note ->{
                    noteEdit = note;
                    if(noteEdit != null){
                        tvChooseLabel.setText(noteEdit.getLabel());
                        edtTitle.setText(noteEdit.getTitle());
                        edtContent.setText(noteEdit.getContent());
                        textPrevious = noteEdit.getContent();
                        Date date = new Date(noteEdit.getDate());
                        isHaveReminder = noteEdit.isHaveReminder();//xem trc do co dat nhac nho hay khong
                        isFavorite = noteEdit.isFavorite();
                        isPin = (noteEdit.isPin() == 1);//bien nay true hay false se phu thuoc vao dk isPin() co = 1 hay khong
                        tvDate.setText(sdf.format(date));
                        colorBackground = noteEdit.getColorBgID();
                        viewMainDetail.setBackgroundResource(colorBackground);
                        setColorDetail(colorBackground,true);
                        //lay danh sach cac reminder da set cho note nay
                        LiveData<List<Reminder>> listReminder = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(id));
                        listReminder.observe(this, reminderList -> {
                            isHaveReminder = reminderList != null && !reminderList.isEmpty();
                            listReminder.removeObservers(this);//huy observe
                        });
                        observeNote.removeObservers(this);//huy observe
                    }
                });
            }
        }
    }


    // TODO: 7/29/2025 mai them id node vao noteHide
    // TODO: 7/29/2025 nghi cach bam vao image thi show anh nen 
}
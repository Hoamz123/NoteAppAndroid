package com.hoamz.hoamz.ui.act;

import static java.util.Calendar.DAY_OF_MONTH;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.EvenDecor;
import com.hoamz.hoamz.utils.SelectedDayCustom;
import com.hoamz.hoamz.utils.TodayCustom;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalenderViewDetail extends AppCompatActivity {
    private ConstraintLayout fabAddNote;
    private MaterialCalendarView materialCalendarView;
    private long startOfDay = 0,endOfDay = 0;
    private SelectedDayCustom selectedDayCustom;
    private RecyclerView rcViewByDate;
    private NoteViewModel noteViewModel;
    private NoteAdapter noteAdapter;
    private LiveData<List<Note>> listNotes;
    private LiveData<List<Long>> listTimestamp;
    private List<CalendarDay> calendarDayList;
    private TextView tvSumNotesADay;
    private String dateSelected;
    private SimpleDateFormat sdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calender_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        initData();
        onClick();
    }

    private void onClick() {
        fabAddNote.setOnClickListener(v ->{
            Intent intent = new Intent(this, CreateNote.class);
            intent.putExtra(Constants.DATE_SELECTED,dateSelected);
            startActivity(intent);
        });

        noteAdapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                //gui du lieu sang act edit de chinh sua
                Intent intent = new Intent(CalenderViewDetail.this,NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                //an li de xoa
                //hien thi log thong bao xoa
                AlertDialog.Builder builder = new AlertDialog.Builder(CalenderViewDetail.this);
                View viewDialog = View.inflate(CalenderViewDetail.this,R.layout.dialog_delete,null);
                builder.setView(viewDialog);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                TextView acCancel = viewDialog.findViewById(R.id.tvCancel);
                TextView acDelete = viewDialog.findViewById(R.id.tvDelete);
                acCancel.setOnClickListener(v -> dialog.dismiss());
                acDelete.setOnClickListener(v -> {
                    noteViewModel.deleteNote(note);
                    dialog.dismiss();
                });

                WindowManager.LayoutParams layoutParams = Objects.requireNonNull(dialog.getWindow()).getAttributes();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                layoutParams.gravity = Gravity.BOTTOM;
                layoutParams.y = 100;
                viewDialog.setLayoutParams(layoutParams);
                dialog.show();
            }
        });
    }

    private void initData() {

        if(listTimestamp != null){
            listTimestamp.removeObservers(this);
        }

        listTimestamp = Transformations.distinctUntilChanged(noteViewModel.getAllDate());
        listTimestamp.observe(this, timestamps -> {

            CalendarDay calendarDay;
            for(long time : timestamps){
                calendarDay = getCalenderDay(time);
                calendarDayList.add(calendarDay);
            }
            if(materialCalendarView != null){
                materialCalendarView.addDecorator(new EvenDecor(calendarDayList));
            }
        });

        //materialCalendarView.removeDecorators();
        materialCalendarView.addDecorator(new TodayCustom(CalenderViewDetail.this,CalendarDay.today()));

        int currentDay = CalendarDay.today().getDay();
        int currentMonth = CalendarDay.today().getMonth();
        int currentYear = CalendarDay.today().getYear();

        setStartOfDayAndEndOfDay(currentDay,currentMonth,currentYear);
        loadData();

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            int day = date.getDay();
            int month = date.getMonth();
            int year = date.getYear();
            dateSelected = sdf.format(getDate(day,month,year));//thoi gian da chon
            setStartOfDayAndEndOfDay(day,month,year);

            if(selectedDayCustom != null){
                materialCalendarView.removeDecorator(selectedDayCustom);
            }
            selectedDayCustom = new SelectedDayCustom(this,date);
            materialCalendarView.addDecorator(selectedDayCustom);
            loadData();
        });
    }

    private CalendarDay getCalenderDay(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return CalendarDay.from(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(DAY_OF_MONTH));
    }

    private Date getDate(int day,int month,int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        return calendar.getTime();
    }

    @SuppressLint("SetTextI18n")
    private void loadData(){
        //do du lieu nen theo truy van
        if(listNotes != null){
            listNotes.removeObservers(this);
        }
        listNotes = Transformations.distinctUntilChanged(noteViewModel.getNotesByTime(startOfDay,endOfDay));//chi khi du lieu thay doi thi moi update
        listNotes.observe(this, notes -> {
            if (notes != null) {
                noteAdapter.setNoteList(notes);
                tvSumNotesADay.setText("Total : " + notes.size());
            }
            else{
                tvSumNotesADay.setText("Total : 0");
            }
            if(rcViewByDate.getAdapter() == null){
                rcViewByDate.setAdapter(noteAdapter);
            }
        });
    }

    private void setStartOfDayAndEndOfDay(int day,int month,int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(DAY_OF_MONTH,day);

        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        startOfDay = calendar.getTimeInMillis();//dau ngay

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        endOfDay = calendar.getTimeInMillis();//cuoi ngay
    }

    private void initView() {
        fabAddNote = findViewById(R.id.fab_add_calender);
        materialCalendarView = findViewById(R.id.materialCalender);
        rcViewByDate = findViewById(R.id.rcViewByDate);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteAdapter = new NoteAdapter();
        rcViewByDate.setLayoutManager(new GridLayoutManager(this,1));
        listNotes = new MutableLiveData<>();
        listTimestamp = new MutableLiveData<>();
        calendarDayList = new ArrayList<>();
        tvSumNotesADay = findViewById(R.id.tvSumNote);
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    }
}
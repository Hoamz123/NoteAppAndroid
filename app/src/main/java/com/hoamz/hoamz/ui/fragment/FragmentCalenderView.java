package com.hoamz.hoamz.ui.fragment;

import static java.util.Calendar.DAY_OF_MONTH;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.ui.act.CreateNote;
import com.hoamz.hoamz.ui.act.MainActivity;
import com.hoamz.hoamz.ui.act.NoteDetail;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.utils.EvenDecor;
import com.hoamz.hoamz.utils.SelectedDayCustom;
import com.hoamz.hoamz.utils.TodayCustom;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentCalenderView extends Fragment {

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
    private TextView tvEmptyNotify;
    private TextView acBackToMain;
    private String dateSelected;
    private SimpleDateFormat sdf;

    public FragmentCalenderView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            getActivity().getWindow().setStatusBarColor(getColor(getResources(), R.color.color_bg, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_calender_view, container, false);
        initView(view);
        initData();
        onClick();
        return view;
    }

    private void onClick() {

        acBackToMain.setOnClickListener(v ->{
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        fabAddNote.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), CreateNote.class);
            intent.putExtra(Constants.DATE_SELECTED,dateSelected);
            startActivity(intent);
        });

        //create new note
        tvEmptyNotify.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), CreateNote.class);
            intent.putExtra(Constants.DATE_SELECTED,dateSelected);
            startActivity(intent);
        });

        noteAdapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                //gui du lieu sang act edit de chinh sua
                Intent intent = new Intent(getActivity(), NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                DialogUtils.ActionOnLongClickNote(getActivity(),Constants.DELETE, isAccept -> {
                    if(isAccept){
                        note.setDeleted(true);//xoa
                        noteViewModel.updateNote(note,state ->{});
                        deleteAllReminder(note);
                    }
                });
            }
        });
    }

    private void deleteAllReminder(Note note){
        LiveData<List<Reminder>> reminderLiveData = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(note.getId()));
        reminderLiveData.observe(this, reminderList -> {
            if(reminderList != null){
                for(Reminder reminder : reminderList){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            requireActivity(),
                            reminder.getIdReminder(),
                            new Intent(requireActivity(), MyBroadCastReminder.class),
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    getReminderViewModel().deleteReminder(reminder);
                    AlarmUtils.getInstance().setCancelAlarm(requireActivity(), pendingIntent);
                }
                reminderLiveData.removeObservers(this);
            }
            else{
                reminderLiveData.removeObservers(this);
            }
        });
    }

    private void initData() {

        if(listTimestamp != null){
            listTimestamp.removeObservers(this);
        }

        listTimestamp = Transformations.distinctUntilChanged(noteViewModel.getAllDate());
        listTimestamp.observe(getViewLifecycleOwner(), timestamps -> {

            CalendarDay calendarDay;
            for(long time : timestamps){
                calendarDay = getCalenderDay(time);
                calendarDayList.add(calendarDay);
            }
            if(materialCalendarView != null){
                materialCalendarView.addDecorator(new EvenDecor(calendarDayList));
            }
        });

        materialCalendarView.removeDecorators();
        materialCalendarView.addDecorator(new TodayCustom(getActivity(),CalendarDay.today()));

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
            selectedDayCustom = new SelectedDayCustom(getActivity(),date);
            materialCalendarView.addDecorator(selectedDayCustom);
            loadData();
        });
    }

    private ReminderViewModel getReminderViewModel(){
        return new ViewModelProvider(this).get(ReminderViewModel.class);
    }

    private CalendarDay getCalenderDay(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return CalendarDay.from(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(DAY_OF_MONTH));
    }

    private Date getDate(int day, int month, int year){
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
        listNotes.observe(getViewLifecycleOwner(), notes -> {
            show(notes);
            if (notes != null) {
                noteAdapter.setNoteList(notes);
            }
            if(rcViewByDate.getAdapter() == null){
                rcViewByDate.setAdapter(noteAdapter);
            }
        });
    }

    private void show(List<Note> list){
        if(list == null || list.isEmpty()){
            showWhenEmpty();
        }else{
            showWhenNotEmpty();
        }
    }

    private void showWhenEmpty(){
        tvEmptyNotify.setVisibility(View.VISIBLE);
        rcViewByDate.setVisibility(View.INVISIBLE);
        tvEmptyNotify.setHint(Constants.EMPTY_NOTIFY);
        tvEmptyNotify.setGravity(Gravity.CENTER);
    }

    private void showWhenNotEmpty(){
        tvEmptyNotify.setVisibility(View.INVISIBLE);
        rcViewByDate.setVisibility(View.VISIBLE);
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

    private void initView(View view) {
        fabAddNote = view.findViewById(R.id.fab_add_calender);
        materialCalendarView = view.findViewById(R.id.materialCalender);
        rcViewByDate = view.findViewById(R.id.rcViewByDate);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteAdapter = new NoteAdapter();
        rcViewByDate.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        listNotes = new MutableLiveData<>();
        listTimestamp = new MutableLiveData<>();
        calendarDayList = new ArrayList<>();
        tvEmptyNotify = view.findViewById(R.id.tvEmptyNotify);
        acBackToMain = view.findViewById(R.id.acBackToMain);
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    }

}
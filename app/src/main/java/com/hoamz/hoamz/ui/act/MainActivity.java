package com.hoamz.hoamz.ui.act;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.LabelAdapter;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.ui.fragment.FragmentArchiver;
import com.hoamz.hoamz.ui.fragment.FragmentBin;
import com.hoamz.hoamz.ui.fragment.FragmentFavoriteNote;
import com.hoamz.hoamz.ui.fragment.FragmentSetting;
import com.hoamz.hoamz.ui.fragment.FragmentTypeNote;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_showSearch,iv_menuNav;
    private EditText edtSearchView;
    private ConstraintLayout fabAdd;
    private ImageView iv_TypeShow;
    private NavigationView navMenu;
    private DrawerLayout drawerLayout;
    private TextView tvCancel;
    private ImageView ivClearText,icFilter;
    private boolean isType;//kieu hien thi hai cot hoac 1 cot
    private LabelAdapter adapter;
    private RecyclerView rcListLabel;
    private LabelViewModel labelViewModel;
    private RecyclerView rcNotes;
    private NoteViewModel viewModel;
    private NoteAdapter noteAdapter;
    private ImageView ivEmptyList;
    private TextView tvEmptyList;
    private LiveData<List<Note>> listNotesCurrent;
    private LiveData<List<Label>> listLabelsCurrent;
    private String labelCurrentClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        //khoa dung man hinh
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setStatusBarColor(getColor(R.color.color_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();//anh xa view

        showLabels();//hien thi label
        adapter.setOnMyClick(label ->{
            labelCurrentClick = label;//luu lai label dang click
            showNotesByLabel(label);//bat su kien khi click vao label
        });

        showAllNotes();
        onClickToolbar();//bat su kien click toolbar
    }

    //show danh sach theo nhan
    private void showNotesByLabel(String label) {
        //labelCurrent = label;//gan hien tai = label moi

        if (listNotesCurrent != null) {
            listNotesCurrent.removeObservers(this); // Hủy tất cả observer của Activity này trên LiveData cũ
        }
        if (Objects.equals(label, Constants.labelAll)) {
            icFilter.setVisibility(View.VISIBLE);//cho phep loc
            showAllNotes();
        } else {
            icFilter.setVisibility(View.INVISIBLE);// ko cho phep loc
            displayType();
            listNotesCurrent = Transformations.distinctUntilChanged(viewModel.getListNotesByLabel(label));
            listNotesCurrent.observe(this, notes -> {
                if (notes != null) {
                    showOrHideEmptyImage(notes);
                    noteAdapter.setNoteList(notes);//nho la chi can  != null thi cng set(phong th danh sach cu van con)
                }
            });
        }
    }

    private void displayType() {
        if(!isType){
            iv_TypeShow.setImageResource(R.drawable.ic_app);
            rcNotes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            iv_TypeShow.setImageResource(R.drawable.ic_list_land);
            rcNotes.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        }
        noteAdapter.notifyDataSetChanged();
    }

    //method show or not show image empty view
    private void showOrHideEmptyImage(List<Note> list){
        boolean isEmpty = (list == null || list.isEmpty());

        tvEmptyList.setVisibility((isEmpty) ? View.VISIBLE : View.INVISIBLE);
        ivEmptyList.setVisibility((isEmpty) ? View.VISIBLE : View.INVISIBLE);
        if(isEmpty){
            ivEmptyList.setImageResource(R.drawable.empty_bg);
        }
    }

    private void showAllNotes() {

        if (listNotesCurrent != null) {
            listNotesCurrent.removeObservers(this); // Hủy tất cả observer của Activity này trên LiveData cũ
        }
        listNotesCurrent = Transformations.distinctUntilChanged(viewModel.getListNotes());//UI chi thay doi khi data thay doi
        listNotesCurrent.observe(this, notes -> {
            List<Note> list = new ArrayList<>(notes);
            showOrHideEmptyImage(list);
            noteAdapter.setNoteList(notes);
        });
        if(rcNotes.getAdapter() == null){
            rcNotes.setAdapter(noteAdapter);
        }
    }


    private void showLabels() {
        listLabelsCurrent = Transformations.distinctUntilChanged(labelViewModel.getListLabels());
        listLabelsCurrent.observe(this, labels -> adapter.setListLabel(labels));
        if(rcListLabel.getAdapter() == null){
            rcListLabel.setAdapter(adapter);
        }
    }

    private void showFilter(String condition){
        noteAdapter.setNoteList(new ArrayList<>());
        displayType();
        switch (condition) {
            case Constants.sortAToZ:
                viewModel.getListNotesByAlphabet(Constants.sortAToZ).observe(this, list -> noteAdapter.setNoteList(list));
                break;
            case Constants.sortZToA:
                viewModel.getListNotesByAlphabet(Constants.sortZToA).observe(this, list -> noteAdapter.setNoteList(list));
                break;
            case Constants.sortOldToNew:
                viewModel.getListNotesByTime(Constants.sortOldToNew).observe(this, list -> noteAdapter.setNoteList(list));
                break;
            case Constants.sortNewToOld:
                viewModel.getListNotesByTime(Constants.sortNewToOld).observe(this, list -> noteAdapter.setNoteList(list));
                break;
        }
    }

    private void onClickToolbar() {
        //hien thi edt de tim kiem
        iv_showSearch.setOnClickListener(v ->{
            //an icon search
            iv_showSearch.setVisibility(View.INVISIBLE);
            //hien thanh search
            edtSearchView.setVisibility(View.VISIBLE);
            //focus vao thanh nhap
            edtSearchView.requestFocus();
            //an icon type show
            iv_TypeShow.setVisibility(View.INVISIBLE);
            //hien thi cancel
            tvCancel.setVisibility(View.VISIBLE);
            ShowKey();
        });

        //an thanh tim kiem
        tvCancel.setOnClickListener(v ->{
            //an  ban phim ao
            hideKey();
            edtSearchView.setText("");
            //an cancel
            tvCancel.setVisibility(View.INVISIBLE);
            //hien thi type show
            iv_TypeShow.setVisibility(View.VISIBLE);
            //an thanh search di
            edtSearchView.setVisibility(View.INVISIBLE);
            //hien thi icon search
            iv_showSearch.setVisibility(View.VISIBLE);

        });

        //thay doi bo cuc hien thi
        iv_TypeShow.setOnClickListener(v ->{
            if(isType){
                iv_TypeShow.setImageResource(R.drawable.ic_app);
                rcNotes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            }
            else{
                iv_TypeShow.setImageResource(R.drawable.ic_list_land);
                rcNotes.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            }
            isType = !isType;
            SharePre.getInstance().saveTypeShow(isType);
            noteAdapter.notifyDataSetChanged();
        });

        //thay doi nhap search -> du lieu thay doi theo nhap
        edtSearchView.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(s.length() > 0){
                    //hien thi clear text
                    ivClearText.setVisibility(View.VISIBLE);
                    showListSearch(s.toString());
                }
                if(s.length() == 0){
                    //an clear Text
                    ivClearText.setVisibility(View.INVISIBLE);
                    showAllNotes();
                }
            }
        });

        //khi nhan search tren ban phim
        edtSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                String query = edtSearchView.getText().toString();
                showListSearch(query);
            }
            hideKey();//an ban phim
            return true;
        });

        //hien thi navView
        iv_menuNav.setOnClickListener(v ->{
            drawerLayout.openDrawer(GravityCompat.START);
        });

        //clear text
        ivClearText.setOnClickListener(v ->{
            edtSearchView.setText("");
        });

        fabAdd.setOnClickListener(v ->{
            Intent intent = new Intent(this, CreateNote.class);
            intent.putExtra(Constants.LABEL_CURRENT,labelCurrentClick);
            startActivity(intent);
        });

        icFilter.setOnClickListener(v ->{
            ContextWrapper wrapper = new ContextThemeWrapper(this,R.style.CustomViewPopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper,icFilter);
            popupMenu.getMenuInflater().inflate(R.menu.menu_filter,popupMenu.getMenu());
            popupMenu.show();

            //bat su kien
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if(id == R.id.it_filterByZA){
                    showFilter(Constants.sortZToA);
                }
                else if(id == R.id.it_filterByAZ){
                    showFilter(Constants.sortAToZ);
                }
                else if(id == R.id.it_filterByTimeNew){
                    showFilter(Constants.sortNewToOld);
                }
                else if(id == R.id.it_filterByTimeOld){
                    showFilter(Constants.sortOldToNew);
                }
                else if(id == R.id.it_default){
                    showAllNotes();
                }
                return true;
            });
        });


        noteAdapter.setOnClickItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                //chuyen sang act xem va chinh sua
                Intent intent = new Intent(MainActivity.this,NoteDetail.class);
                intent.putExtra(Constants.KEY_NOTE,note);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Note note) {
                //hien thi log thong bao xoa

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View viewDialog = View.inflate(MainActivity.this,R.layout.dialog_delete,null);
                builder.setView(viewDialog);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                TextView acCancel = viewDialog.findViewById(R.id.tvCancel);
                TextView acDelete = viewDialog.findViewById(R.id.tvDelete);
                acCancel.setOnClickListener(v -> dialog.dismiss());
                acDelete.setOnClickListener(v -> {
                    viewModel.deleteNote(note);
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

        View headerNav = navMenu.getHeaderView(0);

        //man hinh lich
        LinearLayout viewCalender = headerNav.findViewById(R.id.item_calender);
        viewCalender.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            Intent intent = new Intent(MainActivity.this, CalenderViewDetail.class);
            startActivity(intent);
        });

        //man hinh reminder
        LinearLayout viewReminder = headerNav.findViewById(R.id.item_reminder);
        viewReminder.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
            startActivity(intent);
        });

        //man hinh typeNote
        LinearLayout viewTypeNote = headerNav.findViewById(R.id.item_categories);
        viewTypeNote.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fagContainer,new FragmentTypeNote(),FragmentTypeNote.class.getName())
                    .addToBackStack(null)
                    .commit();
        });

        //man hinh favorite
        LinearLayout viewFavorite = headerNav.findViewById(R.id.item_favorites);
        viewFavorite.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fagContainer,new FragmentFavoriteNote(),FragmentFavoriteNote.class.getName())
                    .addToBackStack(null)
                    .commit();
        });

        //man hinh archiver
        LinearLayout viewArchiver = headerNav.findViewById(R.id.item_archiver);
        viewArchiver.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fagContainer,new FragmentArchiver(),FragmentArchiver.class.getName())
                    .addToBackStack(null)
                    .commit();
        });

        //man hinh Bin
        LinearLayout viewBin = headerNav.findViewById(R.id.item_deleted);
        viewBin.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fagContainer,new FragmentBin(),FragmentBin.class.getName())
                    .addToBackStack(null)
                    .commit();
        });

        //man hinh cai dat
        LinearLayout viewSetting = headerNav.findViewById(R.id.item_setting);
        viewSetting.setOnClickListener(v ->{
            drawerLayout.closeDrawer(GravityCompat.START);//an
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fagContainer,new FragmentSetting(),FragmentSetting.class.getName())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showListSearch(String query) {
        viewModel.getListNotesByQuerySearch(query).observe(this, list -> {
            if(list == null || list.isEmpty()){
                ivEmptyList.setVisibility(View.VISIBLE);
                ivEmptyList.setImageResource(R.drawable.iv_no_result);
            }
            else{
                ivEmptyList.setVisibility(View.INVISIBLE);
            }
            noteAdapter.setNoteList(list);
        });
    }

    //anh xa view thong qua id
    private void initView() {
        iv_showSearch = findViewById(R.id.iv_show_search);
        edtSearchView = findViewById(R.id.edtSearch);
        iv_TypeShow = findViewById(R.id.ic_show_type);
        icFilter = findViewById(R.id.ic_filter);
        tvCancel = findViewById(R.id.tv_cancel);
        fabAdd = findViewById(R.id.fab_add);
        ivClearText = findViewById(R.id.iv_clearText);
        iv_menuNav =findViewById(R.id.menu_nav);
        drawerLayout = findViewById(R.id.drawerNav);
        navMenu = findViewById(R.id.navView);
        rcListLabel = findViewById(R.id.rcListLabel);//danh sach nhan
        rcNotes = findViewById(R.id.rcListNotes);//danh sach ghi chu
        rcNotes.setItemAnimator(null);

        //viewmodel cua label -> nhan
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        adapter = new LabelAdapter();
        rcListLabel.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        noteAdapter = new NoteAdapter();
        //viewmodel cua ghi chu
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        isType = SharePre.getInstance().getTypeShow();//ban dau mac dinh la false
//        rcNotes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        displayType();

        ivEmptyList = findViewById(R.id.iv_empty);
        tvEmptyList = findViewById(R.id.tv_emptyList);

    }
    private void ShowKey(){
        //hien thi ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(edtSearchView,InputMethodManager.SHOW_IMPLICIT);
    }
    private void hideKey(){
        //an ban phim ao
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }
}
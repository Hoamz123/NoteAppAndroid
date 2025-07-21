package com.hoamz.hoamz.ui.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.hoamz.hoamz.data.model.NoteDeleted;
import com.hoamz.hoamz.databinding.ActivityMainBinding;
import com.hoamz.hoamz.ui.fragment.FragmentBin;
import com.hoamz.hoamz.ui.fragment.FragmentCalenderView;
import com.hoamz.hoamz.ui.fragment.FragmentFavoriteNote;
import com.hoamz.hoamz.ui.fragment.FragmentReminder;
import com.hoamz.hoamz.ui.fragment.FragmentSetting;
import com.hoamz.hoamz.ui.fragment.FragmentTypeNote;
import com.hoamz.hoamz.ui.fragment.FragmentWidget;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.utils.DialogShow;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.TypeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_showSearch,iv_menuNav;
    private EditText edtSearchView;
    private ConstraintLayout fabAdd;
    private ImageView ivShowMoreSetup;
    private NavigationView navMenu;
    private DrawerLayout drawerLayout;
    private TextView tvCancel;
    private ImageView ivClearText;
    private SwipeRefreshLayout refreshLayout;
    private boolean isGrid;//kieu hien thi hai cot hoac 1 cot
    private boolean isShow = false;
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
    private String labelCurrentClick = Constants.labelAll;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        showNotesByLabel(labelCurrentClick);
        onClickToolbar();//bat su kien click toolbar
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(binding.drawerNav.isOpen()){
                    binding.drawerNav.closeDrawer(GravityCompat.START);
                }
                else{
                    finish();
                }
            }
        });
        onReceiverData();
        onClickMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onReLoadData();
    }

    private void onClickMenu(){

        //bat su kien
        //sort
        binding.layoutMoreSetup.tvSort.setOnClickListener(click ->{

            //bat su kien click sap xep
            DialogShow.showDialogSort(this, this::showFilter);

            //cancelOnTouchOutSide
            binding.constraintInclude.setVisibility(View.INVISIBLE);
            isShow = false;
        });
        //chon che do hien thi
        binding.layoutMoreSetup.tvTypeShow.setOnClickListener(click ->{
            if(isGrid){
                //neu dang la dang luoi
                binding.layoutMoreSetup.tvTypeShow.setText("Danh sách");
            }
            else{
                binding.layoutMoreSetup.tvTypeShow.setText("Lưới");
            }
            isGrid = !isGrid;
            SharePre.getInstance(this).saveTypeShow(isGrid);
            displayType();
            binding.constraintInclude.setVisibility(View.INVISIBLE);
            isShow = false;
        });

        //nhan ra ngoai menu thi an menu
        binding.constraintInclude.setOnClickListener(click ->{
            binding.constraintInclude.setVisibility(View.INVISIBLE);
            isShow = false;
        });
    }

    //show danh sach theo nhan
    private void showNotesByLabel(String label) {
        //labelCurrent = label;//gan hien tai = label moi

        if (listNotesCurrent != null) {
            listNotesCurrent.removeObservers(this); // Hủy tất cả observer của Activity này trên LiveData cũ
        }
        if (Objects.equals(label, Constants.labelAll)) {
            showAllNotes();
        } else {
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
        if(!isGrid){
            rcNotes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
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
            refreshLayout.setEnabled(false);
            //an icon search
            iv_showSearch.setVisibility(View.INVISIBLE);
            //hien thanh search
            edtSearchView.setVisibility(View.VISIBLE);
            //focus vao thanh nhap
            edtSearchView.requestFocus();
            //an icon type show
            ivShowMoreSetup.setVisibility(View.INVISIBLE);
            //hien thi cancel
            tvCancel.setVisibility(View.VISIBLE);
            ShowKey();
        });

        //an thanh tim kiem
        tvCancel.setOnClickListener(v ->{
            //an  ban phim ao
            refreshLayout.setEnabled(true);
            hideKey();
            edtSearchView.setText("");
            //an cancel
            tvCancel.setVisibility(View.INVISIBLE);
            //hien thi type show
            ivShowMoreSetup.setVisibility(View.VISIBLE);
            //an thanh search di
            edtSearchView.setVisibility(View.INVISIBLE);
            //hien thi icon search
            iv_showSearch.setVisibility(View.VISIBLE);
        });

        ivShowMoreSetup.setOnClickListener(v ->{
            if(isShow) {
                binding.constraintInclude.setVisibility(View.INVISIBLE);
            }
            else{
                binding.constraintInclude.setVisibility(View.VISIBLE);
            }
            isShow = !isShow;
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
                dialog.setCanceledOnTouchOutside(false);
                TextView acCancel = viewDialog.findViewById(R.id.tvCancel);
                TextView acDelete = viewDialog.findViewById(R.id.tvDelete);
                acCancel.setOnClickListener(v -> dialog.dismiss());
                acDelete.setOnClickListener(v -> {
                    NoteDeleted noteDeleted = new NoteDeleted(note.getTitle()
                            ,note.getContent(),note.getLabel(),
                            note.getColorBgID(),System.currentTimeMillis());
                    viewModel.deleteNote(note);
                    viewModel.insertNoteDeleted(noteDeleted);
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

        navMenu.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int idItem = item.getItemId();
            if(idItem == R.id.idWidget){
                new Handler().postDelayed(()->{
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,FragmentWidget.getInstance(0,"MAIN"),FragmentWidget.class.getName())
                            .addToBackStack(null)
                            .commit();
                },280);
            }
            else if(idItem == R.id.idCalender){

                new Handler().postDelayed(() ->{
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,new FragmentCalenderView(),FragmentCalenderView.class.getName())
                            .addToBackStack(null)
                            .commit();
                },280);
            }
            else if(idItem == R.id.idReminder) {
                new Handler().postDelayed(() ->{
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,new FragmentReminder(),FragmentReminder.class.getName())
                            .addToBackStack(null)
                            .commit();
                },250);
            }
            else if(idItem == R.id.idCategories){
                //do something
                new Handler().postDelayed(() ->{
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,new FragmentTypeNote(),FragmentTypeNote.class.getName())
                            .addToBackStack(null)
                            .commit();
                },250);
            }
            else if(idItem == R.id.idFavorites) {
                //do something
                new Handler().postDelayed(() ->{
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,new FragmentFavoriteNote(),FragmentFavoriteNote.class.getName())
                            .addToBackStack(null)
                            .commit();
                },250);
            }
            else if(idItem == R.id.idBin){
                //do something
                new Handler().postDelayed(() -> {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,new FragmentBin(),FragmentBin.class.getName())
                            .addToBackStack(null)
                            .commit();
                },250);
            }
            else if(idItem == R.id.idSetting){
                //do st
                new Handler().postDelayed(() -> {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                            .replace(R.id.fagContainer,new FragmentSetting(),FragmentSetting.class.getName())
                            .addToBackStack(null)
                            .commit();
                },250);
            }
            return false;
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
        //check permission
        if(ActivityCompat.checkSelfPermission(this, Arrays.toString(new String[]{Manifest.permission.POST_NOTIFICATIONS})) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requirePermissionNotify();
            }
        };
        iv_showSearch = findViewById(R.id.iv_show_search);
        edtSearchView = findViewById(R.id.edtSearch);
        //iv_TypeShow = findViewById(R.id.ic_show_type);
        ivShowMoreSetup = findViewById(R.id.ic_show_more_setting);
        tvCancel = findViewById(R.id.tv_cancel);
        fabAdd = findViewById(R.id.fab_add);
        ivClearText = findViewById(R.id.iv_clearText);
        iv_menuNav =findViewById(R.id.menu_nav);
        drawerLayout = findViewById(R.id.drawerNav);
        navMenu = findViewById(R.id.navView);
        rcListLabel = findViewById(R.id.rcListLabel);//danh sach nhan
        rcNotes = findViewById(R.id.rcListNotes);//danh sach ghi chu
        rcNotes.setItemAnimator(null);
        refreshLayout = findViewById(R.id.id_swiper);

        //viewmodel cua label -> nhan
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        adapter = new LabelAdapter();
        rcListLabel.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        noteAdapter = new NoteAdapter();
        //viewmodel cua ghi chu
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        isGrid = SharePre.getInstance(this).getTypeShow();//ban dau mac dinh la false
        binding.layoutMoreSetup.tvTypeShow.setText((isGrid) ? "Lưới" : "Dang sách");
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

    //nhan thong tin khi ben typeNote nhan chon theo chi muc
    private void onReceiverData(){
        TypeModel typeModel = new ViewModelProvider(this).get(TypeModel.class);
        typeModel.getType().observe(this, s -> {
            if(s != null){
                labelCurrentClick = s;
                showNotesByLabel(s);
            }
        });
        typeModel.getIndex().observe(this, integer -> {
            if (integer != null) {
                adapter.setCurrPosition(integer);
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    private void onReLoadData(){
        refreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.color8)
        );
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.postDelayed(() -> {
                refreshLayout.setRefreshing(false);
                showNotesByLabel(labelCurrentClick);}, 600);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requirePermissionNotify() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},1);
    }

}
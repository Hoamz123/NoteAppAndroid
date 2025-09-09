package com.hoamz.hoamz.ui.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.LabelAdapter;
import com.hoamz.hoamz.adapter.NoteAdapter;
import com.hoamz.hoamz.adapter.SelectLabelAdapter;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.databinding.ActivityMainBinding;
import com.hoamz.hoamz.ui.fragment.BaseFragment;
import com.hoamz.hoamz.ui.fragment.FragmentArchive;
import com.hoamz.hoamz.ui.fragment.FragmentBin;
import com.hoamz.hoamz.ui.fragment.FragmentCalenderView;
import com.hoamz.hoamz.ui.fragment.FragmentFavoriteNote;
import com.hoamz.hoamz.ui.fragment.FragmentReminder;
import com.hoamz.hoamz.ui.fragment.FragmentContact;
import com.hoamz.hoamz.ui.fragment.FragmentTypeNote;
import com.hoamz.hoamz.utils.AlarmUtils;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.CustomTextWatcher;
import com.hoamz.hoamz.utils.DialogUtils;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;
import com.hoamz.hoamz.viewmodel.TypeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        private boolean isSelectAllNote = false;
        private boolean isGrid = true;//kieu hien thi hai cot hoac 1 cot
        private boolean isShow = false;
        private boolean isMultiSelect = false;
        private LabelAdapter adapter;
        private RecyclerView rcListLabel;
        private LabelViewModel labelViewModel;
        private RecyclerView rcNotes;
        private NoteViewModel viewModel;
        private NoteAdapter noteAdapter;
        private ImageView ivEmptyList;
        private TextView tvEmptyList;
        private LiveData<List<Note>> listNotesCurrent;
        private String labelCurrentClick = Constants.labelAll;
        private long timeExit = 0;
        private ActivityMainBinding binding;
        private String sortCondition = Constants.sortNewToOld;
        private final AtomicReference<Set<Note>> listNoteSelectMulti = new AtomicReference<>(new HashSet<>());//luu lai danh sach cac note da chon

    //att in dialog
    /********************************/
    private RecyclerView rcViewInDialog;
    private SelectLabelAdapter selectLabelAdapter;
    private List<LabelDetail> labelDetailList;
    private EditText edtInputNewLabel;
    private TextView acCancelCreateNewLabel,acSaveNewLabel;
    private LiveData<List<Label>> listLabel;
    /********************************/

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //khoa dung man hinh
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            initView();//anh xa view

            showLabels();//hien thi label
            adapter.setOnMyClick(label ->{
                labelCurrentClick = label;//luu lai label dang click
                showNotes(sortCondition,label);
            });
            onClickToolbar();//bat su kien click toolbar
            onReceiverData();
            onClickMenu();
        }

        @Override
        protected void onResume() {
            super.onResume();
            sortCondition = SharePre.getInstance(this).getSortCondition("sort_main");
            showNotes(sortCondition,labelCurrentClick);
            onReLoadData();
        }


        @Override
        public void onBackPressed() {
            try{
                boolean handled = false;
                //duyen cac fragment trong backList
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for(Fragment fragment : fragments){
                    if(fragment instanceof BaseFragment && fragment.isVisible()){
                        handled = ((BaseFragment) fragment).onBackPressed();
                        if(handled) return;//neu true tuc la da xu li ben trong fragment
                    }
                }
                //khong fragment nao xu li back tren device
                //xu li theo act
                if(isMultiSelect){
                    binding.toolbarSM.setVisibility(View.INVISIBLE);
                    isMultiSelect = false;
                    binding.toolbar.setVisibility(View.VISIBLE);//hien toolbar cu
                    //hien fab add
                    binding.fabAdd.setVisibility(View.VISIBLE);
                    //an thanh action
                    binding.toolbarSelMul.titleMulSel.setText("0 Selected");
                    binding.constrainActionMulSel.setVisibility(View.INVISIBLE);
                    noteAdapter.setClearAllClick();
                    noteAdapter.setCancelMultiSelect();
                    return;
                }
                if(binding.drawerNav.isOpen()){
                    binding.drawerNav.closeDrawer(GravityCompat.START);
                }
                else{
                    //neu con fragment trong stack-> pop ra
                    if(getSupportFragmentManager().getBackStackEntryCount() > 0){
                        getSupportFragmentManager().popBackStack();
                    }
                    else {
                        if (System.currentTimeMillis() - timeExit > 2000) {
                            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                            timeExit = System.currentTimeMillis();
                        } else {
                            super.onBackPressed();
                        }
                    }
                }
            }catch (Exception e){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }

        private void onClickMove(){
            if(listNoteSelectMulti.get().isEmpty()){
                //empty -> toast
                Toast.makeText(this, "No notes selected", Toast.LENGTH_SHORT).show();
                return;
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
                    viewModel.getCountNotes(label.getLabel()).observe(MainActivity.this, integer -> {
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
            //logic show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialog = View.inflate(this,R.layout.dialog_choose_label,null);
            builder.setView(viewDialog);
            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);

            rcViewInDialog = viewDialog.findViewById(R.id.rcView);
            TextView btnCreateNewLabel = viewDialog.findViewById(R.id.btnCreateNewLabel);
            TextView acSave = viewDialog.findViewById(R.id.acSaveInDialog);
            TextView acCancel = viewDialog.findViewById(R.id.acCancelInDialog);
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
                for(Note note : listNoteSelectMulti.get()){
                    note.setLabel(labelSelected[0]);
                    viewModel.updateNote(note,state ->{});
                }
                ExitMultiSelect();
                Toast.makeText(this, "Move to " + labelSelected[0], Toast.LENGTH_SHORT).show();
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
        }

        private void onClickMenu(){
            //bat su kien
            //sort
            binding.layoutMoreSetup.tvSort.setOnClickListener(click ->{

                //bat su kien click sap xep
                DialogUtils.showDialogSort(this, sortCondition, sort -> {
                    sortCondition = sort;
                    SharePre.getInstance(MainActivity.this).saveSortCondition("sort_main",sort);
                    showNotes(sort,labelCurrentClick);
                });

                //cancelOnTouchOutSide
                binding.constraintInclude.setVisibility(View.INVISIBLE);
                isShow = false;
            });

            //chon che do hien thi
            binding.layoutMoreSetup.tvTypeShow.setOnClickListener(click ->{
                if(isGrid){
                    //neu dang la dang luoi
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort,null);
                    binding.layoutMoreSetup.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                    binding.layoutMoreSetup.tvTypeShow.setText("List View");
                }
                else{
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps,null);
                    binding.layoutMoreSetup.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                    binding.layoutMoreSetup.tvTypeShow.setText("Grid View");
                }
                isGrid = !isGrid;
                SharePre.getInstance(this).saveTypeShow(isGrid);
                displayType();
                binding.constraintInclude.setVisibility(View.INVISIBLE);
                isShow = false;
            });

            //chon che do select mul
            binding.layoutMoreSetup.tvChoose.setOnClickListener(v ->{
                noteAdapter.setMultiSelect(true);
                isMultiSelect = true;
                binding.toolbar.setVisibility(View.INVISIBLE);//an toolbar cu
                binding.toolbarSM.setVisibility(View.VISIBLE);
                //an fab add
                binding.fabAdd.setVisibility(View.INVISIBLE);
                //hien thi thanh action
                binding.constrainActionMulSel.setVisibility(View.VISIBLE);
                binding.constraintInclude.setVisibility(View.INVISIBLE);
                isShow = false;
            });

            //chon tat ca
            binding.toolbarSelMul.acSelectAll.setOnClickListener(v ->{
                if(!isSelectAllNote){
                    noteAdapter.setAllClick();
                    isSelectAllNote = true;
                }
                else{
                    noteAdapter.setClearAllClick();
                    isSelectAllNote = false;
                }
            });

            //thoat khoi che do chon nhieu
            binding.toolbarSelMul.icExitMulSel.setOnClickListener(v ->{
                noteAdapter.setCancelMultiSelect();
                isMultiSelect = false;
                binding.toolbar.setVisibility(View.VISIBLE);//an toolbar cu
                binding.toolbarSM.setVisibility(View.INVISIBLE);
                //hien fab add
                binding.fabAdd.setVisibility(View.VISIBLE);
                //an thanh action
                binding.toolbarSelMul.titleMulSel.setText("0 Selected");
                binding.constrainActionMulSel.setVisibility(View.INVISIBLE);
            });


            //xu li su kien chon nhieu item
            noteAdapter.setOnMultiSelectItem(listMultiSelected -> {
                String title = listMultiSelected.size() + " Selected";
                binding.toolbarSelMul.titleMulSel.setText(title);
                listNoteSelectMulti.set(listMultiSelected);
            });
            //nhan ra ngoai menu thi an menu
            binding.constraintInclude.setOnClickListener(click ->{
                binding.constraintInclude.setVisibility(View.INVISIBLE);
                isShow = false;
            });

            //xu li su kien sau khi nhan chon nhieu note
            //xoa
            binding.layoutActionMulSel.tvDeleteMul.setOnClickListener(v ->{
                onClickActionDelArc(Constants.DELETE_S);
            });

            //archive
            binding.layoutActionMulSel.tvArchiveMul.setOnClickListener(v ->{
                onClickActionDelArc(Constants.ARCHIVE_S);
            });

            //move
            binding.layoutActionMulSel.tvMoveMul.setOnClickListener(v ->{
                onClickMove();
                showNotes(sortCondition,labelCurrentClick);
            });
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


        //method thoat khoi che do chon nhieu
        private void ExitMultiSelect(){
            noteAdapter.setCancelMultiSelect();
            binding.toolbar.setVisibility(View.VISIBLE);//hien toolbar cu
            binding.toolbarSM.setVisibility(View.INVISIBLE);
            //hien fab add
            binding.fabAdd.setVisibility(View.VISIBLE);
            //an thanh action
            binding.toolbarSelMul.titleMulSel.setText("0 Selected");
            binding.constrainActionMulSel.setVisibility(View.INVISIBLE);
        }

    private void updateTypeShowIcon() {
        if (isGrid) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_apps, null);
            binding.layoutMoreSetup.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetup.tvTypeShow.setText("Grid View");
        } else {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.apps_sort, null);
            binding.layoutMoreSetup.tvTypeShow.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            binding.layoutMoreSetup.tvTypeShow.setText("List View");
        }
    }

        private void showLabels() {
            LiveData<List<Label>> listLabelsCurrent = Transformations.distinctUntilChanged(labelViewModel.getListLabels());
            listLabelsCurrent.observe(this, labels -> {
                if(labels.isEmpty()){
                    adapter.setListLabel(Constants.listLabelDefault);
                }
                else {
                    adapter.setListLabel(labels);
                }
            });
            if(rcListLabel.getAdapter() == null){
                rcListLabel.setAdapter(adapter);
            }
        }

        //method xu li khi user chon xoa/archive/move nhieu note
        private void onClickActionDelArc(String action){
            if(!listNoteSelectMulti.get().isEmpty()){
                List<Note> noteList = new ArrayList<>(listNoteSelectMulti.get());
                DialogUtils.ActionOnLongClickNote(this,action, isAccept -> {
                    if(isAccept){
                        if(action.equals(Constants.DELETE_S)){
                            for(Note note : noteList){
                                note.setDeleted(true);
                                note.setTimeDeleteNote(System.currentTimeMillis());
                                viewModel.updateNote(note,state ->{});
                                deleteAllReminder(note);
                            }
                        }
                        else{
                            for(Note note : noteList){
                                note.setArchived(true);
                                viewModel.updateNote(note,state ->{});
                                deleteAllReminder(note);
                            }
                        }
                        ExitMultiSelect();
                    }
                });
            }
            else{
                //empty -> toast
                Toast.makeText(this, "No notes selected", Toast.LENGTH_SHORT).show();
            }
        }

        private void showNotes(String sortCondition,String label){
            if (listNotesCurrent != null) {
                listNotesCurrent.removeObservers(this); // Hủy tất cả observer của Activity này trên LiveData cũ
            }
                switch (sortCondition){
                    case Constants.sortAToZ:
                        listNotesCurrent = Transformations.distinctUntilChanged(viewModel.getListNotesByAlphabet(Constants.sortAToZ,label));
                        break;
                    case Constants.sortZToA:
                        listNotesCurrent = Transformations.distinctUntilChanged(viewModel.getListNotesByAlphabet(Constants.sortZToA,label));
                        break;
                    case Constants.sortOldToNew:
                        listNotesCurrent = Transformations.distinctUntilChanged(viewModel.getListNotesByTime(Constants.sortOldToNew,label));
                        break;
                    case Constants.sortNewToOld:
                        listNotesCurrent = Transformations.distinctUntilChanged(viewModel.getListNotesByTime(Constants.sortNewToOld,label));
                        break;
                }
            if (listNotesCurrent != null) {
                listNotesCurrent.observe(this, notes -> {
                    List<Note> list = new ArrayList<>(notes);
                    showOrHideEmptyImage(list);
                    noteAdapter.setNoteList(list);
                });
            }

            if (rcNotes.getAdapter() == null) {
                rcNotes.setAdapter(noteAdapter);
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
//                        showAllNotes();
                        showNotes(sortCondition,labelCurrentClick);
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
                    intent.putExtra(Constants.KEY_NOTE,note.getId());
                    startActivity(intent);
                }
                @Override
                public void onItemLongClick(Note note) {
                    //hien thi log thong bao xoa note
                    DialogUtils.ActionOnLongClickNote(MainActivity.this,Constants.DELETE, isAccept ->{
                        if(isAccept){
                            note.setDeleted(true);
                            viewModel.updateNote(note,state ->{});
                            deleteAllReminder(note);
                        }
                    });
                }
            });

            navMenu.setNavigationItemSelectedListener(item -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                int idItem = item.getItemId();
                if(idItem == R.id.idArchive){
                    new Handler().postDelayed(()->{
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                                .replace(R.id.fagContainer,new FragmentArchive(),FragmentArchive.class.getName())
                                .addToBackStack(null)
                                .commit();
                    },250);
                }
                else if(idItem == R.id.idCalender){

                    new Handler().postDelayed(() ->{
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                                .replace(R.id.fagContainer,new FragmentCalenderView(),FragmentCalenderView.class.getName())
                                .addToBackStack(null)
                                .commit();
                    },250);
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
                else if(idItem == R.id.idContact){
                    //do st
                    new Handler().postDelayed(() -> {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.pop_enter_from_left,R.anim.pop_exit_to_right)
                                .replace(R.id.fagContainer,new FragmentContact(), FragmentContact.class.getName())
                                .addToBackStack(null)
                                .commit();
                    },250);
                }
                return false;
            });
        }

        private void deleteAllReminder(Note note){
            LiveData<List<Reminder>> reminderLiveData = Transformations.distinctUntilChanged(getReminderViewModel().getAllRemindersByIdNote(note.getId()));
            reminderLiveData.observe(MainActivity.this, reminderList -> {
                if(reminderList != null){
                    for(Reminder reminder : reminderList){
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                reminder.getIdReminder(),
                                new Intent(MainActivity.this, MyBroadCastReminder.class),
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        getReminderViewModel().deleteReminder(reminder);
                        AlarmUtils.getInstance().setCancelAlarm(MainActivity.this, pendingIntent);
                    }
                    reminderLiveData.removeObservers(MainActivity.this);
                }
            });
        }


        private ReminderViewModel getReminderViewModel(){
            return new ViewModelProvider(this).get(ReminderViewModel.class);
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
            binding.layoutMoreSetup.tvTypeShow.setText((isGrid) ? "Grid View" : "List View");
            displayType();

            ivEmptyList = findViewById(R.id.iv_empty);
            tvEmptyList = findViewById(R.id.tv_emptyList);
            updateTypeShowIcon();
            sortCondition = SharePre.getInstance(this).getSortCondition("sort_main");
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
                    showNotes(sortCondition,labelCurrentClick);
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
                    sortCondition = Constants.sortNewToOld;
                    SharePre.getInstance(this).saveSortCondition("sort_main",sortCondition);
                    showNotes(sortCondition,labelCurrentClick);
                    }, 600);
            });
        }

}
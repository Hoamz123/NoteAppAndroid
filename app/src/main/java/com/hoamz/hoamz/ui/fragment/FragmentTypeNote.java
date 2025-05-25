package com.hoamz.hoamz.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.LabelAdapter;
import com.hoamz.hoamz.adapter.TypeNoteAdapter;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.ui.act.MainActivity;
import com.hoamz.hoamz.utils.Constants;
import com.hoamz.hoamz.utils.DialogShow;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.TypeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FragmentTypeNote extends Fragment {

    private Context context;
    private ImageView icExitCategory;
    private Button btnAddLabel;
    private NoteViewModel noteViewModel;
    private LabelViewModel labelViewModel;
    private TypeNoteAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Label>> listLabel;
    private List<Label> listNameLabel;
    private List<LabelDetail> listLabelDetail;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public FragmentTypeNote() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_note, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        onClick();
    }

    private void initView(View view) {
        icExitCategory = view.findViewById(R.id.icExitTypeNote);
        btnAddLabel = view.findViewById(R.id.btnAddLabel);
        recyclerView = view.findViewById(R.id.rcViewCategory);
        listLabelDetail = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        adapter = new TypeNoteAdapter();
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        labelViewModel = new ViewModelProvider(requireActivity()).get(LabelViewModel.class);
        adapter.setListLabelDetail(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        // Xóa observer cũ
        if (listLabel != null) {
            listLabel.removeObservers(getViewLifecycleOwner());
        }

        // Lấy danh sách nhãn
        listLabel = Transformations.distinctUntilChanged(labelViewModel.getListLabels());
        listLabel.observe(getViewLifecycleOwner(), labels -> {
            if (!isAdded() || getView() == null) return;

            listLabelDetail = new ArrayList<>();
            listNameLabel = new ArrayList<>(labels);
            List<Label> labelListCopy = new ArrayList<>(labels);
            Map<String, Integer> labelCountMap = new HashMap<>();
            AtomicInteger completedCount = new AtomicInteger(0);

            if (labels.isEmpty()) {
                adapter.setListLabelDetail(listLabelDetail);
                return;
            }

            for (Label label : labels) {
                LiveData<Integer> countLiveData = noteViewModel.getCountNotes(label.getLabel());
                countLiveData.observe(getViewLifecycleOwner(), count -> {
                    if (!isAdded() || getView() == null) return;

                    labelCountMap.put(label.getLabel(), count != null ? count : 0);
                    if (completedCount.incrementAndGet() == labels.size()) {
                        listLabelDetail.clear();
                        for (Label l : labelListCopy) {
                            Integer cnt = labelCountMap.get(l.getLabel());
                            if (cnt != null) {
                                listLabelDetail.add(new LabelDetail(l.getLabel(), cnt));
                            }
                        }
                        adapter.setListLabelDetail(listLabelDetail);
                    }
                });
            }
        });
    }

    private void onClick() {
        icExitCategory.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnAddLabel.setOnClickListener(v -> {
            if (isAdded()) {
                DialogShow.showDialogLabel(context, Constants.TitleCreateNewLabel, null, listNameLabel,
                        label -> labelViewModel.insertLabel(new Label(label)));
            }
        });

        adapter.setOnClickTypeNote(new TypeNoteAdapter.onClickTypeNote() {
            @Override
            public void onClickTypeLabel(LabelDetail labelDetail,int pos) {
                TypeModel typeModel = new ViewModelProvider(requireActivity()).get(TypeModel.class);
                typeModel.setType(labelDetail.getLabelName());
                typeModel.setIndex(pos);
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onClickForShowMoreOption(LabelDetail labelDetail) {
                if (!isAdded()) return;

                labelViewModel.getLabelByName(labelDetail.getLabelName()).observe(getViewLifecycleOwner(), label -> {
                    if (label != null && isAdded()) {
                        BottomSheetShowMoreOption sheetShowMoreOption = new BottomSheetShowMoreOption(
                                listNameLabel, context, labelViewModel, noteViewModel, label);
                        sheetShowMoreOption.show(requireActivity().getSupportFragmentManager(), sheetShowMoreOption.getTag());
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        listLabel = null;
    }
}
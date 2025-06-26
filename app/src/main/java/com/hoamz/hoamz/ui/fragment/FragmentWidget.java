package com.hoamz.hoamz.ui.fragment;

import static androidx.core.content.res.ResourcesCompat.getColor;
import static androidx.core.content.res.ResourcesCompat.getDrawable;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.test.espresso.remote.EspressoRemoteMessage;

import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hoamz.hoamz.Broadcast.AppwidgetProvider;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.databinding.FragmentWidgetBinding;
import com.hoamz.hoamz.ui.act.MainActivity;
import com.hoamz.hoamz.ui.act.Splash;
import com.hoamz.hoamz.utils.Constants;

import java.util.Objects;

public class FragmentWidget extends Fragment {

    private Context context;
    private FragmentWidgetBinding binding;
    private int idBackGround = 0;
    private int idWidget = 0;//luu idWidget dc truyen sang
    private String source = "";//nguon tu dau(nghia la fragment dc replace tu dau)

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public FragmentWidget() {
        // Required empty public constructor
    }

    public static FragmentWidget getInstance(int idWidget,String source){
        FragmentWidget fragment = new FragmentWidget();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID_WIDGET_CLICK,idWidget);
        bundle.putString(Constants.SOURCE,source);
        fragment.setArguments(bundle);
        return fragment;
    }

    //sang mai xu ly su kien back ve khi luu xong(them tinh nang quan ly)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getActivity().getWindow().setStatusBarColor(getColor(getResources(), R.color.color_bg, null));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //lay du lieu gui qua bundle
        if(getArguments() != null){
            this.idWidget = getArguments().getInt(Constants.ID_WIDGET_CLICK);
            this.source = getArguments().getString(Constants.SOURCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_widget, container, false);
        binding = FragmentWidgetBinding.bind(view);
        initView();
        onClickTheme();
        onClick();
        onSetBackGround();
        return view;
    }

    private void onSendData() {

        binding.edtContentWg.clearFocus();

        //luu lai du lieu vao sharePre theo idWidget
        String contentWidget = binding.edtContentWg.getText().toString();
        SharePre.getInstance(context).saveContentWidget(idWidget,contentWidget);
        SharePre.getInstance(context).saveIdBackgroundWidget(idWidget,idBackGround);
        //gui du lieu den appwidgetProvider
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.tvContentWidget,contentWidget);
        remoteViews.setInt(R.id.mainRelayout,"setBackgroundResource",idBackGround);
        remoteViews.setTextViewText(R.id.tv_dateWG,Constants.getCurrentDay());
        AppWidgetManager.getInstance(context).updateAppWidget(idWidget,remoteViews);
        Toast.makeText(requireActivity(), "Lưu thành công", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        if(Objects.equals(source, "MAIN")){
            binding.edtContentWg.setHint(Constants.HINT);
            binding.edtContentWg.setEnabled(false);
            binding.edtContentWg.setGravity(Gravity.CENTER);
            Drawable drawable = getDrawable(getResources(),R.drawable.bg_btn_disable,null);
            binding.btnSaveWg.setBackgroundDrawable(drawable);
            binding.btnSaveWg.setText("Khong co du lieu");
            binding.btnSaveWg.setTextColor(ColorStateList.valueOf(R.color.color15));
            binding.btnSaveWg.setEnabled(false);
        }
        else {
            //lay du lieu truoc do da luu vao SharePre
            String contentWidget = SharePre.getInstance(context).getContentWidget(idWidget);
            int idBackground_ = SharePre.getInstance(context).getIdBackgroundWidget(idWidget);
            //set cho view
            binding.ivBackgroundWg.setImageResource(idBackground_);
            binding.edtContentWg.setText(contentWidget);
            binding.tvDateWidget.setText(Constants.getCurrentDay());
            idBackGround = idBackground_;
        }
    }

    private void onClick() {
        binding.icExitEdit.setOnClickListener(v->{
            //neu nhu dang o Splash (do minh replace fragment)
            if(requireActivity() instanceof Splash) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            //neu dang o main(do replace fragment)
            else {
                requireActivity().getSupportFragmentManager().popBackStack();//back ve main
            }
        });

        binding.btnSaveWg.setOnClickListener(v ->{
            onSendData();
        });
    }

    //khi click -> dat lai imageBackGround ngay
    private void setImageClickListener(View view, int drawableId) {
        view.setOnClickListener(v -> {
            idBackGround = drawableId;
            onSetBackGround();
            view.setBackgroundResource(R.drawable.bg_choose_theme);
        });
    }

    private void onClickTheme() {
        //bat su kien click vao tung hinh anh
        setImageClickListener(binding.ivWg1, R.drawable.bg_wg1);
        setImageClickListener(binding.ivWg2, R.drawable.bg_wg2);
        setImageClickListener(binding.ivWg3, R.drawable.bg_wg3);
        setImageClickListener(binding.ivWg4, R.drawable.bg_wg4);
        setImageClickListener(binding.ivWg5, R.drawable.bg_wg5);
        setImageClickListener(binding.ivWg6, R.drawable.bg_wg6);
        setImageClickListener(binding.ivWg7, R.drawable.bg_wg7);
        setImageClickListener(binding.ivWg8, R.drawable.bg_wg8);
        setImageClickListener(binding.ivWg9, R.drawable.bg_wg9);
        setImageClickListener(binding.ivWg10, R.drawable.bg_wg10);

        setImageClickListener(binding.ivWg11, R.drawable.img_9);
        setImageClickListener(binding.ivWg12, R.drawable.img_8);
        setImageClickListener(binding.ivWg13, R.drawable.img_7);
        setImageClickListener(binding.ivWg14, R.drawable.img_6);
        setImageClickListener(binding.ivWg15, R.drawable.img_5);
        setImageClickListener(binding.ivWg16, R.drawable.img_4);
        setImageClickListener(binding.ivWg17, R.drawable.img_3);
        setImageClickListener(binding.ivWg18, R.drawable.img_2);
        setImageClickListener(binding.ivWg19, R.drawable.img_1);
        setImageClickListener(binding.ivWg20, R.drawable.img);
    }

    private void onSetBackGround() {
        if(idBackGround != 0){
            binding.ivBackgroundWg.setImageResource(idBackGround);
        }
        else {
            binding.ivBackgroundWg.setImageResource(R.drawable.img_30);
            SharePre.getInstance(context).saveIdBackgroundWidget(idWidget,R.drawable.img_30);
        }
    }
}
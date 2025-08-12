package com.hoamz.hoamz.utils;

import static androidx.core.content.ContextCompat.getColor;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.hoamz.hoamz.Broadcast.MyBroadCastReminder;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.adapter.SelectLabelAdapter;
import com.hoamz.hoamz.data.model.Label;
import com.hoamz.hoamz.data.model.LabelDetail;
import com.hoamz.hoamz.data.model.Note;
import com.hoamz.hoamz.data.model.Reminder;
import com.hoamz.hoamz.viewmodel.LabelViewModel;
import com.hoamz.hoamz.viewmodel.NoteViewModel;
import com.hoamz.hoamz.viewmodel.ReminderViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DialogUtils {
    public static void showDialogLabel(Context context, String title, String hint, List<Label> listLabel, onDialogListener onDialogCreateLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.layout_add_label, null);
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        TextView tvTitle = view.findViewById(R.id.tvHeadTitle);
        tvTitle.setText(title);
        EditText edtEnterLabel = view.findViewById(R.id.edtInputLabel);
        if (hint != null) {
            edtEnterLabel.setText(hint);
        }
        TextView btnSave = view.findViewById(R.id.tvSaveNewLabel);
        TextView btnExit = view.findViewById(R.id.tvCancelCreateLabel);
        ImageView ivCancelText = view.findViewById(R.id.ivClearText);
        btnExit.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String labelNew = Objects.requireNonNull(edtEnterLabel.getText()).toString();
            if (checkLabelExits(labelNew, listLabel)) {
                edtEnterLabel.setError("The category already exists");
                ivCancelText.setVisibility(View.INVISIBLE);
            } else {
                onDialogCreateLabel.onSaveNewLabel(edtEnterLabel.getText().toString());
                Toast.makeText(context, "Category added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        ivCancelText.setOnClickListener(v -> edtEnterLabel.setText(""));//clear text

        edtEnterLabel.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    edtEnterLabel.setError(null);
                    ivCancelText.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    ivCancelText.setVisibility(View.INVISIBLE);
                }
            }
        });
        dialog.show();
    }

    private static boolean checkLabelExits(String label, List<Label> listLabel) {
        for (Label l : listLabel) {
            if (Objects.equals(l.getLabel(), label)) {
                return true;//da ton tai roi
            }
        }
        return false;
    }

    public interface onDialogListener {
        void onSaveNewLabel(String label);
    }

    //show dialog notify xoa du lieu
    public interface onDialogDeleteListener {
        void onDeleteLabel(boolean delete);
    }

    public static void showDialogNotifyDelete(Context context, onDialogDeleteListener onDeleteLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_notify_delete, null);
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvCancel = view.findViewById(R.id.tvCancelNotify);
        TextView tvDelete = view.findViewById(R.id.tvDeleteOk);
        tvCancel.setOnClickListener(v -> {
            onDeleteLabel.onDeleteLabel(false);
            dialog.dismiss();
        });
        tvDelete.setOnClickListener(v -> {
            onDeleteLabel.onDeleteLabel(true);
            dialog.dismiss();
        });
        dialog.show();
    }

    public static void showDialogSort(Context context,String condition,showDialogSortListener showDialogSortListener) {
        View view = View.inflate(context, R.layout.layout_dialog_type_sort, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        TextView tvCancel = view.findViewById(R.id.tvCancelSort);
        TextView tvOKSort = view.findViewById(R.id.tvOkSort);
        switch (condition) {
            case Constants.sortAToZ:
                radioGroup.check(R.id.acSortByA_Z);
                break;
            case Constants.sortZToA:
                radioGroup.check(R.id.acSortByZ_A);
                break;
            case Constants.sortOldToNew:
                radioGroup.check(R.id.acSortByTimeOldToNew);
                break;
            case Constants.sortNewToOld:
                radioGroup.check(R.id.acSortByTimeNewToOld);
                break;
        }
        //bat su kien click
        tvCancel.setOnClickListener(v -> dialog.dismiss());
        tvOKSort.setOnClickListener(click -> {
            int idClicked = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = view.findViewById(idClicked);
            String typeSort = radioButton.getText().toString();
            showDialogSortListener.onSort(typeSort);
            radioButton.setChecked(true);
            dialog.dismiss();
        });
        dialog.show();
    }

    public interface showDialogSortListener {
        void onSort(String sort);
    }

    public static void ActionOnLongClickNote(Context context, String action, onLongClickNote onLongClickNote) {
        //hien thi log thong bao xoa/archive
        View viewDialog = View.inflate(context, R.layout.dialog_delete, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView title = viewDialog.findViewById(R.id.title_delete_note);
        TextView acCancel = viewDialog.findViewById(R.id.tvCancel);
        TextView acAction = viewDialog.findViewById(R.id.tvAction);
        acAction.setText(action);
        //xet title va mau sac cho title
        switch (action) {
            case Constants.DELETE:
                title.setText("Delete this note?");
                acAction.setTextColor(Color.RED);
                break;
            case Constants.ARCHIVE_S:
                title.setText("Archive selected notes?");
                acAction.setTextColor(getColor(context, R.color.color5));
                break;
            case Constants.DELETE_S:
                title.setText("Delete selected notes?");
                acAction.setTextColor(Color.RED);
                break;
            case Constants.ARCHIVE:
                title.setText("Archive this note?");
                acAction.setTextColor(getColor(context, R.color.color5));
                break;
            case Constants.UN_ARCHIVE_s:
                title.setText("Unachive selected notes?");
                acAction.setTextColor(getColor(context, R.color.color5));
                break;
            case Constants.RESTORE:
                title.setText("Restore selected notes?");
                acAction.setTextColor(getColor(context, R.color.color5));
                break;
        }
        acCancel.setOnClickListener(v -> dialog.dismiss());
        acAction.setOnClickListener(v -> {
            onLongClickNote.onActionLongClickNote(true);
            dialog.dismiss();
        });

        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.y = 100;
        viewDialog.setLayoutParams(layoutParams);
        dialog.show();
    }

    public static void ActionOnLongReminder(Context context, onLongClickNote onLongClickNote) {
        //hien thi log thong bao xoa/archive
        View viewDialog = View.inflate(context, R.layout.dialog_delete, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView title = viewDialog.findViewById(R.id.title_delete_note);
        TextView acCancel = viewDialog.findViewById(R.id.tvCancel);
        TextView acAction = viewDialog.findViewById(R.id.tvAction);
        title.setText("Delete this reminder");
        acAction.setTextColor(Color.RED);
        acCancel.setOnClickListener(v -> dialog.dismiss());
        acAction.setOnClickListener(v -> {
            onLongClickNote.onActionLongClickNote(true);
            dialog.dismiss();
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public interface onLongClickNote {
        void onActionLongClickNote(boolean isAccept);
    }

    public static void ActionManagerReminder(Context context, FragmentManager fragmentManager, long triggerPrevious, long repeatPrevious, MiConsumer miConsumer) {
        View viewDialog = View.inflate(context, R.layout.dialog_create_reminder_2, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewDialog);
        AlertDialog dialogManager = builder.create();
        dialogManager.setCancelable(true);
        dialogManager.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialogManager.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvAcAddTime = viewDialog.findViewById(R.id.tvActionAddTime);
        TextView tcAcCancelManageReminder = viewDialog.findViewById(R.id.tcAcCancelManageReminder);
        TextView tcAcAcceptManageReminder = viewDialog.findViewById(R.id.tcAcAcceptManageReminder);

        //neu trc do da dat nhac nho
        if (triggerPrevious != 0) {
            String timeReminder = getFormatReminder(triggerPrevious);
            tvAcAddTime.setText(timeReminder);
            //neu nhac nho da qua roi -> doi icon -> mau xam
            if (triggerPrevious < System.currentTimeMillis()) {
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable_Detail = context.getResources().getDrawable(R.drawable.baseline_arrow_drop_down_24, null);
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getResources().getDrawable(R.drawable.outline_alarm_24, null);
                tvAcAddTime.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, drawable_Detail, null);
            }
        } else {
            tvAcAddTime.setText("Add Time");
        }

        //on click
        AtomicLong trigger = new AtomicLong(0);
        AtomicLong repeat = new AtomicLong(0);
        tvAcAddTime.setOnClickListener(click -> {
            //hien thi dialog set_time_reminder
            ActionSetReminder(context, fragmentManager, triggerPrevious, getTimeRepeat(repeatPrevious), (triggerSet, repeatSet) -> {
                trigger.set(triggerSet);
                repeat.set(repeatSet);
                tvAcAddTime.setText(getFormatReminder(trigger.get()));
            });
        });

        tcAcAcceptManageReminder.setOnClickListener(click -> {
            miConsumer.accept(trigger.get(), repeat.get());
            dialogManager.dismiss();
        });
        tcAcCancelManageReminder.setOnClickListener(click -> {
            dialogManager.dismiss();
        });
        dialogManager.show();
    }

    public static void ActionManagerReminder2(Activity activity, FragmentManager fragmentManager, Note note
            ,List<Reminder> reminderList, ReminderViewModel reminderViewModel,MiConsumerManagerReminder miConsumerManagerReminder) {
        View viewDialog = View.inflate(activity, R.layout.dialog_create_reminder_2, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(viewDialog);
        AlertDialog dialogManager = builder.create();
        dialogManager.setCancelable(true);
        dialogManager.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialogManager.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tcAcCancelManageReminder = viewDialog.findViewById(R.id.tcAcCancelManageReminder);
        TextView tcAcAcceptManageReminder = viewDialog.findViewById(R.id.tcAcAcceptManageReminder);
        LinearLayout lnContainAllReminder = viewDialog.findViewById(R.id.lnViewShowAllReminders);
        TextView tvAcAddTime = viewDialog.findViewById(R.id.tvActionAddTime);

        //do la toi da mot note gan dc 5 reminder -> do phuc tap O(5)
        for (Reminder reminder : reminderList) {
            TextView tvReminder = (TextView) LayoutInflater.from(activity).inflate(R.layout.layout_add_reminder, lnContainAllReminder, false);
            String timeReminder = getFormatReminder(reminder.getTrigger());
            tvReminder.setText(timeReminder);
            //neu da qua thoi gian
            if (reminder.getTrigger() < System.currentTimeMillis() && reminder.getTimeRepeat() == 0) {
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable_Detail = activity.getResources().getDrawable(R.drawable.baseline_arrow_drop_down_24, null);
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = activity.getResources().getDrawable(R.drawable.iv_alarm_svg_black, null);
                tvReminder.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, drawable_Detail, null);
            } else {
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable_Detail = activity.getResources().getDrawable(R.drawable.baseline_arrow_drop_down_24, null);
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_alarm_svg, null);
                tvReminder.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, drawable_Detail, null);
            }
            lnContainAllReminder.addView(tvReminder);
            //bat su kien click luon
            tvReminder.setOnClickListener(v -> {
                ActionSetReminder(activity, fragmentManager, reminder.getTrigger(), getTimeRepeat(reminder.getTimeRepeat()),
                        (triggerSetEdit, repeatSetEdit) -> {
                            reminder.setTrigger(triggerSetEdit);
                            reminder.setTimeRepeat(repeatSetEdit);
                            reminderViewModel.updateReminder(reminder);
                            Constants.setCancelAlarm(activity,reminder.getIdReminder());//huy di de set lai theo trigger moi va repeat moi
                            Constants.setUpAlarm(activity,note,reminder.getIdReminder(),triggerSetEdit,repeatSetEdit);//set theo trigger moi
                            TextView tvReminderEdit = (TextView) v;
                            tvReminderEdit.setTag(reminder);
                            tvReminderEdit.setText(getFormatReminder(reminder.getTrigger()));
                        });
            });
            //bat su kien nhan long press
            tvReminder.setOnLongClickListener(v -> {
                ActionOnLongReminder(activity, isAccept -> {
                    if (isAccept) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                activity,
                                reminder.getIdReminder(),
                                new Intent(activity, MyBroadCastReminder.class),
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        AlarmUtils.getInstance().setCancelAlarm(activity, pendingIntent);
                        reminderViewModel.deleteReminder(reminder);
                        TextView tvReminderEdit = (TextView) v;
                        lnContainAllReminder.removeView(tvReminderEdit);
                    }
                });
                return false;
            });
        }

        //on click
        tvAcAddTime.setOnClickListener(view -> {
            //hien thi dialog set_time_reminder
            //day la view co san lam host -> nhan vao them moi
            ActionSetReminder(activity, fragmentManager, 0, getTimeRepeat(0), (triggerSet, repeatSet) -> {
                if (lnContainAllReminder.getChildCount() <= 6) {
                    //luu thang vao room
                    Reminder reminder = new Reminder(note.getId(), triggerSet, repeatSet);
                    reminderViewModel.insertReminder(reminder, id -> {
                        reminder.setIdReminder(Math.toIntExact(id));
                        reminderViewModel.updateReminder(reminder);
                        Constants.setUpAlarm(activity,note,reminder.getIdReminder(),triggerSet,repeatSet);//set theo trigger moi
                    });
                    TextView tvReminder = (TextView) LayoutInflater.from(activity).inflate(R.layout.layout_add_reminder, lnContainAllReminder, false);
                    lnContainAllReminder.addView(tvReminder);
                    Toast.makeText(activity, "Add reminder successfully", Toast.LENGTH_SHORT).show();
                    //da co nhac nho
                    tvReminder.setTag(reminder);
                    tvReminder.setText(getFormatReminder(reminder.getTrigger()));
                    //bat su kien click luon
                    tvReminder.setOnClickListener(v -> {
                        Reminder reminderTag = (Reminder) v.getTag();
                        ActionSetReminder(activity, fragmentManager, reminderTag.getTrigger(), getTimeRepeat(reminderTag.getTimeRepeat()),
                                (triggerSetEdit, repeatSetEdit) -> {
                                    reminderTag.setTrigger(triggerSetEdit);
                                    reminderTag.setTimeRepeat(repeatSetEdit);
                                    reminderViewModel.updateReminder(reminderTag);
                                    Constants.setCancelAlarm(activity,reminderTag.getIdReminder());//huy di de set lai theo trigger moi va repeat moi
                                    Constants.setUpAlarm(activity,note,reminderTag.getIdReminder(),triggerSetEdit,repeatSetEdit);//set theo trigger moi
                                    TextView tvReminderEdit = (TextView) v;
                                    tvReminderEdit.setTag(reminderTag);
                                    tvReminderEdit.setText(getFormatReminder(reminderTag.getTrigger()));
                                });
                    });
                    //bat su kien nhan long press
                    tvReminder.setOnLongClickListener(v -> {
                        Reminder reminderTag = (Reminder) v.getTag();
                        ActionOnLongReminder(activity, isAccept -> {
                            if (isAccept) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                        activity,
                                        reminderTag.getIdReminder(),
                                        new Intent(activity, MyBroadCastReminder.class),
                                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                                );
                                AlarmUtils.getInstance().setCancelAlarm(activity, pendingIntent);
                                reminderViewModel.deleteReminder(reminderTag);
                                TextView tvReminderEdit = (TextView) v;
                                lnContainAllReminder.removeView(tvReminderEdit);
                            }
                        });
                        return false;
                    });
                } else {
                    Toast.makeText(activity, "You can only add up to 5 reminders", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tcAcAcceptManageReminder.setOnClickListener(click -> {
            miConsumerManagerReminder.accept();
            dialogManager.dismiss();
        });
        tcAcCancelManageReminder.setOnClickListener(click -> {
            miConsumerManagerReminder.accept();
            dialogManager.dismiss();
        });
        dialogManager.show();
    }

    public static String getTimeRepeat(long repeat) {
        if (repeat == 0L) {
            return Constants.NONE;
        } else if (repeat == Constants.daily) {
            return Constants.DAILY;
        } else if (repeat == Constants.weekly) {
            return Constants.WEEKLY;
        } else if (repeat == Constants.monthly) {
            return Constants.MONTHLY;
        }
        return Constants.YEARLY;
    }

    public static Long getTimeRepeat(String repeat) {
        if (Objects.equals(repeat, Constants.NONE)) {
            return 0L;
        } else if (Objects.equals(repeat, Constants.DAILY)) {
            return Constants.daily;
        } else if (Objects.equals(repeat, Constants.WEEKLY)) {
            return Constants.weekly;
        } else if (Objects.equals(repeat, Constants.MONTHLY)) {
            return Constants.monthly;
        }
        return Constants.yearly;
    }

    @SuppressLint("DefaultLocale")
    public static void ActionSetReminder(Context context, FragmentManager fragmentManager, long triggerPrevious, String repeatPrevious, MiConsumer miConsumer) {
        View viewDialog = View.inflate(context, R.layout.layout_dialog_set_time_reminder, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView tvShowTimeAdded = viewDialog.findViewById(R.id.tvShowTimeAdded);
        TextView tvShowTimeRepeat = viewDialog.findViewById(R.id.tvShowTimeRepeat);
        CalendarView calendarView = viewDialog.findViewById(R.id.vCalendarView);
        TextView tcAcCancelAddReminder = viewDialog.findViewById(R.id.tcAcCancelAddReminder);
        TextView tcAcSaveReminder = viewDialog.findViewById(R.id.tcAcSaveReminder);
        ConstraintLayout clAddTime = viewDialog.findViewById(R.id.clAddTime);
        ConstraintLayout clAddRepeat = viewDialog.findViewById(R.id.clAddRepeat);
        Calendar calendarAlarm = Calendar.getInstance();
        if (triggerPrevious != 0) {
            //neu truoc do da cai nhac nho roi
            calendarView.setDate(triggerPrevious);
            calendarAlarm.setTimeInMillis(triggerPrevious);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(triggerPrevious);
            String timePrevious = getFormatTime(calendar);
            tvShowTimeAdded.setText(timePrevious);
        } else {
            //khi chua chon ngay -> mac dinh la ngay hom nay
            Calendar calendarCurrent = Calendar.getInstance();
            int minutes = calendarCurrent.get(Calendar.MINUTE) + 1;
            calendarCurrent.set(Calendar.MINUTE, minutes);
            calendarView.setDate(calendarCurrent.getTimeInMillis());
            calendarAlarm.setTimeInMillis(calendarCurrent.getTimeInMillis());
            String timeCurrent = getFormatTime(calendarCurrent);
            tvShowTimeAdded.setText(timeCurrent);//hien thi thoi gian nen text
        }
        //khi user nhan cancel -> khong luu gi ca
        tcAcCancelAddReminder.setOnClickListener(v -> {
            dialog.dismiss();
        });
        //khi user nhan save -> gui trigger va repeat ve manager reminder
        tcAcSaveReminder.setOnClickListener(v -> {
            long triggerEdit = calendarAlarm.getTimeInMillis();
            if(triggerEdit > System.currentTimeMillis()){
                miConsumer.accept(triggerEdit, getTimeRepeat(tvShowTimeRepeat.getText().toString()));
            }
            dialog.dismiss();
        });

        //chon ngay
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            int hour = calendarAlarm.get(Calendar.HOUR_OF_DAY);
            int minute = calendarAlarm.get(Calendar.MINUTE);

            calendarAlarm.set(Calendar.YEAR, year);
            calendarAlarm.set(Calendar.MONTH, month);
            calendarAlarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
            calendarAlarm.set(Calendar.MINUTE, minute);
            calendarAlarm.set(Calendar.SECOND, 0);
            calendarAlarm.set(Calendar.MILLISECOND, 0);
        });

        //chon thoi gian
        clAddTime.setOnClickListener(v -> {
            //lay thoi gian hien tai set cho time picker
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            MaterialTimePicker builder_ = new MaterialTimePicker.Builder()
                    .setHour(hour)
                    .setMinute(minute)
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setTitleText("Select time")
                    .setNegativeButtonText("CANCEL")
                    .setPositiveButtonText("SAVE")
                    .setTheme(R.style.CustomTimePicker).build();

            //bat su kien chon thoi gian -> khi nhan chon
            builder_.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onClick(View v) {
                    int hour = builder_.getHour();
                    int minutes = builder_.getMinute();
                    //set thoi gian cho calendar
                    calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
                    calendarAlarm.set(Calendar.MINUTE, minutes);
                    calendarAlarm.set(Calendar.SECOND, 0);//giay thu 0 cua phut
                    calendarAlarm.set(Calendar.MILLISECOND,0);
                    tvShowTimeAdded.setText(getFormatTime(calendarAlarm));//hien thi thoi gian nen text
                }
            });
            builder_.show(fragmentManager, "Picker_Time");
        });

        //chon nhac lai hay khong
        //khi chua chon nhac lai mac dinh la repeat previous
        tvShowTimeRepeat.setText(repeatPrevious);
        clAddRepeat.setOnClickListener(v -> {
            //show dialog repeat de chon
            View viewDialogRepeat = View.inflate(context, R.layout.layout_dialog_reapeat, null);
            AlertDialog.Builder builderRepeat = new AlertDialog.Builder(context);
            builderRepeat.setView(viewDialogRepeat);
            AlertDialog dialogRepeat = builderRepeat.create();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialogRepeat.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogRepeat.show();
            //view
            TextView tcAcCancelAddRepeat = viewDialogRepeat.findViewById(R.id.tcAcCancelRepeat);
            TextView tcAcSaveRepeat = viewDialogRepeat.findViewById(R.id.tcAcAcceptRepeat);
            RadioGroup rbGroupRepeats = viewDialogRepeat.findViewById(R.id.rbGroupRepeats);

            //su kien chon thoi gian nhac lai
            final String[] strRepeat = {repeatPrevious};

            switch (repeatPrevious) {
                case Constants.NONE:
                    rbGroupRepeats.check(R.id.rbNone);
                    break;
                case Constants.DAILY:
                    rbGroupRepeats.check(R.id.rbDaily);
                    break;
                case Constants.WEEKLY:
                    rbGroupRepeats.check(R.id.rbWeekly);
                    break;
                case Constants.MONTHLY:
                    rbGroupRepeats.check(R.id.rbMonthly);
                    break;
                case Constants.YEARLY:
                    rbGroupRepeats.check(R.id.rbYearly);
                    break;
            }

            rbGroupRepeats.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbNone) {
                    strRepeat[0] = Constants.NONE;
                } else if (checkedId == R.id.rbDaily) {
                    strRepeat[0] = Constants.DAILY;
                } else if (checkedId == R.id.rbWeekly) {
                    strRepeat[0] = Constants.WEEKLY;
                } else if (checkedId == R.id.rbMonthly) {
                    strRepeat[0] = Constants.MONTHLY;
                } else if (checkedId == R.id.rbYearly) {
                    strRepeat[0] = Constants.YEARLY;
                }
            });
            //huy
            tcAcCancelAddRepeat.setOnClickListener(view -> {
                dialogRepeat.dismiss();
            });
            //save
            tcAcSaveRepeat.setOnClickListener(view -> {
                tvShowTimeRepeat.setText(strRepeat[0]);
                dialogRepeat.dismiss();
            });
        });
    }

    public static String getFormatTime(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static String getFormatReminder(Long trigger) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(trigger);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public interface MiConsumer {
        void accept(long trigger, long repeat);
    }

    public interface MiConsumerManagerReminder {
        void accept();
    }


    //dialog add image
    public static void showAddImage(Context context, Consumer<String> consumer) {
        View viewAddImage = View.inflate(context, R.layout.layout_add_photo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewAddImage);
        Dialog dialogAddImage = builder.create();
        Objects.requireNonNull(dialogAddImage.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddImage.setCanceledOnTouchOutside(true);
        dialogAddImage.setCancelable(true);
        dialogAddImage.show();

        //su li su kien
        TextView tvActionTakePhoto = viewAddImage.findViewById(R.id.acTakePhoto);
        TextView tvActionChooseImage = viewAddImage.findViewById(R.id.acChooseImage);

        tvActionChooseImage.setOnClickListener(v -> {
            consumer.accept(Constants.CHOOSE_IMAGE);
            dialogAddImage.dismiss();
        });

        tvActionTakePhoto.setOnClickListener(v -> {
            consumer.accept(Constants.TAKE_PHOTO);
            dialogAddImage.dismiss();
        });
    }

    public static void ActionClickLongItem(Context context, String action, Consumer<String> consumer) {
        View viewAddImage = View.inflate(context, R.layout.layout_dialog_click_item_trash, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewAddImage);
        Dialog dialogAddImage = builder.create();
        Objects.requireNonNull(dialogAddImage.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddImage.setCanceledOnTouchOutside(true);
        dialogAddImage.setCancelable(true);
        dialogAddImage.show();

        //su li su kien
        TextView tvActionRestore = viewAddImage.findViewById(R.id.acRestore);
        TextView tvActionDelete = viewAddImage.findViewById(R.id.acDeleteItem);

        if (action.equals(Constants.ARCHIVE)) {
            tvActionRestore.setText("Unarchive");
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getResources().getDrawable(R.drawable.ic_un_archive, null);
            tvActionRestore.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }

        tvActionRestore.setOnClickListener(v -> {
            consumer.accept("");
            dialogAddImage.dismiss();
        });

        tvActionDelete.setOnClickListener(v -> {
            consumer.accept("delete");
            dialogAddImage.dismiss();
        });
    }
}


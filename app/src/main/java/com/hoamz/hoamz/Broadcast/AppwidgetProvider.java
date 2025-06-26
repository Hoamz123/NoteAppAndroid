package com.hoamz.hoamz.Broadcast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.local.SharePre;
import com.hoamz.hoamz.ui.act.Splash;
import com.hoamz.hoamz.ui.fragment.FragmentWidget;
import com.hoamz.hoamz.utils.Constants;

public class AppwidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        ///noi xu ly khi user click vao (keo widget) ra hien thi
        /**
         * -> di den splash -> sang fgWidget(edit) -> senBroadCart ->
         */
        for(int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, Splash.class);
            intent.setAction(Constants.EDIT);
            intent.putExtra(Constants.ID_WIDGET_CLICK, appWidgetId);
            String contentWidget = SharePre.getInstance(context).getContentWidget(appWidgetId);
            int idBackgroundWidget = SharePre.getInstance(context).getIdBackgroundWidget(appWidgetId);
            String date = Constants.getCurrentDay();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            //khoi tao remoteView
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setOnClickPendingIntent(R.id.tvContentWidget, pendingIntent);
            if(idBackgroundWidget != 0){
                remoteViews.setTextViewText(R.id.tvContentWidget, contentWidget);
                remoteViews.setTextViewText(R.id.tv_dateWG, date);
                remoteViews.setInt(R.id.mainRelayout,"setBackgroundResource",idBackgroundWidget);
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //xoa noi dung da luu trong sharePre theo id
        for(int appWidgetId : appWidgetIds){
            SharePre.getInstance(context).saveContentWidget(appWidgetId,"");
            SharePre.getInstance(context).saveIdBackgroundWidget(appWidgetId,0);
        }
    }
}

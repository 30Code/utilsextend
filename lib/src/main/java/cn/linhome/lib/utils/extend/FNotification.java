package cn.linhome.lib.utils.extend;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * 通知
 */
public class FNotification extends Notification.Builder
{
    private Context mContext;
    private NotificationManager mManager;

    public FNotification(Context context)
    {
        super(context.getApplicationContext());
        mContext = context.getApplicationContext();
        mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //---------- NotificationManager start ----------

    public void notify(int id)
    {
        mManager.notify(id, getNotification());
    }

    public void notify(String tag, int id)
    {
        mManager.notify(tag, id, getNotification());
    }

    public void cancel(int id)
    {
        mManager.cancel(id);
    }

    public void cancel(String tag, int id)
    {
        mManager.cancel(tag, id);
    }

    public void cancelAll()
    {
        mManager.cancelAll();
    }

    //---------- NotificationManager end ----------
}

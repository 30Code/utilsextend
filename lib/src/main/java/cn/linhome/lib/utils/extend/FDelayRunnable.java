package cn.linhome.lib.utils.extend;

import android.os.Handler;
import android.os.Looper;

/**
 * 延迟执行Runnable
 */
public abstract class FDelayRunnable implements Runnable
{
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 延迟执行
     *
     * @param delay (单位毫秒)
     */
    public final void runDelay(long delay)
    {
        removeDelay();
        MAIN_HANDLER.postDelayed(this, delay);
    }

    /**
     * 立即在当前线程执行，如果有延迟任务会先移除延迟任务
     */
    public final void runImmediately()
    {
        removeDelay();
        run();
    }

    /**
     * 延迟或者立即执行
     *
     * @param delay (单位毫秒) 小于等于0-立即在当前线程执行，大于0-延迟执行
     */
    public final void runDelayOrImmediately(long delay)
    {
        if (delay > 0)
        {
            runDelay(delay);
        } else
        {
            runImmediately();
        }
    }

    /**
     * 移除延迟任务
     */
    public final void removeDelay()
    {
        MAIN_HANDLER.removeCallbacks(this);
    }
}

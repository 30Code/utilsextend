package cn.linhome.lib.utils.extend;

/**
 * 循环线程
 */
public abstract class FLoopThread extends Thread
{
    private boolean mIsPaused = false;

    /**
     * 是否处于暂停循环状态
     *
     * @return
     */
    public synchronized final boolean isPaused()
    {
        return mIsPaused;
    }

    /**
     * 暂停循环
     */
    public final void pauseLoop()
    {
        synchronized (this)
        {
            mIsPaused = true;
        }
    }

    /**
     * 恢复循环
     */
    public final void resumeLoop()
    {
        synchronized (this)
        {
            mIsPaused = false;
            notifyAll();
        }
    }

    @Override
    public final void run()
    {
        super.run();
        try
        {
            onStart();
            while (!isInterrupted())
            {
                if (mIsPaused)
                {
                    onPaused();
                    synchronized (this)
                    {
                        wait();
                    }
                    onResume();
                }
                onLoop();
            }
        } catch (Exception e)
        {
            onError(e);
        } finally
        {
            onFinally();
        }
    }

    protected abstract void onLoop() throws Exception;

    protected void onStart()
    {
    }

    protected void onPaused()
    {
    }

    protected void onResume()
    {
    }

    protected void onFinally()
    {
    }

    protected void onError(Exception e)
    {
    }
}

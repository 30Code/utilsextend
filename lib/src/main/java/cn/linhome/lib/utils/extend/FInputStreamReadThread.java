package cn.linhome.lib.utils.extend;

import java.io.InputStream;

/**
 * 输入流读取线程
 */
public abstract class FInputStreamReadThread extends Thread
{
    private InputStream mInputStream;
    private ReadConfig mReadConfig;

    public FInputStreamReadThread(ReadConfig readConfig)
    {
        super("FInputStreamReadThread");
        if (readConfig == null)
        {
            readConfig = new ReadConfig();
        } else
        {
            if (readConfig.buffer == null)
            {
                throw new NullPointerException("ReadConfig.buffer must not be null");
            }
            if (readConfig.buffer.length <= 0)
            {
                throw new NullPointerException("ReadConfig.buffer length must not be > 0");
            }
        }
        mReadConfig = readConfig;
    }

    /**
     * 设置输入流
     *
     * @param inputStream
     */
    public void setInputStream(InputStream inputStream)
    {
        mInputStream = inputStream;
    }

    private void trySleep() throws InterruptedException
    {
        if (mReadConfig.sleepTime > 0)
        {
            sleep(mReadConfig.sleepTime);
        }
    }

    @Override
    public final void run()
    {
        super.run();

        while (!isInterrupted())
        {
            try
            {
                if (mInputStream == null)
                {
                    trySleep();
                } else
                {
                    int readSize = mInputStream.read(mReadConfig.buffer);
                    if (readSize > 0)
                    {
                        onReadData(mReadConfig.buffer, readSize);
                    } else
                    {
                        trySleep();
                    }
                }
            } catch (Exception e)
            {
                if (e instanceof InterruptedException)
                {
                    return;
                } else
                {
                    onReadError(e);
                }
            }
        }
    }

    /**
     * 读取到数据
     *
     * @param data
     * @param readSize
     */
    protected abstract void onReadData(byte[] data, int readSize);

    /**
     * 读取异常
     *
     * @param e
     */
    protected abstract void onReadError(Exception e);

    /**
     * 读取配置
     */
    public static class ReadConfig
    {
        /**
         * 用于接收读取数据的字节数组
         */
        public byte[] buffer = new byte[64];
        public long sleepTime = 1000;
    }
}

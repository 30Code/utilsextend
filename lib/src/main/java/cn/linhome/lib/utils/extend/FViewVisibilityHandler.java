package cn.linhome.lib.utils.extend;

import android.animation.Animator;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * view的显示隐藏处理
 */
public class FViewVisibilityHandler
{
    private WeakReference<View> mView;
    /**
     * 显示动画
     */
    private Animator mVisibleAnimator;
    /**
     * 隐藏动画
     */
    private Animator mInvisibleAnimator;
    /**
     * true-gone，false-invisible
     */
    private boolean mIsGoneMode = true;

    private VisibilityInvoker mVisibilityInvoker;

    public FViewVisibilityHandler(View view)
    {
        if (view == null)
        {
            throw new NullPointerException("view is null");
        }
        mView = new WeakReference<>(view);
    }

    private View getView()
    {
        return mView == null ? null : mView.get();
    }

    public void setVisibilityInvoker(VisibilityInvoker visibilityInvoker)
    {
        mVisibilityInvoker = visibilityInvoker;
    }

    private VisibilityInvoker getVisibilityInvoker()
    {
        if (mVisibilityInvoker == null)
        {
            mVisibilityInvoker = new VisibilityInvoker()
            {
                @Override
                public void setVisibility(View view, int visibility)
                {
                    view.setVisibility(visibility);
                }
            };
        }
        return mVisibilityInvoker;
    }

    /**
     * 设置显示动画
     *
     * @param visibleAnimator
     */
    public void setVisibleAnimator(Animator visibleAnimator)
    {
        if (mVisibleAnimator != visibleAnimator)
        {
            if (mVisibleAnimator != null)
            {
                mVisibleAnimator.removeListener(mVisibleListener);
            }
            mVisibleAnimator = visibleAnimator;
            if (visibleAnimator != null)
            {
                visibleAnimator.addListener(mVisibleListener);
            }
        }
    }

    /**
     * 设置隐藏动画
     *
     * @param invisibleAnimator
     */
    public void setInvisibleAnimator(Animator invisibleAnimator)
    {
        if (mInvisibleAnimator != invisibleAnimator)
        {
            if (mInvisibleAnimator != null)
            {
                mInvisibleAnimator.removeListener(mInvisibleListener);
            }
            mInvisibleAnimator = invisibleAnimator;
            if (invisibleAnimator != null)
            {
                invisibleAnimator.addListener(mInvisibleListener);
            }
        }
    }

    /**
     * 设置View的可见状态
     *
     * @param visibility
     * @param anim
     */
    public void setVisibility(int visibility, boolean anim)
    {
        final View view = getView();
        if (view == null)
        {
            return;
        }
        if (visibility == view.getVisibility())
        {
            return;
        }

        if (visibility == View.VISIBLE)
        {
            setVisible(anim);
        } else if (visibility == View.INVISIBLE)
        {
            setInvisible(anim);
        } else if (visibility == View.GONE)
        {
            setGone(anim);
        }
    }

    private void setVisible(boolean anim)
    {
        if (anim)
        {
            startVisibleAnimator();
        } else
        {
            setVisibilityDirectly(View.VISIBLE);
        }
    }

    /**
     * 开始显示动画
     */
    private void startVisibleAnimator()
    {
        if (isVisibleAnimatorStarted())
        {
            return;
        }

        cancelInvisibleAnimatorIfNeed();
        if (mVisibleAnimator != null)
        {
            mVisibleAnimator.start();
        } else
        {
            setVisibilityDirectly(View.VISIBLE);
        }
    }

    private void setInvisible(boolean anim)
    {
        if (anim)
        {
            startInvisibleAnimator(false);
        } else
        {
            setVisibilityDirectly(View.INVISIBLE);
        }
    }

    private void setGone(boolean anim)
    {
        if (anim)
        {
            startInvisibleAnimator(true);
        } else
        {
            setVisibilityDirectly(View.GONE);
        }
    }

    /**
     * 开始隐藏动画
     *
     * @param isGoneMode
     */
    private void startInvisibleAnimator(boolean isGoneMode)
    {
        if (isInvisibleAnimatorStarted())
        {
            return;
        }

        mIsGoneMode = isGoneMode;
        cancelVisibleAnimatorIfNeed();
        if (mInvisibleAnimator != null)
        {
            mInvisibleAnimator.start();
        } else
        {
            if (isGoneMode)
            {
                setVisibilityDirectly(View.GONE);
            } else
            {
                setVisibilityDirectly(View.INVISIBLE);
            }
        }
    }

    private void setVisibilityDirectly(int visibility)
    {
        final View view = getView();
        if (view == null)
        {
            return;
        }
        getVisibilityInvoker().setVisibility(view, visibility);
    }

    /**
     * 显示动画是否已经启动
     */
    private boolean isVisibleAnimatorStarted()
    {
        return mVisibleAnimator != null && mVisibleAnimator.isStarted();
    }

    /**
     * 隐藏动画是否已经启动
     *
     * @return
     */
    private boolean isInvisibleAnimatorStarted()
    {
        return mInvisibleAnimator != null && mInvisibleAnimator.isStarted();
    }

    /**
     * 取消显示动画
     */
    private void cancelVisibleAnimatorIfNeed()
    {
        if (mVisibleAnimator != null)
        {
            mVisibleAnimator.cancel();
        }
    }

    /**
     * 取消隐藏动画
     */
    private void cancelInvisibleAnimatorIfNeed()
    {
        if (mInvisibleAnimator != null)
        {
            mInvisibleAnimator.cancel();
        }
    }

    /**
     * 显示监听
     */
    private Animator.AnimatorListener mVisibleListener = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animation)
        {
            setVisibilityDirectly(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {
        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {
        }
    };

    /**
     * 隐藏监听
     */
    private Animator.AnimatorListener mInvisibleListener = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animation)
        {
        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
            if (mIsGoneMode)
            {
                setVisibilityDirectly(View.GONE);
            } else
            {
                setVisibilityDirectly(View.INVISIBLE);
            }
            resetView(getView());
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {
            if (mIsGoneMode)
            {
                setVisibilityDirectly(View.GONE);
            } else
            {
                setVisibilityDirectly(View.INVISIBLE);
            }
            resetView(getView());
        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {
        }
    };

    private static void resetView(View view)
    {
        if (view != null)
        {
            view.setAlpha(1.0f);
            view.setRotation(0.0f);
            view.setRotationX(0.0f);
            view.setRotationY(0.0f);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        }
    }

    /**
     * 在View.VISIBLE和View.GONE之前切换
     *
     * @param anim
     */
    public void toggleVisibleOrGone(boolean anim)
    {
        final View view = getView();
        if (view == null)
        {
            return;
        }

        if (view.getVisibility() == View.VISIBLE)
        {
            setGone(anim);
        } else
        {
            setVisible(anim);
        }
    }

    /**
     * 在View.VISIBLE和View.INVISIBLE之前切换
     *
     * @param anim
     */
    public void toggleVisibleOrInvisible(boolean anim)
    {
        final View view = getView();
        if (view == null)
        {
            return;
        }

        if (view.getVisibility() == View.VISIBLE)
        {
            setInvisible(anim);
        } else
        {
            setVisible(anim);
        }
    }

    public interface VisibilityInvoker
    {
        void setVisibility(View view, int visibility);
    }
}

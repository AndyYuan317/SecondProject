package com.wingplus.coomohome.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.manager.ThreadManager;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.ViewUtils;


/**
 * 自定义界面的帧布局
 * 用一个Framelayout装下四种界面：加载中，加载失败，加载为空，加载成功。
 * @author leaffun
 * 
 */
public abstract class LoadingPage extends FrameLayout {

	public static final int STATE_UNKNOWN = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_ERROR = 2;
	public static final int STATE_EMPTY = 3;
	public static final int STATE_SUCCESS = 4;
	public int state = STATE_UNKNOWN;

	private View loading;
	private View errorLoad;
	private View emptyLoad;
	private View success;

	public LoadingPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LoadingPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoadingPage(Context context) {
		super(context);
		init();
	}

	/** 在framelayout中添加4种不同的界面 */
	public void init() {
		loading = createLoading(); // 加载中
		if (loading != null) { // 防止子类返回null
			addView(loading, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		}
		errorLoad = createEmptyLoad(); // 加载失败
		if (errorLoad != null) {
			addView(errorLoad, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		}
		emptyLoad = createEmptyLoad(); // 加载为空
		if (emptyLoad != null) {
			addView(emptyLoad, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		}
		showPage();
	}

	/** 根据不同的状态显示不同的界面 */
	public void showPage() {

		if (loading != null) {
			loading.setVisibility(state == STATE_UNKNOWN
					|| state == STATE_LOADING ? View.VISIBLE : View.INVISIBLE);
		}
		if (errorLoad != null) {
			errorLoad.setVisibility(state == STATE_ERROR ? View.VISIBLE
					: View.INVISIBLE);
		}
		if (emptyLoad != null) {
			emptyLoad.setVisibility(state == STATE_EMPTY ? View.VISIBLE
					: View.INVISIBLE);
		}
		if (state == STATE_SUCCESS && success == null) {//每个viewpager下的Fragment都只有一个success对象
				success = createSuccessLoad();
			if (success != null) {
				ViewUtils.removeParent(success);// 如果是每次都创建新的success，那么就要走这个方法。
				
				addView(success);
			}
		}

		if (success != null) {
			success.setVisibility(state == STATE_SUCCESS ? View.VISIBLE
					: View.INVISIBLE);
		}

		refreshUIInHere();
	}

	protected abstract void refreshUIInHere();

	/** 请求数据 切换状态 */
	public void showState() {
		if (state == STATE_EMPTY || state == STATE_ERROR) {
			// 如果以失败或空数据的状态进来则重新恢复unknown状态,-->其实每次请求数据，都应该恢复unknown状态才保证初始化不出错
			state = STATE_UNKNOWN;
		}
		
		//线程池改造
		ThreadManager.getThreadPool().exeute(new Runnable() {
			
			@Override
			public void run() {
				final LoadResult result = load(); // 请求服务器数据
				UIUtils.runOnUIThread(new Runnable() {

					@Override
					public void run() {
						if (result != null) {
							 state = result.getValue();
						}else{
							state = STATE_ERROR;
						}
						showPage(); // 状态改变，就切换界面
					}
				});
			}
		});
	}

	/* 请求结果枚举 */
	public enum LoadResult {
		error(2), empty(3), success(4);
		int value;

		// 分别赋值
		LoadResult(int value) {
			this.value = value;
		}

		// 对应取值
		public int getValue() {
			return value;
		}
	}

	/* 返回加载为空界面 */
	private View createEmptyLoad() {
		View view = View.inflate(UIUtils.getContext(), R.layout.loadpage_empty,
				null);
		ImageView img = view.findViewById(R.id.iv);
		setEmptyImage(img);
		return view;
	}

    /**
     * 开放给子类重写，设置数据为空时的图片
     * @param img
     */
    public void setEmptyImage(ImageView img) {

    }

    /* 返回加载失败界面 */
	private View createErrorLoad() {
		View view = View.inflate(UIUtils.getContext(), R.layout.loadpage_error,
				null);
		Button page_bt = view.findViewById(R.id.page_bt);
		page_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 重新加载
                state = STATE_LOADING;
                showPage();
				showState();
			}
		});
		return view;
	}

	/* 返回加载中界面 */
	private View createLoading() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.loadpage_loading, null);
		return view;
	}

	// 由子类实现加载成功的界面
	public abstract View createSuccessLoad();

	// 由子类实现请求数据的结果
	public abstract LoadResult load();
}

package com.android.picshow.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.android.picshow.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuntao.wei on 2018/1/2.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PicPopupWindow {
	private ViewGroup mPopupContent;
	private ViewGroup mPopupItemContent;
	private PopupWindow mPopupWindow;
	private TextView title;
	private Context mContext;
	private LayoutInflater mInflater;
	private ActivityAnimationListener mAnimationListener;
	private HashMap<String, ItemViewHolder> mViewMap = new HashMap<String, ItemViewHolder>();
	private String mSelected;

	public interface ActivityAnimationListener {

		public void doAnimation(boolean in);
	}

	public interface PicPopupWindowListener {

		public void onClick(View v);

	}

	class PicActivityAnimationListener implements ActivityAnimationListener {

		private static final float inAlpha = 0.3f;
		private static final float outAlpha = 1.0f;

		private Window mWindow;

		public PicActivityAnimationListener(Activity activity) {
			if (activity == null) {
				throw new RuntimeException(
						"ActivityAnimationListener Activity is null");
			}
			mWindow = activity.getWindow();
		}

		@Override
		public void doAnimation(boolean in) {
			// TODO Auto-generated method stub
			if (in) {
				setPopupWindowAlpha();
			} else {
				resetPopupWindowAlpha();
			}
		}

		private void setPopupWindowAlpha() {
			setWindowAlpha(inAlpha);
		}

		private void resetPopupWindowAlpha() {
			setWindowAlpha(outAlpha);
		}

		private void setWindowAlpha(float f) {
			WindowManager.LayoutParams lp = mWindow.getAttributes();
			lp.alpha = f;
			mWindow.setAttributes(lp);
		}

	}

	class ItemViewHolder {
		private View mItem;
		private View mIcon;
		private TextView mText;

		public ItemViewHolder(View item, View icon, TextView text) {
			mItem = item;
			mIcon = icon;
			mText = text;
		}

		public View getItem() {
			return mItem;
		}

		public View getIcon() {
			return mIcon;
		}

		public TextView getTextView() {
			return mText;
		}
	}

	/*
	 * get a PopupWindow instance
	 */
	public static PicPopupWindow getPicPopupWindow(Context context) {
		return new PicPopupWindow(context);
	}

	private PicPopupWindow() {

	}

	private PicPopupWindow(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupContent = (ViewGroup) mInflater.inflate(
				R.layout.popup_window_list, null);
		mPopupItemContent = (ViewGroup) mPopupContent
				.findViewById(R.id.popup_window_item_content);
		title = (TextView) mPopupContent
				.findViewById(R.id.popup_window_title_text);
		mPopupWindow = new PopupWindow(mPopupContent,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				resetItemByTag(setTag);
				if (mAnimationListener != null) {
					mAnimationListener.doAnimation(false);
				}
			}
		});
		mPopupWindow.setAnimationStyle(R.style.popup_window_animation);
	}

	/*
	 * set animation listener for popup window
	 */
	public void setActivityAnimationListener(
			ActivityAnimationListener animationListener) {
		mAnimationListener = animationListener;
	}

	/*
	 * use the default animation for popup window
	 */
	public void setActivityAnimationListener(Activity activity) {
		mAnimationListener = new PicActivityAnimationListener(activity);
	}

	/*
	 * set popup window title
	 */
	public void setPopupWindowTitle(int id) {
		title.setText(id);
		title.setVisibility(View.VISIBLE);
	}

    /*
     * set popup window title
     * @windowTitle A string will be set as window title.
     */
    public void setPopupWindowTitle(CharSequence windowTitle) {
        title.setText(windowTitle);
		title.setVisibility(View.VISIBLE);
    }

	/*
	 * add popup window item
	 */
	public void addPopupWindowItem(String title, String tag,
                                   final PicPopupWindowListener listener) {
		View item = mInflater.inflate(R.layout.item_popup_window, null);
		View icon = item.findViewById(R.id.item_popup_window_icon);
		TextView itemText = (TextView) item
				.findViewById(R.id.item_popup_window_text);
		itemText.setText(title);
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onClick(v);
				mPopupWindow.dismiss();
			}
		});
        item.setTag(title);
		ItemViewHolder holder = new ItemViewHolder(item, icon, itemText);
		mViewMap.put(tag, holder);
		mPopupItemContent.addView(item);
	}

	/*
	 * remove popup window item
	 */
	public void removePopupWindowItemByTag(String tag) {
		ItemViewHolder holder = mViewMap.remove(tag);
		if (holder != null) {
			mPopupItemContent.removeView(holder.getItem());
		}
	}

	/*
	 * remove all popup window item
	 */
	public void removeAllPopupWindowItem() {
		Iterator<Map.Entry<String, ItemViewHolder>> iterator = mViewMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			mPopupItemContent.removeView(iterator.next().getValue().getItem());
		}
		mViewMap.clear();
	}

	/*
	 * get popup window item count
	 */
	public int getItemCount() {
		return mViewMap.size();
	}

	/*
	 * set selected item
	 */
	public void setSelectedItemByTag(String tag) {
		setTag = tag;
		ItemViewHolder holder = mViewMap.get(tag);
		if (holder != null) {
			holder.getIcon().setVisibility(View.VISIBLE);
			holder.getTextView().setTextColor(
					mContext.getResources().getColor(R.color.accent_color));
			
		}
	}

    private String setTag;
    public void resetItemByTag(String tag) {
    	if(setTag == null) return;
    	ItemViewHolder holder = mViewMap.get(tag);
		if (holder != null) {
			holder.getIcon().setVisibility(View.VISIBLE);
			holder.getTextView().setTextColor(
					mContext.getResources().getColor(R.color.top_btn_text_select_color));
		}
		setTag = null;
    }

	/*
	 * to see if we have a listener
	 */
	public boolean hasAnimationListener() {
		return (mAnimationListener != null);
	}

	/*
	 * set popup window in/out animation style
	 */
	public void setAnimationStyle(int animationStyle) {
		mPopupWindow.setAnimationStyle(animationStyle);
	}

	/*
	 * show popup window by default
	 */
	public void show(View parent) {
		show(parent, Gravity.BOTTOM, 0, 0);
	}

	/*
	 * show popup window
	 */
	public void show(View parent, int gravity, int x, int y) {
		mPopupWindow.showAtLocation(parent, gravity, x, y);
		if (mAnimationListener != null) {
			mAnimationListener.doAnimation(true);
		}
	}
}

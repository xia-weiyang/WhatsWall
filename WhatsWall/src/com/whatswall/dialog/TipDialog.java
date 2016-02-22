package com.whatswall.dialog;

import com.whatswall.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class TipDialog extends AlertDialog {

	public TipDialog(Context context) {
		super(context);

	}

	public TipDialog(Context context, int theme) {
		super(context, theme);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tip);
	}
	
	public void setPosition(int x, int y) {
		
		Window window = this.getWindow();
		LayoutParams params = new LayoutParams();
		params.x = x;//设置x坐标
		params.y = y;//设置y坐标
		window.setAttributes(params);
		//设置点击Dialog外部任意区域关闭Dialog
		this.setCanceledOnTouchOutside(true);
	}
	
	public void setContent(String content){
		
		TextView textView = (TextView) this.findViewById(R.id.dialog_tip_content);
		textView.setText(content);
	}

	public void setIKnowClickListener(android.view.View.OnClickListener l){
		
		Button iKnow = (Button) this.findViewById(R.id.dialog_tip_Iknow);
		
		iKnow.setOnClickListener(l);
	}
	
}

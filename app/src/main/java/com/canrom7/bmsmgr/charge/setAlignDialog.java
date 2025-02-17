package com.canrom7.bmsmgr.charge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.canrom7.bmsmgr.R;


/* loaded from: classes.dex */
public class setAlignDialog extends Dialog {
    private TextView alignNum;
    private EditText alignNumIn;
    private TextView alignTips;
    private Bundle bundle;
    private View.OnClickListener buttonDialogListener;
    private DialogCallback callback;
    private Button confirm;

    public interface DialogCallback {
        void DialogReturn(int i);
    }

    private void assignViews() {
        this.alignTips = (TextView) findViewById(R.id.align_tips);
        this.alignNum = (TextView) findViewById(R.id.align_num);
        this.alignNumIn = (EditText) findViewById(R.id.align_num_in);
        this.confirm = (Button) findViewById(R.id.confirm);
    }

    protected setAlignDialog(Context context, DialogCallback callback) {
        super(context);
        this.buttonDialogListener = new View.OnClickListener() { // from class: com.example.main.setAlignDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (view.getId() == R.id.confirm) {
                    String s = setAlignDialog.this.alignNumIn.getText().toString();
                    if (s.length() == 0) {
                        setAlignDialog.this.alignNumIn.setHint("输入不能为空");
                        return;
                    }
                    int num = Integer.valueOf(s).intValue();
                    setAlignDialog.this.callback.DialogReturn(num);
                    setAlignDialog.this.dismiss();
                }
            }
        };
        this.callback = callback;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_align_dialog);
        setTitle("对齐设置");
        setCanceledOnTouchOutside(false);
        assignViews();
        this.confirm.setOnClickListener(this.buttonDialogListener);
    }
}
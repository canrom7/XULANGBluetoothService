package com.canrom7.bmsmgr.charge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/* loaded from: classes.dex */
public class defineButtonDialog extends Dialog {
    private EditText Str2Send;
    private View.OnClickListener buttonDialogListener;
    private EditText buttonName;
    private Button callButton;
    private Button cancelButton;
    private StringBuffer codeString;
    private TextView errorTip;
    private Button okButton;

    protected defineButtonDialog(Context context, Button callButton, StringBuffer Str2Send) {
        super(context);
        this.buttonDialogListener = new View.OnClickListener() { // from class: com.example.main.defineButtonDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (view.getId() == R.id.okButton) {
                    String str = defineButtonDialog.this.buttonName.getText().toString();
                    if (str.length() == 0) {
                        defineButtonDialog.this.errorTip.setVisibility(0);
                        defineButtonDialog.this.errorTip.setText("按键名称不能为空");
                        return;
                    }
                    if (str.length() > 2) {
                        defineButtonDialog.this.errorTip.setVisibility(0);
                        defineButtonDialog.this.errorTip.setText("按键名称需少于2个字");
                        return;
                    }
                    String str2 = defineButtonDialog.this.Str2Send.getText().toString();
                    if (str2.length() == 0) {
                        defineButtonDialog.this.errorTip.setVisibility(0);
                        defineButtonDialog.this.errorTip.setText("发送文本不能为空");
                        return;
                    }
                    if (MainActivity.isHEXsend) {
                        for (char c : str2.toCharArray()) {
                            if ((c < '0' || c > '9') && ((c < 'a' || c > 'f') && ((c < 'A' || c > 'F') && c != ' '))) {
                                defineButtonDialog.this.errorTip.setVisibility(0);
                                defineButtonDialog.this.errorTip.setText("发送文本含非法字符(当前为HEX发送)");
                                return;
                            }
                        }
                    }
                    defineButtonDialog.this.callButton.setText(defineButtonDialog.this.buttonName.getText());
                    defineButtonDialog.this.codeString.setLength(0);
                    defineButtonDialog.this.codeString.append(str2);
                    defineButtonDialog.this.cancel();
                    return;
                }
                defineButtonDialog.this.dismiss();
            }
        };
        this.callButton = callButton;
        this.codeString = Str2Send;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_dialog);
        setTitle("按键配置");
        setCanceledOnTouchOutside(false);
        this.errorTip = (TextView) findViewById(R.id.errorTip);
        this.buttonName = (EditText) findViewById(R.id.buttonName);
        this.Str2Send = (EditText) findViewById(R.id.Str2Send);
        this.cancelButton = (Button) findViewById(R.id.cancelButton);
        this.okButton = (Button) findViewById(R.id.okButton);
        this.cancelButton.setOnClickListener(this.buttonDialogListener);
        this.okButton.setOnClickListener(this.buttonDialogListener);
        this.buttonName.setText(this.callButton.getText());
        this.Str2Send.setText(this.codeString);
    }
}
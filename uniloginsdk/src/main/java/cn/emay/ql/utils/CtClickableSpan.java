package cn.emay.ql.utils;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import cn.com.chinatelecom.account.sdk.CtAuth;

public class CtClickableSpan extends ClickableSpan {

    private String protocolUrl , protocolTitle;
    private int textColorInt;
    private Context context;
    public CtClickableSpan(Context context , String protocolUrl , String protocolTitle , int textColorInt){
        this.context = context;
        this.protocolUrl = protocolUrl;
        this.protocolTitle = protocolTitle;
        this.textColorInt = textColorInt;
    }
    @Override
    public void onClick(View widget) {
        CtAuth.getInstance().openWebviewActivity(context , protocolUrl ,protocolTitle );
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        if(textColorInt != 0){
            paint.setColor(textColorInt);
        }
        // 设置下划线 true显示、false不显示
        paint.setUnderlineText(false);
    }
}

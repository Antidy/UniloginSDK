package cn.emay.ql.net;

public abstract class HttpCallback {

    public abstract void onSuccess(String msg);

    public abstract void onFailed(String msg);
}
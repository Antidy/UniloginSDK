package cn.emay.ql.listeners;

public abstract class LoginCallback {

    public abstract void onSuccess(String msg);

    public abstract void onFailed(String msg);
}
package com.udemy.basemodule.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseActivity<BD extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {

    protected BD binding;
    protected VM viewModel;
    private PermissionCallback permissionCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
    }

    protected abstract Class<VM> getViewModelClass();

    @LayoutRes
    protected abstract int getLayoutId();

    protected void checkPermission(String[] p, PermissionCallback callback){
        if (checkPermission(p, true)){
            callback.onGranted();
        }else {
            this.permissionCallback = callback;
        }
    }

    private boolean checkPermission(String[] permission, boolean withRequest){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (String p : permission){
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED){
                    if (withRequest){
                        requestPermissions(permission, 0);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermission(permissions, false)){
            permissionCallback.onGranted();
        }else {
            permissionCallback.onDenied();
        }
    }
}

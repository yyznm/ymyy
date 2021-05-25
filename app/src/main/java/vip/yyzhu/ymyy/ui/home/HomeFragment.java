package vip.yyzhu.ymyy.ui.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import vip.yyzhu.ymyy.R;
import vip.yyzhu.ymyy.WebActivity;
import vip.yyzhu.ymyy.service.FileService;
import vip.yyzhu.ymyy.tool.Tool;

import static android.content.Context.BIND_AUTO_CREATE;

public class HomeFragment extends Fragment {

    Context con;
    HomeFragment _this = this;
    private FileService.FileBinder iBinder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        con = getActivity();
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // 微信一秒语音
        View btn = root.findViewById(R.id.btn);
        // 必读
        View btn2 = root.findViewById(R.id.btn2);
        // 微信一秒语音 点击事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动 FileService
                Intent intent3 = new Intent(con, FileService.class);
                con.startService(intent3);
                con.bindService(intent3, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder1) {
                        //获得service中的MyBinder
                        iBinder = (FileService.FileBinder) iBinder1;
                        // 显示悬浮窗
                        iBinder.beginWX();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                }, BIND_AUTO_CREATE);
            }
        });

        // 必读 点击事件
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转的 WebActivity 页面
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("mod", 1);
                intent.putExtra("url", "http://ymyy.yyzhu.vip/tutorial.html");
                con.startActivity(intent);
            }
        });
        return root;
    }
}
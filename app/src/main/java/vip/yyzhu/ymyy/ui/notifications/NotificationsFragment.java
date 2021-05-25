package vip.yyzhu.ymyy.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import vip.yyzhu.ymyy.R;
import vip.yyzhu.ymyy.WebActivity;
import vip.yyzhu.ymyy.tool.Tool;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    //Browser实体集合
    List<BrowserAdapter.Browser> browsers = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        notificationsViewModel =
//                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        TextView ver_name = root.findViewById(R.id.ver_name);
        ver_name.setText("v" + Tool.getVerName(getActivity()));

        //初始化数据
        browsers.add(new BrowserAdapter.Browser("项目地址", R.drawable.on_gitee));
        browsers.add(new BrowserAdapter.Browser("关于我", R.drawable.on_about_me));
        browsers.add(new BrowserAdapter.Browser("日志", R.drawable.on_log));
        browsers.add(new BrowserAdapter.Browser("反馈", R.drawable.on_feedback));
        browsers.add(new BrowserAdapter.Browser("赞赏", R.drawable.on_appreciate));

        //初始化适配器
        BrowserAdapter adapter = new BrowserAdapter(getActivity(), R.layout.about_item, browsers);
        ListView listView = root.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("mod", 1);
                if(i == 0){
//                    Tool.makeText1(getActivity(), browsers.get(i).getName() + " 在做了在做了");
                    intent.putExtra("url", "http://ymyy.yyzhu.vip/gitee.html");
                    getActivity().startActivity(intent);
                }else if(i == 1){
                    intent.putExtra("url", "http://ymyy.yyzhu.vip/about_me.html");
                    getActivity().startActivity(intent);
                }else if(i == 2){
                    intent.putExtra("url", "http://ymyy.yyzhu.vip/log.html");
                    getActivity().startActivity(intent);
                }else if(i == 3){
                    intent.putExtra("url", "http://ymyy.yyzhu.vip/feedback.html");
                    getActivity().startActivity(intent);
                }else if(i == 4){
                    intent.putExtra("url", "http://ymyy.yyzhu.vip/appreciate.html");
                    getActivity().startActivity(intent);
                }
            }
        });

        return root;
    }
}
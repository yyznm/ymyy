package vip.yyzhu.ymyy.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import vip.yyzhu.ymyy.R;

import java.util.List;

//自定义适配器，继承自ArrayAdapter
public class BrowserAdapter extends ArrayAdapter<BrowserAdapter.Browser> {
    //resourceID指定ListView的布局方式
    private int resourceID;
    //重写BrowserAdapter的构造器
    public BrowserAdapter(Context context, int textViewResourceID , List<Browser> objects){
        super(context,textViewResourceID,objects);
        resourceID = textViewResourceID;
    }
    //自定义item资源的解析方式
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取当前Browser实例
        Browser browser = getItem(position);
        //使用LayoutInfater为子项加载传入的布局
        View view = LayoutInflater.from(getContext()).inflate(resourceID,null);
        ImageView browserIcon = (ImageView)view.findViewById(R.id.icon);
        TextView browserName = (TextView)view.findViewById(R.id.name);
        //引入Browser对象的属性值
        browserIcon.setImageResource(browser.getIcon());
        browserName.setText(browser.getName());
        return view;
    }

    public static class Browser {
        private String name;
        private int icon;

        public Browser(String name, int icon) {
            this.name = name;
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public int getIcon() {
            return icon;
        }
    }
}
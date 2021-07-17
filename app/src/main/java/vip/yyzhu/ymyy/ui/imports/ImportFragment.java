package vip.yyzhu.ymyy.ui.imports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vip.yyzhu.ymyy.InputFileActivity;
import vip.yyzhu.ymyy.R;
import vip.yyzhu.ymyy.St;
import vip.yyzhu.ymyy.adapter.RecycleViewDivider;
import vip.yyzhu.ymyy.tool.Tool;
import vip.yyzhu.ymyy.adapter.XzListAdapter;
import vip.yyzhu.ymyy.adapter.yypItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportFragment extends Fragment {

    XzListAdapter adapter;
    String path;
    Context con;
    TextView more;
    TextView play_name;
    View play_box;
    MediaPlayer player = new MediaPlayer();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        con = getActivity();
        View root = inflater.inflate(R.layout.fragment_import, container, false);

        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += St.getVoicePackage();
        File file = new File(path);
        // 文件夹是否存在
        if (!file.exists()) {
            // 不存在就创建
            file.mkdirs();
        }

        // 菜单
        more = root.findViewById(R.id.more);
        // 播放 信息的box
        play_box = root.findViewById(R.id.play_box);
        // 播放名
        play_name = root.findViewById(R.id.play_name);
        // 列表
        RecyclerView list = root.findViewById(R.id.list);
        // 列表数据
        List<yypItem> datas = new ArrayList<>();
        // 分割线
//        Color.argb(0.2f, 0, 0, 0)
        list.addItemDecoration(new RecycleViewDivider(con, 1, 855638016));
        // 布局
        list.setLayoutManager(new LinearLayoutManager(con));
        // 关闭动画
        ((DefaultItemAnimator) list.getItemAnimator()).setSupportsChangeAnimations(false);
        // 适配器
        adapter = new XzListAdapter(datas, file, con);
        // 设置适配器
        list.setAdapter(adapter);
        // 初始化文件列表
        adapter.setFile(file);

        // 文件 列表 的 回调
        adapter.setCallback(new XzListAdapter.Callback() {
            @Override
            public void file(final File file) {
                stop();
//                Tool.makeText1(con, file.getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("操作");
                //    指定下拉列表的显示数据
                final String[] cities = {"试听", "删除", "按错了"};
                //    设置一个下拉的列表选择项
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            play(file);
                        } else if (which == 1) {
//                            Tool.makeText1(con, "删除");
                            AlertDialog.Builder builder = new AlertDialog.Builder(con);
                            builder.setTitle("警告");
                            builder.setMessage("确定删除 " + file.getName() + " 吗?");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    file.delete();
                                    adapter.Refresh();
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.show();
                        } else {

                        }
                    }
                });
                builder.show();
            } // 文件点击

            @Override
            public boolean fileLong(final File file) {
                play(file);
                return true;
            } // 文件长按

            @Override
            public boolean folder(File file) {
                stop();
                return false;
            } // 文件夹点击

            @Override
            public boolean folderLong(final File file) {
                stop();
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle("操作");
                //    指定下拉列表的显示数据
                final String[] cities = {"删除", "按错了"};
                //    设置一个下拉的列表选择项
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(con);
                            builder.setTitle("警告");
                            builder.setMessage("确定删除 " + file.getName() + " 吗?");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tool.deleteDirectory(file);
                                    adapter.Refresh();
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.show();
                        }
                    }
                });
                builder.show();
                return false;
            } // 文件夹长按
        });

        // 返回
        View back = root.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!adapter.getFile().getPath().equals(path)) {
                    adapter.setFile(adapter.getFile().getParentFile());
                }
                stop();
            }
        });

        // 菜单被点击
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop(); // 停止音乐
                // 新建一个 dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                // 设置标题
                builder.setTitle("更多操作");
                // 指定下拉列表的显示数据
                final String[] cities = {"从网络下载", "从本地导入", "打开系统文件管理", "长按语音文件可以试听"};
                // 设置一个下拉的列表选择项
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { //从网络下载
                            Tool.makeText1(con, "在做了在做了");
                        } else if (which == 1) { // 从本地导入
                            // 跳转到导入页面
                            Intent intent = new Intent(getActivity(), InputFileActivity.class);
                            con.startActivity(intent);
                        } else if (which == 2) { // 打开系统文件管理
                            new AlertDialog.Builder(con)
                                    .setTitle("提示")
                                    .setMessage("可以手动到系统的文件管理器找到以下路径并管理\n" + St.getVoicePackage())
                                    .setPositiveButton("确定", null)
                                    .show();
                        } else if (which == 3) { //点击语音文件可以试听
                            // 提示
                            Tool.makeText2(con, "长按语音文件可以试听\n点击文件可以管理");
                        }
                    }
                });
                // 显示
                builder.show();
            }
        });

        // 播放的字 被点击
        play_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop(); // 停止播放
            }
        });

        // 播放完毕事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // 隐藏字
                play_box.setVisibility(View.GONE);
            }
        });
        return root;
    }

    // 播放
    public void play(File file) {
        player.reset();
        try {
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
            player.start();
            play_box.setVisibility(View.VISIBLE);
            play_name.setText(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            Tool.makeText1(con, "播放出错");
        }
    }

    // 停止播放
    public void stop() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.reset();
        play_box.setVisibility(View.GONE);
    }

    // 页面关闭
    @Override
    public void onPause() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.reset();
        play_box.setVisibility(View.GONE);
        super.onPause();
    }
}
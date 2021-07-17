package vip.yyzhu.ymyy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vip.yyzhu.ymyy.adapter.RecycleViewDivider;
import vip.yyzhu.ymyy.adapter.XzListAdapter;
import vip.yyzhu.ymyy.adapter.yypItem;
import vip.yyzhu.ymyy.dialog.SelectCopyPath;
import vip.yyzhu.ymyy.tool.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputFileActivity extends AppCompatActivity {

    private XzListAdapter adapter;
    private Context con;
    private File file;
    private String path;
    private ProgressDialog myProgressDialog;
    private Handler handler;
    private String copePath;
    private SelectCopyPath dialog;

    TextView play_name;
    View play_box;
    MediaPlayer player = new MediaPlayer();
    private View back;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_flie);
        con = InputFileActivity.this;
        setTitle("本地导入语音");

        play_box = findViewById(R.id.play_box);
        play_name = findViewById(R.id.play_name);
        back = findViewById(R.id.back);

        dialog = new SelectCopyPath(con, copePath);

        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        copePath = path + St.getVoicePackage();
        file = new File(path);

        final List<yypItem> datas = new ArrayList<>();
        RecyclerView list = findViewById(R.id.list);
//        Color.argb(0.2f, 0, 0, 0)
        list.addItemDecoration(new RecycleViewDivider(con, 1, 855638016));
        list.setLayoutManager(new LinearLayoutManager(con));
        ((DefaultItemAnimator) list.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new XzListAdapter(datas, file, con);
        adapter.setFilterIF(false);
        list.setAdapter(adapter);
        adapter.setFile(file);

        adapter.setCallback(new XzListAdapter.Callback() {
            @Override
            public void file(final File file) {
                stop();
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("amr")) {
//                    myProgressDialog.show();
                    dialog.setListener(new SelectCopyPath.OnListener() {
                        @Override
                        public void onCancel(String path1) {
                            File file1 = new File(path1 + "/" + file.getName());

                            if (!file1.exists()) {// 文件存在返回false
                                copyfile(file, file1);
                            }else{
                                Tool.makeText1(con, "已有重名文件");
                            }
                        }
                    });
                    dialog.show();
//                    copyfile(file, new File(path + St.getVoicePackage() + "/" + file.getName()));
                } else {
                    Tool.makeText1(con, "只能选择'amr'后缀的语音文件");
                }
            }

            @Override
            public boolean fileLong(File file) {
                stop();
//                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("amr")) {
//
//                } else {
//                    Tool.makeText1(con, "只能选择'amr'后缀的语音文件");
//                }
                try {
                    player.setDataSource(file.getAbsolutePath());
                    player.prepare();
                    player.start();
                    play_box.setVisibility(View.VISIBLE);
                    play_name.setText(file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    Tool.makeText1(con, "播放出错\n只能播放音乐文件");
                }
                return true;
            }

            @Override
            public boolean folder(File file) {
                stop();
                return false;
            }

            @Override
            public boolean folderLong(File file) {
                return false;
            }
        });

        onCreateDialog();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                //更新进度条
                myProgressDialog.setProgress(msg.what);
                //复制完毕，关闭进度条
                if (myProgressDialog.getProgress() >= 100) {
                    //关闭进度条
                    myProgressDialog.dismiss();
                }
                super.handleMessage(msg);
            }
        };

        play_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play_box.setVisibility(View.GONE);
            }
        });

        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

    }


    //创建progressdialog
    protected Dialog onCreateDialog() {
        myProgressDialog = new ProgressDialog(con);
        myProgressDialog.setMax(100);
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        myProgressDialog.setTitle("正在复制文件");
        myProgressDialog.setCancelable(false);
        return myProgressDialog;
    }

    //判断复制后文件是否存在，并判断大小是否一致
    public void check(File file1, File file2) {
        if (file1.exists()) {
            if (file1.length() == file2.length()) {
                //文件复制成功
                Tool.makeText1(con, "复制文件成功");
                return;
            }
        }
        Tool.makeText1(con, "复制文件失败");
    }

    //复制文件
    public void copyfile(File oldFile, File newFile) {
        try {
            FileInputStream fis = new FileInputStream(oldFile);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buf = new byte[1024 * 1024];
            int readline = 0;
            long size = oldFile.length();
            while ((readline = fis.read(buf)) != -1) {
                fos.write(buf, 0, readline);
                //计算复制进度
                int i = (int) ((100 * newFile.length() / size));
                handler.sendEmptyMessage(i);
            }
            check(newFile, oldFile);
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void stop(){
        if (player.isPlaying()){
            player.stop();
        }
        player.reset();
        play_box.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (back()){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean back(){
        stop();
        if (!adapter.getFile().getPath().equals(St.getRootPath())) {
            adapter.setFile(adapter.getFile().getParentFile());
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }
}
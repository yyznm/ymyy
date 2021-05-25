package vip.yyzhu.ymyy.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vip.yyzhu.ymyy.R;
import vip.yyzhu.ymyy.St;
import vip.yyzhu.ymyy.adapter.RecycleViewDivider;
import vip.yyzhu.ymyy.adapter.XzListAdapter;
import vip.yyzhu.ymyy.adapter.yypItem;
import vip.yyzhu.ymyy.tool.Tool;

import java.io.File;
import java.util.ArrayList;

public class SelectCopyPath extends Dialog {

    public OnListener mListener;
    TextView yes;
    TextView no;
    TextView pathText;
    TextView back;
    TextView new_f;
    RecyclerView list;
    private Context con;
    private File file;
    private String path;
    private XzListAdapter adapter;


    public SelectCopyPath(@NonNull Context context, String path) {
        super(context, R.style.BoxDialog);
        con = context;
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_copy_path);
        setCanceledOnTouchOutside(false);


        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        pathText = findViewById(R.id.path);
        list = findViewById(R.id.list);
        new_f = findViewById(R.id.new_f);
        back = findViewById(R.id.back);

        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += St.getVoicePackage();
        file = new File(path);

        if (path.equals(St.getVoicePackageRoot())) {
            back.setVisibility(View.GONE);
        }
        pathText.setText(path.substring(St.getVoicePackageRoot().length()));

        list.addItemDecoration(new RecycleViewDivider(con, 1, Color.argb(0.2f, 0, 0, 0)));
        list.setLayoutManager(new LinearLayoutManager(con));
        ((DefaultItemAnimator) list.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new XzListAdapter(new ArrayList<yypItem>(), file, con);
        adapter.setFilterIF(false);
        list.setAdapter(adapter);
        adapter.setFile(file);
        adapter.setCallback(new XzListAdapter.Callback() {
            @Override
            public void file(File file) {
                Tool.makeText1(con, "请选择一个文件夹");
            }

            @Override
            public boolean fileLong(File file) {
                return false;
            }

            @Override
            public boolean folder(File file) {
                path = file.getAbsolutePath();
                pathText.setText(path.substring(St.getVoicePackageRoot().length()));
                back.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean folderLong(File file) {
                return false;
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCancel(path);
                }
                adapter.setFile(adapter.getFile());
                dismiss();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!adapter.getFile().getPath().equals(St.getVoicePackageRoot())) {
                    System.out.println(adapter.getFile().getParentFile().getPath());
                    adapter.setFile(adapter.getFile().getParentFile());
                    if (adapter.getFile().getPath().equals(St.getVoicePackageRoot())) {
                        back.setVisibility(View.GONE);
                    }
                    path = adapter.getFile().getAbsolutePath();
                    pathText.setText(path.substring(St.getVoicePackageRoot().length()));
                    return;
                }
                back.setVisibility(View.GONE);
            }
        });

        new_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(con);
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle("新建文件夹").setView(inputServer)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String s = inputServer.getText().toString();
                        File f = new File(path + "/" + s);
//                        Tool.makeText1(con, path + "/" + s);
                        Tool.makeText1(con, "新建成功");
                        if (!f.exists()) {// 目录存在返回false
                            f.mkdirs();// 创建一个目录
                            path += ("/" + s);
                            pathText.setText(path.substring(St.getVoicePackageRoot().length()));
                            adapter.setFile(f);
                        } else {
                            Tool.makeText1(con, "目录以存在");
                        }
                    }
                });
                builder.show();
            }
        });
    }


    public void setListener(OnListener mListener) {
        this.mListener = mListener;
    }

    public interface OnListener {
        void onCancel(String path);
    }
}
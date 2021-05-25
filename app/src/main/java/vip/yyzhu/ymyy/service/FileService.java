package vip.yyzhu.ymyy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.*;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vip.yyzhu.ymyy.R;
import vip.yyzhu.ymyy.St;
import vip.yyzhu.ymyy.adapter.RecycleViewDivider;
import vip.yyzhu.ymyy.tool.Tool;
import vip.yyzhu.ymyy.adapter.XzListAdapter;
import vip.yyzhu.ymyy.adapter.yypItem;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileService extends Service {
    private static final String TAG = "FileService";
    private Context con;
    FileService _this = this;

//    private SharedPreferences mSpf;

    Handler mainHandler;
    private WindowManager windowManager;

    public FileService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new FileBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        con = FileService.this;
//        mSpf = con.getSharedPreferences("text", Context.MODE_PRIVATE);
        mainHandler = new Handler(Looper.getMainLooper());
        //初始化 voice 的目录
        initVoice();
    }

    // voice2 目录
    ArrayList<File> voices = new ArrayList<>();
    //初始化 voice 的目录
    public void initVoice() {
        String path = St.getRootPath() + "/Android/data/com.tencent.mm/MicroMsg/";
        File file = new File(path);
        // 目录不存在
        if (!file.exists()) {
            System.out.println("目录不存在, 出错");
        } else {
            // voice2会有多个 而且在第二击
            File[] files = file.listFiles();
            // 遍历 第一级目录 /Android/data/com.tencent.mm/MicroMsg/
            for (File file1 : Objects.requireNonNull(files)) {
                // 是文件夹
                if (file1.isDirectory()) {
                    // 第二级目录 /Android/data/com.tencent.mm/MicroMsg/********
                    File[] files2 = file1.listFiles();
                    // 遍历
                    for (File file2 : Objects.requireNonNull(files2)) {
                        // 是文件夹 而且 名字=voice2
                        if (file1.isDirectory() && file2.getName().equals("voice2")) {
                            // 添加到数组
                            voices.add(file2);
                        }
                    }
                }
            }
        }
    }


    public class FileBinder extends Binder {
        // 显示 悬浮窗
        public void beginWX() {
            initWindow();
        }
    }

    WindowManager.LayoutParams layoutParams;
    int screenWidth;
    int screenHeight;
    View mView;
    View tit;
    RecyclerView list;
    ImageView left;
    TextView logo;
    View refresh;
    boolean windowManagerIF = false; //是否已经打开
    boolean Narrow = false; //是否已经缩小

    int topH = 40;  //操作的高度
    int h = 600;  //总体高度

    private void initWindow() {
        if (windowManagerIF) {
            Tool.makeText1(con, "已经打开了");
            return;
        }
        windowManagerIF = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                //获取WindowManager服务
                windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

                //取得屏幕尺寸
                DisplayMetrics dm = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(dm);
                screenWidth = dm.widthPixels;
                screenHeight = dm.heightPixels;

                h = (int) (screenWidth / 2 * 1.2);

                //设置LayoutParams
                layoutParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//                    layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                layoutParams.format = PixelFormat.RGBA_8888; //背景透明效果
                layoutParams.width = screenWidth / 2; //悬浮窗口长宽值，单位为 px 而非 dp
                layoutParams.height = h;
                layoutParams.gravity = Gravity.LEFT; //想要x,y生效，一定要指定Gravity为top和left //Gravity.TOP | Gravity.LEFT
                layoutParams.x = screenWidth / 2; //启动位置
                layoutParams.y = 50;


                //创建view  就是悬浮窗里面的内容
                mView = LayoutInflater.from(con).inflate(R.layout.floating_window, null, false);
                tit = mView.findViewById(R.id.tit);
                list = mView.findViewById(R.id.list);
                left = mView.findViewById(R.id.left);
                refresh = mView.findViewById(R.id.refresh);
                logo = mView.findViewById(R.id.logo);

                View.OnTouchListener ont = new View.OnTouchListener() {
                    //这个用于保存 当手指按下时候，离悬浮窗左上角的 x，y距离,这里设定初始值 是悬浮窗宽高的一半
                    private int dX;
                    private int dY;
                    private int sX;
                    private int sY;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                dX = (int) motionEvent.getRawX();
                                dY = (int) motionEvent.getRawY();
                                sX = (int) motionEvent.getRawX();
                                sY = (int) motionEvent.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int nX = (int) motionEvent.getRawX();
                                int nY = (int) motionEvent.getRawY();
                                int cW = nX - dX;
                                int cH = nY - dY;
                                dX = nX;
                                dY = nY;
                                layoutParams.x = layoutParams.x + cW;
                                layoutParams.y = layoutParams.y + cH;
                                windowManager.updateViewLayout(mView, layoutParams);
                                break;
                            case MotionEvent.ACTION_UP:
                                //如果抬起时的位置和按下时的位置大致相同视作单击事件
                                int nX2 = (int) motionEvent.getRawX();
                                int nY2 = (int) motionEvent.getRawY();
                                int cW2 = nX2 - sX;
                                int cH2 = nY2 - sY;
                                //间隔值可能为负值，所以要取绝对值进行比较
                                if (Math.abs(cW2) < 3 && Math.abs(cH2) < 3) {
                                    //点的是logo
                                    if (view.getId() == R.id.logo) {
                                        if (!adapter.getFile().getPath().equals(path)) {
                                            logo.setText("上一级");
                                            if (!player.isPlaying()) {
                                                adapter.setFile(adapter.getFile().getParentFile());
                                            }
                                        }
                                        if (adapter.getFile().getPath().equals(path)) {
                                            logo.setText("yy祝");
                                        }
                                    } else if (view.getId() == R.id.left) {
                                        windowManagerNarrow();
                                    } else if (view.getId() == R.id.refresh) {
                                        windowManager.removeView(mView);
                                        windowManagerIF = false;
                                    }
                                    player.reset();
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                };

                //给view添加触摸事件，来使用悬浮窗可以移动
                tit.setOnTouchListener(ont);
                left.setOnTouchListener(ont);
                logo.setOnTouchListener(ont);
                refresh.setOnTouchListener(ont);
                //提交布局
//                windowManager.updateViewLayout(mView, layoutParams);
                windowManager.addView(mView, layoutParams);
                loadData();

//                windowManager.

            }
        }
    }

    String path;
    File file;
    private XzListAdapter adapter;
    MediaPlayer player = new MediaPlayer();

    // 加载悬浮窗 数据
    public void loadData() {
        if (list == null) {
            return;
        }
        // 语音播放完毕事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //是否有上一级
                if (adapter.getFile().getPath().equals(path)) {
                    logo.setText("yy祝");
                } else {
                    logo.setText("上一级");
                }
            }
        });
        path = St.getVoicePackageRoot(); //初始目录
        file = new File(path);
        // 文件夹是否存在
        if (!file.exists()) {
            file.mkdirs();
        }

        // 列表数据
        List<yypItem> datas = new ArrayList<>();
        // 布局
        list.setLayoutManager(new LinearLayoutManager(con));
        // 分割线
        list.addItemDecoration(new RecycleViewDivider(con, 1, Color.argb(0.2f, 0, 0, 0)));
        // 关闭动画
        ((DefaultItemAnimator) list.getItemAnimator()).setSupportsChangeAnimations(false);
        // 适配器
        adapter = new XzListAdapter(datas, file, con);
        // 使用小的 view
        adapter.setXiao(true);
        // 设置适配器
        list.setAdapter(adapter);
        // 初始化文件列表
        adapter.setFile(file);
        // 文件 列表 的 回调
        adapter.setCallback(new XzListAdapter.Callback() {
            @Override
            public void file(File file1) { // 文件点击
                if (adapter.getFile().getPath().equals(path)) {
                    logo.setText("yy祝");
                } else {
                    logo.setText("上一级");
                }
                // 停止语音播放
                player.reset();
                // 记录选中的file
                file = file1;
                // 开始
                kaiS();
            }

            @Override
            public boolean fileLong(File file) {  // 文件长按
                //
//                if (!file.isDirectory()) {
                    Tool.makeText1(con, "开始播放");
                    try {
                        player.reset();
                        player.setDataSource(file.getAbsolutePath());
                        player.prepare();
                        player.start();
                        logo.setText("停止");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                }
                return true;
            }

            @Override
            public boolean folder(File file) {   // 文件夹点击
                player.reset();
                logo.setText("上一级");
                return false;
            }

            @Override
            public boolean folderLong(File file) { // 文件夹长按
                return false;
            }
        });
    }

    boolean Regular = false; //是否正在找

    //开始
    public void kaiS() {
        //是否已经开始找
        if (Regular) {
            Tool.makeText1(con, "请不要重复点击");
            return;
        }
        Tool.makeText1(con, "已准备, 请按下录音");
        // 标记 正在寻找
        Regular = true;
        // 时间
        final Date date = new Date();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean b = false; // 标记
                // 时间没有超过 5 秒
                while (new Date().getTime() - date.getTime() < 5000) {
                    // 寻找
                    File ss = ss(date);
                    // 是否找到
                    if (ss != null) {
                        // 替换
                        boolean b1 = CopyFile(file.getAbsolutePath(),
                                ss.getAbsolutePath());
                        // 替换成功
                        if (b1) {
                            // 提示
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Tool.makeText1(con, "替换成功");
                                }
                            });
                        }
                        // 标记设置为真
                        b = true;
                        // 退出循环
                        break;
                    }

                    // 延时 200 毫秒
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 没找到
                if (!b) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Tool.makeText1(con, "没检测到, 请重试");
                        }
                    });
                }
                // 标记
                Regular = false;
            }
        }).start();
    }

    // 寻找新添加的语音文件
    public File ss(Date d) {
        // 循环voice2文件夹
        for (File voice : voices) {
            // 语音文件在第三级文件夹
            // 列如: voice2/s2/ds/语音.amr

            // voice2/
            for (File file : voice.listFiles()) {
                // voice2/s2/
                for (File file1 : file.listFiles()) {
                    // voice2/s2/ds/
                    for (File file2 : file1.listFiles()) {
                        // 时间大于 时间 d , d 是开始时间
                        if (file2.lastModified() > d.getTime()) {

                            //这里 文件名 4-20 是纯数字, 如果不是纯数字有可能是别人发来的语音, 为了防止换错了
                            String s = file2.getName().substring(4, 20);
                            Pattern pattern = Pattern.compile("[0-9]*");
                            Matcher isNum = pattern.matcher(s);
                            if (isNum.matches()) {
                                return file2;
                            }
                        }
                    }
                }
            }
        }
        // 没有新加的 语音文件
        return null;
    }

    // 悬浮窗 缩小放大
    public void windowManagerNarrow() {
        if (Narrow) {
            Narrow = false;
            logo.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
            left.setImageResource(R.drawable.left);
            layoutParams.width = screenWidth / 2; //悬浮窗口长宽值，单位为 px 而非 dp
            layoutParams.height = h;
            layoutParams.x = screenWidth / 2; //启动位置
            layoutParams.y = layoutParams.y + h / 2 - dip2px(con, topH / 2);
//            layoutParams.y = 100;
            windowManager.updateViewLayout(mView, layoutParams);
        } else {
            Narrow = true;
            logo.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
            left.setImageResource(R.drawable.right);
            layoutParams.width = dip2px(con, topH); //悬浮窗口长宽值，单位为 px 而非 dp
            layoutParams.height = dip2px(con, topH);
            layoutParams.x = screenWidth - dip2px(con, topH); //启动位置
//            layoutParams.x = layoutParams.x + (layoutParams.x -  dip2px(con,40)); //启动位置
            layoutParams.y = layoutParams.y - h / 2 + dip2px(con, topH / 2);
            windowManager.updateViewLayout(mView, layoutParams);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //复制文件
    public boolean CopyFile(String fromfile, String tofile) {
        try {
            File oldFile = new File(fromfile);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }
            File newFile = new File(tofile);
            if (newFile.exists()) {
                newFile.delete();
//                return false;
            }


            FileInputStream fosfrom = new FileInputStream(fromfile);
            FileOutputStream fosto = new FileOutputStream(tofile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.flush();
            fosto.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

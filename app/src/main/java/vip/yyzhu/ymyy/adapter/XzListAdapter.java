package vip.yyzhu.ymyy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vip.yyzhu.ymyy.R;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class XzListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<yypItem> datas;
    private Context con;
    private XzListAdapter ap = this;
    File file;
    boolean xiao = false;
    boolean filterIF = true;  //是否过滤


    public XzListAdapter(List<yypItem> datas, File file, Context con) {
        this.datas = datas;
        this.con = con;
        this.file = file;

    }

    public void setXiao(boolean xiao) {
        this.xiao = xiao;
    }

    public void setFilterIF(boolean filterIF) {
        this.filterIF = filterIF;
    }

    public File getFile() {
        return file;
    }


    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (xiao) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yy_list_item_xx, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yy_list_item, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        File file = datas.get(position).getFile();
        vh.name.setText(file.getName());
        if (datas.get(position).getFile().isDirectory()) {
//            vh.icon_file.setVisibility(View.GONE);
//            vh.icon_folder.setVisibility(View.VISIBLE);
            vh.icon_file.setImageResource(R.drawable.folder_icon);
            vh.box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean b = false;
                    if (callback != null) {
                        b = callback.folder(datas.get(position).getFile());
                    }
                    if (!b) {
                        setFile(datas.get(position).getFile());
                    }
                }
            });
            vh.box.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (callback != null) {
                        return callback.folderLong(datas.get(position).getFile());
                    }
                    return false;
                }
            });
        } else {
//            vh.icon_file.setVisibility(View.VISIBLE);
//            vh.icon_folder.setVisibility(View.GONE);
            if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("amr")){
                vh.icon_file.setImageResource(R.drawable.music_icon);
            }else{
                vh.icon_file.setImageResource(R.drawable.file_icon);
            }
            vh.box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.file(datas.get(position).getFile());
                    }
                }
            });
            vh.box.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (callback != null) {
                        return callback.fileLong(datas.get(position).getFile());
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setData(List<yypItem> datas) {
        this.datas = new ArrayList<>();
        notifyDataSetChanged();
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setFile(File file) {
        if (!file.isDirectory()) {
            return;
        }
        List<yypItem> datas1 = new ArrayList<>();
        File[] files = file.listFiles();
//        for (File file1 : files) {
//            if (file1.isDirectory()){
//                datas1.add(yypItem);
//            }
//        }
        for (File file1 : files) {
            yypItem yypItem = new yypItem();
            yypItem.setFile(file1);
            yypItem.setType(file1.isDirectory() ? 1 : 0);
            if (!file1.isDirectory() && filterIF &&
                    !file1.getName().substring(file1.getName().lastIndexOf(".") + 1).equals("amr")) {
                continue;
            }
            datas1.add(yypItem);
        }
        Collections.sort(datas1, new Comparator<yypItem>() {
            @Override
            public int compare(yypItem t0, yypItem t1) {
                if (t0.getType() == 1 && t1.getType() == 0){
                    return -1;
                }
                if (t0.getType() == 0 && t1.getType() == 1){
                    return 1;
                }
                return t0.getFile().getName().compareTo(t1.getFile().getName());
            }
        });
        this.file = file;
        setData(datas1);
    }

    public void Refresh() {
        setFile(file);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View box;
        TextView name;
        ImageView icon_file;
//        ImageView icon_folder;

        public ViewHolder(View view) {
            super(view);
            box = view;
            icon_file = view.findViewById(R.id.icon_file);
//            icon_folder = view.findViewById(R.id.icon_folder);
            name = view.findViewById(R.id.name);
        }

    }

    Callback callback;

    public interface Callback {
        public void file(File file);

        public boolean fileLong(File file);

        public boolean folder(File file);

        public boolean folderLong(File file);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
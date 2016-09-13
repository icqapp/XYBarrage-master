package example.com.xybarrage;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * 读取文件,并包装成一个数组返回。
 * 目前读取的是assets文件夹,后面可以继续扩展至读取数据库等
 * 字幕数据读取类
 */
public class ReaderBarrage {

    private Context context;

    public ReaderBarrage(Context context){
        this.context = context;

    }

    //读取assets文件夹数据
    public Vector<String> readerAssetsFolder(String foldername){
        try {
            InputStream in = context.getAssets().open(foldername);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            final Vector<String> data = new Vector<String>();
            while ( (line = br.readLine()) != null){
                data.add(line);
            }
            br.close();
            isr.close();
            in.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

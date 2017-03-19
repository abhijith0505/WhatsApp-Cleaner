package com.whatscrap;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Abhijith on 15-03-2017.
 */

public class Utilities {
    String path;
    Context mContext;

    Utilities(Context context){
        path = Environment.getExternalStorageDirectory()
                + "/WhatsApp/Databases/";
        mContext = context;
    }


    int delete(boolean all){
        int success=0;

        int initial;
        if(all) initial = 0;
        else initial = 1;

        File directory = new File(path);
        File[] files = directory.listFiles();

        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
            } });

        if(files.length > 1){
            Log.d("Files", "Size: "+ files.length);
            for (int i = initial; i < files.length; i++)
            {   boolean deleted = files[i].delete();
                Log.d("Files", "FileName:" + files[i].getName());
            }
        }
        else{
            Toast.makeText(mContext, "You do not have unwanted chat backups", Toast.LENGTH_SHORT).show();
        }

        return success;
    }

    float getSize(){
        float size = -1;
        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files.length > 1){
            Arrays.sort(files, new Comparator<File>(){
                public int compare(File f1, File f2)
                {
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                } });
            Log.d("Files", "Size: "+ files.length);
            for (int i = 1; i < files.length; i++)
            {   size += files[i].length();
            }
        }
        return size;
    }

    int getNumberOfFiles(){
        File directory = new File(path);
        File[] files = directory.listFiles();
        return files.length - 1;
    }

}



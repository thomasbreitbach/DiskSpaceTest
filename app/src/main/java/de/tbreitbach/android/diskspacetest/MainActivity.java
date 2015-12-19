package de.tbreitbach.android.diskspacetest;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private long blockSize, totalSize, availableSize, freeSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Environment4.initDevices(this);

        // Root
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        fetchSizes(statFs);
        logSizes("ROOT");

        // External Storage
        statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        fetchSizes(statFs);
        logSizes("External");

        // External Storage via Environment 4
        Environment4.Device[] devices = Environment4.getExternalStorage(this);
        for (File file : devices){
            statFs = new StatFs(file.getAbsolutePath());
            fetchSizes(statFs);
            logSizes("Devices");
        }
    }

    private void fetchSizes(StatFs statFs){
        if (Build.VERSION.SDK_INT >= 18){
            this.blockSize = statFs.getBlockSizeLong();
            this.totalSize = statFs.getBlockCountLong()*blockSize;
            this.availableSize = statFs.getAvailableBlocksLong()*blockSize;
            this.freeSize = statFs.getFreeBlocksLong()*blockSize;
        } else {
            this.blockSize = statFs.getBlockSize();
            this.totalSize = statFs.getBlockCount()*blockSize;
            this.availableSize = statFs.getAvailableBlocks()*blockSize;
            this.freeSize = statFs.getFreeBlocks()*blockSize;
        }
    }

    private void logSizes(String txt){
        StringBuilder sb = new StringBuilder();
        sb.append("Total Size:\t\t").append(readableFileSize(this.totalSize)).append("\n");
        sb.append("Avail. Size:\t\t").append(readableFileSize(this.availableSize)).append("\n");
        sb.append("Free Size:\t\t").append(readableFileSize(this.freeSize)).append("\n");

        Log.d(txt, sb.toString());
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}

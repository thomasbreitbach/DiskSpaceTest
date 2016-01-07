package de.tbreitbach.android.diskspacetest;

import android.content.Context;
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
        logSizes("Environment - ROOT");

        // External Storage
        statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        fetchSizes(statFs);
        logSizes("Environment - ExternalStorageDirectory");

        // External Storage via Environment 4
        Environment4.Device[] devices =  Environment4.getExternalStorage(this);
        for (Environment4.Device device : devices){
            statFs = new StatFs(device.getAbsolutePath());
            fetchSizes(statFs);
            logDevice("Environment 4 - getExternalStorage", device);
            logSizes("Environment 4 - getExternalStorage");
        }

        // Storage via Environment 4
        Environment4.Device[] devicesAll = Environment4.getStorage(this);
        for (Environment4.Device device : devicesAll){
            statFs = new StatFs(device.getAbsolutePath());
            fetchSizes(statFs);
            logDevice("Environment 4 - getStorage" , device);
            logSizes("Environment 4 - getStorage");
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

    public void logDevice(String txt, Environment4.Device device){
        StringBuilder sb = new StringBuilder();
        sb.append("AbsPath:\t").append(device.getAbsolutePath()).append("\n");
        sb.append("Path:\t").append(device.getPath()).append("\n");
        sb.append("is emulated:\t").append(device.isEmulated()).append("\n");
        sb.append("is primary:\t").append(device.isPrimary()).append("\n");
        sb.append("UUID:\t").append(device.getUuid()).append("\n");
        sb.append("Label\t").append(device.getUserLabel()).append("\n");
        sb.append("is removable:\t").append(device.isRemovable()).append("\n");

        Log.d(txt, sb.toString());
    }

    private void logSizes(String txt){
        StringBuilder sb = new StringBuilder();
        sb.append("Total Size:\t").append(readableFileSize(this.totalSize)).append("\n");
        sb.append("Avail Size:\t").append(readableFileSize(this.availableSize)).append("\n");
        sb.append("Free Size:\t\t").append(readableFileSize(this.freeSize)).append("\n");

        Log.d(txt, sb.toString());
        Log.d("break", "\n\n");
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public long getAbsDiskSpace(Context context) {

        long totalSpace = 0;

        Environment4.Device[] devices = Environment4.getExternalStorage(context);

        for (Environment4.Device device : devices) {
            totalSpace += device.getTotalSpace();
        }

        return totalSpace;
    }


    public long getFreeDiskSpace(Context context) {

        long totalSpace = 0;

        Environment4.Device[] devices = Environment4.getExternalStorage(context);

        for (Environment4.Device device : devices) {
            totalSpace += device.getFreeSpace();
        }

        return totalSpace;
    }

}

package com.poc.team;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

import android.content.ComponentName;

import javax.security.cert.X509Certificate;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.security.cert.X509Certificate;

/*El código correspondiente a la explotación Certifi-gate ha sido borrado.*/

public class MainLogic extends Service {
    int size = 0;


        void doLogic() {
        Log.i(TAG, "entra en doLogic");
        try {
            this.serviceConnection.getInterface().verifyCaller();
            this.serviceConnection.getInterface().isAvailable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!this.isBound) {
            Log.d(TAG, "Error");
            return;
        }
        Client client = new Client("192.168.43.229", 80, this, this);
    }
}
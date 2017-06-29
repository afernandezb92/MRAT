// com.teamviewer.incomingrcsharedlib.communication.IAddonService.aidl
package com.teamviewer.incomingrcsharedlib.communication;

// Declare any non-default types here with import statements

import com.poc.team.ScreenShot;
import com.poc.team.ScreenShotDataParcelable;
import android.os.ParcelFileDescriptor;

interface IAddonService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int verifyCaller();
    int isAvailable();
    int dummy3();
    int dummy4();
    ScreenShot getScreenshotData();
    int captureScreenshotToFile(in ScreenShotDataParcelable fd, int size);
    int dummy7();
    int injectKeyCode(int newkey, int keycode, int newaction, int action);
    int injectKeyCode2(int keycode, int newaction, int action);
    int dummy10();
    int dummy11();
    int injectPointerEvent(int action, int isnew, int aa, int x, int y);
    int injectPointerEvent2(int action, int isnew, int aa, int x, int y, long aaa);
}

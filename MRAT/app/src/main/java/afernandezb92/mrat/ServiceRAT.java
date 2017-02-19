package afernandezb92.mrat;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

public class ServiceRAT extends IntentService {
    private static final String hostname = "10.0.2.2";
    private static final int portnumber = 8888;

    public ServiceRAT() {
        super("ServiceRAT");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servicio creado!", Toast.LENGTH_SHORT).show();
        System.out.println("Servicio creado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servicio destruído!", Toast.LENGTH_SHORT).show();
        System.out.println("Servicio creado");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Client client = new Client(hostname, portnumber, this);
    }

}

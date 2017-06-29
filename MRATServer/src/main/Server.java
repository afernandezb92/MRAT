package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import cipher.CipherAES;
import cipher.Keys;
import messages.Mensaje;
import messages.Mensaje.RErr;
import messages.Mensaje.RGetContact;
import messages.Mensaje.RGetInstalledApps;
import messages.Mensaje.RImage;
import messages.Mensaje.RListImages;
import messages.Mensaje.ROk;
import messages.Mensaje.RScreenshot;
import messages.Mensaje.TGetContact;
import messages.Mensaje.TGetInstalledApps;
import messages.Mensaje.TImage;
import messages.Mensaje.TListImages;
import messages.Mensaje.TScreenshot;
import messages.Mensaje.TSendId;
import messages.Mensaje.TSendKey;
import messages.Mensaje.TGetInfo;
import messages.Mensaje.RGetInfo;


public class Server {
	protected static final int TGETCONTACT = 1;
	protected static final int RGETCONTACT = 2;
	protected static final int TGETINSTALLEDAPPS = 3;
	protected static final int RGETINSTALLEDAPPS = 4;
	protected static final int TGETINFO = 5;
	protected static final int RGETINFO = 6;
	protected static final int TSENDID = 7;
	protected static final int TGETKEY = 8;
	protected static final int RGETKEY = 9;
	protected static final int TSENDKEY = 10;
	protected static final int TSCREENSHOT = 11;
	protected static final int RSCREENSHOT = 12;
	protected static final int ROK = 13;
	protected static final int RERR = 14;
	protected static final int TLISTIMAGES = 15;
    protected static final int RLISTIMAGES = 16;
    protected static final int TIMAGE = 17;
    protected static final int RIMAGE = 18;
	ServerSocket s;
	Socket socket;
	OutputStream o;
	InputStream i;
	Boolean debug = false;
	Mensaje m;
	int clientId;
	byte [] keyMaster;
	CipherAES cipherMaster;
	CipherAES cipherClient;
	
	
	
	public Server(int port){
		try {
			s = new ServerSocket (port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean Start(){
		try {
			System.out.println("Waiting client");
			socket = s.accept();
			clientId = getId();
			if(clientId != -1){
				keyMaster = Keys.generateKey();
				cipherMaster = new CipherAES(keyMaster);
				if(sendKey()){
					return true;
				}
			} else {
				System.out.println("Client ID incorrect");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getId(){
		Mensaje reply;
		try {
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if(reply.getType() == TSENDID){
				TSendId tid = (TSendId) reply;
				System.out.println("Client " + tid.getId() + " connected");
				return tid.getId();
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private boolean sendKey(){
		byte[] key;
		Timestamp ts;
		int nonce;
		key = Keys.getKey(clientId);
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		if (debug){
            System.out.println("key: " + new String(Base64.getEncoder().encodeToString(key)));
            System.out.println("keyMaster: " + new String(Base64.getEncoder().encodeToString(keyMaster)));
            System.out.println("nonce: " + nonce);
            System.out.println("ts: " + ts);
        }
		cipherClient = new CipherAES(key);
		TSendKey tkey = new TSendKey(cipherClient, keyMaster, ts, nonce);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			tkey.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == ROK){
				ROk ok = (ROk) reply;
				if(ok.isValid(tkey.getNonce())){
					return true;
				}
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String[] GetAppsInstalled(){
		Timestamp ts;
		int nonce;
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		TGetInstalledApps apps = new TGetInstalledApps(nonce, ts, cipherMaster);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			apps.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETINSTALLEDAPPS){
				RGetInstalledApps rapps = (RGetInstalledApps) reply;
				if(rapps.isValid(apps.getNonce())){
					return rapps.getValor();
				} else{
					System.out.println("Corrupted Message");
				}
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] GetScreenshot(){
		Timestamp ts;
		int nonce;
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		TScreenshot screen = new TScreenshot(nonce, ts, cipherMaster);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			screen.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if(reply.getType() == RSCREENSHOT){
				RScreenshot rscreenshot = (RScreenshot) reply;
				if(rscreenshot.isValid(screen.getNonce())){
					return rscreenshot.getScreenshot();
				} else{
					System.out.println("Corrupted Message");
				}
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String[][] GetContact(){
		Timestamp ts;
		int nonce;
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		TGetContact contact = new TGetContact(nonce, ts, cipherMaster);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			contact.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETCONTACT){
				RGetContact rcontact = (RGetContact) reply;
				if(rcontact.isValid(contact.getNonce())){
					return rcontact.getValor();
				} else {
					System.out.println("Corrupted Message");
				}
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getInfo() {
		Timestamp ts;
		int nonce;
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		TGetInfo info = new TGetInfo(nonce, ts, cipherMaster);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			info.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETINFO){
				RGetInfo rinfo = (RGetInfo) reply;
				if(rinfo.isValid(info.getNonce())){
					return rinfo.getValor();
				} else {
					System.out.println("Corrupted Message");
				}
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<byte[]> getImages(){
		Timestamp ts;
		int nonce;
		String [] imagesPath = getImagesPath();
		if(imagesPath.length > 0 && imagesPath != null){
			ArrayList<byte[]> images = new ArrayList<byte[]>(imagesPath.length);
			for (int j = 0; j < imagesPath.length; j++){
				ts = new Timestamp(System.currentTimeMillis());
				nonce = Keys.generateNonce();
				TImage image = new TImage(nonce, ts, cipherMaster, imagesPath[j]);
				Mensaje reply;
				try {
					o = socket.getOutputStream();
					image.writeTo(o);
					i = socket.getInputStream();
					reply = Mensaje.readMsg(i);
					if(reply.getType() == RIMAGE){
						RImage rImage = (RImage) reply;
						if(rImage.isValid(image.getNonce())){
							images.add(rImage.getImage());
						} else{
							System.out.println("Corrupted Message");
						}
					} else{
						RErr err = (RErr) reply;
						System.out.println(err.getError());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return images;	
		}
		return null;
	}
	
	public String[] getImagesPath(){
		Timestamp ts;
		int nonce;
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		TListImages listImages = new TListImages(nonce, ts, cipherMaster);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			listImages.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if(reply.getType() == RLISTIMAGES){
				RListImages rListImages = (RListImages) reply;
				if(rListImages.isValid(listImages.getNonce())){
					return rListImages.getImagesPath();
				} else{
					System.out.println("Corrupted Message");
				}
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close(){
		try {
			o.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			System.out.println("Conexion cerrada");
		}
	}
}
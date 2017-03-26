package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Random;

import cipher.CipherAES;
import cipher.Keys;
import cipher.RSADecryption;
import cipher.RSAEncryption;
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
import messages.Mensaje.TSendId;
import messages.Mensaje.TSendKey;
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
	protected static final int TSENDID = 8;
	protected static final int TGETKEY = 9;
	protected static final int RGETKEY = 10;
	protected static final int TSENDKEY = 11;
	protected static final int ROK = 12;
	protected static final int RERR = 13;
	ServerSocket s;
	Socket socket;
	OutputStream o;
	InputStream i;
	Boolean debug = true;
	Mensaje m;
	int clientId;
	byte [] keyMaster;
	
	
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
				getId();
				sendKey();
				keyMaster = Keys.generateKey();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void getId(){
		Mensaje reply;
		try {
			i = socket.getInputStream();
			reply = Mensaje.readMsg(i);
			if(reply.getType() == TSENDID){
				TSendId tid = (TSendId) reply;
				clientId = tid.getId();
				System.out.println("Client " + clientId + " connected");
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendKey(){
		byte[] key;
		Timestamp ts;
		int nonce;
		key = Keys.getKey(clientId);
		ts = new Timestamp(System.currentTimeMillis());
		nonce = Keys.generateNonce();
		TSendKey tkey = new TSendKey(key, key, ts, nonce);
		Mensaje reply;
		try {
			o = socket.getOutputStream();
			tkey.writeTo(o);
			i = socket.getInputStream();
			System.out.println("Esperando recibir");
			reply = Mensaje.readMsg(i);
			if (reply.getType() == ROK){
				System.out.println("C");
				ROk ok = (ROk) reply;
				System.out.println(ok);
			} else{
				RErr err = (RErr) reply;
				System.out.println(err.getError());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] GetAppsInstalled(){
		Timestamp ts;
		ts = new Timestamp(System.currentTimeMillis());
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
		Timestamp ts;
		ts = new Timestamp(System.currentTimeMillis());
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
		Timestamp ts;
		ts = new Timestamp(System.currentTimeMillis());
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
		Timestamp ts;
		ts = new Timestamp(System.currentTimeMillis());
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

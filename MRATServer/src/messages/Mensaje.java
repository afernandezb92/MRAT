package messages;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Mensaje {
	protected static final int TGETCONTACT = 1;
	protected static final int RGETCONTACT = 2;
	protected static final int TGETSCREENSHOT = 3;
	protected static final int TGETINSTALLEDAPPS = 4;
	protected static final int RGETINSTALLEDAPPS = 5;
	protected static final int TGETINFO = 6;
	protected static final int RGETINFO = 7;
	protected static final int ROK = 8;
	protected static final int RERR = 9;
	private final int type;
	private final int tag;
	private static int taggen;
	DataOutputStream outstream = null;
	Socket socket = null;
	
	public Mensaje(int t, int msg){
		type = msg;
		tag = t;
	}
	
	protected static int  newTag(){
		taggen++;
		return taggen;
	}
	
	protected int getTag (){
		return tag;
	}
	
	public int getType(){
		int Type;
		Type = type;
		return Type;
	}
	
	public void writeTo(OutputStream o) throws Exception{
		DataOutputStream output =  new DataOutputStream(o);
		output.writeInt(type);
		output.writeInt(tag);
	}
	
	protected abstract void readFrom (InputStream i) throws Exception;
	
	public static Mensaje readMsg(InputStream i) throws Exception{
		Mensaje msg = null; 
		int type = 7;
		int tag = 0;
		DataInputStream incon = new DataInputStream(i);
		type = incon.readInt();
		tag = incon.readInt();
		switch(type){
		case TGETCONTACT:
			msg = new Mensaje.TGetContact(tag);
			break;
		case RGETCONTACT:
			msg = new Mensaje.RGetContact(tag);
			break;
		case TGETSCREENSHOT:
			msg = new Mensaje.TGetScreenshot(tag);
			break;
		case TGETINSTALLEDAPPS:
			msg = new Mensaje.TGetInstalledApps(tag);
			break;	
		case RGETINSTALLEDAPPS:
			msg = new Mensaje.RGetInstalledApps(tag);
			break;	
		case TGETINFO:
			msg = new Mensaje.TGetInfo(tag);
			break;
		case RGETINFO:
			msg = new Mensaje.RGetInfo(tag);
			break;
		case ROK:
			msg = new Mensaje.ROk(tag);
			break;
		case RERR:
			msg = new Mensaje.RErr(tag);
			break;
		default:
			System.out.println("Mensaje desconocido");
		}
		msg.readFrom(incon);
		return msg;
	}
	
	public String toString (){
		String tp;
		switch(type){
		case TGETCONTACT:
			tp = "TGET";
			break;
		case RGETCONTACT:
			tp = "RGET";
			break;
		case TGETSCREENSHOT:
			tp = "TGETSCREENSHOT";
			break;	
		case TGETINSTALLEDAPPS:
			tp = "TGETINSTALLEDAPPS";
			break;	
		case RGETINSTALLEDAPPS:
			tp = "RGETINSTALLEDAPPS";
			break;
		case TGETINFO:
			tp = "TGETINFO";
			break;	
		case RGETINFO:
			tp = "RGETINFO";
			break;
		case ROK:
			tp = "ROK";
			break;
		case RERR:
			tp = "RERR";
			break;		
		default:
			tp = "Mensaje Desconocido";
		}
		return tp;
		}
	
	public static class TGetContact extends Mensaje{
		
		public TGetContact(int tag) {
			super(tag, TGETCONTACT);
		}
		
		public TGetContact(){
			super(newTag(), TGETCONTACT);
		}

		protected void readFrom(InputStream i) throws Exception {
			;
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
		}
		
	}	
	
	
	public static class RGetContact extends Mensaje{
		String[] names;
		String[] numbers;
		int numContacts;
		
		public RGetContact(int tag){
			super(tag, RGETCONTACT);
		}
		
		public RGetContact(TGetContact t, String[] nam, String[] num){
			super(t.getTag(), RGETCONTACT);
			names = nam;
			numbers = num;
		}
		
		public String[][] getValor(){
			String[][] contacts = new String[2][numContacts];
			contacts[0] = names;
			contacts[1] = numbers;
			return contacts;
		}
		
		protected void readFrom(InputStream i) throws IOException{
			DataInputStream incon = new DataInputStream(i);
			int lonnames = incon.readInt();
			numContacts = lonnames;
			String namesrec[] = new String[lonnames];
			for(int j = 0; j<lonnames; j++){
				namesrec[j] = incon.readUTF();
			}
			names = namesrec;
			int lonnumbers = incon.readInt();
			String numbersrec[] = new String[lonnumbers];
			for(int j = 0; j<lonnames; j++){
				numbersrec[j] = incon.readUTF();
			}
			numbers = numbersrec;	
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
			DataOutputStream output = new DataOutputStream(o);
			output.writeInt(names.length);
			for(int j = 0; j<names.length; j++){
				output.writeUTF(names[j]);
			}
			output.writeInt(numbers.length);
			for(int j = 0; j<numbers.length; j++){
				output.writeUTF(numbers[j]);
			}
		}
		
		public String toString(){
			return super.toString() + names + numbers;
		}
	}
	
	public static class TGetScreenshot extends Mensaje{
		
		public TGetScreenshot(int tag) {
			super(tag, TGETSCREENSHOT);
		}
		
		public TGetScreenshot(){
			super(newTag(), TGETSCREENSHOT);
		}

		protected void readFrom(InputStream i) throws Exception {
			;
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
		}
		
	}	
	
	public static class TGetInstalledApps extends Mensaje{
		
		public TGetInstalledApps(int tag) {
			super(tag, TGETINSTALLEDAPPS);
		}
		
		public TGetInstalledApps(){
			super(newTag(), TGETINSTALLEDAPPS);
		}

		protected void readFrom(InputStream i) throws Exception {
			;
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
		}
		
	}	
	
	public static class RGetInstalledApps extends Mensaje{
		String[] appsNames;
		int numApps;
		
		public RGetInstalledApps(int tag){
			super(tag, RGETINSTALLEDAPPS);
		}
		
		public RGetInstalledApps(TGetInstalledApps t, String[] apps){
			super(t.getTag(), RGETINSTALLEDAPPS);
			appsNames = apps;
		}
		
		public String[] getValor(){
			return appsNames;
		}
		
		protected void readFrom(InputStream i) throws IOException{
			DataInputStream incon = new DataInputStream(i);
			int lonnames = incon.readInt();
			numApps = lonnames;
			String namesrec[] = new String[lonnames];
			for(int j = 0; j<lonnames; j++){
				namesrec[j] = incon.readUTF();
			}
			appsNames = namesrec;
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
			DataOutputStream output = new DataOutputStream(o);
			output.writeInt(appsNames.length);
			for(int j = 0; j<appsNames.length; j++){
				output.writeUTF(appsNames[j]);
			}
		}
		
		public String toString(){
			return super.toString() + appsNames;
		}
	}
	
	public static class TGetInfo extends Mensaje{
		
		public TGetInfo(int tag) {
			super(tag, TGETINFO);
		}
		
		public TGetInfo(){
			super(newTag(), TGETINFO);
		}

		protected void readFrom(InputStream i) throws Exception {
			;
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
		}
		
	}
	
	public static class RGetInfo extends Mensaje{
		String info;
		
		public RGetInfo(int tag){
			super(tag, RGETINFO);
		}
		
		public RGetInfo(TGetInfo t, String inf){
			super(t.getTag(), RGETINFO);
			info = inf;
		}
		
		public String getValor(){
			return info;
		}
		
		protected void readFrom(InputStream i) throws IOException{
			DataInputStream incon = new DataInputStream(i);
			info = incon.readUTF();
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
			DataOutputStream output = new DataOutputStream(o);
			output.writeUTF(info);
		}
		
		public String toString(){
			return super.toString() + info;
		}
	}
	
	public static class ROk extends Mensaje{
	
		public ROk(int tag){
			super(tag, ROK);
		}
	
		public ROk(Mensaje t){
			super(t.getTag(), ROK);
		}

		protected void readFrom(InputStream i) throws Exception {
			;
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
		}
	}
	
	public static class RErr extends Mensaje{
		private String error;
		
		public RErr(int tag){
			super(tag, RERR);
		}
		
		public RErr(Mensaje t, String e){
			super(t.getTag(), RERR);
			error = e;
		}
		
		public String getError(){
			return error;
		}

		protected void readFrom(InputStream i) throws Exception {
			DataInputStream incon = new DataInputStream(i);
			error = incon.readUTF();
		}
		
		public void writeTo(OutputStream o) throws Exception{
			super.writeTo(o);
			DataOutputStream output = new DataOutputStream(o);
			output.writeUTF(error);
		}
		
		public String toString(){
			return super.toString() + " Error: " + error;
		}
	}
	
}

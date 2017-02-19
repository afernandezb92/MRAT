package elements;

public class Apps {
	String[] arrayApps;
	
	public Apps(String[] app){
		arrayApps = app;
	}
	
	public String toString(){
		String stringApps = "";
		for (int i = 0; i < arrayApps.length; i++){
			stringApps = stringApps + "App: " + arrayApps[i] + "\n";
		}
		return stringApps;
	}

}

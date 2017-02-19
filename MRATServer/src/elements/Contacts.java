package elements;

public class Contacts {
	String[][] arrayContacts;
	
	public Contacts(String[][] c){
		arrayContacts = c;
	}
	
	public String toString(){
		String stringContacts = "";
		for (int i = 0; i < arrayContacts[0].length; i++){
			stringContacts = stringContacts + arrayContacts[0][i] + ": " + arrayContacts[1][i] + "\n";
		}
		return stringContacts;
	}

}

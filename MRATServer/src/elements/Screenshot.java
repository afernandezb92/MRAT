package elements;

import java.awt.FlowLayout;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Screenshot {
	byte [] screenshot;

	public Screenshot(byte[] screenshot) {
		super();
		this.screenshot = screenshot;
	}
	
	public void show(){
		JFrame frame=new JFrame();
		JLabel lbl=new JLabel();
		ImageIcon icon = new ImageIcon(screenshot);
		frame.setLayout(new FlowLayout());
		frame.setSize(600,1000);
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}
	
	public void save(String path) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(path);
			stream.write(screenshot);
			stream.flush();
			stream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

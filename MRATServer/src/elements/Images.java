package elements;

import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Images {

	private ArrayList<byte[]> arrayImages;

    public Images(ArrayList<byte[]> images) {
        arrayImages = images;
    }

    public void show(){
    	for(int i = 0; i < arrayImages.size(); i++){
    		JFrame frame=new JFrame();
    		JLabel lbl=new JLabel();
    		ImageIcon icon = new ImageIcon(arrayImages.get(i));
    		frame.setLayout(new FlowLayout());
    		frame.setSize(600,1000);
    		lbl.setIcon(icon);
    		frame.add(lbl);
    		frame.setVisible(true);
    		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    	}
	}

    public ArrayList<byte[]> getArrayImages() {
        return arrayImages;
    }
}

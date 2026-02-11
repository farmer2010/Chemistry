package sct;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.*;

public class Main{
	public static void main(String[] args) {
		new File("record/A").mkdirs();
		new File("record/B").mkdirs();
		new File("record/C").mkdirs();
		new File("record/D").mkdirs();
		new File("record/E").mkdirs();
		new File("record/F").mkdirs();
		new File("record/G").mkdirs();
		new File("record/H").mkdirs();
		new File("record/I").mkdirs();
		new File("record/J").mkdirs();
		new File("record/energy").mkdirs();
		new File("record/temp").mkdirs();
		//
		new File("saved objects").mkdirs(); 
		new File("saved worlds").mkdirs();
		//
		JFrame frame = new JFrame("Chemistry");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new World());
		Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Dimension screenSize = toolkit.getScreenSize();
		int W = (int) screenSize.getWidth();
		int H = (int) screenSize.getHeight();
		frame.setSize(W, H);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}
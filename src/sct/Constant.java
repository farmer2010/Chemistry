package sct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Constant {
	static Toolkit toolkit = Toolkit.getDefaultToolkit();
    static Dimension screenSize = toolkit.getScreenSize();
    //
	public static int W = (int) screenSize.getWidth();
	public static int H = (int) screenSize.getHeight();
    //public static int W = 3840;
    //public static int H = 2160;
	public static double temp = 30;//температура среды
	public static int ch_bot_limit = 80;
	public static int bot_collect_speed = 8;
	public static int genome_length = 256;
	public static int[] photo_list = {6, 5, 4, 3, 0, 0, 0, 0};//энергия от фотосинтеза в разнях слоях
	public static int[] world_scale = {540, 360};
	public static int cell_size = Math.max(Math.min((W - 300) / world_scale[0], H / world_scale[1]), 1);
	public static int starting_bots = world_scale[0] * world_scale[1] / 13;
	public static int max_age = 1000;
	public static int max_energy = 1000;
	public static int max_health = 100;
	public static int max_temp = 200;
	//
	//
	//
	public static int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	//
	public static int[] get_rotate_position(int rot, int[] sp){
		int[] pos = new int[2];
		pos[0] = sp[0] + Constant.movelist[rot][0];
		pos[1] = sp[1] + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = world_scale[0] - 1;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	//
	public static Color gradient(Color color1, Color color2, double grad) {
		int r = Math.min(Math.max((int)(color1.getRed() * (1 - grad) + color2.getRed() * grad), 0), 255);
		int g = Math.min(Math.max((int)(color1.getGreen() * (1 - grad) + color2.getGreen() * grad), 0), 255);
		int b = Math.min(Math.max((int)(color1.getBlue() * (1 - grad) + color2.getBlue() * grad), 0), 255);
		return(new Color(r, g, b));
	}
	//
	public static int sector(int y) {
		int sec = y / (Constant.world_scale[1] / 8);
		if (sec > 7) {
			sec = 7;
		}
		return(sec);
	}
}

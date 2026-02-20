package sct;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Draw {
	public static void draw_background(World world, Graphics canvas, int gas_draw_type) {
		if (world.gas_draw_type > 0) {
			for (int x = 0; x < Constant.world_scale[0]; x++) {
				for (int y = 0; y < Constant.world_scale[1]; y++) {
					if (gas_draw_type == 1) {//глюкоза
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(0, 0, 0), Math.min(world.ch[0][x][y] / 500, 1)));
					}else if (gas_draw_type == 2) {//кристалл
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(129, 164, 240), Math.min(world.ch[1][x][y] / 500, 1)));
					}else if (gas_draw_type == 3) {//кислород
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(112, 219, 235), Math.min(world.ch[2][x][y] / 500, 1)));
					}else if (gas_draw_type == 4) {//углекислота
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(38, 36, 235), Math.min(world.ch[3][x][y] / 500, 1)));
					}else if (gas_draw_type == 5) {//водород
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(200, 176, 250), Math.min(world.ch[4][x][y] / 500, 1)));
					}else if (gas_draw_type == 6) {//вольфрам
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(168, 194, 209), Math.min(world.ch[5][x][y] / 100, 1)));//
					}else if (gas_draw_type == 7) {//катализатор
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(94, 158, 122), Math.min(world.ch[6][x][y] / 500, 1)));
					}else if (gas_draw_type == 8) {//торий
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(246, 122, 236), Math.min(world.ch[7][x][y] / 500, 1)));
					}else if (gas_draw_type == 9) {//яд
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(153, 0, 0), Math.min(world.ch[8][x][y] / 500, 1)));
					}else if (gas_draw_type == 10) {//железо
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(105, 73, 62), Math.min(world.ch[9][x][y] / 500, 1)));
					}else if (gas_draw_type == 11) {//температура
						canvas.setColor(Constant.gradient(new Color(255, 255, 0), new Color(255, 0, 0), world.temp_map[x][y] / 400.0));
					}
					canvas.fillRect(x * Constant.cell_size, y * Constant.cell_size, Constant.cell_size, Constant.cell_size);
					if (world.ch[1][x][y] > 500 && world.gas_draw_type < 11) {
						canvas.setColor(Constant.gradient(new Color(255, 255, 255), new Color(129, 164, 240), Math.min(world.ch[1][x][y] / 500, 1)));
						canvas.fillRect(x * Constant.cell_size, y * Constant.cell_size, Constant.cell_size, Constant.cell_size);
					}
				}
			}
		}
	}
	//
	public static void draw_text(World world, Graphics canvas) {
		canvas.setColor(new Color(0, 0, 0));
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", Constant.W - 300, 20);
		canvas.drawString("version 0.1", Constant.W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(world.steps), Constant.W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(world.obj_count) + ", bots: " + String.valueOf(world.b_count), Constant.W - 300, 80);
		String txt = "";
		if (world.draw_type == 0) {
			txt = "predators view";
		}else if (world.draw_type == 1) {
			txt = "color view";
		}else if (world.draw_type == 2) {
			txt = "energy view";
		}else if (world.draw_type == 3) {
			txt = "age view";
		}else if (world.draw_type == 4){
			txt = "A view";
		}else if (world.draw_type == 5){
			txt = "B view";
		}else if (world.draw_type == 6){
			txt = "C view";
		}else if (world.draw_type == 7){
			txt = "D view";
		}else if (world.draw_type == 8){
			txt = "E view";
		}else if (world.draw_type == 9){
			txt = "F view";
		}else if (world.draw_type == 10){
			txt = "G view";
		}else if (world.draw_type == 11){
			txt = "H view";
		}else if (world.draw_type == 12){
			txt = "I view";
		}else if (world.draw_type == 13){
			txt = "J view";
		}else if (world.draw_type == 14){
			txt = "temp view";
		}
		canvas.drawString("render type: " + txt, Constant.W - 300, 100);
		if (world.mouse == 0) {
			txt = "select";
		}else if (world.mouse == 1) {
			txt = "set";
		}else {
			txt = "remove";
		}
		canvas.drawString("mouse function: " + txt, Constant.W - 300, 120);
		canvas.drawString("Render types:", Constant.W - 300, 180);
		canvas.drawString("Selection:", Constant.W - 300, 275);
		canvas.drawString("enter name:", Constant.W - 300, 405);
		canvas.drawString("Mouse functions:", Constant.W - 300, 445);
		canvas.drawString("Load:", Constant.W - 300, 490);
		canvas.drawString("enter name:", Constant.W - 300, 510);
		canvas.drawString("Controls:", Constant.W - 300, 580);
		canvas.drawString("Gas draw type:", Constant.W - 300, 655);
		canvas.drawString("Tungsten count: " + String.valueOf(world.c_tungsten), Constant.W - 300, 1000);
		canvas.drawString("For cell: " + String.valueOf(world.c_tungsten / (Constant.world_scale[0] * Constant.world_scale[1])), Constant.W - 300, 1025);
		if (world.selection != null) {
			canvas.drawString("energy: " + String.valueOf((int)world.selection.energy) + ", temp: " + String.valueOf((int)world.selection.temp) + ", age: " + String.valueOf(world.selection.age), Constant.W - 300, 295);
			canvas.drawString("position: " + "[" + String.valueOf(world.selection.xpos) + ", " + String.valueOf(world.selection.ypos) + "]", Constant.W - 300, 315);
			canvas.drawString("color: " + "(" + String.valueOf(world.selection.c_red) + ", " + String.valueOf(world.selection.c_green) + ", " + String.valueOf(world.selection.c_blue) + ")", Constant.W - 300, 335);
			String t = "sp: " + String.valueOf(Reactions.get_reaction_from_num(world.selection.genes[0][0])) + ", " + String.valueOf(Reactions.get_reaction_from_num(world.selection.genes[1][0])) + ", " + String.valueOf(Reactions.get_reaction_from_num(world.selection.genes[2][0]));
			if (world.draw_type >= 4 && world.draw_type <= 13) {
				canvas.drawString(t + ", ch: " + String.valueOf(world.selection.my_ch[world.draw_type - 4]), Constant.W - 300, 355);
			}else {
				canvas.drawString(t, Constant.W - 300, 355);
			}
			canvas.setColor(new Color(0, 0, 0, 200));
			canvas.fillRect(0, 0, Constant.W - 300, 1080);
			if (!world.sh_brain) {
				canvas.setColor(new Color(255, 0, 0));
				canvas.fillRect(world.selection.xpos * Constant.cell_size, world.selection.ypos * Constant.cell_size, Constant.cell_size, Constant.cell_size);
			}
			canvas.setColor(new Color(0, 0, 0));
			//for (int i = 0; i < 10; i++) {
			//	canvas.drawString(String.valueOf(selection.pred_colors[i][0]) + ", " + String.valueOf(selection.pred_colors[i][1]) + ", " + String.valueOf(selection.pred_colors[i][2]), selection.xpos * 3, selection.ypos * 3 + 20 * i);
			//}
		}else {
			canvas.drawString("none", Constant.W - 300, 295);
		}
		if (world.sh_brain) {
			canvas.setColor(new Color(90, 90, 90));
			canvas.fillRect(0, 0, 600, 200);
			canvas.setColor(new Color(50, 50, 50));
			canvas.fillOval(25, 25, 130, 130);
			canvas.setColor(new Color(90, 90, 90));
			canvas.fillOval(40, 40, 100, 100);
			canvas.setColor(new Color(0, 128, 255));
			int n = 50;
			if (world.selection.rotate % 2 == 1) {n = 35;}
			for (int i = 0; i < n; i++) {
				canvas.fillOval(82 + Constant.movelist[world.selection.rotate][0] * i, 82 + Constant.movelist[world.selection.rotate][1] * i, 16, 16);
			}
			draw_bar(canvas, new Color(255, 0, 0), new Color(0, 255, 0), 175, 25, 30, 150, world.selection.health, Constant.max_health, "Health:");//окно с параметрами бота
			draw_bar(canvas, new Color(255, 255, 0), new Color(255, 0, 0), 240, 25, 20, 150, world.selection.energy, Constant.max_energy, "Energy:");
			draw_bar(canvas, new Color(0, 0, 255), new Color(255, 255, 0), 280, 25, 20, 150, world.selection.age, Constant.max_age, "Age:");
			draw_bar(canvas, new Color(255, 255, 0), new Color(255, 0, 0), 320, 25, 20, 150, world.selection.temp, Constant.max_age, "Temp:");
			draw_bar(canvas, new Color(128, 128, 128), new Color(255, 255, 0), 370, 25, 10, 150, world.selection.my_ch[0], Constant.ch_bot_limit, "A:");
			draw_bar(canvas, new Color(255, 255, 0), new Color(60, 255, 128), 390, 25, 10, 150, world.selection.my_ch[1], Constant.ch_bot_limit, "B:");
			draw_bar(canvas, new Color(255, 0, 0), new Color(112, 219, 235), 410, 25, 10, 150, world.selection.my_ch[2], Constant.ch_bot_limit, "C:");
			draw_bar(canvas, new Color(112, 219, 235), new Color(38, 36, 235), 430, 25, 10, 150, world.selection.my_ch[3], Constant.ch_bot_limit, "D:");
			draw_bar(canvas, new Color(0, 255, 0), new Color(200, 176, 250), 450, 25, 10, 150, world.selection.my_ch[4], Constant.ch_bot_limit, "E:");
			draw_bar(canvas, new Color(50, 50, 50), new Color(200, 200, 200), 470, 25, 10, 150, world.selection.my_ch[5], Constant.ch_bot_limit, "F:");
			draw_bar(canvas, new Color(255, 195, 120), new Color(43, 158, 111), 490, 25, 10, 150, world.selection.my_ch[6], Constant.ch_bot_limit, "G:");
			draw_bar(canvas, new Color(136, 61, 221), new Color(246, 122, 236), 510, 25, 10, 150, world.selection.my_ch[7], Constant.ch_bot_limit, "H:");
			draw_bar(canvas, new Color(0, 255, 0), new Color(180, 0, 0), 530, 25, 10, 150, world.selection.my_ch[8], Constant.ch_bot_limit, "I:");
			draw_bar(canvas, new Color(236, 158, 0), new Color(105, 73, 62), 550, 25, 10, 150, world.selection.my_ch[9], Constant.ch_bot_limit, "J:");
			canvas.setColor(new Color(128, 128, 128));
			//for (int x = 0; x < 16; x++) {
			//	for (int y = 0; y < 16; y++) {
			//		canvas.setColor(new Color(128, 128, 128));
			//		canvas.fillRect(x * 45, 220 + y * 45, 40, 40);
			//		canvas.setColor(new Color(0, 0, 0));
			//		canvas.drawString(String.valueOf(world.selection.commands[x + y * 16]), x * 45, y * 45 + 240);
			//	}
			//}
		}
	}
	public static void draw_bar(Graphics canvas, Color color1, Color color2, int x, int y, int w, int h, double d, double d_max, String name) {
		canvas.setColor(new Color(70, 70, 70));
		canvas.fillRect(x - 3, y - 3, w + 6, h + 6);
		canvas.setColor(new Color(120, 120, 120));
		canvas.fillRect(x, y, w + 3, h + 3);
		canvas.setColor(new Color(50, 50, 50));
		canvas.fillRect(x, y, w, h);
		for (int i = 0; i < h; i++) {
			if ((h - 1 - i) / ((h - 1) * 1.0) * d_max < d) {
				canvas.setColor(Constant.gradient(color1, color2, (h - 1 - i) / ((h - 1) * 1.0)));
				canvas.fillRect(x, y + i, w, 1);
			}
		}
		canvas.setColor(new Color(0, 0, 0));
		canvas.setFont(new Font("arial", Font.BOLD, 11));
		canvas.drawString(name, x - 3, y - 10);
		canvas.setFont(new Font("arial", Font.BOLD, 14));
		canvas.drawString(String.valueOf((int)(d)), x, y + h + 16);
	}
}

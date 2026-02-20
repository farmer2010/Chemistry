package sct;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;
//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Bot> objects;
	Timer timer;
	int delay = 10;
	Random rand = new Random();
	Bot[][] Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
	Color gray = new Color(100, 100, 100);
	Color green = new Color(0, 255, 0);
	Color red = new Color(255, 0, 0);
	Color black = new Color(0, 0, 0);
	int steps = 0;
	int draw_type = 0;
	int b_count = 0;
	int obj_count = 0;
	int org_count = 0;
	int mouse = 0;
	JButton stop_button = new JButton("Stop");
	boolean pause = false;
	boolean render = true;
	Bot selection = null;
	int[] botpos = new int[2];
	int[] for_set;
	int[] mpos = new int[]{0, 0};
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show more");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JTextField for_save = new JTextField();
	JTextField for_load = new JTextField();
	boolean sh_brain = false;
	boolean rec = false;
	double[][][] ch = new double[10][Constant.world_scale[0]][Constant.world_scale[1]];
	double[][] temp_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
	int gas_draw_type = 0;
	JRadioButton[] bs = new JRadioButton[12];
	double c_tungsten = 0;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(e -> start_stop());
		stop_button.setBounds(Constant.W - 300, 125, 250, 35);
        add(stop_button);
        add_dr_button(Constant.W - 300, 190, "Predators", 0);
        add_dr_button(Constant.W - 170, 190, "Energy", 2);
        add_dr_button(Constant.W - 170, 215, "Age", 3);;
        add_dr_button(Constant.W - 300, 215, "Color", 1);
        add_dr_button(Constant.W - 300, 240, "Temp", 14);
        JButton select_button = new JButton("Select");
        select_button.addActionListener(e -> change_mouse(0));
		select_button.setBounds(Constant.W - 300, 455, 95, 20);
        add(select_button);
        JButton set_button = new JButton("Set");
        set_button.addActionListener(e -> change_mouse(1));
        set_button.setBounds(Constant.W - 200, 455, 95, 20);
        add(set_button);
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(e -> change_mouse(2));
        remove_button.setBounds(Constant.W - 100, 455, 95, 20);
        add(remove_button);
        //
        save_button.addActionListener(e -> save_bot());
        save_button.setBounds(Constant.W - 300, 365, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(e -> show_brain());
        show_brain_button.setBounds(Constant.W - 170, 365, 125, 20);
        show_brain_button.setEnabled(false);
        add(show_brain_button);
        //
        for_save.setBounds(Constant.W - 300, 410, 250, 20);
        add(for_save);
        //
        for_load.setBounds(Constant.W - 300, 515, 250, 20);
        add(for_load);
        //
        JButton load_bot_button = new JButton("Load bot");
        load_bot_button.addActionListener(e -> load_bot());
        load_bot_button.setBounds(Constant.W - 300, 540, 90, 20);
        add(load_bot_button);
        //
        JButton load_world_button = new JButton("Load world");
        //load_world_button.addActionListener(e -> load_world());
        load_world_button.setBounds(Constant.W - 205, 540, 90, 20);
        add(load_world_button);
        //
        JButton save_world_button = new JButton("Save world");
        //save_world_button.addActionListener(e -> save_world());
        save_world_button.setBounds(Constant.W - 110, 540, 90, 20);
        add(save_world_button);
        //
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(e -> WorldUtils.new_population(this));
        new_population_button.setBounds(Constant.W - 300, 590, 125, 20);//w-300, 590
        add(new_population_button);
        //
        render_button.addActionListener(e -> rndr());
        render_button.setBounds(Constant.W - 300, 615, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(e -> rcrd());
        record_button.setBounds(Constant.W - 170, 615, 125, 20);
        add(record_button);
        //
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(e -> WorldUtils.kill_all(this));
        kill_button.setBounds(Constant.W - 170, 590, 125, 20);
        add(kill_button);
        //
        ButtonGroup group = new ButtonGroup();//кнопки для переключения отрисовки фона
        add_radio_button(Constant.W - 300, 660, group, "no render", 0, true, bs);
        add_radio_button(Constant.W - 300, 680, group, "A", 1, false, bs);
        add_radio_button(Constant.W - 300, 700, group, "B", 2, false, bs);
        add_radio_button(Constant.W - 300, 720, group, "C", 3, false, bs);
        add_radio_button(Constant.W - 300, 740, group, "D", 4, false, bs);
        add_radio_button(Constant.W - 300, 760, group, "E", 5, false, bs);
        add_radio_button(Constant.W - 300, 780, group, "F", 6, false, bs);
        add_radio_button(Constant.W - 300, 800, group, "G", 7, false, bs);
        add_radio_button(Constant.W - 300, 820, group, "H", 8, false, bs);
        add_radio_button(Constant.W - 300, 840, group, "I", 9, false, bs);
        add_radio_button(Constant.W - 300, 860, group, "J", 10, false, bs);
        add_radio_button(Constant.W - 300, 880, group, "temp", 11, false, bs);
        //
        add_dr_button(Constant.W - 150, 680, "A", 4);//кнопки для переключения отрисовки веществ в ботах
        add_dr_button(Constant.W - 150, 700, "B", 5);
        add_dr_button(Constant.W - 150, 720, "C", 6);
        add_dr_button(Constant.W - 150, 740, "D", 7);
        add_dr_button(Constant.W - 150, 760, "E", 8);
        add_dr_button(Constant.W - 150, 780, "F", 9);
        add_dr_button(Constant.W - 150, 800, "G", 10);
        add_dr_button(Constant.W - 150, 820, "H", 11);
        add_dr_button(Constant.W - 150, 840, "I", 12);
        add_dr_button(Constant.W - 150, 860, "J", 13);
        //
		WorldUtils.kill_all(this);
		//ch[1][200][300] = 500;
		timer.start();
	}
	public void add_dr_button(int x, int y, String name, int dtype) {
		JButton button = new JButton(name);
        button.addActionListener(e -> change_draw_type(dtype));
		button.setBounds(x, y, 125, 20);
        add(button);
	}
	public void add_radio_button(int x, int y, ButtonGroup g, String name, int i, boolean selected, JRadioButton[] lst) {
		JRadioButton c = new JRadioButton(name, selected);
        c.setBounds(x, y, 100, 20);
		add(c);
		g.add(c);
		lst[i] = c;
	}
	public boolean find_map_pos(int[] pos, int state) {//не сказать, что это прямо нужно.
		if (Map[pos[0]][pos[1]] != null) {
			if (Map[pos[0]][pos[1]].state == state) {
				return(true);
			}
		}
		return(false);
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		if (render) {//рисуем
			Draw.draw_background(this, canvas, gas_draw_type);
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
			canvas.setColor(new Color(30, 30, 30));
			//
			if (mpos[0] < Constant.world_scale[0] * Constant.cell_size && mpos[1] < Constant.world_scale[1] * Constant.cell_size && gas_draw_type >= 1) {//сколько вещества(или температуры под курсором)
				if (gas_draw_type <= 10) {
					canvas.drawString(String.valueOf(ch[gas_draw_type - 1][mpos[0] / Constant.cell_size][mpos[1] / Constant.cell_size]), mpos[0], mpos[1]);
				}else if (gas_draw_type == 11) {
					canvas.drawString(String.valueOf(temp_map[mpos[0] / Constant.cell_size][mpos[1] / Constant.cell_size]), mpos[0], mpos[1]);
				}
			}
			canvas.drawString("[" + String.valueOf(mpos[0] / Constant.cell_size) + ", " + String.valueOf(mpos[1] / Constant.cell_size) + "]", mpos[0], mpos[1] + 20);
		}
		canvas.setColor(gray);
		canvas.fillRect(Constant.W - 300, 0, 300, 1080);
		Draw.draw_text(this, canvas);//рисуем текст
	}
	//
	public void update() {
		for (int i = 0; i < 12; i++) {//смена режима отрисовки фона
			if (bs[i].isSelected()) {
				gas_draw_type = i;
			}
		}
		//
		PointerInfo a = MouseInfo.getPointerInfo();//координаты мыши для отображения текста
		Point b = a.getLocation();
		int x = (int) b.getX();
		int y = (int) b.getY();
		mpos[0] = x;
		mpos[1] = y;
		//
		if (!pause) {
			if (rec && steps % 25 == 0) {
				WorldUtils.record(this);
			}
			if (steps % 100 == 0) {
				c_tungsten = 0;
				for (int cx = 0; cx < Constant.world_scale[0]; cx++) {
					for (int cy = 0; cy < Constant.world_scale[1]; cy++) {
						c_tungsten += ch[5][cx][cy];
					}
				}
			}
			steps++;
			b_count = 0;//количество ботов
			obj_count = 0;
			org_count = 0;
			ListIterator<Bot> bot_iterator = objects.listIterator();
			while (bot_iterator.hasNext()) {
				Bot next_bot = bot_iterator.next();
				next_bot.Update(bot_iterator);
				if (selection != null) {
					if (next_bot.xpos == selection.xpos && next_bot.ypos == selection.ypos) {
						if (next_bot != selection) {
							selection = null;
							save_button.setEnabled(false);
							show_brain_button.setEnabled(false);
							sh_brain = false;
						}
					}
				}
				obj_count++;
				if (next_bot.state != 0) {
					org_count++;
				}else {
					b_count++;
				}
			}
			if (obj_count == 0) {
				rec = false;
				record_button.setText("Record: off");
			}
			if (selection != null) {//если выбранный бот умер, снимаем выделение
				int[] pos = {selection.xpos, selection.ypos};
				if (selection.killed == 1 || !find_map_pos(pos, 0) || selection.state != 0){
					selection = null;
					save_button.setEnabled(false);
					show_brain_button.setEnabled(false);
					sh_brain = false;
				}
			}
			for (int i = 0; i < 10; i++) {//обновление веществ
				if (Reactions.viscosity[i] > 0) {
					WorldUtils.gas(this, ch[i], i);//газы и жидкости
				}
			}
			//WorldUtils.crystal(this, ch[1]);
			WorldUtils.hydrogenium(this, ch[4], 4);
			WorldUtils.iron(ch[9]);
			WorldUtils.temp(temp_map);
		}
		ListIterator<Bot> iterator = objects.listIterator();//удаляем мертвых ботов
		while (iterator.hasNext()) {
			Bot next_bot = iterator.next();
			if (next_bot.killed == 1) {
				iterator.remove();
			}
		}
		repaint();
	}
	//
	public void update_mouse(MouseEvent e, boolean do_select) {
		if (e.getX() < Constant.world_scale[0] * Constant.cell_size && e.getY() < Constant.world_scale[1] * Constant.cell_size) {
			botpos[0] = e.getX() / Constant.cell_size;
			botpos[1] = e.getY() / Constant.cell_size;
			if (mouse == 0) {//select
				if (do_select) {
					if (find_map_pos(botpos, 0)) {
						Bot b = Map[botpos[0]][botpos[1]];
						selection = b;
						save_button.setEnabled(true);
						show_brain_button.setEnabled(true);
					}else {
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}
			}else if (mouse == 1) {//set
				if (for_set != null) {
					if (Map[botpos[0]][botpos[1]] == null) {
						if (for_set != null) {
							Bot new_bot = new Bot(botpos[0], botpos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Constant.temp, Map, ch, temp_map, objects);
							for (int i = 0; i < Constant.genome_length; i++) {
								new_bot.commands[i] = for_set[i];
							}
							objects.add(new_bot);
							Map[botpos[0]][botpos[1]] = new_bot;
						}
					}
				}
			}else {//remove
				if (Map[botpos[0]][botpos[1]] != null) {
					Bot b = Map[botpos[0]][botpos[1]];
					b.energy = 0;
					b.killed = 1;
					Map[botpos[0]][botpos[1]] = null;
				}
			}
		}
	}
	//
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			update_mouse(e, true);
		}
		public void mouseDragged(MouseEvent e) {
			update_mouse(e, false);
		}
		public void actionPerformed(ActionEvent e) {
			update();
		}
	}
	//
	//Функции кнопок
	//
	public void change_draw_type(int num) {
		draw_type = num;
	}
	public void start_stop() {
		pause = !pause;
		if (pause) {
			stop_button.setText("Start");
		}else {
			stop_button.setText("Stop");
		}
	}
	public void change_mouse(int num) {
		mouse = num;
	}
	public void rndr() {
		render = !render;
		if (render) {
			render_button.setText("Render: on");
		}else {
			render_button.setText("Render: off");
		}
	}
	public void rcrd() {
		rec = !rec;
		if (rec) {
			record_button.setText("Record: on");
		}else {
			record_button.setText("Record: off");
		}
	}
	public void show_brain() {
		sh_brain = !sh_brain;
		if (pause == false) {
			pause = true;
		}else if (sh_brain == false) {
			pause = false;
		}
	}
	public void save_bot() {
		WorldUtils.save_bot(selection, for_save.getText());
	}
	public void load_bot() {
		for_set = WorldUtils.load_bot(for_load.getText());
	}
}

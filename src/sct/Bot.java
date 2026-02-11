package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	public int x;
	public int y;
	public int xpos;
	public int ypos;
	public Color color;
	public Bot[][] map;
	public double[][] temp_map;
	public double[][][] ch;//карта веществ
	//
	public double energy;
	public double temp;
	public int health = Constant.max_health;
	public int age = Constant.max_age;
	public double[] my_ch = new double[10];//вещества внутри бота
	public int rotate = rand.nextInt(8);
	//
	public int killed = 0;
	public int index = 0;
	public int state = 0;//
	public int c_red = -1;
	public int c_green = -1;
	public int c_blue = -1;
	public int[][] pred_colors = new int[][] {//цвет в режиме отрисовки хищников
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1},
		{-1, -1, -1}
	};
	//
	public int[] commands = new int[Constant.genome_length];//геном
	public int[][] genes = new int[2][3];//органеллы
	//
	public Bot(int new_xpos, int new_ypos, Color new_color, double new_energy, double new_temp, Bot[][] new_map, double[][][] new_ch, double[][] new_temp_map, ArrayList<Bot> new_objects) {
		ch = new_ch;
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * Constant.cell_size;
		y = new_ypos * Constant.cell_size;
		color = new_color;
		energy = new_energy;
		temp = new_temp;
		objects = new_objects;
		map = new_map;
		temp_map = new_temp_map;
		for (int i = 0; i < Constant.genome_length; i++) {
			commands[i] = rand.nextInt(Constant.genome_length);
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				genes[i][j] = rand.nextInt(64);
			}
		}
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
			if (draw_type == 0) {//режим отрисовки хищников
				if (c_red == -1 || c_green == -1 || c_blue == -1) {
					canvas.setColor(new Color(128, 128, 128));
				}else {
					canvas.setColor(new Color(c_red, c_green, c_blue));
				}
			}else if (draw_type == 1) {//цвета
				canvas.setColor(color);//сейчас не нужная функция
			}else if (draw_type == 2) {//энергии
				int g = 255 - (int)(energy / 1000.0 * 255.0);
				if (g > 255) {
					g = 255;
				}else if (g < 0) {
					g = 0;
				}
				canvas.setColor(new Color(255, g, 0));
			}else if (draw_type == 3) {//возраста
				canvas.setColor(Constant.gradient(new Color(0, 0, 255), new Color(255, 255, 0), (age * 1.0) / 1000));
			}else if (draw_type == 4) {//глюкозы
				canvas.setColor(Constant.gradient(new Color(128, 128, 128), new Color(255, 255, 0), Math.min(my_ch[0] / 80, 1)));
			}else if (draw_type == 5) {//кристалла
				canvas.setColor(Constant.gradient(new Color(255, 255, 0), new Color(60, 255, 128), Math.min(my_ch[1] / 80, 1)));
			}else if (draw_type == 6) {//кислорода
				canvas.setColor(Constant.gradient(new Color(255, 0, 0), new Color(112, 219, 235), Math.min(my_ch[2] / 80, 1)));
			}else if (draw_type == 7) {//углекислоты
				canvas.setColor(Constant.gradient(new Color(112, 219, 235), new Color(38, 36, 235), Math.min(my_ch[3] / 80, 1)));
			}else if (draw_type == 8) {//водорода
				canvas.setColor(Constant.gradient(new Color(0, 255, 0), new Color(200, 176, 250), Math.min(my_ch[4] / 80, 1)));
			}else if (draw_type == 9) {//вольфрама
				canvas.setColor(Constant.gradient(new Color(50, 50, 50), new Color(200, 200, 200), Math.min(my_ch[5] / 80, 1)));
			}else if (draw_type == 10) {//катализатора
				canvas.setColor(Constant.gradient(new Color(255, 195, 120), new Color(43, 158, 111), Math.min(my_ch[6] / 80, 1)));
			}else if (draw_type == 11) {//тория
				canvas.setColor(Constant.gradient(new Color(136, 61, 221), new Color(246, 122, 236), Math.min(my_ch[7] / 80, 1)));
			}else if (draw_type == 12) {//яда
				canvas.setColor(Constant.gradient(new Color(0, 255, 0), new Color(180, 0, 0), Math.min(my_ch[8] / 80, 1)));
			}else if (draw_type == 13) {//железа
				canvas.setColor(Constant.gradient(new Color(236, 158, 0), new Color(105, 73, 62), Math.min(my_ch[9] / 80, 1)));
			}else if (draw_type == 14) {//температуры
				canvas.setColor(Constant.gradient(new Color(255, 255, 0), new Color(255, 0, 0), Math.min(temp / 400, 1)));
			}
			canvas.fillRect(x, y, Constant.cell_size, Constant.cell_size);
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//бот
				update_ch();
				energy -= 1;
				age -= 1;
				update_genes();//органеллы
				update_commands(iterator);//геном
				if (energy <= 0 || age <= 0 || ch[1][xpos][ypos] >= 500 || health <= 0) {//помереть от: недостатка энергии; старости; переизбытка кристалла; мало хп
					BotUtils.die(this);
					return(0);
				}
				if (energy > Constant.max_energy) {
					energy = Constant.max_energy;
				}
				if (energy >= 800) {//автоматическое деление
					BotCommands.multiply(this, rotate, iterator);
				}
			}
		}
		return(0);
	}
	public void update_genes() {//органеллы
		for (int i = 0; i < 2; i++) {
			if (gene_condition(genes[i][1], genes[i][2]) && genes[i][0] < Reactions.reactions.length) {
				BotUtils.reaction(this, Reactions.get_reaction_from_num(genes[i][0]));//функция выполнения реакции
			}
		}
	}
	public boolean gene_condition(int cond, int param) {//сейчас условия для органелл берутся отсюда. Не думаю, что они очень сильно нужны ботам
		if (cond == 0) {//энергии >= x * 15
			return(energy >= param * 15);
		}else if (cond == 1) {//энергии <= x * 15
			return(energy <= param * 15);
		}else if (cond == 2) {//возраст >= x * 15
			return(age >= param * 15);
		}else if (cond == 3) {//возраст <= x * 15
			return(age <= param * 15);
		}else if (cond == 4) {//направление > x % 8
			return(rotate > param % 8);
		}else if (cond == 5) {//направление == x % 8
			return(rotate == param % 8);
		}else if (cond == 6) {//направление < x % 8
			return(rotate < param % 8);
		}else if (cond == 7) {//xpos >= x * 8.4
			return(xpos >= param * 8.4);
		}else if (cond == 8) {//xpos <= x * 8.4
			return(xpos <= param * 8.4);
		}else if (cond == 9) {//ypos >= x * 5.6
			return(ypos >= param * 5.6);
		}else if (cond == 10) {//xpos <= x * 5.6
			return(ypos <= param * 5.6);
		}else if (cond == 11) {//соседей > x % 8
			return(BotUtils.count_neighbours(this) > param % 8);
		}else if (cond == 12) {//соседей == x % 8
			return(BotUtils.count_neighbours(this) == param % 8);
		}else if (cond == 13) {//соседей < x % 8
			return(BotUtils.count_neighbours(this) < param % 8);
		}else if (cond == 14) {//"A" у меня >= x * 1.25
			return(my_ch[0] >= param * 1.25);
		}else if (cond == 15) {//"A" у меня <= x * 1.25
			return(my_ch[0] <= param * 1.25);
		}else if (cond == 16) {//"B" у меня >= x * 1.25
			return(my_ch[1] >= param * 1.25);
		}else if (cond == 17) {//"B" у меня <= x * 1.25
			return(my_ch[1] <= param * 1.25);
		}else if (cond == 18) {//"C" у меня >= x * 1.25
			return(my_ch[2] >= param * 1.25);
		}else if (cond == 19) {//"C" у меня <= x * 1.25
			return(my_ch[2] <= param * 1.25);
		}else if (cond == 20) {//"D" у меня >= x * 1.25
			return(my_ch[3] >= param * 1.25);
		}else if (cond == 21) {//"D" у меня <= x * 1.25
			return(my_ch[3] <= param * 1.25);
		}else if (cond == 22) {//"E" у меня >= x * 1.25
			return(my_ch[4] >= param * 1.25);
		}else if (cond == 23) {//"E" у меня <= x * 1.25
			return(my_ch[4] <= param * 1.25);
		}else if (cond == 24) {//"F" у меня >= x * 1.25
			return(my_ch[5] >= param * 1.25);
		}else if (cond == 25) {//"F" у меня <= x * 1.25
			return(my_ch[5] <= param * 1.25);
		}else if (cond == 26) {//"G" у меня >= x * 1.25
			return(my_ch[6] >= param * 1.25);
		}else if (cond == 27) {//"G" у меня <= x * 1.25
			return(my_ch[6] <= param * 1.25);
		}else if (cond == 28) {//"H" у меня >= x * 1.25
			return(my_ch[7] >= param * 1.25);
		}else if (cond == 29) {//"H" у меня <= x * 1.25
			return(my_ch[7] <= param * 1.25);
		}else {
			return(true);
		}
	}
	public void update_ch() {//обновление веществ
		for (int i = 0; i < 10; i++) {
			if (ch[i][xpos][ypos] > my_ch[i]) {//сбор веществ из клетки под собой
				double a = Math.min(ch[i][xpos][ypos] - my_ch[i], 500);
				double c = Math.min(Math.min(a * Reactions.collect_speed[i], Constant.ch_bot_limit - my_ch[i]), ch[i][xpos][ypos]);
				my_ch[i] += c;
				ch[i][xpos][ypos] -= c;
			}
			if (my_ch[i] > Constant.ch_bot_limit) {//если слишком много, выбрасываем обратно
				double c = (my_ch[i] - Constant.ch_bot_limit) / 9.0;
				my_ch[i] = Constant.ch_bot_limit;
				for (int j = 0; j < 8; j++) {
					int[] pos = Constant.get_rotate_position(j, new int[] {xpos, ypos});
					if (BotUtils.is_free(this, j)) {
						ch[i][pos[0]][pos[1]] += c;
					}else {
						ch[i][xpos][ypos] += c;
					}
				}
				ch[i][xpos][ypos] += c;
			}
		}
		double t = temp;//усреднение температуры с температурой среды со временем
		double tm = temp_map[xpos][ypos];
		temp += (tm - t) / 10;
		temp_map[xpos][ypos] += (t - tm) / 10;
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		for (int i = 0; i < 5; i++) {
			int command = commands[index] % 64;
			if (command == 0) {//повернуться
				rotate += commands[(index + 1) % Constant.genome_length] % 8;
				rotate %= 8;
				index += 2;
				index %= Constant.genome_length;
			}else if (command == 1) {//сменить направление
				rotate = commands[(index + 1) % Constant.genome_length] % 8;
				index += 2;
				index %= Constant.genome_length;
			}else if (command == 2) {//походить относительно
				int sens = BotCommands.move(this, commands[(index + 1) % Constant.genome_length] % 8);
				if (sens == 1) {
					energy -= 1;
					temp += 3;
				}
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if(command == 3) {//походить абсолютно
				int sens = BotCommands.move(this, rotate);
				if (sens == 1) {
					energy -= 1;
					temp += 3;
				}
				index += 1;
				index %= Constant.genome_length;
				break;
			}else if (command == 4) {//атаковать относительно
				energy -= 10;
				BotCommands.attack(this, commands[(index + 1) % Constant.genome_length] % 8, commands[(index + 2) % Constant.genome_length] % 32 + 10);
				index += 3;
				index %= Constant.genome_length;
				break;
			}else if (command == 5) {//атаковать абсолютно
				energy -= 10;
				BotCommands.attack(this, rotate, commands[(index + 1) % Constant.genome_length] % 32 + 10);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 6) {//посмотреть относительно
				int rot = commands[(index + 1) % Constant.genome_length] % 8;
				index = commands[(index + 2 + BotCommands.see(this, rot)) % Constant.genome_length];
			}else if (command == 7) {//посмотреть абсолютно
				index = commands[(index + 1 + BotCommands.see(this, rotate)) % Constant.genome_length];
			}else if (command == 8 || command == 9) {//отдать ресурсы относительно
				BotCommands.give(this, commands[(index + 1) % Constant.genome_length] % 8);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 10 || command == 11) {//отдать ресурсы абсолютно
				BotCommands.give(this, rotate);
				index += 1;
				index %= Constant.genome_length;
				break;
			}else if (command == 12) {//сколько у меня энергии
				int ind = (int)(commands[(index + 1) % Constant.genome_length] / (Constant.genome_length * 1.0) * Constant.max_energy);
				if (energy >= ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else if (command == 13) {//есть ли фотосинтез
				int sec = Constant.sector(ypos);
				if (Constant.photo_list[sec] > 0) {
					index = commands[(index + 1) % Constant.genome_length];
				}else {
					index = commands[(index + 2) % Constant.genome_length];
				}
			}else if (command == 14) {//поделиться относительно
				BotCommands.multiply(this, commands[(index + 1) % Constant.genome_length] % 8, iterator);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 15) {//поделиться абсолютно
				BotCommands.multiply(this, rotate, iterator);
				index += 1;
				index %= Constant.genome_length;
				break;
			}else if (command == 16) {//какая моя позиция x
				double ind = commands[(index + 1) % Constant.genome_length] / (Constant.genome_length / 1.0);
				if (xpos * 1.0 / Constant.world_scale[0] >= ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else if (command == 17) {//какая моя позиция y
				double ind = commands[(index + 1) % Constant.genome_length] / (Constant.genome_length * 1.0);
				if (ypos * 1.0 / Constant.world_scale[1] >= ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else if (command == 18) {//какой мой возраст
				int ind = (int)(commands[(index + 1) % Constant.genome_length] / (Constant.genome_length * 1.0) * Constant.max_age);
				if (age >= ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else if (command == 19) {//равномерное распределение ресурсов относительно
				BotCommands.give2(this, commands[(index + 1) % Constant.genome_length] % 8);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 20) {//равномерное распределение ресурсов абсолютно
				BotCommands.give2(this, rotate);
				index += 1;
				index %= Constant.genome_length;
				break;
			}else if (command == 21 || command == 22) {//переработка глюкозы
				BotUtils.reaction(this, 1);
				index += 1;
				index %= Constant.genome_length;
				break;
			}else if (command == 23) {//сдвинуть вещество из - под себя относительно
				BotCommands.move_ch(this, commands[(index + 1) % Constant.genome_length] % 8, commands[(index + 2) % Constant.genome_length] % Reactions.ch_count);
				index += 3;
				index %= Constant.genome_length;
				break;
			}else if (command == 24) {//сдвинуть вещество из - под себя абсолютно
				BotCommands.move_ch(this, rotate, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 25) {//сдвинуть вещество под себя относительно
				BotCommands.move_ch_under(this, commands[(index + 1) % Constant.genome_length] % 8, commands[(index + 2) % Constant.genome_length] % Reactions.ch_count);
				index += 3;
				index %= Constant.genome_length;
				break;
			}else if (command == 26) {//сдвинуть вещество под себя абсолютно
				BotCommands.move_ch_under(this, rotate, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 27 || command == 28 || command == 29) {//собирать относительно
				BotCommands.collect(this, commands[(index + 1) % Constant.genome_length] % 8, commands[(index + 2) % Constant.genome_length] % Reactions.ch_count);
				index += 3;
				index %= Constant.genome_length;
				break;
			}else if (command == 30 || command == 31 || command == 32) {//собирать абсолютно
				BotCommands.collect(this, rotate, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 33) {//произвести реакцию
				BotUtils.reaction(this, commands[(index + 1) % Constant.genome_length] % Reactions.reactions.length);
				index += 2;
				index %= Constant.genome_length;
				break;
			}else if (command == 34) {//восстановить жизнь
				if (energy >= 20 && health < Constant.max_health) {
					energy -= 20;
					health += 1;
				}
				index += 1;
				index %= Constant.genome_length;
				break;
			}else if (command == 35) {//вещества подо мной больше чем у меня
				int c = commands[(index + 1) % Constant.genome_length] % Reactions.ch_count;
				if (ch[c][xpos][ypos] > my_ch[c]) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else if (command == 36) {//сколько жизней
				int ind = (int)(commands[(index + 1) % Constant.genome_length] / (Constant.genome_length * 1.0) * Constant.max_health);
				if (health >= ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else if (command == 37) {//сколько вещества
				int ind = (int)(commands[(index + 1) % Constant.genome_length] / (Constant.genome_length * 1.0) * Constant.ch_bot_limit);
				if (my_ch[commands[(index + 2) % Constant.genome_length] % Reactions.ch_count] >= ind) {
					index = commands[(index + 3) % Constant.genome_length];
				}else {
					index = commands[(index + 4) % Constant.genome_length];
				}
			}else if (command == 38) {//сколько соседей вокруг
				int c = BotUtils.count_neighbours(this);
				int ind = commands[(index + 1) % Constant.genome_length] % 8;
				if (c > ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else if (c < ind) {
					index = commands[(index + 3) % Constant.genome_length];
				}else {
					index = commands[(index + 4) % Constant.genome_length];
				}
			}else if (command == 39) {//какое направление
				int ind = commands[(index + 1) % Constant.genome_length] % 8;
				if (rotate > ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else if (rotate < ind) {
					index = commands[(index + 3) % Constant.genome_length];
				}else {
					index = commands[(index + 4) % Constant.genome_length];
				}
			}else if (command == 40) {//с какой стороны меньше вещества
				int c = BotCommands.min_ch_rotate(this, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
				int ind = commands[(index + 2) % Constant.genome_length] % 8;
				if (c > ind) {
					index = commands[(index + 3) % Constant.genome_length];
				}else if (c < ind) {
					index = commands[(index + 4) % Constant.genome_length];
				}else {
					index = commands[(index + 5) % Constant.genome_length];
				}
			}else if (command == 41) {//с какой стороны больше вещества
				int c = BotCommands.max_ch_rotate(this, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
				int ind = commands[(index + 2) % Constant.genome_length] % 8;
				if (c > ind) {
					index = commands[(index + 3) % Constant.genome_length];
				}else if (c < ind) {
					index = commands[(index + 4) % Constant.genome_length];
				}else {
					index = commands[(index + 5) % Constant.genome_length];
				}
			}else if (command == 42) {//повернуться к минимуму вещества
				rotate = BotCommands.min_ch_rotate(this, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
			}else if (command == 43) {//повернуться к максимуму вещества
				rotate = BotCommands.max_ch_rotate(this, commands[(index + 1) % Constant.genome_length] % Reactions.ch_count);
			}else if (command == 44) {//какая температура
				int ind = (int)(commands[(index + 1) % Constant.genome_length] / (Constant.genome_length * 1.0) * Constant.max_temp);
				if (temp >= ind) {
					index = commands[(index + 2) % Constant.genome_length];
				}else {
					index = commands[(index + 3) % Constant.genome_length];
				}
			}else {
				index = commands[(index + 1) % Constant.genome_length];
			}
		}
	}
}

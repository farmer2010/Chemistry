package sct;

import java.awt.Color;

public class BotUtils {
	public static void reaction(Bot bot, int reaction_type) {//выполнение реакции
		double speed = 1;
		for (int j = 0; j < Reactions.reactions[reaction_type].catalyst_coeffs.length; j++) {//вычисление скорости реакции(для реакций с катализаторами)
			double spmin = Reactions.reactions[reaction_type].catalyst_min_coeffs[j];
			int ch_type = (int)(Reactions.reactions[reaction_type].catalyst_types[j]);
			double coeff = Reactions.reactions[reaction_type].catalyst_coeffs[j];
			double spmax = Reactions.reactions[reaction_type].catalyst_max_coeffs[j];
			double count = 0;
			if (ch_type >= 3) {
				count = bot.my_ch[ch_type - 3];
			}else if (ch_type == 0) {
				count = Constant.photo_list[Constant.sector(bot.ypos)];
			}else if (ch_type == 1) {
				count = bot.energy;
			}else if (ch_type == 2) {
				count = bot.temp;
			}
			speed *= Math.min(Math.max(count * coeff, spmin), spmax);
		}
		//
		boolean inp = true;
		for (int j = 0; j < Reactions.reactions[reaction_type].spend_count.length; j++) {//может ли бот провести реакцию
			double c = 0;
			int ch_type = (int)(Reactions.reactions[reaction_type].spend_types[j]);
			if (ch_type >= 3) {
				c = bot.my_ch[ch_type - 3];
			}else if (ch_type == 1) {
				c = bot.energy;
			}else if (ch_type == 2) {
				c = bot.temp;
			}
			inp = inp && (c >= Reactions.reactions[reaction_type].spend_count[j] * speed);
		}
		inp = inp && (bot.temp >= Reactions.reactions[reaction_type].min_temp);
		//
		if (inp) {
			for (int j = 0; j < Reactions.reactions[reaction_type].spend_count.length; j++) {//тратим вещества
				int ch_type = (int)(Reactions.reactions[reaction_type].spend_types[j]);
				if (ch_type >= 3) {
					bot.my_ch[(int)(Reactions.reactions[reaction_type].spend_types[j]) - 3] -= Reactions.reactions[reaction_type].spend_count[j] * speed;
				}else if (ch_type == 1) {
					bot.energy -= Reactions.reactions[reaction_type].spend_count[j] * speed;
				}else if (ch_type == 2) {
					bot.temp -= Reactions.reactions[reaction_type].spend_count[j] * speed;
				}
			}
			bot.temp -= Reactions.reactions[reaction_type].temp_spend;
			//
			for (int j = 0; j < Reactions.reactions[reaction_type].production_count.length; j++) {//производим вещества
				if ((int)(Reactions.reactions[reaction_type].production_types[j]) >= 3) {
					bot.my_ch[(int)(Reactions.reactions[reaction_type].production_types[j]) - 3] += Reactions.reactions[reaction_type].production_count[j] * speed;
				}else if ((int)(Reactions.reactions[reaction_type].production_types[j]) == 1) {
					bot.energy += Reactions.reactions[reaction_type].production_count[j] * speed;
				}else if ((int)(Reactions.reactions[reaction_type].production_types[j]) == 2) {
					bot.temp += Reactions.reactions[reaction_type].production_count[j] * speed;
				}
			}
			bot.temp += Reactions.reactions[reaction_type].temp_production;
			//
			go_color(bot, Reactions.reactions_color[reaction_type]);//перекрасить бота
		}
	}
	//
	public static boolean is_free(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] == null && bot.ch[1][pos[0]][pos[1]] < 500) {
				return(true);
			}
		}
		return(false);
	}
	//
	public static void die(Bot bot) {//помереть
		bot.map[bot.xpos][bot.ypos] = null;
		bot.killed = 1;
		for (int i = 0; i < 10; i++) {//разбрасываем вещества в квадрате 3*3
			double c = bot.my_ch[i] / 9;
			if (i == 0) {
				c += (bot.energy * 0.25 + 50) / 9;//энергия преобразуется в глюкозу
			}
			for (int j = 0; j < 8; j++) {
				int[] pos = Constant.get_rotate_position(j, new int[] {bot.xpos, bot.ypos});
				if (is_free(bot, j)) {
					bot.ch[i][pos[0]][pos[1]] += c;
				}else {
					bot.ch[i][bot.xpos][bot.ypos] += c;
				}
			}
			bot.ch[i][bot.xpos][bot.ypos] += c;
		}
	}
	//
	public static void go_color(Bot bot, Color c) {//цвет режима отрисовки хищников
		int red_count = 0;                         //есть буфер на 10 цветов. При выполнении функции цвет "с" добавляется в начало буфера, сдвигая остальные. Самый поздний исчезает
		int green_count = 0;                       //Для получения цвета просто усредняем буфер(кроме тех, где стоит -1. Это начальное значение, обозначающее, что цвета там нет)
		int blue_count = 0;                        //бот с цветом(-1, -1, -1), рисуется серым.
		int count = 1;
		for (int i = 8; i >= 0; i--) {
			bot.pred_colors[i + 1][0] = bot.pred_colors[i][0];
			bot.pred_colors[i + 1][1] = bot.pred_colors[i][1];
			bot.pred_colors[i + 1][2] = bot.pred_colors[i][2];
			if (bot.pred_colors[i][0] != -1) {
				red_count += bot.pred_colors[i][0];
				green_count += bot.pred_colors[i][1];
				blue_count += bot.pred_colors[i][2];
				count++;
			}
		}
		bot.pred_colors[0][0] = c.getRed();
		bot.pred_colors[0][1] = c.getGreen();
		bot.pred_colors[0][2] = c.getBlue();
		red_count += c.getRed();
		green_count += c.getGreen();
		blue_count += c.getBlue();
		if (bot.c_red != -1 && bot.c_green != -1 && bot.c_blue != -1) {
			bot.c_red = red_count / count;
			bot.c_green = green_count / count;
			bot.c_blue = blue_count / count;
		}else {
			bot.c_red = c.getRed();
			bot.c_green = c.getGreen();
			bot.c_blue = c.getBlue();
		}
	}
	//
	public static boolean is_relative(int[] brain1, int[] brain2) {//проверка родственников. Вроде я ее отключил
		int errors = 0;
		for (int i = 0; i < Constant.genome_length; i++) {
			if (brain1[i] != brain2[i]) {
				errors += 1;
			}
			if (errors > 1) {
				return(false);
			}
		}
		return(errors < 2);
	}
	//
	public static int count_neighbours(Bot bot) {//сколько соседей вокруг
		int c = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = Constant.get_rotate_position(i, new int[] {bot.xpos, bot.ypos});
			if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && bot.map[pos[0]][pos[1]] != null) {
				c++;
			}
		}
		return(c);
	}
}

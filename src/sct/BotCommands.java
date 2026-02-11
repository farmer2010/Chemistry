package sct;

import java.awt.Color;
import java.util.ListIterator;

public class BotCommands {
	public static int min_ch_rotate(Bot bot, int ch_type) {//направление, в котором минимум вещества
		double min = 99999;
		int min_rotate = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = Constant.get_rotate_position(i, new int[] {bot.xpos, bot.ypos});
			if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && bot.map[pos[0]][pos[1]] != null) {
				if (bot.ch[ch_type][pos[0]][pos[1]] < min) {
					min = bot.ch[ch_type][pos[0]][pos[1]];
					min_rotate = i;
				}
			}
		}
		return(min_rotate);
	}
	//
	public static int max_ch_rotate(Bot bot, int ch_type) {//направление, в котором максимум вещества
		double max = -999;
		int max_rotate = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = Constant.get_rotate_position(i, new int[] {bot.xpos, bot.ypos});
			if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && bot.map[pos[0]][pos[1]] != null) {
				if (bot.ch[ch_type][pos[0]][pos[1]] > max) {
					max = bot.ch[ch_type][pos[0]][pos[1]];
					max_rotate = i;
				}
			}
		}
		return(max_rotate);
	}
	//
	public static void collect(Bot bot, int rot, int ch_type) {//собирать вещество
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && bot.my_ch[ch_type] < 80) {
			double c = Math.max(Math.min(Math.min(Constant.bot_collect_speed, bot.ch[ch_type][pos[0]][pos[1]]), 80 - bot.my_ch[ch_type]), 0);
			bot.my_ch[ch_type] += c;
			bot.ch[ch_type][pos[0]][pos[1]] -= c;
		}
	}
	//
	public static void move_ch(Bot bot, int rot, int ch_type) {//сдвинуть вещество из - под себя
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (BotUtils.is_free(bot, rot)) {
			bot.ch[ch_type][pos[0]][pos[1]] += bot.ch[ch_type][bot.xpos][bot.ypos];
			bot.ch[ch_type][bot.xpos][bot.ypos] = 0;
			bot.temp += 3;
		}
	}
	//
	public static void move_ch_under(Bot bot, int rot, int ch_type) {//сдвинуь вещество под себя
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (BotUtils.is_free(bot, rot)) {
			bot.ch[ch_type][bot.xpos][bot.ypos] += bot.ch[ch_type][pos[0]][pos[1]];
			bot.ch[ch_type][pos[0]][pos[1]] = 0;
			bot.temp += 3;
		}
	}
	//
	public static int see(Bot bot, int rot) {//посмотреть
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] == null) {
				return(1);//если ничего
			}else if (bot.map[pos[0]][pos[1]].state == 0) {
				if (BotUtils.is_relative(bot.commands, bot.map[pos[0]][pos[1]].commands)) {
					return(3);//если родственник
				}else {
					return(2);//если враг
				}
			}else {
				return(4);
			}
		}else {
			return(0);//если граница
		}
	}
	//
	public static void give(Bot bot, int rot) {//отдать соседу часть энергии
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null) {
				if (bot.map[pos[0]][pos[1]].state == 0) {
					Bot relative = bot.map[pos[0]][pos[1]];
					if (relative.killed == 0) {
						relative.energy += bot.energy / 4;
						bot.energy -= bot.energy / 4;
						bot.temp += 2;
					}
				}
			}
		}
	}
	//
	public static void give2(Bot bot, int rot) {//равномерное распределение энергии
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null) {
				if (bot.map[pos[0]][pos[1]].state == 0) {
					Bot relative = bot.map[pos[0]][pos[1]];
					if (relative.killed == 0) {
						double enr = relative.energy + bot.energy;
						relative.energy = enr / 2;
						bot.energy = enr / 2;
						bot.temp += 2;
					}
				}
			}
		}
	}
	//
	public static void attack(Bot bot, int rot, double power) {//атаковать
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null) {
				Bot victim = bot.map[pos[0]][pos[1]];
				if (victim != null) {
					power = Math.min(power, victim.energy);
					bot.energy += power;
					victim.energy -= power;//атака отнимает у бота энергию и жизни
					victim.health -= power / 4;
					if (victim.energy <= 0 || victim.health <= 0) {
						BotUtils.die(victim);
					}
					BotUtils.go_color(bot, new Color(255, 0, 0));
					bot.temp += 4;
				}
			}
		}
	}
	//
	public static int move(Bot bot, int rot) {//передвижение
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (BotUtils.is_free(bot, rot)) {
			Bot self = bot.map[bot.xpos][bot.ypos];
			bot.map[bot.xpos][bot.ypos] = null;
			bot.xpos = pos[0];
			bot.ypos = pos[1];
			bot.x = bot.xpos * Constant.cell_size;
			bot.y = bot.ypos * Constant.cell_size;
			bot.map[bot.xpos][bot.ypos] = self;
			return(1);
		}
		return(0);
	}
	//
	public static void multiply(Bot bot, int rot, ListIterator<Bot> iterator) {//размножение
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (BotUtils.is_free(bot, rot)) {
			bot.energy -= 150;
			if (bot.energy <= 0) {
				BotUtils.die(bot);
			}else {
				Color new_color = bot.color;
				int[] new_brain = new int[Constant.genome_length];
				for (int i = 0; i < Constant.genome_length; i++) {
					new_brain[i] = bot.commands[i];
				}
				int[][] new_genes = new int[2][3];
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 3; j++) {
						new_genes[i][j] = bot.genes[i][j];
					}
				}
				if (bot.rand.nextInt(4) == 0) {//мутация
					new_color = new Color(bot.rand.nextInt(256), bot.rand.nextInt(256), bot.rand.nextInt(256));
					new_brain[bot.rand.nextInt(Constant.genome_length)] = bot.rand.nextInt(Constant.genome_length);
					if (bot.rand.nextInt(16) == 0) {
						new_genes[bot.rand.nextInt(2)][bot.rand.nextInt(3)] = bot.rand.nextInt(64);
					}
				}
				Bot new_bot = new Bot(pos[0], pos[1], new_color, bot.energy / 2, bot.temp, bot.map, bot.ch, bot.temp_map, bot.objects);
				for (int i = 0; i < 10; i++) {
					new_bot.my_ch[i] = bot.my_ch[i] / 2;
					bot.my_ch[i] /= 2;
				}
				bot.energy /= 2;
				new_bot.commands = new_brain;
				new_bot.genes = new_genes;
				bot.map[pos[0]][pos[1]] = new_bot;
				iterator.add(new_bot);
				bot.temp += 5;
			}
		}
	}
}

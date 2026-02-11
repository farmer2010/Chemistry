package sct;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class WorldUtils {
	public static void gas(World world, double[][] gas_map, int gas_type) {//распространение газа
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				if (gas_map[x][y] >= 0.009) {
					gas_map[x][y] -= gas_map[x][y] * Reactions.evaporation[gas_type];//испарение
					double g = gas_map[x][y] * Reactions.viscosity[gas_type];
					double ox = g / 9;
					new_map[x][y] += gas_map[x][y] - g + ox;
					int count = 0;
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = Constant.get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && world.Map[pos[0]][pos[1]] == null && world.ch[1][pos[0]][pos[1]] < 500) {
							new_map[pos[0]][pos[1]] += ox;
						}else {
							count++;
						}
					}
					for (int i = 0; i < count; i++) {
						new_map[x][y] += ox;
					}
				}else {
					new_map[x][y] += gas_map[x][y];
				}
			}
		}
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				gas_map[x][y] = new_map[x][y];
			}
		}
	}
	//
	public static void hydrogenium(World world, double[][] gas_map, int gas_type) {//водород поднимается вверх
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				int[] pos = Constant.get_rotate_position(0, new int[] {x, y});
				if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && //условия для подъема
						world.Map[pos[0]][pos[1]] == null && 
						world.ch[1][pos[0]][pos[1]] < 500 && 
						gas_map[x][y] - gas_map[pos[0]][pos[1]] > gas_map[x][y] / 10 && 
						gas_map[pos[0]][pos[1]] < Reactions.up_max[gas_type]) 
				{
					new_map[pos[0]][pos[1]] += gas_map[x][y] * Reactions.up[gas_type];
					new_map[x][y] += gas_map[x][y] * (1 - Reactions.up[gas_type]);//сколько веества поднимается
				}else {
					new_map[x][y] += gas_map[x][y];
				}
			}
		}
		for (int x = 0; x < Constant.world_scale[0]; x++) {//спавн водорода
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				gas_map[x][y] = new_map[x][y];
				if (y > Constant.world_scale[1] * 0.625 && gas_map[x][y] < 60) {
					gas_map[x][y] += (60 - gas_map[x][y]) / 50;
				}
			}
		}
		//gas_map[100][215] += Constant.hydro_pl;
		//gas_map[200][215] += Constant.hydro_pl;
		//gas_map[300][215] += Constant.hydro_pl;
	}
	//
	public static void crystal(World world, double[][] crystal_map) {//появление кристалла
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				new_map[x][y] = crystal_map[x][y];
			}
		}
		for (int i = 0; i < 1000; i++) {//1000 попыток роста
			int x = world.rand.nextInt(Constant.world_scale[0]);
			int y = world.rand.nextInt((int)(Constant.world_scale[1] * 0.625), Constant.world_scale[1]);
			if (crystal_map[x][y] > 10) {//если есть кристалл, он растет. Чем больше кристалла, тем медленнее он растет. больше 600 - нельзя
				if (world.rand.nextInt(300) == 0) {
					new_map[x][y] += world.rand.nextInt(10, 30) * (1.2 - Math.min(crystal_map[x][y] / 300, 1));
					if (new_map[x][y] > 600) {
						new_map[x][y] = 600;
					}
				}
			}else if (world.Map[x][y] != null) {//под ботом появляется случайное количество кристалла
				if (world.rand.nextInt(300) == 0) {
					new_map[x][y] += world.rand.nextInt(10, 30);
					if (new_map[x][y] > 600) {
						new_map[x][y] = 600;
					}
				}
			}else if (crystal_map[x][y] < 10){//если мало кристалла, спавним его с шансом из параметра(crystal_chances)
				int count = 0;
				for (int j = 0; j < 8; j++) {
					int[] f = {x, y};
					int[] pos = Constant.get_rotate_position(j, f);
					if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && (crystal_map[pos[0]][pos[1]] > 10)) {
						count++;
					}
				}
				if (world.rand.nextInt(500) < Reactions.crystal_chances[count]) {
					new_map[x][y] += world.rand.nextInt(10, 15);
					if (new_map[x][y] > 600) {
						new_map[x][y] = 600;
					}
				}
			}
		}
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				crystal_map[x][y] = new_map[x][y];
			}
		}
	}
	//
	public static void temp(double[][] temp_map) {//температура - почти газ. только постепенно стремится к 30 градусам на клетку
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				double ox = temp_map[x][y] / 9;
				new_map[x][y] += ox;
				int count = 0;
				for (int i = 0; i < 8; i++) {
					int[] f = {x, y};
					int[] pos = Constant.get_rotate_position(i, f);
					if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
						new_map[pos[0]][pos[1]] += ox;
					}else {
						count++;
					}
				}
				for (int i = 0; i < count; i++) {
					new_map[x][y] += ox;
				}
			}
		}
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				temp_map[x][y] = new_map[x][y];
				temp_map[x][y] += (30 - temp_map[x][y]) / 100;//
			}
		}
	}
	//
	public static void iron(double[][] iron_map) {//спавн железа
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = Constant.world_scale[1] / 2; y < Constant.world_scale[1]; y++) {
				if (iron_map[x][y] < (y - Constant.world_scale[1] / 2) / 3) {//чем выше, тем меньше железа
					iron_map[x][y] += (((y - Constant.world_scale[1] / 2) / 3) - iron_map[x][y]) / 300;
				}
			}
		}
	}
	//
	public static void save_world(World world, String name) {//сохранялка не работает!
		try {
			FileWriter fileWriter = new FileWriter("saved worlds/" + name + ".dat");
		    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(String.valueOf(world.steps) + ";");
			//
			for (int x = 0; x < Constant.world_scale[0]; x++) {
				for (int y = 0; y < Constant.world_scale[1]; y++) {
					bufferedWriter.write(String.valueOf(world.temp_map[x][y]) + "'");
				}
				bufferedWriter.write(":");
			}
			bufferedWriter.write(";");
			//
			for (int x = 0; x < Constant.world_scale[0]; x++) {
				for (int y = 0; y < Constant.world_scale[1]; y++) {
					for (int i = 0; i < 10; i++) {
					bufferedWriter.write(String.valueOf(world.ch[i][x][y]) + "|");
					}
					bufferedWriter.write("'");
				}
				bufferedWriter.write(":");
			}
			bufferedWriter.write(";");
			//
			for(Bot b: world.objects) {//bot length - 90
				bufferedWriter.write(String.valueOf(b.energy) + ":");//          0
				bufferedWriter.write(String.valueOf(b.age) + ":");//             1
				bufferedWriter.write(String.valueOf(b.xpos) + ":");//            2
				bufferedWriter.write(String.valueOf(b.ypos) + ":");//            3
				bufferedWriter.write(String.valueOf(b.rotate) + ":");//          4
				bufferedWriter.write(String.valueOf(b.state) + ":");//           5
				bufferedWriter.write(String.valueOf(b.c_red) + ":");//           6
				bufferedWriter.write(String.valueOf(b.c_green) + ":");//         7
				bufferedWriter.write(String.valueOf(b.c_blue) + ":");//          8
				bufferedWriter.write(String.valueOf(b.color.getRed()) + ":");//  9
				bufferedWriter.write(String.valueOf(b.color.getGreen()) + ":");//10
				bufferedWriter.write(String.valueOf(b.color.getBlue()) + ":");// 11
				bufferedWriter.write(String.valueOf(b.index) + ":");//           12
				bufferedWriter.write(String.valueOf(b.temp) + ":");//            13
				for (int i = 0; i < 10; i++) {//                                 14 - 23
					bufferedWriter.write(String.valueOf(b.my_ch[i]) + ":");
				}
				for (int i = 0; i < 2; i++) {//                                  24 - 25
					for (int j = 0; j < 3; j++) {
						bufferedWriter.write(String.valueOf(b.genes[i][j]) + "'");
					}
					bufferedWriter.write(":");
				}
				for (int i = 0; i < 64; i++) {//                                 26 - 89
					bufferedWriter.write(String.valueOf(b.commands[i]) + ":");
				}
				bufferedWriter.write(";");
			}
	        bufferedWriter.close();
	    } catch (IOException ex) {
	        System.out.println("Ошибка при записи в файл");
	        ex.printStackTrace();
	    }
	}
	//
	public static void load_world(World world, String name) {//загрузка не работает!
		try {
	        FileReader fileReader = new FileReader("saved worlds/" + name + ".dat");
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = bufferedReader.readLine();
	        bufferedReader.close();
	        String[] l = line.split(";");
	        world.steps = Integer.parseInt(l[0]);
	        world.objects = new ArrayList<Bot>();
	        world.Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
	        //world.ch = new double[10][world_scale[0]][world_scale[1]];
	        //
	        String[] temp = l[1].split(":");
    		for (int i = 0; i < temp.length; i++) {
    			String[] temp_col = temp[i].split("'");
    			for (int j = 0; j < temp_col.length; j++) {
    				world.temp_map[i][j] = Double.parseDouble(temp_col[j]);
    			}
    		}
	        //
	    	for (int i = 3; i < l.length; i++) {
	    		String[] bot_data = l[i].split(":");
	    		Bot new_bot = new Bot(
	    			Integer.parseInt(bot_data[2]),
	    			Integer.parseInt(bot_data[3]),
	    			new Color(Integer.parseInt(bot_data[9]), Integer.parseInt(bot_data[10]), Integer.parseInt(bot_data[11])),
	    			Double.parseDouble(bot_data[0]),
	    			Constant.temp,
	    			world.Map,
	    			world.ch,
	    			world.temp_map,
	    			world.objects
	    		);
	    		new_bot.age = Integer.parseInt(bot_data[1]);
	    		new_bot.rotate = Integer.parseInt(bot_data[4]);
	    		new_bot.state = Integer.parseInt(bot_data[5]);
	    		new_bot.c_red = Integer.parseInt(bot_data[6]);
	    		new_bot.c_green = Integer.parseInt(bot_data[7]);
	    		new_bot.c_blue = Integer.parseInt(bot_data[8]);
	    		new_bot.index = Integer.parseInt(bot_data[12]);
	    		new_bot.temp = Double.parseDouble(bot_data[13]);
	    		for (int j = 0; j < 64; j++) {
	    			new_bot.commands[j] = Integer.parseInt(bot_data[26 + j]);;
	    		}
	    		world.Map[Integer.parseInt(bot_data[2])][Integer.parseInt(bot_data[3])] = new_bot;
	    		world.objects.add(new_bot);
	    	}
	    } catch (IOException ex) {
	        System.out.println("Ошибка при чтении файла");
	        ex.printStackTrace();
	    }
	}
	//
	public static void save_bot(Bot bot, String name) {//сохранение, загрузка ботов не работают!
		String txt = "";
		for (int i = 0; i < 64; i++) {
			txt += String.valueOf(bot.commands[i]) + " ";
		}
		try {
	        FileWriter fileWriter = new FileWriter("saved objects/" + name + ".dat");
	        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	        bufferedWriter.write(txt);
	        bufferedWriter.close();
	    } catch (IOException ex) {
	        System.out.println("Ошибка при записи в файл");
	        ex.printStackTrace();
	    }
	}
	//
	public static int[] load_bot(String name) {
		try {
	        FileReader fileReader = new FileReader("saved objects/" + name + ".dat");
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = bufferedReader.readLine();
	        bufferedReader.close();
	        String[] l = line.split(" ");
	        int[] for_set = new int[64];
	        for (int i = 0; i < 64; i++) {
	        	for_set[i] = Integer.parseInt(l[i]);
	        }
	        return(for_set);
	    } catch (IOException ex) {
	    	System.out.println("Ошибка при чтении файла");
	        ex.printStackTrace();
	    	return(null);
	    }
	}
	//
	public static void record(World world) {
		try {
			BufferedImage[] images = new BufferedImage[12];
			for (int i = 0; i < 12; i++) {
				images[i] = new BufferedImage(1620, 1080, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = images[i].createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				Draw.draw_background(world, g2d, i);
				for(Bot b: world.objects) {
					if (i == 0) {
						b.Draw(g2d, 2);
					}else {
						b.Draw(g2d, 0);
					}
				}
				g2d.dispose();
			}
			//
			ImageIO.write(images[0], "png", new File("record/energy/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[1], "png", new File("record/A/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[2], "png", new File("record/B/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[3], "png", new File("record/C/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[4], "png", new File("record/D/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[5], "png", new File("record/E/screen" + String.valueOf(world.steps / 25) + ".png"));
			//ImageIO.write(images[6], "png", new File("record/F/screen" + String.valueOf(world.steps / 25) + ".png"));
			//ImageIO.write(images[7], "png", new File("record/G/screen" + String.valueOf(world.steps / 25) + ".png"));
			//ImageIO.write(images[8], "png", new File("record/H/screen" + String.valueOf(world.steps / 25) + ".png"));
			//ImageIO.write(images[9], "png", new File("record/I/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[10], "png", new File("record/J/screen" + String.valueOf(world.steps / 25) + ".png"));
			ImageIO.write(images[11], "png", new File("record/temp/screen" + String.valueOf(world.steps / 25) + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//
	public static void kill_all(World world) {
		world.steps = 0;
		world.objects = new ArrayList<Bot>();
		world.Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
		world.ch = new double[10][Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				world.temp_map[x][y] = Constant.temp;
				for (int i = 0; i < 10; i++) {
					world.ch[i][x][y] = Reactions.start_count[i];
				}
			}
		}
		world.ch[1][Constant.world_scale[0] / 2][(int)(Constant.world_scale[1] * 0.8)] = 500;
	}
	//
	public static void custom_population(World world) {//для отладки
		WorldUtils.kill_all(world);                    //создает одного кастомного бота
		int x = world.rand.nextInt(Constant.world_scale[0]);
		int y = world.rand.nextInt((int)(Constant.world_scale[1] * 0.3));
		Bot new_bot = new Bot(
			x,
			y,
			new Color(world.rand.nextInt(256), world.rand.nextInt(256), world.rand.nextInt(256)),
			1000,
			Constant.temp,
			world.Map,
			world.ch,
			world.temp_map,
			world.objects
		);
		new_bot.commands[0] = 3;
		new_bot.commands[1] = 2;
		new_bot.commands[2] = 5;
		new_bot.commands[3] = 2;
		new_bot.commands[4] = 0;
		new_bot.commands[5] = 1;
		new_bot.commands[6] = 25;
		new_bot.commands[7] = 0;
		//
		new_bot.genes[0][0] = 2;
		new_bot.genes[0][1] = 17;
		new_bot.genes[0][2] = 0;
		//
		new_bot.genes[1][0] = 3;
		new_bot.genes[1][1] = 17;
		new_bot.genes[1][2] = 0;
		//
		world.objects.add(new_bot);
		world.Map[x][y] = new_bot;
		world.repaint();
	}
	//
	public static void new_population(World world) {
		WorldUtils.kill_all(world);
		//
		for (int i = 0; i < Constant.starting_bots; i++) {
			while(true){
				int x = world.rand.nextInt(Constant.world_scale[0]);
				int y = world.rand.nextInt(Constant.world_scale[1]);
				if (world.Map[x][y] == null) {
					Bot new_bot = new Bot(
						x,
						y,
						new Color(world.rand.nextInt(256), world.rand.nextInt(256), world.rand.nextInt(256)),
						1000,
						Constant.temp,
						world.Map,
						world.ch,
						world.temp_map,
						world.objects
					);
					world.objects.add(new_bot);
					world.Map[x][y] = new_bot;
					break;
				}
			}
		}
		world.repaint();
	}
}

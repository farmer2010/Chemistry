package sct;

import java.awt.Color;

public class Reactions {
	public static int ch_count = 10;
	//Вещества и их свойства
	//
	public static double[] start_count = new double[] {200, 0, 20, 200, 50, 20, 0, 2, 0, 0};//стартовое количество веществ
	public static double[] viscosity = new double[] {0.01, 0, 1, 1, 0.5, 0, 0.005, 0, 0.01, 0};//скорость. 0 - не распространяется, 1 - распространяется с максимально скоростью
	public static double[] evaporation = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//испарение. 0 - не испаряется, 1 - испаряется все за раз
	public static double[] up = new double[] {0, 0, 0, 0, 0.3, 0, 0, 0, 0, 0};//подъем(только водород). 0 - не поднимается, 1 - поднимается весь объем
	public static double[] up_max = new double[] {0, 0, 0, 0, 300, 0, 0, 0, 0, 0};//максимальное количество вещества на клетке выше для подъема
	public static double[] collect_speed = new double[] {0.02, 0, 0.04, 0.04, 0.04, 0, 0.01, 0, 0.015, 0.001};//скорость сбора вещества
	public static int[] crystal_chances = new int[] {0, 10, 6, 5, 4, 3, 2, 1, 0};//шансы появления кристалла в зависимости от количества соседей
	//
	public static int hydro_pl = 100;//приход водорода
	//
	public static enum c{
		LIGHT,
		ENERGY,
		TEMP,
		GLUCOSE,
		CRYSTAL,
		OXYGEN,
		CO2,
		HYDROGENIUM,
		TUNGSTEN,
		CATALYST,
		THORIUM,
		POISON,
		IRON
	}
	//
	//Реакции
	//
	//массив реакций: [тип реакции][трата, производство][параметры реакции][вещества]
	public static double[][][][] reactions = new double[][][][] {//типы:
		//0 - свет
		//1 - энергия
		//2 - тепло
		//3(A) - глюкоза
		//4(B) - кристалл
		//5(C) - кислород
		//6(D) - углекислый газ
		//7(E) - водород
		//8(F) - вольфрам
		//9(G) - катализатор
		//10(H) - торий
		//11(I) - яд
		//12(J) - железо
		//
		//массив: 0 - количество, 1 - тип
		//для катализаторов: 0 - ускорение за единицу катализатора, 1 - тип, 2 - минимальное значение коефф скорости, 3 - максимальное количество катализатора
		//температура: 0 - трата(0 - требуемая температура, 1 - скорость охлаждения), 1 - нагрев(0 - до какой температуры нагреваем, 1 - скорость нагрева)
		new double[][][] {//гликолиз
			new double[][] {new double[] {5}, new double[] {c.GLUCOSE.ordinal()}},//тратим                    ;5 - количество вещества, глюкоза - тип вещества
			new double[][] {new double[] {3}, new double[] {c.ENERGY.ordinal()}},//производим                 ;
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы;
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура                           ;
		},
		new double[][][] {//анаэробное дыхание
			new double[][] {new double[] {5, 4.5}, new double[] {c.GLUCOSE.ordinal(), c.CO2.ordinal()}},//тратим
			new double[][] {new double[] {4}, new double[] {c.ENERGY.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//аэробное дыхание
			new double[][] {new double[] {5, 1.7}, new double[] {c.GLUCOSE.ordinal(), c.OXYGEN.ordinal()}},//тратим
			new double[][] {new double[] {6, 1.7}, new double[] {c.ENERGY.ordinal(), c.CO2.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//фотосинтез
			new double[][] {new double[] {0.5, 0.4}, new double[] {c.HYDROGENIUM.ordinal(), c.CO2.ordinal()}},//тратим
			new double[][] {new double[] {1.2, 0.6}, new double[] {c.GLUCOSE.ordinal(), c.OXYGEN.ordinal()}},//производим
			new double[][] {new double[] {1}, new double[] {c.LIGHT.ordinal()}, new double[] {0}, new double[] {100}},//катализаторы ;свет с ускорением за единицу х1, обязателен для выполнения
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//переработка углекислоты
			new double[][] {new double[] {10, 4}, new double[] {c.CO2.ordinal(), c.OXYGEN.ordinal()}},//тратим
			new double[][] {new double[] {6}, new double[] {c.GLUCOSE.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//переработка кристалла
			new double[][] {new double[] {5, 1}, new double[] {c.CRYSTAL.ordinal(), c.HYDROGENIUM.ordinal()}},//тратим
			new double[][] {new double[] {2.5, 3, 3, 2.5}, new double[] {c.CO2.ordinal(), c.TUNGSTEN.ordinal(), c.CATALYST.ordinal(), c.GLUCOSE.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//синтез тория
			new double[][] {new double[] {3, 1, 4}, new double[] {c.CATALYST.ordinal(), c.HYDROGENIUM.ordinal(), c.CO2.ordinal()}},//тратим
			new double[][] {new double[] {4, 2}, new double[] {c.THORIUM.ordinal(), c.ENERGY.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//ядерный синтез
			new double[][] {new double[] {7}, new double[] {c.THORIUM.ordinal()}},//тратим
			new double[][] {new double[] {5}, new double[] {c.ENERGY.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//окисление водорода
			new double[][] {new double[] {2.5, 5}, new double[] {c.OXYGEN.ordinal(), c.HYDROGENIUM.ordinal()}},//тратим
			new double[][] {new double[] {3}, new double[] {c.ENERGY.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//окисление железа
			new double[][] {new double[] {1.5, 3}, new double[] {c.OXYGEN.ordinal(), c.IRON.ordinal()}},//тратим
			new double[][] {new double[] {4}, new double[] {c.ENERGY.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//синтез железа
			new double[][] {new double[] {3, 3}, new double[] {c.TUNGSTEN.ordinal(), c.POISON.ordinal()}},//тратим
			new double[][] {new double[] {3, 2}, new double[] {c.ENERGY.ordinal(), c.IRON.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//синтез яда
			new double[][] {new double[] {5, 3.5}, new double[] {c.CATALYST.ordinal(), c.CO2.ordinal()}},//тратим
			new double[][] {new double[] {2.8}, new double[] {c.POISON.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//переработка яда
			new double[][] {new double[] {2.7, 8}, new double[] {c.HYDROGENIUM.ordinal(), c.POISON.ordinal()}},//тратим
			new double[][] {new double[] {12}, new double[] {c.GLUCOSE.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//разложение железа
			new double[][] {new double[] {4, 5}, new double[] {c.CO2.ordinal(), c.IRON.ordinal()}},//тратим
			new double[][] {new double[] {0.3, 3}, new double[] {c.CRYSTAL.ordinal(), c.POISON.ordinal()}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
		new double[][][] {//нагрев
			new double[][] {new double[] {5, 3}, new double[] {c.ENERGY.ordinal(), c.OXYGEN.ordinal()}},//тратим
			new double[][] {new double[] {}, new double[] {}},//производим
			new double[][] {new double[] {}, new double[] {}, new double[] {}, new double[] {}},//катализаторы
			new double[][] {new double[] {0, 1}, new double[] {0, 1}},//температура
		},
	};
	public static Color[] reactions_color = new Color[]{//цвета для ботов, выполняющих реакции
		new Color(203, 242, 68),// гликолиз
		new Color(255, 255, 128),//анаэробное дыхание
		new Color(255, 255, 0),//  аэробное дыхание
		new Color(0, 255, 0),//    фотосинтез
		new Color(82, 170, 229),// переработка углекислоты
		new Color(87, 173, 233),// переработка кристалла
		new Color(251, 117, 255),//синтез тория
		new Color(250, 92, 145),// ядерный синтез
		new Color(0, 0, 255),//    окисление водорода
		new Color(105, 91, 126),// окисление железа
		new Color(106, 84, 70),//  синтез железа
		new Color(150, 0, 0),//    синтез яда
		new Color(140, 43, 59),//  переработка яда
		new Color(171, 71, 73),//  разложение железа
		new Color(255, 255, 255),//нагрев
	};
	public static int get_reaction_from_num(int num) {//дается число. возвращается выполняемая реакция
		if (num >= 0 && num <= 2) {
			return(0);
		}else if (num >= 3 && num <= 5) {
			return(1);
		}else if (num >= 6 && num <= 8) {
			return(2);
		}else if (num >= 9 && num <= 11) {
			return(3);
		}else {
			return((num - 12) % reactions.length);
		}
	}
}

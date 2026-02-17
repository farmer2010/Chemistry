package sct;

import java.awt.Color;

public class Reactions {
	public static class Reaction{
		double[] spend_count;
		int[] spend_types;
		double[] production_count;
		int[] production_types;
		//
		int min_speed;
		//
		double[] catalyst_coeffs;    //ускорение за единицу катализатора
		int[] catalyst_types;        //тип вещества - катализатора
		double[] catalyst_min_coeffs;//минимальное ускорение
		double[] catalyst_max_coeffs;//максимальное ускорение
		//
		double min_temp;
		double temp_production;
		double temp_spend;
		//
		public Reaction(
				double[] new_spend_count,
				int[] new_spend_types,
				double[] new_production_count,
				int[] new_production_types,
				double[] new_catalyst_coeffs,
				int[] new_catalyst_types,
				double[] new_catalyst_min_coeffs,
				double[] new_catalyst_max_coeffs,
				double new_min_temp,
				double new_temp_spend,
				double new_temp_production,
				int new_min_speed
			) {
			spend_count = new_spend_count;
			spend_types = new_spend_types;
			production_count = new_production_count;
			production_types = new_production_types;
			min_speed = new_min_speed;
			catalyst_coeffs = new_catalyst_coeffs;
			catalyst_types = new_catalyst_types;
			catalyst_min_coeffs = new_catalyst_min_coeffs;
			catalyst_max_coeffs = new_catalyst_max_coeffs;
			min_temp = new_min_temp;
			temp_production = new_temp_production;
			temp_spend = new_temp_spend;
		}
	}
	//
	public static int ch_count = 10;
	//Вещества и их свойства
	//
	public static double[] start_count = new double[] {200, 0, 20, 200, 50, 20, 0, 2, 0, 0};//стартовое количество веществ
	public static double[] viscosity = new double[] {0.01, 0, 1, 1, 0.5, 0, 0.7, 0, 0.01, 0};//скорость. 0 - не распространяется, 1 - распространяется с максимально скоростью
	public static double[] evaporation = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//испарение. 0 - не испаряется, 1 - испаряется все за раз
	public static double[] up = new double[] {0, 0, 0, 0, 0.3, 0, 0, 0, 0, 0};//подъем(только водород). 0 - не поднимается, 1 - поднимается весь объем
	public static double[] up_max = new double[] {0, 0, 0, 0, 300, 0, 0, 0, 0, 0};//максимальное количество вещества на клетке выше для подъема
	public static double[] collect_speed = new double[] {0.02, 0, 0.04, 0.04, 0.04, 0, 0.04, 0, 0.015, 0.001};//скорость сбора вещества
	public static int[] crystal_chances = new int[] {0, 10, 6, 5, 4, 3, 2, 1, 0};//шансы появления кристалла в зависимости от количества соседей
	//
	public static int hydro_pl = 100;//водорода
	//
	public static enum c{
		LIGHT,
		ENERGY,
		TEMP,
		A,//глюкоза
		B,//кристалл
		C,//кислород
		D,//углекислый газ
		E,//водород
		F,//вольфрам
		G,//азот
		H,//торий
		I,//яд
		J//железо
	}
	//
	//Реакции
	//
	//массив реакций: [тип реакции][трата, производство][параметры реакции][вещества]
	public static Reaction[] reactions = new Reaction[] {
		new Reaction(//гликолиз
			new double[] {5}, new int[] {c.A.ordinal()},//тратим
			new double[] {3}, new int[] {c.ENERGY.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//анаэробное дыхание
			new double[] {5, 4.5}, new int[] {c.A.ordinal(), c.D.ordinal()},//тратим
			new double[] {4}, new int[] {c.ENERGY.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//аэробное дыхание
			new double[] {5, 1.7}, new int[] {c.A.ordinal(), c.C.ordinal()},//тратим
			new double[] {6, 1.7}, new int[] {c.ENERGY.ordinal(), c.D.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//фотосинтез
			new double[] {0.5, 0.4}, new int[] {c.E.ordinal(), c.D.ordinal()},//тратим
			new double[] {1.2, 0.6}, new int[] {c.A.ordinal(), c.C.ordinal()},//производим
			new double[] {1}, new int[] {c.LIGHT.ordinal()}, new double[] {0}, new double[] {100},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//переработка углекислоты
			new double[] {5, 2}, new int[] {c.D.ordinal(), c.C.ordinal()},//тратим
			new double[] {6}, new int[] {c.A.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//переработка кристалла
			new double[] {5, 1}, new int[] {c.B.ordinal(), c.E.ordinal()},//тратим
			new double[] {2.5, 3, 3, 2.5}, new int[] {c.D.ordinal(), c.F.ordinal(), c.G.ordinal(), c.A.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//синтез тория
			new double[] {3, 1, 4}, new int[] {c.G.ordinal(), c.E.ordinal(), c.D.ordinal()},//тратим
			new double[] {4, 2}, new int[] {c.H.ordinal(), c.ENERGY.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//ядерный синтез
			new double[] {7}, new int[] {c.H.ordinal()},//тратим
			new double[] {5}, new int[] {c.ENERGY.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//окисление водорода
			new double[] {2, 4}, new int[] {c.C.ordinal(), c.E.ordinal()},//тратим
			new double[] {5}, new int[] {c.ENERGY.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//окисление железа
			new double[] {1.5, 3}, new int[] {c.C.ordinal(), c.J.ordinal()},//тратим
			new double[] {4}, new int[] {c.ENERGY.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//синтез железа
			new double[] {3, 3}, new int[] {c.F.ordinal(), c.I.ordinal()},//тратим
			new double[] {3, 2}, new int[] {c.ENERGY.ordinal(), c.J.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//синтез яда
			new double[] {5, 3.5}, new int[] {c.G.ordinal(), c.D.ordinal()},//тратим
			new double[] {2.8}, new int[] {c.I.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//переработка яда
			new double[] {2.7, 8}, new int[] {c.E.ordinal(), c.I.ordinal()},//тратим
			new double[] {12}, new int[] {c.A.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//разложение железа
			new double[] {4, 5}, new int[] {c.D.ordinal(), c.J.ordinal()},//тратим
			new double[] {0.3, 3}, new int[] {c.B.ordinal(), c.I.ordinal()},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		),
		new Reaction(//нагрев
			new double[] {5, 3}, new int[] {c.ENERGY.ordinal(), c.C.ordinal()},//тратим
			new double[] {}, new int[] {},//производим
			new double[] {}, new int[] {}, new double[] {}, new double[] {},//катализаторы
			0, 0, 0,//температура: минимальная для выполнения, трата, производство
			1//минимальная скорость
		)
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

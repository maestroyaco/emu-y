package variables;

import java.util.ArrayList;
import java.util.List;

public class Idiomas {
	private static List<String> textosEN = new ArrayList<>();
	private static List<String> textosES = new ArrayList<>();
	@SuppressWarnings("unused")
	private static List<String> textosFR = new ArrayList<>();

	public static String getTexto(String idioma, int valor) {
		switch (idioma) {
		case "es":
			return textosES.get(valor);
		case "en":
			return textosEN.get(valor);
		case "fr":
			return textosES.get(valor);
		default:
			return textosES.get(valor);
		}
	}

	public static void cargarIdiomaES() {
		int index = 0;
		textosES.add(index, "El nombre no se encuentra disponible");
		index++;
		textosES.add(index, "Tu nombre ha sido cambiado a ");
		index++;
		textosES.add(index, "Tu cercado se ha vendido a ");
		index++;
		textosES.add(index, "No puedes equiparte una montura mientras tienes una mascota equipada");
		index++;
		textosES.add(index, "Esta opci�n no estÁ habilitada todav�a");
		index++;
		textosES.add(index, "Vuestro amor ya no estÁ en l�nea");
		index++;
		textosES.add(index, "No se pueden tirar las Kolichas");
		index++;
		textosES.add(index, "No se pueden tirar las Chapas");
		index++;
		textosES.add(index, "Los Objetos VIP no se pueden tirar al suelo");
		index++;
		textosES.add(index, "No cumples las condiciones necesarias para equiparte este objeto");
		index++;
		textosES.add(index, "Hubo un error al borrar este objeto");
		index++; // 10
		textosES.add(index, "No puedes destruir un objeto equipado");
		index++;
		textosES.add(index, "No puedes modificar tu inventario estando en combate");
		index++;// 12
		textosES.add(index, "No puedes equiparte una mascota si estÁs subido a una montura");
		index++;
		textosES.add(index, "No puedes poner un objeto viviente encima de otro");
		index++;
		textosES.add(index, "El objeto no existe o estÁ bugeado, intenta cambiar de personaje para evitar mÁs errores");
		index++;
		textosES.add(index, "No se le pueden equipar objetos vivientes a los sets de clase");
		index++;// 16
		textosES.add(index, "Tus oficios estÁn ahora en modo p�blico y estÁs catalogado en la lista de los artesanos");
		index++;
		textosES.add(index, "Ya no estÁs catalogado en la lista de los artesanos");
		index++;
		textosES.add(index, "No puedes mineralizar una mascota");
		index++;// 19
		textosES.add(index, "No puedes mineralizar un objevivo");
		index++;
		textosES.add(index, "No puedes mineralizar este tipo de objetos");
		index++;
		textosES.add(index, "Ya estÁs en un crafteo");
		index++;
		textosES.add(index, "Error Desconocido!!!");
		index++;// 23
		textosES.add(index, "No es posible maguear una mascota");
		index++;
		textosES.add(index, "No es posible maguear un objevivo");
		index++;
		textosES.add(index, "SÓlo se pueden agregar runas de 300 en 300");
		index++;// 26
		textosES.add(index, "Los Objevivo no se pueden poner en venta");
		index++;// 27
		textosES.add(index, "El mÁximo valor de Kamas para ingresar es 999.999.999");
		index++;
		textosES.add(index, "SÓlo puedes poner <b>x1 Polvo de resurecci�n</b> o <b>x1 Fantasma de Mascota</b>");
		index++;
		textosES.add(index, "Necesitas 1.000.000 Kamas para poder perfeccionar un objeto");
		index++;
		textosES.add(index, "SÓlo puedes poner un objeto para perfeccionar");
		index++;
		textosES.add(index, "La cantidad m�xima del objeto es 100");
		index++;// 32
		textosES.add(index, "No se pueden intercambiar las Kolichas");
		index++;
		textosES.add(index, "No se pueden intercambiar las Chapas");
		index++;// 34
		textosES.add(index, "Este objeto no se puede perfeccionar");
		index++;// 35
		textosES.add(index, "SÓlo puedes poner objetos R1, R2 y R3");
		index++;// 36
		textosES.add(index, "No puedes mineralizar un objeto de la Caja Legendaria");
		index++;// 37
		textosES.add(index, "No puedes tener 2 mascotas iguales");
		index++;// 38
		textosES.add(index, "No puedes desafiar estando en un grupo de Maitre");
		index++;
		textosES.add(index, "No puedes ponerte en modo mercante porque no tienes objetos");
		index++;//
		textosES.add(index, "Has comprado el objeto");
		index++;// 41
		textosES.add(index, "El objeto no se puede comprar");
		index++;//
		textosES.add(index, "Espera 3 seg antes de usar esta acciÓn de nuevo.");
		index++;//
		textosES.add(index, "Tu dragopavo tiene objetos en el inventario y no se puede mover");
		index++;// 44
		textosES.add(index, "Tienes que bajarte de la montura primero");
		index++;// 45
		textosES.add(index, "cMK|-99|Mylo|Ahora f�jate en los stats del Objeto creado|");
		index++;//
		textosES.add(index, "Es necesario poner x1 Mineral y x1 Objeto a mineralizar");
		index++;//
		textosES.add(index, "Es necesario poner x1 Mineral");
		index++;// 48
		textosES.add(index, "Es necesario poner x1 Objeto a mineralizar");
		index++;//
		textosES.add(index, "No puedes agregar este mineral al objeto");
		index++;// 50
		textosES.add(index, "El mÁximo de minerales que puedes agregar son 3");
		index++;//
		textosES.add(index, "Tienes que poner <b>x1 Polvo de resurecci�n</b> y <b>x1 Fantasma de Mascota</b>");
		index++;//
		textosES.add(index, "Tienes que poner al menos un objeto para perfeccionar");
		index++;// 53
		textosES.add(index, "No puedes comprar tantos items a la vez");
		index++;// 54
		textosES.add(index, "No se pudo realizar la compra, dado que le faltan ");
		index++;//
		textosES.add(index, " Puntos VIP");
		index++;// 56
		textosES.add(index, "Gracias por comprar en el servidor. A�n te quedan ");
		index++;// 57
		textosES.add(index, "No puedes comprar tantos items a la vez");
		index++;// 58
		textosES.add(index, " Kolichas");
		index++;//
		textosES.add(index, " Chapeuvep�s");
		index++;//
		textosES.add(index, "No puedes comprar este objeto porque el personaje mercante se acaba de reconectar");
		index++;//
		textosES.add(index, "No puedes iniciar un intercambio mientras estÁs de esclavo en un grupo de Maitre");
		index++;// 62
		textosES.add(index, "AcciÓn deshabilitada temporalmente");
		index++;// 63
		textosES.add(index, "<b>El personaje mercante estaba bug y ha sido retirado con Éxito</b>");
		index++;//
		textosES.add(index, "No puedes usar este comando en combate");
		index++;// 65
		textosES.add(index, "No puedes usar este comando mientras estÁs en un intercambio");
		index++;//
		textosES.add(index, "No puedes usar este comando mientras estÁs en una acciÓn");
		index++;//
		textosES.add(index, "No puedes usar este comando porque estÁs ocupado");
		index++;//
		textosES.add(index, "Has llegado al inicio");
		index++;//
		textosES.add(index,
				"Los comandos disponibles son:\n<b>.astrub</b> - Te lleva al Zaap de Astrub\n<b>.start</b> - Te lleva al mapa de Inicio\n<b>.pvp</b> - Te lleva al mapa PvP\n<b>.shop</b> - Te lleva al mapa de Shop\n<b>.puntos</b> - Te muestra tus Puntos VIP disponibles\n<b>.vida</b> - Te restaura toda la vida\n<b>.taller</b> - Te lleva al taller de FM\n<b>.cercado</b> - Te lleva al cercado\n<b>.rs</b> - Te lleva al zaap Niifus\n<b>.dojo</b> - Te lleva al mapa de Raids\n<b>.infos</b> - Te muestra la informaciÓn del server\n<b>.r2</b> - Te lleva al zaap de frigost R2\n<b>.pass</b> - Te Permite Pasar turno Rapido en Pelea");
		index++;//
		textosES.add(index, "Has llegado al mapa PvP");
		index++;// 71
		textosES.add(index, "Has llegado a la Taberna");
		index++;//
		textosES.add(index, "SÓlo puedes usar el editor de casas estando en tu propia casa");
		index++;// 73
		textosES.add(index, "Has reiniciado tu casa con Éxito");
		index++;// 74
		textosES.add(index, "Has llegado al Taller");
		index++;//
		textosES.add(index, "Te has curado por completo");
		index++;// 76
		textosES.add(index, "Has llegado a Astrub");
		index++;//
		textosES.add(index, "Has llegado al mapa shop");
		index++;//
		textosES.add(index, "Tienes un total de <b>");
		index++;// 79
		textosES.add(index, "El personaje <b>");
		index++;//
		textosES.add(index, "</b> tiene nivel ");
		index++;// 81
		textosES.add(index, "Has ganado esta pelea porque tu adversario se ha desconectado");
		index++;//
		textosES.add(index, "No puedes realizar acciones mientras estÁs en un grupo de Maitre");
		index++;//
		textosES.add(index, "No puedes atacar Recaudadores mientras estÁs en un grupo de Maitre");
		index++;//
		textosES.add(index, "No puedes atacar Prismas mientras estÁs en un grupo de Maitre");
		index++;//
		textosES.add(index, "Necesitas una alineaciÓn para poder atacar un prisma");
		index++;// 86
		textosES.add(index, "No puedes agredir estando en un grupo de Maitre");
		index++;//
		textosES.add(index, "No se le puede agredir al objetivo, por diferentes causas");
		index++;// 88
		textosES.add(index, "No puedes agredir a alguien mientras estÁs en un Torneo");
		index++;//
		textosES.add(index, "No puedes desafiar a alguien mientras estÁ en un Torneo");
		index++;// 90
		textosES.add(index, "No puedes desafiar a alguien que estÁ en un grupo de Maitre");
		index++;//
		textosES.add(index, "No puedes agredir a esta persona porque estÁ realizando una acciÓn");
		index++;//
		textosES.add(index, "No puedes agredir a alguien que estÁ en un grupo de Maitre");
		index++;//
		textosES.add(index,
				"No puedes agredir a la misma persona otra vez. Para volver a agredirla, necesitas agredir a otras 3 personas diferentes");
		index++;//
		textosES.add(index, "No puedes agredir a tus propias cuentas");
		index++;// 95
		textosES.add(index, "No puedes agredir a alguien mientras estÁ en un Torneo");
		index++;//
		textosES.add(index, "No puedes agredir a alguien neutral");
		index++;//
		textosES.add(index, "El objetivo se encuentra en una agresi�n");
		index++;// 98
		textosES.add(index, "No puedes desafiar a alguien mientras estÁs en un Torneo");
		index++;// 99
		textosES.add(index, "SÓlo el lider del grupo se puede mover por el mapa");
		index++;//
		textosES.add(index,
				"Acabas de salir del rango de Maitre, si el lider se mueve por este mapa, no te llevar� con �l");
		index++;// 101
		textosES.add(index, "Espera 5 segundos antes de volver a inscribirte");
		index++;// 102
		textosES.add(index, "Te has unido a una cola de espera junto a una persona mÁs");
		index++;// 103
		textosES.add(index, "Te has unido a una cola de espera junto a ");
		index++;// 104
		textosES.add(index, " personas mÁs");
		index++;//
		textosES.add(index, " acaba de unirse al grupo de Koliseo");
		index++;//
		textosES.add(index, "Ya estÁs inscrito en un Koliseo");
		index++;//
		textosES.add(index, "No estÁs inscrito en ning�n Koliseo");
		index++;//
		textosES.add(index, "La fase 2 del Torneo empezarÁ pronto");
		index++;// 109
		textosES.add(index, "La fase 3 del Torneo empezarÁ pronto");
		index++;//
		textosES.add(index, "Has ganado el Torneo");
		index++;//
		textosES.add(index, "Has perdido el Torneo");
		index++;//
		textosES.add(index, "Has abandonado el Torneo");
		index++;// 113
		textosES.add(index, "La pelea empezarÁ en unos segundos!!!");
		index++;//
		textosES.add(index, "No puedes desinscribirte del Torneo porque ya ha empezado");
		index++;//
		textosES.add(index, "Ya no estÁs en el Torneo");
		index++;// 116
		textosES.add(index, "No puedes inscribirte al Torneo porque ya ha empezado");
		index++;//
		textosES.add(index, "Ya estÁs inscrito en el Torneo");
		index++;//
		textosES.add(index, "El Torneo estÁ lleno, pronto comenzar�n las partidas");
		index++;//
		textosES.add(index, "No puedes inscribir multicuentas al Torneo");
		index++;//
		textosES.add(index, "Hubo un error al inscribirse, int�ntalo de nuevo mÁs tarde");
		index++;// 11
		textosES.add(index, "El Torneo empezarÁ en 5 segundos");
		index++;//
		textosES.add(index,
				"<b>RECUERDA:</b> La primera fase serÁ aleatoria y se mezclar�n todos los personajes aleatoriamente");
		index++;//
		textosES.add(index, "No hay ning�n Torneo programado todav�a");
		index++;// 124
		textosES.add(index, "Error al reclamar el objeto");
		index++;//
		textosES.add(index,
				"Ya has reclamado el mÁximo de objetos por este combate, recuerda que sÓlo puedes reclamar ");
		index++;//
		textosES.add(index, " objetos por combate. Los recursos sin incluir equipamiento no tienen l�mites");
		index++;//
		textosES.add(index, " Este tipo de objetos no tiene l�mite de reclamos");
		index++;//
		textosES.add(index, "Objeto reclamado con Éxito.");
		index++;//
		textosES.add(index, "Necesitas 5 Puntos VIP para cambiar de color");
		index++;// 130
		textosES.add(index, "<b>Tu mensaje ha sido enviado con Éxito.</b>");
		index++;//
		textosES.add(index, "Ya estÁs en un Grupo Maitre y eres el l�der");
		index++;//
		textosES.add(index, "Ya estÁs en un Grupo Maitre y eres el esclavo");
		index++;//
		textosES.add(index, "Para crear un grupo de Maitre todos tus personajes necesitan estar en el mismo mapa");
		index++;//
		textosES.add(index, "Acabas de crear un grupo Maitre");
		index++;//
		textosES.add(index, "SÓlo puedes hacer Maitre con tus propias cuentas");
		index++;// 136
		textosES.add(index,
				"Bienvenido a Niifus\n\n Recuerda:\n\n Si Votas en la Pagina Web y compartes la pagina de Niifus por Facebook \n\nPuedes ganar Puntos VIP");
		index++;//
		textosES.add(index, "Has ganado 200 Puntos VIP por registrarte con un enlace de referido");
		index++;// 138
		textosES.add(index, "No puedes usar esta funcion con un objeto que ya es un Mimobionte");
		index++;
		textosES.add(index, "No puedes usar esta funcion con este tipo de objetos");
		index++;
		textosES.add(index, "No puedes usar esta funcion con un item objevivo");
		index++;
		textosES.add(index,
				"No puedes usar fusionar estos 2 objetos de distinto tipo, sÓlo puedes fusionar entre s� Armas, Sombreros, Capas, Mascotas y Escudos y objetos del mismo tipo.");
		index++;
		textosES.add(index, "Tu objeto ha sido creado");
		index++;
		textosES.add(index, "Necesitas 50 Puntos VIP para crear un Mimobionte.");
		index++;// 144
		textosES.add(index, "No puedes perfeccionar un Objevivo");
		index++;
		textosES.add(index, "No puedes perfeccionar un Objeto Legendario");
		index++;
		textosES.add(index, "Tu objeto acaba de ser perfeccionado");
		index++;
		textosES.add(index, "Necesitas 200.000 Kamas para poder perfeccionar un objeto");
		index++;// 148
		textosES.add(index, "No puedes participar en este Torneo porque no cumples con los requisitos de R");
		index++;// 149
		textosES.add(index, "Has llegado a la Arena");
		index++;
		textosES.add(index,
				"<b>[EVENTO R3]:</b> Un Ángel de la creaci�n ha ca�do del cielo, estÁ en la zona del Rinc�n de los Tofus (Debajo de Astrub)");
		index++;// 151
		textosES.add(index,
				"<b>[EVENTO R3]:</b> Un demonio se ha alzado de las profundidades del infierno, estÁ en la zona del Rinc�n de los Tofus (Debajo de Astrub)");
		index++;// 152
		textosES.add(index,
				"<b>[EVENTO R3]:</b> Un Ángel de la creaci�n ha ca�do del cielo, estÁ en la zona de Tainela La Cuna");
		index++;//
		textosES.add(index,
				"<b>[EVENTO R3]:</b> Un demonio se ha alzado de las profundidades del infierno, estÁ en la zona de Tainela La Cuna");
		index++;//
		textosES.add(index, "SÓlo los R3 pueden unirse a este combate");
		index++;// 155
		textosES.add(index, "El combate empezarÁ en unos segundos, no te puedes unir ahora");
		index++;//
		textosES.add(index, "No puedes unirte a esta pelea de Koliseo");
		index++;// 157
		textosES.add(index, "No puedes unirte a esta pelea de Torneo");
		index++;// 158
		textosES.add(index, "No puedes unirte a una pelea de Agresi�n");
		index++;//
		textosES.add(index, "No puedes unirte a esta pelea");
		index++;//
		textosES.add(index, "En las mazmorras sÓlo se permiten 3 cuentas por IP en una misma pelea");
		index++;//
		textosES.add(index, "En las peleas de evento sÓlo se permite 1 cuenta por IP en una misma pelea");
		index++;// 162
		textosES.add(index, "No puedes unirte a un combate de DesafÍo Diario");
		index++;// 163
		textosES.add(index, "Se han actualizado los Objetivos Diarios");
		index++;// 164
		textosES.add(index, "Quedan apr�ximadamente <b>");
		index++;//
		textosES.add(index, "</b> minutos para que los Objetivos Diarios se reinicien.");
		index++;//
		textosES.add(index, "Quedan <b>");
		index++;//
		textosES.add(index, "</b> horas y <b>");
		index++;// 168
		textosES.add(index, " Moneda/s Astro");
		index++;// 169
		textosES.add(index, "No puedes modificar tu inventario mientras interactuas con un NPC");
		index++;// 170
		textosES.add(index, "<b>[EVENTO GLOBAL]:</b> Una Gema Roja misteriosa ha aparecido por Astrub/Alrededores!!!");
		index++;// 171
		textosES.add(index, "<b>[EVENTO GLOBAL]:</b> Una Gema Azul misteriosa ha aparecido por Astrub/Alrededores!!!");
		index++;// 172
		textosES.add(index, "<b>[EVENTO GLOBAL]:</b> La Gema ha sido encontrada");
		index++;// 173
		textosES.add(index, "<b>[EVENTO GLOBAL]:</b> Un bot�n de Kamas ha aparecido por Astrub/Alrededores");
		index++;// 174
		textosES.add(index, "<b>[EVENTO GLOBAL]:</b> Un Regalo Misterioso ha aparecido por Astrub/Alrededores");
		index++;// 175
		textosES.add(index,
				"<b>[EVENTO GLOBAL]:</b> El bot�n de Kamas ha sido encontrado y se han llevado 500.000 Kamas");
		index++;// 176
		textosES.add(index, "<b>[EVENTO GLOBAL]:</b> El Regalo Misterioso ha sido encontrado");
		index++;// 177
		textosES.add(index, "No se pueden tirar la Moneda Astro");
		index++;// 178
		textosES.add(index, "No se pueden intercambiar las Monedas Astro");
		index++;// 179
		textosES.add(index, "SÓlo puedes poner Armas y P�cimas");
		index++;// 180
		textosES.add(index, "Necesitas 500.000 Kamas para cambiar el elemento de un Arma");
		index++;
		textosES.add(index, "Necesitas ser R1 o superior para cambiar el elemento de un Arma");
		index++;
		textosES.add(index,
				"Necesitas completar la misi�n de las Mazmorras R0 para poder cambiar el elemento de un Arma");
		index++;// 183
		textosES.add(index, "Tienes que poner x1 Arma y x1 P�cima");
		index++;// 184
		textosES.add(index,
				"<b>[EVENTO GLOBAL]:</b> Un portal interdimensional se ha abierto en el Cementerio de Bonta. Se mantendrÁ abierto 10 minutos y luego empezarÁ el evento");
		index++;// 185
		textosES.add(index, " ha cruzado el portal");
		index++;// 186
		textosES.add(index, "El portal se ha cerrado y el evento ha comenzado!!!");
		index++;// 187
		textosES.add(index,
				"Bienvenido al Servidor!! Supongo que la Sadida de mi lado te ha dado Kamas, Usalas con cabeza!|");
		index++;// 188
		textosES.add(index, "Puede empezar en el servidor haciendo las misiones que dan los NPCs en esta isla|");
		index++;// 189
		textosES.add(index, "Encima del bot�n Conquista hay una flecha, esos son los Objetivos Diarios|");
		index++;// 190
		textosES.add(index,
				"Te recomiendo completar todos Objetivos Diarios y todas las misiones que tengas disponibles|");
		index++;// 191
		textosES.add(index, "Al bajar a la planta de abajo te encontrar�s con algunos vendedores y NPCs de Mazmorras|");
		index++;// 192
		textosES.add(index, "Recuerda que puedes ver los comandos disponibles escribiendo .comandos|");
		index++;// 193
		textosES.add(index,
				"En este servidor tienes la posibilidad de personalizar tu propia casa, te mostrar� un ejemplo|");
		index++;// 194
		textosES.add(index,
				"Este es el Editor de Casas, podrÁs quitar los objetos de tu casa y personalizarla a tu gusto!!!|");
		index++;// 195
		textosES.add(index,
				"Recuerda que tener una Casa NO es Gratis y tendrÁs que pagar los impuestos por ella, podrÁs pagar los impuestos visitando al Banquero|");
		index++;// 196
		textosES.add(index,
				"Todo esto y mucho mÁs, ya lo ir�s descubriendo explorando el servidor, que te diviertas!!!|");
		index++;// 197
		textosES.add(index, " Pirutas");
		index++;// 198
		textosES.add(index, "No puedes intercambiar este objeto");
		index++;// 199
		textosES.add(index, "No puedes tirar este objeto al suelo");
		index++;// 200
		textosES.add(index, " Tabla/s de Olivioleta/s");
		index++;// 201
		textosES.add(index, "Has llegado a Incarnam");
		index++;// 202
		textosES.add(index, "\n<b>.incarnam</b> - Te lleva a Incarnam");
		index++;// 203
		textosES.add(index, "Los miembros NO-Vip y R0 sÓlo pueden curarse el 80% de su vida");
		index++;// 204
		textosES.add(index,
				"<b>[EVENTO GLOBAL]:</b> Un Mercader Misterioso ha aparecido por las Praderas de Astrub. Se quedar� <b>5 minutos</b> y luego desaparecer�!!!");
		index++;// 205
		textosES.add(index, "El Mercader Misterioso ha desaparecido!!!");
		index++;// 206
		textosES.add(index, "SÓlo se pueden comprar 2 objetos por persona");
		index++;// 207
		textosES.add(index,
				"<b>[EVENTO GLOBAL]:</b> La temporada de caza ha empezado!!! Escribe <b>.evento</b> y caza todos los Jalat�s que puedas!!! Termina en <b>5 minutos</b>");
		index++;// 208
		textosES.add(index,
				"<b>[EVENTO GLOBAL]:</b> Una misteriosa carrera se llevar� a cabo!!! Escribe <b>.evento</b> y apuesta por un ganador!!! La carrera empieza en <b>3 minutos</b>");
		index++;// 209
		textosES.add(index, "Ha empezado la carrera de Larvas!!! Qui�n serÁ el ganador???");
		index++;// 210
		textosES.add(index, "El evento de Larvas ha finalizado!!!");
		index++;// 211
		textosES.add(index, " Moneda(s) de la Fraternidad");
		index++;// 212
		textosES.add(index, " Moneda(s)");
		index++;// 213
		textosES.add(index, " Moneda(s)");
		index++;// 214
		textosES.add(index, " Moneda(s) R2");
		index++;// 215
		textosES.add(index, " Moneda(s) R3");
		index++;// 216
		textosES.add(index, " Dopl�n(es)");
		index++;// 217
		textosES.add(index, "Has llegado a un lugar extraÑo...");
		index++;// 218
		textosES.add(index, "Has llegado a un lugar extraÑo...");
		index++;// 219
		textosES.add(index, "Bienvenido a Niifus Duty Free");
		index++;// 220
		textosES.add(index, " Copos Dorados.");
		index++;// 221
		textosES.add(index, "A los Mapas de RAID solo se permite 1 personaje por IP");
		index++;// 222
		textosES.add(index, "Solo es Permitida 1 Clase en la Pelea!");
		index++;// 223
		textosES.add(index, "Has llegado al Dojo");
		index++;// 224
		textosES.add(index, "Has llegado a Frigost");
		index++;// 225
	}

	public static void cargarIdiomaEN() {
		int index = 0;
		textosEN.add(index, "The name is not available");
		index++;
		textosEN.add(index, "Your name has been changed to ");
		index++;
		textosEN.add(index, "Your fence has been sold for ");
		index++;
		textosEN.add(index, "You cannot equip a mount while you have a pet equipped");
		index++;
		textosEN.add(index, "This option is not enabled yet");
		index++;
		textosEN.add(index, "Your love is no longer online");
		index++;
		textosEN.add(index, "Kolitokens cannot be thrown");
		index++;
		textosEN.add(index, "The Plates cannot be thrown away");
		index++;
		textosEN.add(index, "VIP Items cannot be dropped");
		index++;
		textosEN.add(index, "You do not meet the necessary conditions to equip this item");
		index++;
		textosEN.add(index, "There was an error deleting this object");
		index++; // 10
		textosEN.add(index, "You cannot destroy an equipped item");
		index++;
		textosEN.add(index, "You cannot modify your inventory while in combat");
		index++;// 12
		textosEN.add(index, "You cannot equip a pet if you are on a mount");
		index++;
		textosEN.add(index, "You cannot put one living object on top of another");
		index++;
		textosEN.add(index, "The object does not exist or is buggy, try to change characters to avoid more errors");
		index++;
		textosEN.add(index, "Living items cannot be equipped to class sets");
		index++;// 16
		textosEN.add(index, "Your crafts are now in public mode and you are listed in the list of craftsmen");
		index++;
		textosEN.add(index, "You are no longer listed on the list of artisans");
		index++;
		textosEN.add(index, "You can't mineralize a pet");
		index++;// 19
		textosEN.add(index, "You can't mineralize an objective");
		index++;
		textosEN.add(index, "You cannot mineralize these types of objects");
		index++;
		textosEN.add(index, "You are already in a crafting");
		index++;
		textosEN.add(index, "Unknown Error!!!");
		index++;// 23
		textosEN.add(index, "It is not possible to forge a pet");
		index++;
		textosEN.add(index, "It is not possible to forge an objective");
		index++;
		textosEN.add(index, "You can only add 300 runes in 300");
		index++;// 26
		textosEN.add(index, "Living objects cannot be put up for sale");
		index++;// 27
		textosEN.add(index, "The maximum value of Kamas to enter is 999.999.999");
		index++;
		textosEN.add(index, "You can only put <b>x1 Resurrection Dust</b> or <b>x1 Pet Ghost</b>");
		index++;
		textosEN.add(index, "You need 200,000 Kamas to perfect an item");
		index++;
		textosEN.add(index, "You can only put one object to perfect");
		index++;
		textosEN.add(index, "The maximum quantity of the item is 100");
		index++;// 32
		textosEN.add(index, "Kolitokens cannot be exchanged");
		index++;
		textosEN.add(index, "The plates cannot be exchanged");
		index++;// 34
		textosEN.add(index, "This item cannot be refined");
		index++;// 35
		textosEN.add(index, "You can only put Objects R1, R2 y R3");
		index++;// 36
		textosEN.add(index, "You cannot mineralize an item from the Legendary box");
		index++;// 37
		textosEN.add(index, "You cannot have 2 equal pets");
		index++;// 38
		textosEN.add(index, "You can't challenge being in a Maitre's group");
		index++;
		textosEN.add(index, "You cannot go into merchant mode because you have no items");
		index++;//
		textosEN.add(index, "You have purchased the item");
		index++;// 41
		textosEN.add(index, "The item cannot be purchased");
		index++;//
		textosEN.add(index, "Wait 3 sec before using this action again.");
		index++;//
		textosEN.add(index, "Your dragonfish has items in inventory and cannot be moved");
		index++;// 44
		textosEN.add(index, "You have to get off the saddle first");
		index++;// 45
		textosEN.add(index, "cMK|-99|Zino|Now look at the stats of the created Object|");
		index++;//
		textosEN.add(index, "It is necessary to put x1 Mineral and x1 Object to mineralize");
		index++;//
		textosEN.add(index, "It is necessary to put x1 Mineral");
		index++;// 48
		textosEN.add(index, "It is necessary to put x1 Object to mineralize");
		index++;//
		textosEN.add(index, "You cannot add this mineral to the object");
		index++;// 50
		textosEN.add(index, "The maximum minerals you can add are 3");
		index++;//
		textosEN.add(index, "You have to put <b>x1 Resurrection Dust</b> and <b>x1 Pet Ghost</b>");
		index++;//
		textosEN.add(index, "You have to put at least one object to perfect");
		index++;// 53
		textosEN.add(index, "You can't buy as many items at once");
		index++;// 54
		textosEN.add(index, "The purchase could not be made, since it is missing ");
		index++;//
		textosEN.add(index, " VIP Points");
		index++;// 56
		textosEN.add(index, "Thank you for shopping at This Server. You still have ");
		index++;// 57
		textosEN.add(index, "You can't buy as many items at once");
		index++;// 58
		textosEN.add(index, " Kolitokens");
		index++;//
		textosEN.add(index, " PvPPlates");
		index++;//
		textosEN.add(index, "You cannot buy this item because the merchant character has just reconnected");
		index++;//
		textosEN.add(index, "You cannot start an exchange while you are a slave in a Maitre's group");
		index++;// 62
		textosEN.add(index, "Action temporarily disabled");
		index++;// 63
		textosEN.add(index, "<b>The merchant character was bugged and has been successfully removed</b>");
		index++;//
		textosEN.add(index, "You cannot use this command in combat");
		index++;// 65
		textosEN.add(index, "You cannot use this command while you are in an exchange");
		index++;//
		textosEN.add(index, "You cannot use this command while you are in an action");
		index++;//
		textosEN.add(index, "You cannot use this command because you are busy");
		index++;//
		textosEN.add(index, "You have reached the beginning");
		index++;//
		textosEN.add(index,
				"The available commands are:\n<b>.astrub</b> - Takes you to the Zaap of Astrub\n<b>.start</b> - Takes you to the home map\n<b>.pvp</b> - Takes you to the PvP map\n<b>.shop</b> - Takes you to the Shop map\n<b>.points</b> - Shows you your available VIP Points\n<b>.life</b> - Restores you all life\n<b>.workshop</b> - Takes you to the FM workshop");
		index++;//
		textosEN.add(index, "You have reached the PvP map");
		index++;// 71
		textosEN.add(index, "You have arrived at the Tavern");
		index++;//
		textosEN.add(index, "You can only use the home editor while in your own home");
		index++;// 73
		textosEN.add(index, "You have successfully restarted your home");
		index++;// 74
		textosEN.add(index, "You have arrived at the Workshop");
		index++;//
		textosEN.add(index, "You have completely healed");
		index++;// 76
		textosEN.add(index, "You have arrived at Astrub");
		index++;//
		textosEN.add(index, "You have reached the shop map");
		index++;//
		textosEN.add(index, "You have a total of <b>");
		index++;// 79
		textosEN.add(index, "The character <b>");
		index++;//
		textosEN.add(index, "</b> has level ");
		index++;// 81
		textosEN.add(index, "You have won this fight because your opponent has disconnected");
		index++;//
		textosEN.add(index, "You cannot perform actions while in a Maitre's group");
		index++;//
		textosEN.add(index, "You cannot attack Collectors while in a Maitre's party");
		index++;//
		textosEN.add(index, "You cannot attack Prisms while in a Maitre's party");
		index++;//
		textosEN.add(index, "You need an alignment to be able to attack a prism");
		index++;// 86
		textosEN.add(index, "You cannot attack being in a Maitre's group");
		index++;//
		textosEN.add(index, "You cannot attack the target, for different reasons");
		index++;// 88
		textosEN.add(index, "You cannot attack someone while you are in a Tournament");
		index++;//
		textosEN.add(index, "You cannot challenge someone while in a Tournament");
		index++;// 90
		textosEN.add(index, "You cannot challenge someone who is in a Maitre's group");
		index++;//
		textosEN.add(index, "You cannot attack this person because they are performing an action");
		index++;//
		textosEN.add(index, "You cannot attack someone who is in a Maitre's group");
		index++;//
		textosEN.add(index,
				"You cannot attack the same person again. To attack her again, you need to attack 3 other different people");
		index++;//
		textosEN.add(index, "You cannot attack your own accounts");
		index++;// 95
		textosEN.add(index, "You cannot assault someone while in a Tournament");
		index++;//
		textosEN.add(index, "You can't attack someone neutral");
		index++;//
		textosEN.add(index, "The target is in aggression");
		index++;// 98
		textosEN.add(index, "You cannot challenge someone while you are in a Tournament");
		index++;// 99
		textosEN.add(index, "Only the leader of the group can move around the map");
		index++;//
		textosEN.add(index,
				"You just got out of Maitre's range, if the leader moves across this map, he won't take you with him");
		index++;// 101
		textosEN.add(index, "Wait 5 seconds before re-registering");
		index++;// 102
		textosEN.add(index, "You have joined a waiting queue with one other person");
		index++;// 103
		textosEN.add(index, "You have joined a waiting queue with ");
		index++;// 104
		textosEN.add(index, " other people");
		index++;//
		textosEN.add(index, " just joined Koliseum group");
		index++;//
		textosEN.add(index, "You are already enrolled in a Koliseum");
		index++;//
		textosEN.add(index, "You are not enrolled in any Koliseum");
		index++;//
		textosEN.add(index, "Phase 2 of the Tournament will start soon");
		index++;// 109
		textosEN.add(index, "Phase 3 of the Tournament will start soon");
		index++;//
		textosEN.add(index, "You have won the Tournament");
		index++;//
		textosEN.add(index, "You have lost the Tournament");
		index++;//
		textosEN.add(index, "You have left the Tournament");
		index++;// 113
		textosEN.add(index, "The fight will start in a few seconds!!!");
		index++;//
		textosEN.add(index, "You cannot unsubscribe from the Tournament because it has already started");
		index++;//
		textosEN.add(index, "You are no longer in the Tournament");
		index++;// 116
		textosEN.add(index, "You cannot register for the Tournament because it has already started");
		index++;//
		textosEN.add(index, "You are already registered in the Tournament");
		index++;//
		textosEN.add(index, "The Tournament is full, the games will start soon");
		index++;//
		textosEN.add(index, "You cannot register multi-accounts to the Tournament");
		index++;//
		textosEN.add(index, "There was an error signing up, please try again later");
		index++;// 11
		textosEN.add(index, "The Tournament will start in 5 seconds");
		index++;//
		textosEN.add(index,
				"<b>REMEMBER:</b> The first phase will be random and all the characters will be randomly mixed");
		index++;//
		textosEN.add(index, "There is no Tournament scheduled yet");
		index++;// 124
		textosEN.add(index, "Failed to claim the object");
		index++;//
		textosEN.add(index,
				"You have already claimed the maximum amount of items for this combat, remember that you can only claim ");
		index++;//
		textosEN.add(index, "  items per combat. Resources without equipment are unlimited");
		index++;//
		textosEN.add(index, " This type of objects has no claim limit");
		index++;//
		textosEN.add(index, "Object claimed successfully.");
		index++;//
		textosEN.add(index, "You need 5 VIP Points to change color");
		index++;// 130
		textosEN.add(index, "<b>Your message has been sent successfully.</b>");
		index++;//
		textosEN.add(index, "You are already in a Maitre Group and you are the leader");
		index++;//
		textosEN.add(index, "You are already in a Maitre Group and you are the slave");
		index++;//
		textosEN.add(index, "To create a Maitre group all your characters need to be on the same map");
		index++;//
		textosEN.add(index, "You just created a Maitre group");
		index++;//
		textosEN.add(index, "You can only make Maitre with your own accounts");
		index++;// 136
		textosEN.add(index,
				"Remember:\n\n If you go to our website, you connect to your account and click onn \n\nMy Account\n\nYou can earn +300 VIP Points if you share the page on FaceBook");
		index++;//
		textosEN.add(index, "You have earned 200 VIP Points for registering with a referral link");
		index++;// 138
		textosEN.add(index, "You cannot use this function with an object that is already a Mimo");
		index++;
		textosEN.add(index, "You cannot use this function with this type of objects");
		index++;
		textosEN.add(index, "You cannot use this function with a target item");
		index++;
		textosEN.add(index,
				"You cannot use merge these 2 objects of different types, you can only merge Weapons, Hats, Cloaks, Pets and Shields and objects of the same type with each other.");
		index++;
		textosEN.add(index, "Your object has been created");
		index++;
		textosEN.add(index, "You need 50 VIP Points to create a Mimo");
		index++;// 144
		textosEN.add(index, "You can't perfect an Living Item");
		index++;
		textosEN.add(index, "You can't perfect a Legendary Item");
		index++;
		textosEN.add(index, "Your object has just been perfected");
		index++;
		textosEN.add(index, "You need 200,000 Kamas to perfect an item");
		index++;// 148
		textosEN.add(index, "You cannot participate in this Tournament because you do not meet the R requirements");
		index++;
		textosEN.add(index, "You have reached the Arena");
		index++;
		textosEN.add(index,
				"<b>[EVENT R3]:</b> An angel of creation has fallen from the sky, is in the area of the Corner of the Tofus (Below Astrub)");
		index++;// 151
		textosEN.add(index,
				"<b>[EVENT R3]:</b> A demon has risen from the depths of hell, he is in the area of the Corner of the Tofus (Below Astrub)");
		index++;// 152
		textosEN.add(index,
				"<b>[EVENT R3]:</b> An angel of creation has fallen from the sky, is in the area of Tainela La Cuna");
		index++;//
		textosEN.add(index,
				"<b>[EVENT R3]:</b> A demon has risen from the depths of hell, it is in the area of Tainela La Cuna");
		index++;//
		textosEN.add(index, "Only R3 can join this combat");
		index++;// 155
		textosEN.add(index, "The fight will start in a few seconds, you can't join now");
		index++;//
		textosEN.add(index, "You can't join this Koliseo fight");
		index++;// 157
		textosEN.add(index, "You can't join this tournament fight");
		index++;// 158
		textosEN.add(index, "You cannot join an Aggression fight");
		index++;//
		textosEN.add(index, "You can't join this fight");
		index++;//
		textosEN.add(index, "In the dungeons only 3 accounts per IP are allowed in the same fight");
		index++;//
		textosEN.add(index, "In event fights only 1 account is allowed per IP in the same fight");
		index++;// 162
		textosEN.add(index, "You cannot join a Daily Challenge match");
		index++;// 163
		textosEN.add(index, "Daily Goals have been updated");
		index++;//
		textosEN.add(index, "Approximately <b>");
		index++;//
		textosEN.add(index, "</b> minutes left for the Daily Goals to restart.");
		index++;//
		textosEN.add(index, "<b>");
		index++;//
		textosEN.add(index, "</b> hours and <b>");
		index++;// 168
		textosEN.add(index, " Astro Coins");
		index++;// 169
		textosEN.add(index, "You cannot modify your inventory while interacting with an NPC");
		index++;// 170
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> A mysterious Red Gem has appeared on Astrub/Surroundings!!!");
		index++;// 171
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> A mysterious Blue Gem has appeared on Astrub/Surroundings!!!");
		index++;// 172
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> The gem has been found");
		index++;// 173
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> Loot from Kamas has appeared on Astrub/Surroundings");
		index++;// 174
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> A Mysterious Gift has appeared on Astrub/Surroundings");
		index++;// 175
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> Kamas loot has been found and 500,000 Kamas have been taken");
		index++;// 176
		textosEN.add(index, "<b>[GLOBAL EVENT]:</b> The Mysterious Gift has been found");
		index++;// 177
		textosEN.add(index, "Unable to throw Astro Coin");
		index++;// 178
		textosEN.add(index, "Astro Coins cannot be exchanged");
		index++;// 179
		textosEN.add(index, "You can only put Weapons and Potions");
		index++;// 180
		textosEN.add(index, "You need 500,000 Kamas to change the item of a Weapon");
		index++;
		textosEN.add(index, "You need to be R1 or higher to change the element of a Weapon");
		index++;
		textosEN.add(index, "You need to complete the R0 Dungeon mission to be able to change the item of a Weapon");
		index++;// 183
		textosEN.add(index, "You have to put x1 Weapon and x1 Potion");
		index++;// 184
		textosEN.add(index,
				"<b>[GLOBAL EVENT]:</b> An interdimensional portal has been opened in the Bontaa Cemetery. It will stay open 10 minutes and then the event will start");
		index++;// 185
		textosEN.add(index, " has crossed the portal");
		index++;// 186
		textosEN.add(index, "The portal has closed and the event has started !!!");
		index++;// 187
		textosEN.add(index, "Welcome to Server, remember that you have your Kamas in the bank!!!|");
		index++;// 188
		textosEN.add(index, "You can start on the server doing the missions given by the NPCs on this island|");
		index++;// 189
		textosEN.add(index, "Above the Conquer button there is an arrow, those are the Daily Goals|");
		index++;// 190
		textosEN.add(index, "I recommend you complete all Daily Goals and all the missions you have available|");
		index++;// 191
		textosEN.add(index, "Going downstairs you will meet some vendors and Dungeon NPCs|");
		index++;// 192
		textosEN.add(index, "Remember that you can see the available commands by typing .commands|");
		index++;// 193
		textosEN.add(index,
				"In this server you have the possibility to customize your own house, I will show you an example|");
		index++;// 194
		textosEN.add(index,
				"This is the House Editor, you can remove the objects from your house and customize it to your liking!!!|");
		index++;// 195
		textosEN.add(index,
				"Remember that having a House is NOT Free and you will have to pay taxes for it, you can pay taxes by visiting the Banker|");
		index++;// 196
		textosEN.add(index, "All this and much more, you will discover it by exploring the server, have fun!!!|");
		index++;// 197
		textosEN.add(index, " Pirutes");
		index++;// 198
		textosEN.add(index, "You cannot exchange this item");
		index++;// 199
		textosEN.add(index, "You cannot throw this object on the ground");
		index++;// 200
		textosEN.add(index, " Oliviolet Plank/s");
		index++;// 201
		textosEN.add(index, "You have reached Incarnam");
		index++;// 202
		textosEN.add(index, "\n<b>.incarnam</b> - Takes you to Incarnam");
		index++;// 203
		textosEN.add(index, "NO-Vip members can only heal 80% of their life");
		index++;// 204
		textosEN.add(index,
				"<b>[GLOBAL EVENT]:</b> A Mysterious Merchant has appeared through the Astrub Grasslands. It will stay <b>5 minutes</b> and then disappear!!!");
		index++;// 205
		textosEN.add(index, "The Mysterious Merchant has disappeared!!!");
		index++;// 206
		textosEN.add(index, "You can only buy 2 items per person");
		index++;// 207
		textosEN.add(index,
				"<b>[GLOBAL EVENT]:</b> The hunting season has started!!! Go to <b>.event</b> and hunt all the Gobballs you can!!! Finishes in <b>5 minutes</b>");
		index++;// 208
		textosEN.add(index,
				"<b>[GLOBAL EVENT]:</b> A mysterious race will take place!!! Type <b>.event</b> and bet on a winner!!! The race starts in <b>3 minutes</b>");
		index++;// 209
		textosEN.add(index, "Larvas' career has begun !!! Who will be the winner???");
		index++;// 210
		textosEN.add(index, "Larvas event has ended!!!");
		index++;// 211
		textosEN.add(index, " Fraternity Currency(s)");
		index++;// 212
		textosEN.add(index, " Currency(s) R0");
		index++;// 213
		textosEN.add(index, " Currency(s) R1");
		index++;// 214
		textosEN.add(index, " Currency(s) R2");
		index++;// 215
		textosEN.add(index, " Currency(s) R3");
		index++;// 216
		textosEN.add(index, " Doplo(s)");
		index++;// 217
		textosEN.add(index, "A los Mapas de RAID solo se permite 1 personaje por IP");
		index++;// 218
	}
}
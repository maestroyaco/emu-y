package variables;

import javax.security.auth.login.LoginException;

public class EjecutarBotDiscord {

	public static void ejecutarBotDiscord(String mensaje, String titulo)
	{
		MiEvento miEvento = new MiEvento(mensaje, titulo);
        BotDiscord bot = new BotDiscord(miEvento);
        try {
        bot.iniciarBot();
        } catch (LoginException e) {
            e.printStackTrace();
        }
	}


}

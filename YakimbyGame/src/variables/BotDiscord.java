package variables;

import java.awt.Color;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;



public class BotDiscord  {
	private static final String BOT_TOKEN = "dfdf";

	private MiEvento miEvento;

    // Constructor que acepta un objeto MiEvento como par�metro
    public BotDiscord(MiEvento miEvento) {
        this.miEvento = miEvento;
    }

    public void iniciarBot() throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(BOT_TOKEN);

        // Agrega el objeto MiEvento como listener
        builder.addEventListeners(miEvento);

        builder.build();
    }

}

class MiEvento extends ListenerAdapter {

	private String titulo;
	private String mensaje;

    // Constructor que acepta un mensaje como par�metro
    public MiEvento(String mensaje, String titulo) {
        this.mensaje = mensaje;
        this.titulo = titulo;
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot listo!");

        // ID del canal al que quieres enviar el mensaje
        String canalId = "1172227317934854144"; // Reemplaza "ID_DEL_CANAL" con el ID real del canal

        // Obt�n el canal por su ID
        TextChannel textChannel = event.getJDA().getTextChannelById(canalId);

        if (textChannel != null) {
            // Env�a un mensaje
            //textChannel.sendMessage(mensaje).queue();
        	// Crea un mensaje embed
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(titulo);
            embedBuilder.setDescription(mensaje);
            embedBuilder.setColor(Color.ORANGE);

         // Set the image URL
            String imageUrl = "http://20.55.64.190/template/img/perfil.jpg"; // Replace with the actual image URL
            embedBuilder.setImage(imageUrl);

            // Env�a el mensaje embed al canal
            textChannel.sendMessageEmbeds(embedBuilder.build()).queue();


        } else {
            System.err.println("No se pudo encontrar el canal con ID: " + canalId);
        }
    }
}

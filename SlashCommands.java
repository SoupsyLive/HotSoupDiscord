package live.soupsy;

import live.soupsy.grouping.Grouping;
import live.soupsy.val.PlayerStats;
import live.soupsy.val.Stats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class SlashCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("split"))
        {
            event.deferReply().queue();
            int groupSize = event.getOption("groupsize").getAsInt();
            String eventTitle = event.getOption("eventname").getAsString();
            System.out.println(groupSize);
            System.out.println(eventTitle);

            Grouping teamEvent = new Grouping(groupSize, eventTitle,true, event.getMember(), event);
        }
        else if (event.getName().equals("endsplit"))
        {
            event.deferReply().queue();
            if(App.hasActiveEvent(event.getMember())) {
                App.removeActiveEvent(event.getMember());
                event.getHook().sendMessage("Event ended").queue();
            }
            else
                event.getHook().sendMessage("You are not currently in charge of an event").queue();


        }
        else if (event.getName().equals("rr"))
        {
            event.deferReply().queue();
            EmbedBuilder embedInitial = new EmbedBuilder().setTitle("Starting ranks for the week")
                    .setColor(Color.yellow)
                    .addField("Loading", "prep", false);

            event.getHook().sendMessageEmbeds(embedInitial.build()).queue(new Consumer<Message>() {
                @Override
                public void accept(Message message) {
                    doRRstuff(message);
                }
            });


        }

    }

    private void doRRstuff(Message embedMessage)
    {
        PlayerStats pStats = new PlayerStats();
        if(App.isLastRoundEmpty())
        {
            // Queue rank input

            StringBuilder everyone = new StringBuilder();
            ArrayList<Stats> newElos;

            newElos = pStats.getCurrentRR(embedMessage);

            newElos.sort(Comparator.comparing(Stats::getRating).reversed());

            for(Stats stat : newElos)
            {
                everyone.append(stat.rankToString()).append("\n");
            }


            EmbedBuilder newEmbed = new EmbedBuilder(embedMessage.getEmbeds().get(0));
            newEmbed.getFields().set(0, new MessageEmbed.Field("", everyone.toString(), false));
            newEmbed.setColor(Color.PINK).setTitle("Starting ranks for the week");
            embedMessage.editMessageEmbeds(newEmbed.build()).queue();
            pStats.setRrs(newElos);
        }
        else
        {

            ArrayList<Stats> newElos;

            newElos = pStats.getCurrentRR(embedMessage);

            pStats.setRrs(App.getLastRoundOfStats());
            ArrayList<Stats> eloDiffs = pStats.getRRchange(newElos);

            System.out.println("PRE-SORT NEW ELO LENGTH == "+ eloDiffs.size());
            EmbedBuilder embed = pStats.changesToEmbed(eloDiffs);
            embedMessage.editMessageEmbeds(embed.build()).queue(message -> pStats.setRrs(newElos));
        }
    }
}

package live.soupsy.val;

import com.google.common.io.Resources;
import live.soupsy.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.socketconnection.jva.ValorantAPI;
import net.socketconnection.jva.player.ValorantPlayer;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class PlayerStats {
    private ValorantAPI api;
    private ArrayList<Stats> rrs;
    private ArrayList<String> names;

    public PlayerStats()
    {
        api = App.valApi;
        rrs = new ArrayList<>();

        String allPlayerString;
        try {
            allPlayerString = Resources.toString(Resources.getResource("valtrackers"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        allPlayerString = allPlayerString.replaceAll("\\r|\\n", "");

        names = sortIntoTags(allPlayerString);

    }

    private ArrayList<String> sortIntoTags(String allPlayersString)
    {
        ArrayList<String> names = new ArrayList<>();
        while (!allPlayersString.isEmpty())
        {
            if(allPlayersString.contains(","))
            {
                names.add(allPlayersString.substring(0,allPlayersString.indexOf(",")));
                allPlayersString = allPlayersString.substring(allPlayersString.indexOf(",")+1);
            }
            else
            {
                names.add(allPlayersString);
                allPlayersString = "";
            }
        }
        return names;
    }

    public ArrayList<Stats> getCurrentRR(Message message) {
        ArrayList<Stats> newRrs = new ArrayList<>();
        for(String name : names)
        {
            System.out.println("Checking stats for "+ name);
            EmbedBuilder newEmbed = new EmbedBuilder(message.getEmbeds().get(0));
            newEmbed.getFields().set(0, new MessageEmbed.Field("", name, false));
            newEmbed.setTitle("Fetching");
            message.editMessageEmbeds(newEmbed.build()).queue();
            Stats player = null;
            String username = name.substring(0, name.indexOf("#"));
            String tag = name.substring(name.indexOf("#")+1);
            try {

                player = new Stats(new ValorantPlayer(api).fetchData(username, tag), username, tag);
                System.out.println("Success on "+name);
            } catch (IOException e) {
                System.out.println("FAILED "+name+ " ONCE");
                try{
                    player = new Stats(new ValorantPlayer(api).fetchData(username, tag), username, tag);
                } catch (IOException e2)
                {
                    System.out.println("FAILED "+name+ " TWICE, GIVING UP");
                    player = new Stats(username, tag, null, -1);
                }

            }

            newRrs.add(player);
        }
        return newRrs;
    }

    public void setRrs(ArrayList<Stats> list)
    {
        rrs = list;
        App.setLastRoundOfStats(list);
    }

    public ArrayList<Stats> getRRchange(ArrayList<Stats> stats)
    {
        ArrayList<Stats> rrDiffs = new ArrayList<>();
        System.out.println("WHEN GETTING RR CHANGE, STAT SIZE IS "+stats.size());
        for(Stats user : stats)
        {
            if(user.getRating() == -1)
            {
                Stats diff = user.clone();
                diff.setRating(-9999);
                rrDiffs.add(diff);
                continue;
            }
            for(Stats user2 : rrs)
            {
                if(user.getName().equals(user2.getName()))
                {
                    Stats diff = user.clone();
                    if(user2.getRating() == -1)
                        diff.setRating(-9999);
                    else
                        diff.setRating(user.getRating() - user2.getRating());
                    rrDiffs.add(diff);
                    break;
                }
            }
        }
        System.out.println("AFTER RR CHANGE, DIFF SIZE IS "+rrDiffs.size());
        return rrDiffs;
    }

    public EmbedBuilder changesToEmbed(ArrayList<Stats> stats)
    {
        System.out.println("PRE SORT - 2 == "+stats.size());
        stats.sort(Comparator.comparing(Stats::getRating));

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("This week's rr gain");

        // check if relative positive or negative gains
        String everythang = "";
        int negatives = 0;
        int underFlow = 0;
        String underFlowString = "";
        System.out.println("STATS LENGTH == "+stats.size());
        for(Stats stat : stats)
        {
            int rrChange = stat.getRating();
            if(rrChange == -9999)
            {
                underFlow++;
                underFlowString += "❌ "+stat.getName() + "\n";
                continue;
            }
            if(rrChange < 0)
            {
                negatives++;
                rrChange *= -1;
                everythang += "⏬ "+rrChange+" : ";

            } else if (rrChange == 0) {
                everythang += "\uD83E\uDE76 "+rrChange+" : ";
            } else
            {
                everythang += "⏫ "+rrChange+" : ";
            }

            everythang += stat.getName() + "\n";
        }

        if(negatives > (stats.size() - underFlow)/2)
            embed.setColor(Color.RED);
        else
            embed.setColor(Color.GREEN);

        embed.addField("", everythang, false);
        if(underFlow > 0)
        {
            embed.addField("Couldn't update", underFlowString, false);
        }
        return embed;
    }



}

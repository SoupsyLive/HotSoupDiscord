package live.soupsy;

import com.google.common.io.Resources;
import live.soupsy.grouping.Grouping;
import live.soupsy.val.Stats;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.socketconnection.jva.ValorantAPI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App extends ListenerAdapter implements EventListener
{
    private static String bot_token;
    private static String val_api_key;
    public static long le_server_id = 1271611977306865674L;
    private static HashMap<Member,Grouping> activeEvents;
    public static ValorantAPI valApi;
    private static ArrayList<Stats> lastRoundOfStats;


    static {
        try {
            bot_token = Resources.toString(Resources.getResource("bot_token"), StandardCharsets.UTF_8);
            val_api_key = Resources.toString(Resources.getResource("valapikey"), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main( String[] args ) throws InterruptedException, IOException {

        // Initial setup
        valApi = new ValorantAPI(val_api_key);
        activeEvents = new HashMap<>();
        lastRoundOfStats = new ArrayList<>();

        // - - - - - - - - DISCORD BOT - - - - - - - -
        JDA bot = JDABuilder.createDefault(bot_token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.customStatus("Mixing up soups"))
                .addEventListeners(new App(), new SlashCommands())
                .enableCache(CacheFlag.MEMBER_OVERRIDES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build().awaitReady();

        // add commands




        bot.updateCommands().addCommands(
                Commands.slash("split", "splits your current VC intro groups of __")
                        .addOption(OptionType.INTEGER, "groupsize", "The amount of people split into each group")
                        .addOption(OptionType.STRING, "eventname", "The name of the event"),
                Commands.slash("endsplit", "finish your ongoing VC split"),
                Commands.slash("rr", "display everyone's RR change since the last check")
        ).queue();
    }

    public static boolean hasActiveEvent(Member user)
    {
        return activeEvents.containsKey(user);
    }
    public static void addActiveEvent(Member user, Grouping grouping) {activeEvents.put(user, grouping);}
    public static void removeActiveEvent(Member user)
    {
        activeEvents.get(user).removeVcs(user.getGuild(), user);
        activeEvents.remove(user);
    }

    public static ArrayList<Stats> getLastRoundOfStats() {
        return lastRoundOfStats;
    }

    public static boolean isLastRoundEmpty() {
        return lastRoundOfStats.isEmpty();
    }

    public static void setLastRoundOfStats(ArrayList<Stats> lastRoundOfStats) {
        App.lastRoundOfStats = lastRoundOfStats;
    }
}

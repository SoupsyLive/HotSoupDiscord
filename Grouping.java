package live.soupsy.grouping;

import live.soupsy.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Grouping {

    private ArrayList<TeamGroup> groups;
    private ArrayList<VoiceChannel> vcs;
    private Category categor;
    private VoiceChannel origionalVoiceChannel;
    private String eventName;

    public Grouping(int groupSize, String title, boolean privateChannels, Member requester, SlashCommandInteractionEvent event)
    {
        if(App.hasActiveEvent(requester))
        {
            System.out.println("ERROR: group already exists");
            return;
        }


        vcs = new ArrayList<>();

        eventName = title;

        makeGroupFromVc(groupSize, requester);

        sendGroupsMessage(event);

        App.addActiveEvent(requester, this);

        // Create Categories

        if(requester.getVoiceState().inAudioChannel())
            origionalVoiceChannel = requester.getVoiceState().getChannel().asVoiceChannel();

        Guild server = requester.getGuild();
        server.createCategory(title).queue(new Consumer<Category>() {
            @Override
            public void accept(Category category)
            {
                makePrivateVcs(server, category, privateChannels);
            }
        });



    }

    private void makePrivateVcs(Guild server, Category category,boolean privateChannels)
    {
        categor = category;
        for(TeamGroup group : groups)
        {
            ChannelAction<VoiceChannel> action = category.createVoiceChannel(group.getName());

            if(privateChannels)
            {
                action.addMemberPermissionOverride(server.getJDA().getSelfUser().getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_MOVE_OTHERS), null);
                EnumSet<Permission> deny = EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT);
                action.addRolePermissionOverride(server.getPublicRole().getIdLong(), null, deny);
                for(Member user : group.getUsers())
                {
                    EnumSet<Permission> allow = EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK);

                    action.addMemberPermissionOverride(user.getIdLong(), allow, null);
                }
            }

            action.queue(new Consumer<VoiceChannel>()
            {
                @Override
                public void accept(VoiceChannel voiceChannel) {
                    vcs.add(voiceChannel);
                    for(Member user : group.getUsers())
                    {
                        if(user.getVoiceState().inAudioChannel())
                            server.moveVoiceMember(user, voiceChannel).queue();
                    }
                }
            });
        }
    }


    private void makeGroupFromVc(int groupSize, Member requester)
    {
        System.out.println("Making Group from VC");

        if(requester.getVoiceState().getChannel() == null)
        {
            System.out.println("ERROR: Requester not in a channel");
            return;
        }


        System.out.println("Getting requester's voice channel");
        VoiceChannel channel = requester.getVoiceState().getChannel().asVoiceChannel();
        System.out.println("Getting everyone in requester's VC");
        ArrayList<Member> personPool = new ArrayList<>(channel.getMembers());

        groups = new ArrayList<>();

        System.out.println("Creating new groups");

        for(int i=0; i< Math.ceil((double) personPool.size()/ (double) groupSize); i++)
        {
            System.out.println("Creating Group");
            TeamGroup group = new TeamGroup(groupSize);
            Random random = new Random();
            for(int j=0; j<groupSize; j++)
            {
                int randInt = random.nextInt(personPool.size());
                group.addUser(personPool.get(randInt));
                System.out.println("Adding person to group: "+personPool.get(randInt).toString());
                personPool.remove(randInt);
                if(personPool.isEmpty())
                {
                    System.out.println("ERROR: Person pool is empty");
                    break;
                }
            }
            System.out.println("Adding new group");
            groups.add(group);
        }
    }

    private void sendGroupsMessage(SlashCommandInteractionEvent event)
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(eventName)
                .setColor(Color.BLUE);

        System.out.println("Creating group messages");
        for(TeamGroup group : groups)
        {
            System.out.println("Group message for "+group.getName());
            String teamList = "";
            for(Member user : group.getUsers())
            {
                teamList += user.getNickname() + "\n";
            }
            embed.addField(group.getName(), teamList, false);
        }

        event.getHook().sendMessageEmbeds(embed.build()).queue();

    }

    public void removeVcs(Guild server, Member requester)
    {
        if(origionalVoiceChannel != null)
        {
            for(TeamGroup group : groups)
            {
                for(Member member : group.getUsers())
                {
                    if(member.getVoiceState().inAudioChannel())
                        server.moveVoiceMember(member, origionalVoiceChannel).queue();
                }
            }
        }

        for(VoiceChannel vc : vcs)
        {
            vc.delete().queue();
        }
        categor.delete().queue();

        App.removeActiveEvent(requester);
    }



}

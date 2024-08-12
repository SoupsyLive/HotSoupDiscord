package live.soupsy.grouping;

import net.dv8tion.jda.api.entities.Member;

public class TeamGroup {
    private int maxTeamSize;
    private Member[] users;
    private String teamName;

    public TeamGroup(int teamSize)
    {
        maxTeamSize = teamSize;
        users = new Member[teamSize];
        teamName = GroupName.generateName();
    }

    public int getMaxTeamSize() {return maxTeamSize;}
    public Member[] getUsers() {return users;}
    public Member getUser(int index)
    {
        if (index <= maxTeamSize)
            return users[index];
        else
            return users[users.length-1];
    }
    public void addUser(Member user)
    {
        for (int i=0; i< users.length; i++)
        {
            if(users[i] == null)
            {
                users[i] = user;
                break;
            }
        }
    }
    public void removeUser(Member user)
    {
        for (int i=0; i< users.length; i++)
        {
            if(users[i] == user)
            {
                users[i] = null;
                break;
            }
        }
    }
    public void removeUser(int index)
    {
        users[index] = null;
    }

    public void setUsers(Member[] uzers)
    {
        if (uzers.length == users.length)
            users = uzers;
    }
    public String getName() {return teamName;}
    public String regenerateName()
    {
        teamName = GroupName.generateName();
        return teamName;
    }
}

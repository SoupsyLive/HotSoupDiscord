package live.soupsy.val;

import net.socketconnection.jva.enums.Rank;
import net.socketconnection.jva.player.ValorantPlayer;
import org.jetbrains.annotations.Nullable;

public class Stats {
    private String name;
    private String tag;
    private Rank rank;
    private int rr;
    private int rating;

    public Stats(ValorantPlayer player, String username, String tag)
    {
        rank = player.getRank();
        rr = player.getElo();
        name = username;
        this.tag = tag;

        rating = ((rank.getId()- 3) * 100) + rr;
    }

    public Stats(String name, String tag, @Nullable Rank rank, int rr)
    {
        this.name = name;
        this.tag = tag;
        this.rank = rank;
        this.rr = rr;

        if(rank == null)
            this.rating = -1;
        else
            this.rating = ((rank.getId()- 3) * 100) + rr;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        rating = ((rank.getId()- 3) * 100) + rr;
    }

    public int getRr() {
        return rr;
    }

    public void setRr(int rr) {
        this.rr = rr;
        rating = ((rank.getId()- 3) * 100) + rr;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {this.rating = rating;}

    @Override
    public Stats clone()
    {
        return new Stats(this.name, this.tag, this.rank, this.rr);
    }

    public String rankToString()
    {
        if(this.rank == null)
            return "❌" + " " + this.name;

        String eloSticker;
        switch (this.rank)
        {
            case IRON_1 -> eloSticker = "⬛ <:RankOne:1272370552212029471>";
            case IRON_2 -> eloSticker = "⬛ <:RankTwo:1272370542841954385>";
            case IRON_3 -> eloSticker = "⬛ <:RankThree:1272370531022143540>";
            case BRONZE_1 -> eloSticker = "\uD83D\uDFEB <:RankOne:1272370552212029471>";
            case BRONZE_2 -> eloSticker = "\uD83D\uDFEB <:RankTwo:1272370542841954385>";
            case BRONZE_3 -> eloSticker = "\uD83D\uDFEB <:RankThree:1272370531022143540>";
            case SILVER_1 -> eloSticker = "⬜ <:RankOne:1272370552212029471>";
            case SILVER_2 -> eloSticker = "⬜ <:RankTwo:1272370542841954385>";
            case SILVER_3 -> eloSticker = "⬜ <:RankThree:1272370531022143540>";
            case GOLD_1 -> eloSticker = "\uD83D\uDFE8 <:RankOne:1272370552212029471>";
            case GOLD_2 -> eloSticker = "\uD83D\uDFE8 <:RankTwo:1272370542841954385>";
            case GOLD_3 -> eloSticker = "\uD83D\uDFE8 <:RankThree:1272370531022143540>";
            case PLATINUM_1 -> eloSticker = "\uD83D\uDFE6 <:RankOne:1272370552212029471>";
            case PLATINUM_2 -> eloSticker = "\uD83D\uDFE6 <:RankTwo:1272370542841954385>";
            case PLATINUM_3 -> eloSticker = "\uD83D\uDFE6 <:RankThree:1272370531022143540>";
            case DIAMOND_1 -> eloSticker = "\uD83D\uDFEA <:RankOne:1272370552212029471>";
            case DIAMOND_2 -> eloSticker = "\uD83D\uDFEA <:RankTwo:1272370542841954385>";
            case DIAMOND_3 -> eloSticker = "\uD83D\uDFEA <:RankThree:1272370531022143540>";
            case IMMORTAL_1 -> eloSticker = "\uD83D\uDFE5 <:RankOne:1272370552212029471>";
            case IMMORTAL_2 -> eloSticker = "\uD83D\uDFE5 <:RankTwo:1272370542841954385>";
            case IMMORTAL_3 -> eloSticker = "\uD83D\uDFE5 <:RankThree:1272370531022143540>";
            case RADIANT -> eloSticker = "\uD83C\uDFC6";
            default -> eloSticker = "⭕";
        }

        return eloSticker + " " + this.name;
    }
}

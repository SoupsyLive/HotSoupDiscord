package live.soupsy.grouping;

import java.util.Random;

public class GroupName {
    public static String generateName(int char1, int char2)
    {
        return intToString(char1, true) + " " + intToString(char2, false);
    }
    public static String generateName()
    {
        Random rand = new Random();
        return intToString(rand.nextInt(26)+1, true) + " " + intToString(rand.nextInt(26)+1, false);
    }
    private static String intToString(int seed, boolean adj)
    {
        if(adj)
        return switch (seed) {
            case 1 -> "Absurd";
            case 2 -> "Balanced";
            case 3 -> "Colorful";
            case 4 -> "Dazzling";
            case 5 -> "Edgy";
            case 6 -> "Famous";
            case 7 -> "Genuine";
            case 8 -> "Handsome";
            case 9 -> "Idolized";
            case 10 -> "Jinxed";
            case 11 -> "Klutzy";
            case 12 -> "Large";
            case 13 -> "Magnetic";
            case 14 -> "Naked";
            case 15 -> "Obnoxious";
            case 16 -> "Praiseworthy";
            case 17 -> "Qualified";
            case 18 -> "Real";
            case 19 -> "Sarcastic";
            case 20 -> "Tasty";
            case 21 -> "Unparalleled";
            case 22 -> "Valuable";
            case 23 -> "Wasteful";
            case 24 -> "Xenodochial";
            case 25 -> "Yappy";
            case 26 -> "Zesty";
            default -> "Questionable";
        };
        else
            return switch (seed) {
                case 1 -> "Amateurs";
                case 2 -> "Batteries";
                case 3 -> "Customers";
                case 4 -> "Daffodils";
                case 5 -> "Ecstasies";
                case 6 -> "Facades";
                case 7 -> "Gamblers";
                case 8 -> "Hassles";
                case 9 -> "Idols";
                case 10 -> "Jackpots";
                case 11 -> "Kaleidoscopes";
                case 12 -> "Ladies";
                case 13 -> "Men";
                case 14 -> "Narrators";
                case 15 -> "Objects";
                case 16 -> "Packages";
                case 17 -> "Questions";
                case 18 -> "Red Flags";
                case 19 -> "Scholarships";
                case 20 -> "Therapists";
                case 21 -> "Unessentials";
                case 22 -> "Vanillas";
                case 23 -> "Wives";
                case 24 -> "Xenogenesis";
                case 25 -> "Yardsticks";
                case 26 -> "Zodiacs";
                default -> "Husbands";
            };
    }

}

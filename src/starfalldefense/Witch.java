package starfalldefense;

public class Witch extends Enemy{
    public Witch(){
        // can=67, hız=75, zırh=0, ödül=15, hasar=10
        super(50, 75, 0, 15, 10, 
              "/starfalldefense/assets/images/witch.png",
              "/starfalldefense/assets/images/ice_witch.png");
    }
}
package starfalldefense;

public class Knight extends Enemy {
    public Knight(){
        // can=100, hız=25, zırh=100, ödül=20, hasar=20
        super(75, 25, 100, 20, 20,
              "/starfalldefense/assets/images/knight.png",
              "/starfalldefense/assets/images/ice_knight.png");
    }
}

package starfalldefense;

public class Wizard extends Enemy {
    public Wizard(){
        // can=67, hız=50, zırh=0, ödül=10, hasar=10
        super(50, 50, 0, 10, 10,
              "/starfalldefense/assets/images/wizard.png",
              "/starfalldefense/assets/images/ice_wizard.png");
    }
}

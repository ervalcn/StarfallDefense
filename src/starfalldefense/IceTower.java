package starfalldefense;
import java.util.List;
import javafx.scene.layout.Pane;

// Tower dan kalıtım alır
public class IceTower extends Tower{
    public IceTower(double x, double y){
        // x, y, menzil, hasar, maliyet, resim_yolu
        super(x, y, 140, 15, 70, "/starfalldefense/assets/images/tower_ice.png");
    }
    
    public void attack(java.util.List<Enemy> enemies, Pane gamePane){
        if (!canShot()) return;
        
        Enemy target = null;
        double minDistance = Double.MAX_VALUE;
        
        for(Enemy enemy : enemies){
            if(isEnemyInRange(enemy)){
                double distance = Math.sqrt(Math.pow(getX_position()- enemy.getX_position(),2)+
                                            Math.pow(getY_position()- enemy.getY_position(), 2));
                
                if(distance < minDistance){
                    minDistance = distance;
                    target = enemy;
                }
            }
        }
        
        if(target != null){
            double rawDamage = this.baseDamage;
            double armorReduction = 1.0 - (target.getArmor() / (target.getArmor() + 100.0));
            int netDamage = (int) (rawDamage * armorReduction);
            
            // hasar uygulanır
            target.receiveDamage(netDamage);
            //yavaşlatma
            target.setSlow(3);
            
            showHitEffect(gamePane, target, "effect_ice.png");
            
            Logger.log("Kule '" + getTowerId() + "', '" + target.getId() + "'i hedefledi; Net Hasar " + netDamage +
                       ", Yavaslatma %50 (3 sn) uygulandi. Kalan Can: " + target.getHealth() + "/" + target.getMaxHealth() + ".");
        }
        
    }
}

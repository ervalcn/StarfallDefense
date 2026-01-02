package starfalldefense;

import java.util.List;
import javafx.scene.layout.Pane;

// Tower ı  kalıtım alır
public class LightningTower extends Tower {
    
    public LightningTower(double x, double y) {
        // x, y, menzil, hasar, maliyet, resim yolu
        super(x, y, 140, 10, 50, "/starfalldefense/assets/images/tower_lightning.png");
    }
    
    @Override
    public void attack(List<Enemy> enemies, Pane gamePane) {
        if (!canShot()) return; // atış kontrolü
        
        Enemy target = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Enemy enemy : enemies) {
            if (isEnemyInRange(enemy)) { // düşman menzilde mi?
                double distance = Math.sqrt(Math.pow(getX_position() - enemy.getX_position(), 2) +
                                            Math.pow(getY_position() - enemy.getY_position(), 2));
                
                // en yakın düşmanı bul.
                if (distance < minDistance) {
                    minDistance = distance;
                    target = enemy;
                }
            }
        }
        
        if (target != null) {
            Logger.log("Kule '" + getTowerId() + "', '" + target.getId() + "'i hedefledi (oncelik: uss'e en yakin).");
            
            double rawDamage = this.baseDamage;
            String logDetail = "Okcu atisi: Taban " + (int)rawDamage;
            
            if (target instanceof Knight) {
                rawDamage *= 0.50; // şövalte ise hasarı %50 azalt
                logDetail += " -> Zirhli cezasi %50 -> " + (int)rawDamage;
            }
            
            // zırh kontrol formülü
            double armorReduction = 1.0 - (target.getArmor() / (target.getArmor() + 100.0));
            int netDamage = (int) (rawDamage * armorReduction);
            
            logDetail += "; Zirh formulu ile Net Hasar " + netDamage + ".";
            
            target.receiveDamage(netDamage);
            
            showHitEffect(gamePane, target, "effect_lightning.png");
            
            // loga yazılar eklenir
            logDetail += " Kalan Can: " + target.getHealth() + "/" + target.getMaxHealth() + ".";
            Logger.log(logDetail);
        }
    }
}
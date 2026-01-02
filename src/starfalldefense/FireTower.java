package starfalldefense;
import java.util.List;
import javafx.scene.layout.Pane;

// Tower ı kalıtım alır
public class FireTower extends Tower {
    public FireTower(double x, double y) {
        // x, y, menzil, hasar, maliyet, resim_yolu) 
        super(x, y, 140, 20, 75, "/starfalldefense/assets/images/tower_fire.png");
    }

    @Override
    public void attack(List<Enemy> enemies, Pane gamePane) {
        if (!canShot()) return;

        Enemy primaryTarget = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            if (enemy instanceof Witch) continue;

            if (isEnemyInRange(enemy)) {
                double distance = Math.sqrt(Math.pow(getX_position() - enemy.getX_position(), 2) +
                        Math.pow(getY_position() - enemy.getY_position(), 2));

                if (distance < minDistance) {
                    minDistance = distance;
                    primaryTarget = enemy;
                }
            }
        }

        if (primaryTarget != null) {
            Logger.log("Kule '" + getTowerId() + "' ates etti. Alan Vurusu: merkez " + primaryTarget.getId() + ".");

            showHitEffect(gamePane, primaryTarget, "effect_fire.png");
            
            double splashRadius = 50.0; // yarıçap

            for (Enemy enemyInList : enemies) {
                if (enemyInList instanceof Witch) continue; // uçan düşmanı vuramaz

                double dx = primaryTarget.getX_position() - enemyInList.getX_position();
                double dy = primaryTarget.getY_position() - enemyInList.getY_position();
                double distanceToTarget = Math.sqrt(dx * dx + dy * dy);

                if (distanceToTarget <= splashRadius) {
                    double rawDamage = this.baseDamage;
                    double armorReduction = 1.0 - (enemyInList.getArmor() / (enemyInList.getArmor() + 100.0));
                    int netDamage = (int) (rawDamage * armorReduction);

                    enemyInList.receiveDamage(netDamage);
                    
                    showHitEffect(gamePane, enemyInList, "effect_fire.png");

                    Logger.log(" - " + enemyInList.getId() + " net hasar " + netDamage);
                }
            }
        }
    }
}
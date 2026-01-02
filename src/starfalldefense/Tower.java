package starfalldefense;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.Pane;

//soyut sınıf
public abstract class Tower {
    // ID Sistemi
    private static int idCounter = 0;
    protected String towerId;

    protected double x_position;
    protected double y_position;
    protected int range;
    protected int damage;
    protected int cost;
    
    // atış hızı kontrolü
    protected double shotRateSeconds = 1.0; //  1 saniye
    protected long lastShotTime = 0;

    // görsel + nesne
    protected javafx.scene.Node visualNode;
    protected int baseDamage; 

    // Constructor
    public Tower(double x, double y, int range, int damage, int cost, String imagePath) {
        this.x_position = x;
        this.y_position = y;
        this.range = range;
        this.damage = damage;
        this.baseDamage = damage;
        this.cost = cost;
        this.lastShotTime = 0;

        String trName = "Kule";
        if (this instanceof LightningTower) trName = "OkcuKulesi";
        else if (this instanceof FireTower) trName = "TopcuKulesi";
        else if (this instanceof IceTower) trName = "BuzKulesi";
        this.towerId = trName + "-ID" + String.format("%03d", ++idCounter);

        // Görsel yükleme
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            ImageView view = new ImageView(img);
            
            // Boyutlandırma
            view.setFitWidth(130);
            view.setFitHeight(130);
            
            this.visualNode = view;
            
            // görseller bulunamazsa
        } catch (Exception e) {
            System.out.println("HATA: Kule resmi bulunamadi: " + imagePath);
            // Yedek kareyi de büyütelim
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(130, 130);
            rect.setFill(javafx.scene.paint.Color.MAGENTA);
            this.visualNode = rect;
        }
        
        // merkezleme
        this.visualNode.setTranslateX(x - 65);
        this.visualNode.setTranslateY(y - 65);
        // Atış Hızı Ayarları
        if (this instanceof LightningTower) this.shotRateSeconds = 1.0;
        else if (this instanceof FireTower) this.shotRateSeconds = 3.0;
        else if (this instanceof IceTower) this.shotRateSeconds = 2.0;
    }

    // ateş etme aralığı ayarlaması
    protected boolean canShot() {
        long currentTime = System.currentTimeMillis();
        long waitingMillis = (long) (this.shotRateSeconds * 1000);
        if(currentTime > this.lastShotTime + waitingMillis){
            this.lastShotTime = currentTime;
            return true;
        }
        return false;
    }
    
    // düşman menzil içerisinde mi
    protected boolean isEnemyInRange(Enemy enemy){
        double distance = Math.sqrt(Math.pow(x_position - enemy.getX_position(), 2) + 
                                    Math.pow(y_position - enemy.getY_position(), 2));
        return distance <= this.range;
    }
    
    // düşman hasar aldığında çıkan efektler
    protected void showHitEffect(Pane gamePane, Enemy target, String effectName) {
        try {

            Image img = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/" + effectName));
            ImageView effectView = new ImageView(img);

            effectView.setFitWidth(100);
            effectView.setFitHeight(100);
          
            effectView.setTranslateX(target.getVisualNode().getLayoutX()); 
            effectView.setTranslateY(target.getVisualNode().getLayoutY());

            gamePane.getChildren().add(effectView);
   
            FadeTransition ft = new FadeTransition(Duration.millis(500), effectView);
            ft.setFromValue(1.0); // Tam görünür
            ft.setToValue(0.0);   // Tam şeffaf
            ft.setOnFinished(e -> gamePane.getChildren().remove(effectView)); // Bitince sil
            ft.play();
            
        } catch (Exception e) {
        }
    }

    // getterlar
    public javafx.scene.Node getVisualNode() { return visualNode; }
    public double getX_position() { return x_position; }
    public double getY_position() { return y_position; }
    public String getTowerId() { return towerId; }
    
    // polimorfizm bu kısımda yapılıyor
    // kule saldırılarını alt sınıflar belirler
    public abstract void attack(List<Enemy> enemies, Pane gamePane);
}
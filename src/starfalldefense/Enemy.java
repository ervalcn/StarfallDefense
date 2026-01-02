package starfalldefense;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// abstract -> soyut sınıf
// tüm düşmanlar için ortak olan özellikleri bu sınıfta tanımladık
public abstract class Enemy {
    
    // KIMLIK 
    private static int idCounter = 100; // ID100, ID101 ...
    protected String enemyId;
    
    // OZELLIKLER
    // miras alan sınıflar da görebilsin diye protected
    protected int health; // mevcut can
    protected int maxHealth; // max can
    protected double speed; // hız
    protected int armor; // zırh
    protected int goldReward; // altın ödül
    protected int baseDamage; // üs hasarı
    
    // KONUM
    protected double x_position; 
    protected double y_position;
    protected int currentWaypointIndex = 0; // yolun kaçıncı durağında
    
    // EFEKTLER
    protected boolean slowEffect; //buz kulesi yavaşlattı mı?
    protected long slowEndTime; // yavaşlatma süresi
    
    // GORSELLEŞTİRME
    protected Node visualNode; // ekranda görünen kutu (resim+bar)
    protected Rectangle healthBarRect; // yeşil can barı
    private final double BAR_WIDTH = 40.0; // barın genişliği
    
    protected ImageView characterView; // karakter resmi
    protected Image normalImage; // normal hali
    protected Image frozenImage; // buz büyüsündeki hali

    // constructor
    public Enemy(int health, double speed, int armor, int goldReward, int baseDamage, String normalPath, String frozenPath) {
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.armor = armor;
        this.goldReward = goldReward;
        this.baseDamage = baseDamage;
        
        // başlangıç değerleri
        this.x_position = 0.0;
        this.y_position = 100.0;
        this.slowEffect = false;
        this.slowEndTime = 0;
        
        // ID oluşturma
        this.enemyId = this.getClass().getSimpleName() + "-ID" + (++idCounter);

        // Görsel oluşturma
        Node characterNode;
        
        if (normalPath != null) {
            try {
                // resim yükle
                normalImage = new Image(getClass().getResourceAsStream(normalPath));
                characterView = new ImageView(normalImage);
                characterView.setFitWidth(80); 
                characterView.setFitHeight(80);
                
                // buz büyüsü etkisindeyken donmuş resim yüklenir
                if (frozenPath != null) {
                    frozenImage = new Image(getClass().getResourceAsStream(frozenPath));
                }
                
                characterNode = characterView;
            } catch (Exception e) {
                // resim bulunamadıysa kırmızı kareleri yükle
                characterNode = new Rectangle(80, 80, Color.RED);
            }
        } else {
            characterNode = new Rectangle(80, 80, Color.RED);
        }
        
        // Sağlık barı oluşturma
        Rectangle bgBar = new Rectangle(BAR_WIDTH, 7, Color.RED);
        this.healthBarRect = new Rectangle(BAR_WIDTH, 7, Color.LIGHTGREEN);
        //bar sola yaslı kalır
        Group barGroup = new Group(bgBar, this.healthBarRect);

        // resim ve bar üst üste gelir
        StackPane container = new StackPane();
        container.getChildren().addAll(characterNode, barGroup);

        // bar karakterin tepesinde durur
        StackPane.setAlignment(barGroup, Pos.TOP_CENTER);
        barGroup.setTranslateY(-15);

        this.visualNode = container;
    }
    
    // HAREKET
    public void move(){
        double currentSpeed = this.speed;
        
        // Buz kulesi büyüsü etkisi altındayken
        if(this.isSlowEffect() && System.currentTimeMillis() < this.slowEndTime){
            currentSpeed *= 0.50; //hız yarıya düşer
            
            // eğer buz büyüsü varsa görseli mavi olanla değiştir
            if (frozenImage != null && characterView != null) {
                characterView.setImage(frozenImage);
            }
            
        } else {
            this.slowEffect = false;
            // görsel normale döner
            if (normalImage != null && characterView != null) {
                characterView.setImage(normalImage);
            }
        }
        
        // PATH in sonuna kadar gidebilmesi için
        if(currentWaypointIndex >= StarfallDefense.PATH.length - 1){
            this.x_position += currentSpeed * 0.016; //sağa gitmeye devam et
            return;
        }
        
        // bir sonraki hedef nokta
        Point2D targetPoint = StarfallDefense.PATH[currentWaypointIndex + 1];

        // mesafe ve yön hesabı
        double dx = targetPoint.getX() - this.x_position;
        double dy = targetPoint.getY() - this.y_position;
        double distance = Math.sqrt(dx*dx + dy*dy);

        double moveDistance = currentSpeed * 0.016; // bir karede gidilecek mesafe
        
        // hedefe ulastı mi?
        if(distance <= moveDistance){
            this.x_position = targetPoint.getX();
            this.y_position = targetPoint.getY();
            currentWaypointIndex++;
        } else { // hesefe ilerle
            this.x_position += (dx/distance)*moveDistance;
            this.y_position += (dy/distance)*moveDistance;
        }
    }

    // buz kulesi tarafından çağrılır
    public void setSlow(int durationOfSlow){
        this.slowEffect = true;
        this.slowEndTime = System.currentTimeMillis() + (durationOfSlow * 1000);
    }
    
    // kuleler tarafından çağırlır, hasarı uygula, barı günceller
    public void receiveDamage(int netDamage){
        this.health -= netDamage;
        if(this.health < 0) this.health = 0;
        updateHealthBarVisual(); 
    }
    
    // sağlık barının genişliğini kalan cana göre ayarlar
    private void updateHealthBarVisual() {
        if (healthBarRect != null) {
            double percent = (double) this.health / this.maxHealth;
            healthBarRect.setWidth(BAR_WIDTH * percent);
        }
    }
    
    // mntıksal konumu dışarıdan düzeltmek için
    public void setPosition(double x, double y) {
        this.x_position = x;
        this.y_position = y;
    }
    
    // diğer sınıflardan erişilebilmesi için getterlar
    public boolean isDead(){ return this.getHealth() <= 0; }
    public double getX_position(){ return this.x_position; }
    public double getY_position() { return y_position; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getGoldReward() { return goldReward; }
    public int getBaseDamage() { return baseDamage; }
    public boolean isSlowEffect() { return slowEffect; }
    public Node getVisualNode(){ return this.visualNode; }
    public String getId(){ return enemyId; }
    public int getArmor(){ return armor; }
}
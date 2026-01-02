package starfalldefense;

import javafx.animation.AnimationTimer; // oyunn döngüsü
import javafx.application.Application; // JavaFX başlatmak için
import javafx.application.Platform; // uygulamayı kapatmak için
import javafx.geometry.Insets; // arayüz elemanları kenar boşlukları için
import javafx.geometry.Point2D; // (x,y) koordinatları
import javafx.geometry.Pos; // elemanları sağ, sol hizalamak için
import javafx.scene.Scene; // tüm görsel elemanların içinde bulunduğu ana sahne
import javafx.scene.control.Button; // tıklanabilir butonlar 
import javafx.scene.control.Label; // ekrana yazı yazmak için
import javafx.scene.control.ListView; // sağdaki kaydırılabilir simülasyon günlüğü
import javafx.scene.layout.Background; // panellerin arka planını yönetmek için
import javafx.scene.layout.BackgroundFill; // arka planı düz renkle boyamak için
import javafx.scene.layout.CornerRadii; // arka plan köşelerini yuvarlamak için
import javafx.scene.layout.HBox; // elemanları yan yana (yatay) dizen kutu
import javafx.scene.layout.Pane; // elemanları koordinatla serbestçe yerleştirmek için
import javafx.scene.layout.StackPane; // elemanları üst üste (katmanlı) dizmek için
import javafx.scene.layout.VBox; // elemanları alt alta dikey dizen kutu
import javafx.scene.paint.Color; // renkleri tanımlamak için
import javafx.scene.shape.Rectangle; // kare veya dikdörtgen şekiller çizmek için
import javafx.scene.text.Font; // yazı tiplerini ve boyutlarını ayarlamak için
import javafx.scene.text.FontWeight; // yazının kalınlığı ayarlamak için 
import javafx.stage.Stage; // uygulamanın ana penceresi
import javafx.scene.image.ImageView; // resimleri ekranda göstermek için kullanılır
import javafx.scene.layout.BackgroundImage; // bir resmi arka plan olarak ayarlamak
import javafx.scene.layout.BackgroundRepeat; // arka plan resminin tekrar edip etmediği
import javafx.scene.layout.BackgroundPosition; //arka plan resminin hizasını ayarlamak
import javafx.scene.layout.BackgroundSize; // arka plan resminin boyutunu ayarlamak
import javafx.scene.image.Image; // resim dosyasını hafızaya yüklemek
import javafx.scene.shape.Polyline; // yolu çizgilerle göstermek için
import javafx.scene.text.TextAlignment; // metinleri ortalamak veya yaslamak için
import javafx.scene.media.Media; // müzik dosyasını yüklemek için
import javafx.scene.media.MediaPlayer; // uzun müzikleri çalar ve kontrol eder
import java.io.File; // dosya işlemleri için
import javafx.scene.media.AudioClip; // kısa ses efektlerini anlık verir

import java.util.ArrayList;
import java.util.List;

public class StarfallDefense extends Application {

    private HBox mainLayout; // ekranı ikiye bölen ana kapsayıcı
    private Pane gamePane; // oyunun oynandığı kısım
    private VBox logPane; // simülasyon günlüğü kısmı
    
    private Label moneyLabel; // para miktarını gösteren yazı
    private Label healthLabel; // can miktarını gösteren yazı
    private ImageView waveImage; // ortadaki dalga1 dalga2 görselini tutan resim kutusu
    private ListView<String> logView;  // sağ paneldeki akan yazıların listesi

    private List<Tower> towers = new ArrayList<>(); // towerlar
    private List<Enemy> enemies = new ArrayList<>(); // enemyler
    
    private AnimationTimer gameLoop; // saniyede 60 kez çalışan oyunun ana motoru
    private MediaPlayer musicPlayer; // arka planda müzik oynatır
    
    private int money = 200; // cebindeki güncel para
    private int health = 100; // kalenin canı
    private int currentWave = 1; // şuan dalga1 mi dalga2 mi
    
    private String selectedTowerType = "Lightning"; // oyuncu hangi butona bastı
    
    private List<String> creationQueue = new ArrayList<>(); // sırada bekleyen düşman listesi
    private long lastCreationTime = 0; // en son düşmanın doğma süresi
    private boolean DEBUG_MODE = false; // PATH ve range için debug

    // oyun haritasındaki PATH köşe koordinatları
    public static final Point2D[] PATH = {
        new Point2D(0, 145), 
        new Point2D(250, 145),  
        new Point2D(250, 350),
        new Point2D(550, 350), 
        new Point2D(550, 545), 
        new Point2D(845, 545), 
        new Point2D(845, 720)
    };
    
    // Towerlerın koyulacağı bölmeler
    public static final Point2D[] TOWER_SPOTS = {
        new Point2D(120, 250), 
        new Point2D(425, 240),
        new Point2D(650, 450),
        new Point2D(360, 450),
        new Point2D(800, 450), 
        new Point2D(700, 650)
    };
   
    // pencere başlığını yazar
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Starfall Defense - Proje II");

        // hikaye sahnesini oluşturma
        // parametre olarak this -> StarfallDefense ana sınıfı
        // primaryStage -> pencere veriliyor
        StoryScene story = new StoryScene(this, primaryStage);
        // kitap rün... çizilir
        story.show();
    }

    // storyScene tarafından çağırılır
    public void openMainMenu(Stage stage){
        showMainMenu(stage);
    }

    private void showMainMenu(Stage stage){
        // buton ve logo yerini kendimiz hesaplamak için Pane kullandık
        Pane menuRoot = new Pane(); 
        menuRoot.setPrefSize(1024, 768);

        // arka plan
        try {
            javafx.scene.image.Image bgImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/menu_background.png"));
            BackgroundImage bg = new BackgroundImage(
                bgImg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1024, 768, false, false, false, false) 
            );
            menuRoot.setBackground(new Background(bg));
        } catch (Exception e) {
            menuRoot.setStyle("-fx-background-color: #222;"); 
        }
        
        String imageButtonStyle = "-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-border-width: 0;";

        // başlık kısmı
        // Starfall Defense yazılı logoyu yükler
        ImageView titleView = new ImageView();
        try {
            javafx.scene.image.Image titleImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/starfallDefenseOpening.png"));
            titleView.setImage(titleImg);
            titleView.setFitWidth(750);
            titleView.setPreserveRatio(true);
        } catch (Exception e) { }

        // resim genişliği
        titleView.setLayoutX((1024 - 750) / 2); 
        titleView.setLayoutY(-120);

        // oyunu başlatma butonu
        Button btnStart = new Button();
        try {
            javafx.scene.image.Image playImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/play_button.png"));
            ImageView playView = new ImageView(playImg);
            playView.setFitWidth(350);
            playView.setPreserveRatio(true);
            btnStart.setGraphic(playView); 
            btnStart.setStyle(imageButtonStyle); 
        } catch (Exception e) { btnStart.setText("OYUNA BASLA"); }
        
        btnStart.setOnAction(e -> startGame(stage));

        btnStart.setLayoutX((1024 - 350) / 2); 
        btnStart.setLayoutY(350);

        // oyundan çıkış butonu
        Button btnExit = new Button();
        try {
            javafx.scene.image.Image exitImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/exit_button.png"));
            ImageView exitView = new ImageView(exitImg);
            exitView.setFitWidth(300); 
            exitView.setPreserveRatio(true); // yükseklik ve eni orantılı ayarlar
            btnExit.setGraphic(exitView); 
            btnExit.setStyle(imageButtonStyle); 
        } catch (Exception e) { btnExit.setText("CIKIS"); }
        
        btnExit.setOnAction(e -> Platform.exit());

        btnExit.setLayoutX((1024 - 300) / 2); 
        btnExit.setLayoutY(480); 

        // logo, başlat ve çıkış butonlarını menuRoot (pane) üzerine ekler
        menuRoot.getChildren().addAll(titleView, btnStart, btnExit);
        
        Scene menuScene = new Scene(menuRoot, 1024, 768);
        stage.setScene(menuScene);
        stage.show();
    }

    private void startGame(Stage stage){
        // eski log kayıtları silinir, yeni temiz sayfa açılır
        Logger.clearLog();
        
        // ekran ikiye bölünür
        // OYUN EKRANI + LOG
        mainLayout = new HBox(0);

        // oyun alanı
        gamePane = new Pane();
        gamePane.setPrefSize(1024, 768);
        gamePane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        
        // harita çizilir
        drawMap();
        // oyun arayüzü kurulur
        setupGameUI();
 
        // log paneli
        logPane = new VBox(10); // içindeki yazılar arası 10px boşluk 
        logPane.setPrefWidth(300); // genişlik 300px
        logPane.setPadding(new Insets(15)); // kenarlardan 15px iç boşluk
        logPane.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 50), CornerRadii.EMPTY, Insets.EMPTY)));
        
        // log listesi
        setupLogUI();
        
        // panelleri birleştirir
        // sol tarafa 'gamepane' sağ tarafa 'logpane'
        mainLayout.getChildren().addAll(gamePane, logPane);
        
        // sahne oluşur -> (1324x768)
        Scene gameScene = new Scene(mainLayout, 1324, 768);
        // bu satırda ekrandaki kitap ve hikaye görüntüsü gider, asıl oyun ekranı gelir
        stage.setScene(gameScene);
        
        // oyun döngüsü başlar, saniyede 60 kez çalışan zamanlayıcı
        startGameLoop();
        // dalga1 başlar
        startWave1();
        
        // arka plan müziği çalmaya başlar
        playBackgroundMusic(); 
        
        // ilk log mesajı ekrana ve dosyaya yazar
        logToScreen("Simulasyon Basladi.");

        // oyuncu oyun alanına tıkladığında bu kısım çalışır
        gamePane.setOnMouseClicked(event -> {
            double y = event.getY();
            // kule inşa
            if (y < 680) {
                addTower(event.getX(), event.getY());
            }
        });
    }

    // oyun sahnesini çizen metot
    private void drawMap() {
        try {
            // oyun zemini yüklenir
            javafx.scene.image.Image mapImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/terrain1.png"));
            javafx.scene.image.ImageView backgroundView = new javafx.scene.image.ImageView(mapImg);
            backgroundView.setFitWidth(1024);
            backgroundView.setFitHeight(768);
            // resim sahneye eklenir
            gamePane.getChildren().add(backgroundView);

            // kale resmi yüklenir
            javafx.scene.image.Image castleImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/castle.png")); // Uzantı .jpg ise değiştirin
            javafx.scene.image.ImageView castleView = new javafx.scene.image.ImageView(castleImg);
            castleView.setFitWidth(300);
            castleView.setPreserveRatio(true);
            castleView.setX(830); 
            castleView.setY(460); 
            gamePane.getChildren().add(castleView);
            
        } catch (Exception e) {
            // bulunamazsa oyun çökmesin diye gri bir kare koyulur
            System.out.println("Harita veya Kale resmi bulunamadi!");
            gamePane.setStyle("-fx-background-color: gray;");
        }

        // kule slotları
        try {
            // toprak zemin görseli
            javafx.scene.image.Image slotImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/tower_place.png"));
            
            // TOWER_SPOTS içindeki tüm spotlar için döngü
            for (Point2D spot : TOWER_SPOTS) {
                javafx.scene.image.ImageView slotView = new javafx.scene.image.ImageView(slotImg);

                slotView.setFitWidth(60);
                slotView.setFitHeight(60);
               
                slotView.setX(spot.getX() - 30);
                slotView.setY(spot.getY() - 30);

                // hafif şeffaf
                slotView.setOpacity(0.9); 
                
                gamePane.getChildren().add(slotView);
            }
        } catch (Exception e) {
            System.out.println("Kule yeri resmi (tower_place.png) bulunamadi!");
        }
        
        // hata ayıklama
        if (DEBUG_MODE) {
            // kırmızı çizgi -> düşman yolu
            Polyline debugPath = new Polyline();
            debugPath.setStroke(Color.RED);
            debugPath.setStrokeWidth(3); 
            
            for (Point2D p : PATH) {
                debugPath.getPoints().addAll(p.getX(), p.getY());
            }
            gamePane.getChildren().add(debugPath);
            
            // sarı alan -> yasak bölge
            Polyline exclusionZone = new Polyline();
            exclusionZone.setStroke(Color.rgb(255, 255, 0, 0.3)); // yarı saydam
            exclusionZone.setStrokeWidth(60); 
            exclusionZone.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            
            // sarı alana kule koymak yasak
            for (Point2D p : PATH) {
                exclusionZone.getPoints().addAll(p.getX(), p.getY());
            }
            gamePane.getChildren().add(exclusionZone);
        }
    }

    private void setupGameUI() {
        // para göstergesi
        javafx.scene.image.ImageView coinIcon = new javafx.scene.image.ImageView();
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/coin.png"));
            coinIcon.setImage(img);
            coinIcon.setFitHeight(40); 
            coinIcon.setPreserveRatio(true);
        } catch (Exception e) { }

        // para miktarını gösteren yazı
        moneyLabel = new Label("" + money);
        moneyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        moneyLabel.setTextFill(Color.web("#00CCFF")); 

        // ikon ve yazıyı yan yana koyan kutu
        HBox moneyBox = new HBox(10); 
        moneyBox.setAlignment(Pos.CENTER_LEFT);
        moneyBox.getChildren().addAll(coinIcon, moneyLabel);

        // kutunun görünümü (yarı saydam)
        moneyBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 5px 15px; -fx-background-radius: 10; -fx-border-color: #00CCFF; -fx-border-width: 2; -fx-border-radius: 10;");
        moneyBox.setLayoutX(20);
        moneyBox.setLayoutY(20);

        // dalga göstergesi
        waveImage = new javafx.scene.image.ImageView(); 
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/wave1.png"));
            waveImage.setImage(img);
            waveImage.setFitWidth(400); 
            waveImage.setPreserveRatio(true);
        } catch (Exception e) { }
        // konumu ekranın tam ortası
        waveImage.setLayoutX((1024 - 400) / 2); 
        waveImage.setLayoutY(-60); // biraz yukarı

        // can göstergesi
        javafx.scene.image.ImageView heartIcon = new javafx.scene.image.ImageView();
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/health.png"));
            heartIcon.setImage(img);
            heartIcon.setFitHeight(40); 
            heartIcon.setPreserveRatio(true);
        } catch (Exception e) { }

        // can yazısı 100/100 formatında
        healthLabel = new Label(health + " / 100");
        healthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        healthLabel.setTextFill(Color.web("#D946EF")); 

        // ikon ve yazı kutusu
        HBox healthBox = new HBox(10); 
        healthBox.setAlignment(Pos.CENTER_LEFT);
        healthBox.getChildren().addAll(heartIcon, healthLabel);

        // yarı saydam, mor çerçeve
        healthBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 5px 15px; -fx-background-radius: 10; -fx-border-color: #D946EF; -fx-border-width: 2; -fx-border-radius: 10;");
        healthBox.setLayoutX(820); 
        healthBox.setLayoutY(20);

        // kule seçimi butonları yan yana dizilir
        HBox buttonBox = new HBox(30); 
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setLayoutX(150);
        buttonBox.setLayoutY(650); 

        // şeffaf buton stili -> sadece resim gözüksün diye
        String transparentStyle = "-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;";

        // lightning butonu
        Button btnLightning = new Button();
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/button_lightning.png"));
            javafx.scene.image.ImageView view = new javafx.scene.image.ImageView(img);
            view.setFitHeight(90); view.setPreserveRatio(true); 
            btnLightning.setGraphic(view); 
        } catch (Exception e) { btnLightning.setText("Yildirim"); }
        btnLightning.setStyle(transparentStyle);
        btnLightning.setOnAction(e -> selectedTowerType = "Lightning");

        // fire butonu
        Button btnFire = new Button();
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/button_fire.png"));
            javafx.scene.image.ImageView view = new javafx.scene.image.ImageView(img);
            view.setFitHeight(90); view.setPreserveRatio(true);
            btnFire.setGraphic(view);
        } catch (Exception e) { btnFire.setText("Ates"); }
        btnFire.setStyle(transparentStyle);
        btnFire.setOnAction(e -> selectedTowerType = "Fire");

        // ice butonu
        Button btnIce = new Button();
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/button_ice.png"));
            javafx.scene.image.ImageView view = new javafx.scene.image.ImageView(img);
            view.setFitHeight(90); view.setPreserveRatio(true);
            btnIce.setGraphic(view);
        } catch (Exception e) { btnIce.setText("Buz"); }
        btnIce.setStyle(transparentStyle);
        btnIce.setOnAction(e -> selectedTowerType = "Ice");
        
        // hepsini kutuya ekle
        buttonBox.getChildren().addAll(btnLightning, btnFire, btnIce);

        // oyun alanına eklenir
        gamePane.getChildren().addAll(moneyBox, waveImage, healthBox, buttonBox);
    }

    private void setupLogUI() {
        // panelin tepesindeki yazı
        Label logTitle = new Label("SIMULASYON GUNLUGU");
        logTitle.setTextFill(Color.WHITE);
        logTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        logView = new ListView<>(); // boş liste
        logView.setPrefHeight(700); 
        // dark mode
        logView.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: white;");
        
        logPane.getChildren().addAll(logTitle, logView);
    }

    private void startGameLoop(){
        // animasyon nesnesi oluşturur
        gameLoop = new AnimationTimer(){ // asnimasyonlar için sunulan özel zamanlayıcı
            public void handle(long now_nanos){
                updateGame();
            }
        };
        // döngü başlatılır
        gameLoop.start();
    }
    
    // animationtimer tarafından saniyede 60 kere çağrılır
    private void updateGame(){
        // zamanı al
        long currentTime = System.currentTimeMillis();

        // kuyruk ve zaman kontrolü
        // creationQueue boş değilse (hala dolacak düşman varsa)
        // ve son düşmandan bu yana 4 saniye geçtiyse
        if(!creationQueue.isEmpty() && currentTime > lastCreationTime + 4000){
            String enemyType = creationQueue.remove(0);
            // o türde bir düşman haritaya eklenir
            creationEnemy(enemyType);
            // aon doğma zamanı
            lastCreationTime = currentTime;
        }

        for(Enemy enemy : enemies){
            // hareket (mantıksal)
            enemy.move();
            // görsel hareket
            enemy.getVisualNode().setLayoutX(enemy.getX_position() - 40);
            enemy.getVisualNode().setLayoutY(enemy.getY_position() - 40);
        }

        for(Tower tower : towers){
            //saldırı
            tower.attack(enemies, gamePane);
        }

        // ölü listesi -> silinirler
        List<Enemy> deadEnemies = new ArrayList<>();
        boolean statsChanged = false;
        
        for(Enemy enemy: enemies){
            // düman öldüyse
            if(enemy.isDead()){
                deadEnemies.add(enemy);
                //düşman panelden kaldırılır
                gamePane.getChildren().remove(enemy.getVisualNode());
                
                // para ödülü 
                money += enemy.getGoldReward();
                statsChanged = true;
                
                // ölüm müziği
                playDeathSound();
                
                // günlüğe yazılır
                logToScreen("'" + enemy.getId() + "' oldu. Odul +" + enemy.getGoldReward() + ". Toplam Para: " + money + ".");
            } 

            // düşman üsse ulaştıysa
            // uzaklığı 10px den azsa
            else if (PATH[PATH.length - 1].distance(enemy.getX_position(), enemy.getY_position()) < 10) {
                
                deadEnemies.add(enemy); // silinecekler listesi
                gamePane.getChildren().remove(enemy.getVisualNode()); 
                
                // cadı kahkahası
                if (enemy instanceof Witch) {
                    playWitchLaugh();
                }
                
                // hasar belirlenir
                int damage = enemy.getBaseDamage();
                
                // canı azalt
                health -= damage;
                statsChanged = true;
                
                // günlüğe yaz
                logToScreen("'" + enemy.getId() + "' kaleye carpti! Can: " + health + " (-" + damage + ").");
            }
        }
        // not alınan tüm ölüler listeden temizlenir
        enemies.removeAll(deadEnemies);
        
        // eğer para kazanıldıysa veya can azaldıysa
        if(statsChanged){
            if(health < 0 ) health = 0; // can eksiye düşmesin
            updateStatsLabel(); // üstteki  yazılar güncellensin
            
            // kaybetme kontrolü
            if(health <= 0){
                logToScreen("OYUN BITTI! KAYBETTINIZ.");
                showGameOver(false);
            }
        }

        // sahnede düşman kalmadı ve ölmediysek
        if (creationQueue.isEmpty() && enemies.isEmpty() && health > 0) {
            if (currentWave == 1) {
                currentWave = 2;
                updateStatsLabel();
                startWave2(); // dalga2 başlatılır
            } else if (currentWave == 2) {
                currentWave = 3; 
                logToScreen("TUM DALGALAR TEMIZLENDI! KAZANDINIZ!");
                showGameOver(true);
            }
        }
    }
    
    // dalga1 de gelen düşmanlar listesi
    private void startWave1(){
        logToScreen("--- DALGA 1 BASLADI ---");
        creationQueue.add("Wizard");
        creationQueue.add("Wizard");
        creationQueue.add("Knight");
        creationQueue.add("Witch");
    }
    
    // dalga2 de gelen düşmanlar listesi
    private void startWave2(){
        logToScreen("--- DALGA 2 BASLADI ---");
        creationQueue.add("Wizard");
        creationQueue.add("Knight");
        creationQueue.add("Witch");
        creationQueue.add("Knight");
        creationQueue.add("Witch");
        creationQueue.add("Knight");
        creationQueue.add("Wizard");
        creationQueue.add("Witch");
    }

    // yeni düşman oluşturmak için
    private void creationEnemy(String type){
        Enemy newEnemy = null;
        if(type.equals("Wizard")) newEnemy = new Wizard();
        else if(type.equals("Knight")) newEnemy = new Knight();
        else if(type.equals("Witch")) newEnemy = new Witch();
        
        // yeni düşman konumu
        if(newEnemy != null){
            double startX = PATH[0].getX();
            double startY = PATH[0].getY();

            newEnemy.getVisualNode().setLayoutX(PATH[0].getX());
            newEnemy.getVisualNode().setLayoutY(PATH[0].getY());

            newEnemy.setPosition(startX, startY); 
            
            // düşmanı ekrana ekle
            enemies.add(newEnemy);
            gamePane.getChildren().add(newEnemy.getVisualNode());
            
            // logda düşmanın oyuna girdiğinin bildirimi
            logToScreen("Dusman '" + newEnemy.getId() + "' (Can: " + newEnemy.getHealth() + "/" + newEnemy.getMaxHealth()+
                        ", Zirh:" + newEnemy.getArmor() + ") haritaya girdi.");
        }
    }
    
    // kule eklemek için
    private void addTower(double mouseX, double mouseY) {
        Point2D targetSpot = null;

        // tıklanan noktanın önceden belirlenen spotlarda olup olmadığını kontrol eder
        for (Point2D spot : TOWER_SPOTS) {
            // eğer tıklanan yer ve spot arası 50px den azsa
            if (spot.distance(mouseX, mouseY) < 50) {
                targetSpot = spot; // spot bulundu
                break; 
            }
        }
        if (targetSpot == null) {
            return;
        }
        // seçilen slot dolu mu?
        for (Tower t : towers) {
            double dist = Math.sqrt(Math.pow(t.getX_position() - targetSpot.getX(), 2) + 
                                    Math.pow(t.getY_position() - targetSpot.getY(), 2));
            
            if (dist < 10) { // 10px den azsa spot doludur
                logToScreen("UYARI: Bu slot dolu!");
                return;
            }
        }

        // kuleyi slotun merkezine koyar
        Tower newTower = null;
        double x = targetSpot.getX();
        double y = targetSpot.getY();
        
        // hangi kule seçili
        if (selectedTowerType.equals("Lightning") && money >= 50) {
            newTower = new LightningTower(x, y); money -= 50;
        } else if (selectedTowerType.equals("Fire") && money >= 75) {
            newTower = new FireTower(x, y); money -= 75;
        } else if (selectedTowerType.equals("Ice") && money >= 70) {
            newTower = new IceTower(x, y); money -= 70;
        } else {
            // kule seçilmişse veya para yetmiyorsa
             if (newTower == null) {
                 logToScreen("Yetersiz Bakiye!");
                 return;
             }
        }
        
        if(newTower != null){
            // mantıksal listeye eklenir
            towers.add(newTower);
            // görsel listeye eklenir
            gamePane.getChildren().add(newTower.getVisualNode());
            // arayüz güncellenir -> para azalır
            updateStatsLabel();
            
            // simülasyon günlüğüne kaydedilir
            logToScreen("Kullanici, (" + (int)x + ", " + (int)y + ") konumuna '" +
                        newTower.getTowerId() + "' insa etti. Kalan Para: " + money + ".");

            // kulenin menzilini görebilmek için debug modu
            if (DEBUG_MODE) {
               
                double range = 140; 
                try { range = newTower.getClass().getField("range").getInt(newTower); } catch(Exception e){} // Basit erişim denemesi

                // menzil boyutunda bir daire
                javafx.scene.shape.Circle rangeCircle = new javafx.scene.shape.Circle(range);

                // daire kulenin merkezinde
                rangeCircle.setCenterX(x);
                rangeCircle.setCenterY(y);
                
                // görünüm ayarları (içi boş, dışı siyah)
                rangeCircle.setFill(Color.TRANSPARENT); 
                rangeCircle.setStroke(Color.BLACK); 
                rangeCircle.setStrokeWidth(1);
                rangeCircle.setMouseTransparent(true);  
                // daire ekrana eklenir
                gamePane.getChildren().add(rangeCircle);
            }
        }
    }
    
    // dosya kayıt
    private void logToScreen(String msg) {
        Logger.log(msg);
        if(logView != null) {
            // 0 -> listenin en üstüne ekle demek
            logView.getItems().add(0, msg);
        }
    }
    
    private void updateStatsLabel() {
        // para güncellemesi
        moneyLabel.setText("" + money);
        // sağlık güncellemesi
        healthLabel.setText(health + " / 100");
        // dalga görseli güncellemesi
        String imageName = "wave1.png"; 
        if (currentWave == 2) {
            imageName = "wave2.png";
        }
        
        try {
            // resim yükleme
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/" + imageName));
            waveImage.setImage(img);
        } catch (Exception e) { }
    }
    
    // noktanın doğruya olan en kısa uzaklığı
    private double distanceToSegment(double px, double py, Point2D p1, Point2D p2){
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double dx = x2 - x1;
        double dy = y2 - y1;
        if(dx == 0 && dy == 0) return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx*dx + dy*dy);
        t = Math.max(0, Math.min(1, t));
        double closestX = x1 + t*dx;
        double closestY = y1 + t*dy;
        return Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));
    }

    private void showGameOver(boolean victory){
        // oyun döngüsü sonlanır
        gameLoop.stop(); 
        // müzik durur
        if (musicPlayer != null) {
            musicPlayer.stop();
        }

        // zafer müziği çalar
        if (victory) {
            playVictorySound();
        // mağlubiyet müziği çalar
        } else {
            playGameOverSound();
        }
        
        StackPane root = new StackPane();
        root.setPrefSize(1024, 768);
        // ekranın tamamını kaplayan saydam siyah bir perde gelir
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); 
        
        StackPane windowBox = new StackPane();
        windowBox.setMaxSize(800, 600); // pencere max boyutu

        try {
            // victory background yüklenir
            javafx.scene.image.Image bgImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/victory_background.png"));
            javafx.scene.image.ImageView bgView = new javafx.scene.image.ImageView(bgImg);

            // resim boyutları
            bgView.setFitWidth(1000);  
            bgView.setFitHeight(800); 

            // resmi sağa kaydırır
            bgView.setTranslateX(20); 
            
            // resim kutuya girer
            windowBox.getChildren().add(bgView); 
        } catch (Exception e) {
            windowBox.setStyle("-fx-background-color: #333; -fx-border-color: white; -fx-border-width: 2;");
        }
        VBox contentBox = new VBox(-28); 
        contentBox.setAlignment(Pos.CENTER);

        // victory true ise victory.png değilse gameOver.png
        String titleImageName = victory ? "victory.png" : "gameOver.png";
        ImageView titleView = new ImageView();
        try {
            // seçilen resim yüklenir
            javafx.scene.image.Image titleImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/" + titleImageName));
            titleView.setImage(titleImg);
            titleView.setFitWidth(450); 
            titleView.setPreserveRatio(true);
            contentBox.getChildren().add(titleView);
        } catch (Exception e) { }

        // kalan can ve toplam para yazılır
        Label lblStats = new Label("Kalan Can: " + health + "\nToplam Para: " + money);
        lblStats.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblStats.setTextFill(Color.WHITE); // yazı rengi beyaz
        lblStats.setTextAlignment(TextAlignment.CENTER); // yazı ortalanır
        // yazı arkasına siyah gölge 
        lblStats.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);");

        Button btnExit = new Button();
        try {
            // exit button eklenir
            javafx.scene.image.Image btnImg = new javafx.scene.image.Image(getClass().getResourceAsStream("/starfalldefense/assets/images/exit_button.png"));
            ImageView btnView = new ImageView(btnImg);
            btnView.setFitWidth(250); 
            btnView.setPreserveRatio(true);
            btnExit.setGraphic(btnView);
            btnExit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        } catch (Exception e) { btnExit.setText("CIKIS"); }
        // uygulama kapanır
        btnExit.setOnAction(e -> Platform.exit());
        
        // yazı + buton -> içerik kutusu
        contentBox.getChildren().addAll(lblStats, btnExit);
        // contentBox -> windowBox
        windowBox.getChildren().add(contentBox);
        // windowBox -> root
        root.getChildren().add(windowBox);
        gamePane.getChildren().add(root);
    }
    
    // arka plan müziği
    private void playBackgroundMusic() {
        try {
            String musicFile = getClass().getResource("/starfalldefense/assets/sounds/main_music.mp3").toExternalForm();

            Media sound = new Media(musicFile);
            musicPlayer = new MediaPlayer(sound);

            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.4); 

            musicPlayer.play();
            
        } catch (Exception e) {
            System.out.println("Muzik dosyasi bulunamadi: " + e.getMessage());
        }
    }
    
    // ölüm müziği
    private void playDeathSound() {
        try {
            String path = getClass().getResource("/starfalldefense/assets/sounds/death_sound.wav").toExternalForm();
            AudioClip clip = new AudioClip(path);
            clip.setVolume(0.3);
            clip.play();
        } catch (Exception e) {
            System.out.println("Olum sesi calinamadi (Format hatasi olabilir): " + e.getMessage());
        }
    }
    
    // oyun kaybedilince çalan müzik
    private void playGameOverSound() {
        try {
            String path = getClass().getResource("/starfalldefense/assets/sounds/game_over.mp3").toExternalForm();
            javafx.scene.media.AudioClip clip = new javafx.scene.media.AudioClip(path);
            clip.setVolume(1.0); 
            clip.play();
        } catch (Exception e) {
            System.out.println("Game Over sesi calinamadi: " + e.getMessage());
        }
    }
    
    // cadı kahkası
    private void playWitchLaugh() {
        try {
            String path = getClass().getResource("/starfalldefense/assets/sounds/witch_sound.mp3").toExternalForm();
            javafx.scene.media.AudioClip clip = new javafx.scene.media.AudioClip(path);
            clip.setVolume(1.0); 
            clip.play();
        } catch (Exception e) {
        }
    }
    
    // zafer müziği
    private void playVictorySound() {
        try {
            String path = getClass().getResource("/starfalldefense/assets/sounds/victory.wav").toExternalForm();
            javafx.scene.media.AudioClip clip = new javafx.scene.media.AudioClip(path);
            clip.setVolume(1.0); 
            clip.play();
        } catch (Exception e) {
            System.out.println("Zafer sesi calinamadi: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package starfalldefense;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

public class StoryScene {

    private StarfallDefense mainApp; // ana uygulamaya erişim (menüye geçemk için)
    private Stage stage; // pencere kontrolü
    private StackPane root;  // tüm görsel elemanları tutan ana kök

    // hikaye metni (paragraflara bölünmüş)
    private String[] storyPages = {
        "Uzun yıllar önce gök yarıldı ve bir yıldız dünyaya\n" +
        "çarparak üçe bölündü. Her parça bir kudreti fısıldadı:\n" +
        "Ateş, Yıldırım ve Buz.",
        
        "Tapınak bu ışıkla nefes aldı; şato, bu kudretin\n" +
        "muhafızı oldu. Ama güç kokusu karanlığı uyandırır.",
        
        "Büyücüler ve şövalyeler, yıldızın özünü çalmak için\n" +
        "kapılara dayandı. Gölgelerin üstünde süpürgeli bir cadı\n" +
        "dolaşıyor; ışığı söndürmeye ant içmiş.",
        
        "Muhafız... Şimdi sıra sende.\n" +
        "Şatoyu koru; yıldız yeniden doğsun."
    };
    
    private int currentPageIndex = 0; // o an olunan sayfa sayısı
    private Map<Character, Image> runeImages = new HashMap<>(); // rün alfabesi
    
    // görsel 
    private FlowPane runeContainer; // rünlerin dizildiği alan
    private Label subtitleLabel; // türkçe altyazı
    private int charIndex = 0; // daktilo efekti
    private MediaPlayer storyMusicPlayer; // arka plan müziği

    // constructor
    public StoryScene(StarfallDefense mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        loadRuneImages(); // sınıf oluştuğu an resimler yüklenir
    }

    // sahne
    public void show() {
        root = new StackPane();
        setTableBackground(); // arka plan

        // kapalı kitap resmi
        ImageView bookView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/book_closed.png"));
            bookView.setImage(img);
            bookView.setFitHeight(550); //boyutu
            bookView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Resim bulunamadi!");
        }

        // tıklayın mesajı
        ImageView subtitleView = new ImageView();
        try {
            Image subImg = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/opening_subtitle.png"));
            subtitleView.setImage(subImg);
            subtitleView.setFitWidth(750); 
            subtitleView.setPreserveRatio(true);
            subtitleView.setTranslateY(-80);
            
        } catch (Exception e) {
            System.out.println("Yazı resmi bulunamadı!");
        }

        // kitap + yazı + buton
        VBox openingLayout = new VBox(-350); 
        openingLayout.setAlignment(Pos.CENTER);
        
        // kitap ve altındaki yazıyı ekrana eklenir
        openingLayout.getChildren().addAll(bookView, subtitleView);
        openingLayout.setTranslateY(20);
        
        root.getChildren().add(openingLayout);

        Scene scene = new Scene(root, 1024, 768); // pencere boyutu
        stage.setScene(scene);
        stage.show();
        
        playStoryMusic();
        
        // Tıklama ile geçiş
        root.setOnMouseClicked(e -> {
            root.setOnMouseClicked(null);
            transitionToOpenBook();
        });
    }

    private void transitionToOpenBook() {
        root.getChildren().clear(); // önceki kitap ekranını twmizler
        setTableBackground(); // arka planı koru

        // açık kitap
        ImageView bookView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/book_open.png"));
            bookView.setImage(img);
            bookView.setFitHeight(600); // Kitap boyutu
            bookView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Acik kitap resmi bulunamadi!");
        }

        // rün harfleri
        // harfler yan yana dizileceği ve satır dolunca geçeceği alan
        runeContainer = new FlowPane();
        // boşluk ayarı
        runeContainer.setHgap(2); 
        runeContainer.setVgap(5); 
        runeContainer.setAlignment(Pos.CENTER); 

        runeContainer.setMaxWidth(400);
        runeContainer.setPrefWidth(400);
        
        runeContainer.setMaxHeight(350); 
        runeContainer.setPrefHeight(350);

        // kitap görselinin üstüne rün harflerini koyar
        StackPane bookStack = new StackPane();
        bookStack.getChildren().addAll(bookView, runeContainer);
        StackPane.setAlignment(runeContainer, Pos.CENTER); 
        
        // meşale görseli yüklenir
        ImageView leftTorch = new ImageView();
        ImageView rightTorch = new ImageView();
        try {
            Image torchImg = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/torch.png"));

            leftTorch.setImage(torchImg);
            leftTorch.setFitHeight(350); 
            leftTorch.setPreserveRatio(true);

            rightTorch.setImage(torchImg);
            rightTorch.setFitHeight(350); 
            rightTorch.setPreserveRatio(true);
            
        } catch (Exception e) { 
            System.out.println("Meşale resmi bulunamadı!");
        }
 
        // resim yükleme
        StackPane bookArea = new StackPane();
        bookArea.getChildren().add(bookStack);
        bookArea.getChildren().addAll(leftTorch, rightTorch);
        
        //meşale konumları
        leftTorch.setTranslateX(-420); 
        rightTorch.setTranslateX(420); 
 
        leftTorch.setTranslateY(-50);
        rightTorch.setTranslateY(-50);

        // altyazı kısmı
        subtitleLabel = new Label("");
        subtitleLabel.setTextFill(Color.WHITE);
        subtitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        subtitleLabel.setWrapText(true);
        
        subtitleLabel.setMinWidth(1024);
        subtitleLabel.setAlignment(Pos.CENTER);
        subtitleLabel.setTextAlignment(TextAlignment.CENTER);
        
        subtitleLabel.setMinHeight(60); 

        // başlat butonu
        Button skipButton = new Button();
        try {
            Image btnImg = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/start_game.png"));
            ImageView btnView = new ImageView(btnImg);
            
            btnView.setFitWidth(200);
            btnView.setPreserveRatio(true);
            
            skipButton.setGraphic(btnView);

            skipButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        } catch (Exception e) {

            skipButton.setText("OYUNA BASLA >>");
        }
        skipButton.setOnAction(event -> startGame());

        // hepsini bir box içine alır
        VBox bottomBox = new VBox(5);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(subtitleLabel, skipButton);

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(bookArea, bottomBox);
        mainLayout.setTranslateY(-40);
        
        // başlatma
        root.getChildren().add(mainLayout);
        startTypewriterEffect();
    }

    private void setBackgroundImage(String imagePath) {
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            BackgroundImage bg = new BackgroundImage(img, 
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.CENTER, 
                    // en boy oranını korur
                    new BackgroundSize(100, 100, true, true, true, true)); 
            root.setBackground(new Background(bg));
        } catch (Exception e) {
            System.out.println("Resim yüklenemedi: " + imagePath);
            root.setStyle("-fx-background-color: black;");
        }
    }

    private void loadRuneImages() {
        // rün harflerini alfabeyle eşleştirir
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : chars) {
            try {
                String path = "/starfalldefense/assets/runes/" + c + ".png";
                if (getClass().getResource(path) != null) {
                    Image img = new Image(getClass().getResourceAsStream(path));
                    runeImages.put(c, img);
                }
            } catch (Exception e) { }
        }
    }

    // daktilo efektinin verildiği yer
    private void startTypewriterEffect() {
        // eğer son sayfa bittiyse (index sınırı aştıysa) durdur
        if (currentPageIndex >= storyPages.length) {
            return; 
        }
        
        // şuanki sayfadaki metni al
        String currentText = storyPages[currentPageIndex];
        charIndex = 0;

        // ekranı temizle ve kalan rün ve alfabeleri sil
        runeContainer.getChildren().clear();
        subtitleLabel.setText("");
        
        // zamanlayıcı
        Timeline timeline = new Timeline();

        // bu kod her 50 milisaniyede bir çalışır
        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), event -> {
            // eğer yazılacak harf varsa
            if (charIndex < currentText.length()) {
                // sıradaki harfi alır
                char currentChar = currentText.charAt(charIndex);
                // o harfin karşılığı olan rün harfini kitaba koy
                addRuneToScreen(currentChar);
                // altyazıyı metnin sonuna ekler
                subtitleLabel.setText(subtitleLabel.getText() + currentChar);
                // sayacı bir artır
                charIndex++;
            } 
        });
        
        // zamanlayıcı ayarı
        timeline.getKeyFrames().add(keyFrame);
        // metnin uzunluğu kadar çalışır 
        timeline.setCycleCount(currentText.length());

        // sayfa bitince...
        timeline.setOnFinished(e -> {

            // bekleme süresi -> 2 saniye
            new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
                // sonraki sayfa
                currentPageIndex++;
                // tekrar kendini çağırır, döngü başa döner ve bir sonraki sayfa yazılır
                startTypewriterEffect();
            })).play();
        });
        
        timeline.play();
    }

    // rün harflerini ekrana koymak için
    private void addRuneToScreen(char c) {
        // hikayede alt satıra geçilmesi gerekn yer var mı diye
        if (c == '\n') {
            Label lineBreak = new Label();
            lineBreak.setPrefWidth(400); 
            lineBreak.setMaxHeight(5); 
            runeContainer.getChildren().add(lineBreak);
            return; 
        }
        
        // rünler ingilizce klavye, metodla türkçe karşılığı alınır
        char mappedChar = convertTurkishChar(Character.toLowerCase(c));
        
        // rün ekleme
        if (runeImages.containsKey(mappedChar)) {
            ImageView runeView = new ImageView(runeImages.get(mappedChar));

            runeView.setFitWidth(14); 
            runeView.setFitHeight(14);
            
            runeView.setBlendMode(BlendMode.DIFFERENCE);

            runeContainer.getChildren().add(runeView);
        } else {
            Label space = new Label(" ");
            space.setMinWidth(8);
            runeContainer.getChildren().add(space);
        }
    }
    
    // rünler türkçe olmadığı  için ingilizce klavye kullanımı sağlanıyor
    private char convertTurkishChar(char c) {
        switch (c) {
            case 'ç': return 'c';
            case 'ğ': return 'g';
            case 'ı': return 'i';
            case 'ö': return 'o';
            case 'ş': return 's';
            case 'ü': return 'u';
            default: return c;
        }
    }

    // oyuna başla
    private void startGame() {
        // eski müzik dursun
        if (storyMusicPlayer != null) {
            storyMusicPlayer.stop();
        }
        
        // ana menüye geçiş
        mainApp.openMainMenu(stage); 
    }
    
    // hikayenin arkasındaki backgroun
    private void setTableBackground() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/starfalldefense/assets/images/background.png"));
            
            // tekrar tekrar koymayı önler
            BackgroundImage bg = new BackgroundImage(
                img,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
            );
            
            root.setBackground(new Background(bg));
        } catch (Exception e) {
            System.out.println("Arka plan resmi bulunamadi, siyah yapiliyor.");
            root.setStyle("-fx-background-color: black;");
        }
    }
    
    // hikaye soundu koyulur
    private void playStoryMusic() {
        try {
            String path = getClass().getResource("/starfalldefense/assets/sounds/book_music.mp3").toExternalForm();
            Media sound = new Media(path);
            storyMusicPlayer = new MediaPlayer(sound);
            storyMusicPlayer.setVolume(1.0); 
            storyMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // sonsuz döngüde çalar
            storyMusicPlayer.play();
            
        } catch (Exception e) {
            System.out.println("Hikaye muzigi calinamadi: " + e.getMessage());
        }
    }
}
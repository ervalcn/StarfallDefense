package starfalldefense;

import java.io.BufferedWriter; // Dosyaya verimli yazmak için
import java.io.FileWriter; // Dosyayı açmak ve yazmak için
import java.io.IOException; // Dosya hatası olursa bulmak için
import java.time.LocalDateTime; // Anlık bilgisayar saatini alak için
import java.time.format.DateTimeFormatter; // Saati "hh:mm:ss" formatına çevirmek için

public class Logger {
    // dosya adını tanımladık. artık tüm classlarda sabit
    private static final String FILE_NAME = "savunma_gunlugu.txt";
    // saati almak için
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public static void clearLog(){
        // eski oyun kayıtlarını silip yeni kayıt açyor
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))){
            writer.write("--- SIMULASYON GUNLUGU BASLADI ---\n");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static void log(String message){
        // Zamanı alıp istenen formatta yazmak için
        String timestamp = dtf.format(LocalDateTime.now());
        // mesaj kısmı
        String logMessage = "[" + timestamp + "] " + message;
        
        System.out.println(logMessage);
        
        // eskileri silmeden, dosyanın sonuna ekleme
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))){
            writer.write(logMessage + "\n");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

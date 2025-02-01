package linkedInlogin;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.io.File;


import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class loginautomation {
    private static ScreenRecorder screenRecorder;

    public static void main(String[] args) {
        try {
            // Start screen recording
            startRecording();

            // Set the path to the ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

            // Initialize ChromeDriver
            WebDriver driver = new ChromeDriver();

            try {
                // Open LinkedIn login page
                driver.get("https://www.linkedin.com/login");

                // Locate the username field and enter your email
                WebElement username = driver.findElement(By.id("username"));
                username.sendKeys("silunisilva2@gmail.com");

                // Locate the password field and enter your password
                WebElement password = driver.findElement(By.id("password"));
                password.sendKeys("Silunisilva@123");

                // Locate the login button and click it
                WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
                loginButton.click();

                // Wait for the page to load and verify login
                Thread.sleep(2000);  // Wait for 2 seconds

                // Check if login is successful by verifying the URL
                if (driver.getCurrentUrl().contains("feed")) {
                    System.out.println("Login successful!");
                } else {
                    System.out.println("Login failed!");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close the browser
                driver.quit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Stop screen recording
            try {
                stopRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Start recording method
    public static void startRecording() throws Exception {
        File file = new File("./ScreenRecordings");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        Rectangle captureSize = new Rectangle(0, 0, width, height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        screenRecorder = new ScreenRecorder(gc, captureSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                        FrameRateKey, Rational.valueOf(30)),
                null, file);

        screenRecorder.start();
    }

    // Stop recording method
    public static void stopRecording() throws Exception {
        screenRecorder.stop();
    }
}

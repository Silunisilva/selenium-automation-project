package linkedInlogin;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class linkedinloginTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static ScreenRecorder screenRecorder;

    @Before
    public void setUp() throws Exception {
        // Start screen recording
        startRecording();

        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(35));
    }

    @Test
    public void testValidLogin() {
        login("silunisilva2@gmail.com", "Silunisilva@123");
        captureScreenshot("ValidLogin");
        wait.until(ExpectedConditions.urlContains("feed"));
        Assert.assertTrue("Valid login failed!", driver.getCurrentUrl().contains("feed"));
    }

    @Test
    public void testInvalidLogin() {
        login("silunisilva2@gmail.com", "InvalidPassword");
        captureScreenshot("InvalidLogin");
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-for-password")));
        Assert.assertTrue("Invalid login did not show error!", errorElement.isDisplayed());
    }

    @Test
    public void testEmptyUsername() {
        login("", "Silunisilva@123");
        captureScreenshot("EmptyUsername");
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#error-for-username[role='alert'][class='form__label--error']")));
        Assert.assertTrue("Error message not displayed!", errorElement.isDisplayed());
    }

    @Test
    public void testEmptyPassword() {
        login("silunisilva2@gmail.com", "");
        captureScreenshot("EmptyPassword");
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#error-for-password[role='alert'][class='form__label--error']")));
        Assert.assertTrue("Error message not displayed!", errorElement.isDisplayed());
    }

    @Test
    public void testInvalidUsernameFormat() {
        login("invalidEmailFormat", "Silunisilva@123");
        captureScreenshot("InvalidUsernameFormat");
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-for-username")));
        Assert.assertTrue("Invalid email format did not show error!", errorElement.isDisplayed());
    }

    @Test
    public void testSQLInjection() {
        login("' OR '1'='1", "Silunisilva@123");
        captureScreenshot("SQLInjection");
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-for-username")));
        Assert.assertTrue("SQL Injection attack did not show error!", errorElement.isDisplayed());
    }

    @Test
    public void testXSSAttack() {
        login("<script>alert('XSS')</script>", "Silunisilva@123");
        captureScreenshot("XSSAttack");
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-for-username")));
        Assert.assertTrue("XSS attack did not show error!", errorElement.isDisplayed());
    }

    @Test
    public void testForgotPasswordLink() {
        driver.get("https://www.linkedin.com/login");
        WebElement forgotPasswordLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.forgot-password")));
        forgotPasswordLink.click();
        wait.until(ExpectedConditions.urlContains("/checkpoint/rp/request-password-reset"));
        Assert.assertTrue("Forgot Password page did not open!", driver.getCurrentUrl().contains("/checkpoint/rp/request-password-reset"));
        captureScreenshot("ForgotPasswordLink");
    }

    @Test
    public void testGoogleLogin() {
        try {
            driver.get("https://www.linkedin.com/login");
            WebElement googleLoginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Continue with Google']")));
            googleLoginButton.click();

            String parentHandle = driver.getWindowHandle();
            for (String winHandle : driver.getWindowHandles()) {
                if (!winHandle.equals(parentHandle)) {
                    driver.switchTo().window(winHandle);
                    break;
                }
            }

            WebElement googleUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("identifierId")));
            googleUsername.sendKeys("silunisilva2@gmail.com");
            WebElement nextButton = driver.findElement(By.id("identifierNext"));
            nextButton.click();

            WebElement googlePassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            googlePassword.sendKeys("Siluni0629");
            WebElement signInButton = driver.findElement(By.id("passwordNext"));
            signInButton.click();

            driver.switchTo().window(parentHandle);
            wait.until(ExpectedConditions.urlContains("https://www.linkedin.com/feed/"));
            Assert.assertTrue("Google login failed!", driver.getCurrentUrl().contains("https://www.linkedin.com/feed/"));
            captureScreenshot("GoogleLogin");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception occurred during Google login test: " + e.getMessage());
        }
    }

    private void login(String usernameInput, String passwordInput) {
        try {
            driver.get("https://www.linkedin.com/login");
            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            username.sendKeys(usernameInput);
            WebElement password = driver.findElement(By.id("password"));
            password.sendKeys(passwordInput);
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
            loginButton.click();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception occurred during login: " + e.getMessage());
        }
    }

    private void captureScreenshot(String testName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        try {
            String destination = "C:\\Screenshots\\" + testName + ".png";
            FileUtils.copyFile(source, new File(destination));
            System.out.println("Screenshot taken: " + destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        try {
            // Stop screen recording
            stopRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start recording method
    private static void startRecording() throws Exception {
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
    private static void stopRecording() throws Exception {
        if (screenRecorder != null) {
            screenRecorder.stop();
        }
    }
}

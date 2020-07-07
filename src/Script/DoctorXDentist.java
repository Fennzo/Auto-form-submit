package Script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class DoctorXDentist {
	
	public static void writeToFile(int recordid, String docName, String profession) {
		
		Formatter x = null;
		try {
			// Create file
			FileWriter fw = new FileWriter("Submittedforms.txt", true);
			x = new Formatter(fw);
		}
		 
		catch(Exception e){
			System.out.println("Error creating file");
		}
		
		// x.format( string amount %s is one, string 1, string 2  ), 
		x.format("[%d,%s,%s]%n", recordid, docName, profession);
		x.close();
	}
	
	public static boolean checkExist(int record) {
		int mark = 0;
		try {
		
			BufferedReader reader = new BufferedReader(new FileReader("Submittedforms.txt"));
			String line;
			String recordid = Integer.toString(record);
			while( (line = reader.readLine()) != null) {
				if (line.contains(recordid)) {
					mark = 1;
				}
			}
			reader.close();
		}
		catch(Exception e) {
			System.out.println("Unable to read");
		}
		
		if ( mark == 1) {
			return true;
		}
		else {
			return false;
		}
	}

	public static void sendClick(String xpath, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath(xpath))));
		driver.findElement(By.xpath(xpath)).click();
	}
	
	public static void sendText(String xpath, WebDriver driver, String text) {
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath(xpath))));
		driver.findElement(By.xpath(xpath)).sendKeys(text);;
	}
	
	public static boolean checkElement(WebDriver driver, String xpath) {
		 boolean flag = false;
	        try {
	        	WebElement element = driver.findElement(By.xpath(xpath));
	            if (element.isDisplayed() || element.isEnabled())
	                flag = true;
	        } catch (NoSuchElementException e) {
	            flag = false;
	        } 
	        return flag;
	}
	
	public static void scrollDown(WebDriver driver) {
		try {
		    long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

		    while (true) {
		        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
		        Thread.sleep(2000);

		        long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
		        if (newHeight == lastHeight) {
		            break;
		        }
		        lastHeight = newHeight;
		    }
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}
	public static void initiate() {
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\jinyi\\Downloads\\chromedriver_win32 (2)\\chromedriver.exe");
		/*
		 * ChromeOptions options = new ChromeOptions(); options.setHeadless(true);
		 * To execute Javascript scripts
		 * Scroll to the bottom of the page
		 * JavascriptExecutor js = (JavascriptExecutor) driver;
		 * js.executeScript("window.scrollTo(document.body.scrollHeight,0)");
		 */
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		 
		int[]arr = new int[2];
		// Number of forms submitted
		arr[1] = 0; 
		try {
			driver.get("https://www.doctorxdentist.com/find-a-doctor");

			driver.manage().window().maximize();

			// Click Area of Medicine
			sendClick("//b[contains(text(),'Area of Medicine')]", driver);
			
			// Click Dental
			sendClick("//div[4]//div[1]//div[2]//div[1]//div[6]//label[1]//span[1]", driver);
			
			// Click on Submit
			sendClick("//button[@class='button is-primary is-fullwidth has-text-weight-bold']", driver);
			
			scrollDown(driver);
			
			arr[0] = driver
					.findElements(By.xpath("//*[@class='box']//*[@class='doctor']//button[text()='Get Quote']")).size();
		
			
			System.out.println("size:" + arr[0]);
			IntStream.range(1, arr[0]).forEach($ -> {
				
				/*
				 * Fluent wait
				 * 1) Able to adjust the polling period ( test the condition until it is true )
				 * 2) Able to ignore any exception such as NoSuchElement
				 */
				
				if(!checkExist($)) {		

				System.out.println("id:" + $ + "check: " + checkExist($) + checkElement(driver, "/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/section[1]/div[1]/div["+$+"]/div[1]/div[1]/div[2]/div[1]/a[1]"));
				if ( checkElement(driver, "/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/section[1]/div[1]/div["+$+"]/div[1]/div[1]/div[2]/div[1]/a[1]")) {
				WebElement profession = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/section[1]/div[1]/div["+$+"]/div[1]/div[1]/div[2]/div[1]/a[1]"));
				if ( profession.getText().contains("Dentist")) {
			
					// Goes to the position of the element to avoid clickability issue
					Actions actions = new Actions(driver);
					actions.moveToElement(profession);
					actions.perform();

					// Click get quote
				sendClick("//*[@id=\"app\"]/div/div[1]/div/div[1]/div[2]//div[1]/div["+$+"]/div/div/div[3]/button", driver);
				// Fill in job descriptionsection
				sendText("//div[@class='box p-md']//div[1]//div[1]//div[1]//input[1]",  driver, "Cleaning and polishing");

				// Fill in additional info
				sendText("//div[@class='box p-md']//div[1]//div[2]//div[1]//input[1]",  driver, "Please email me");
				// Submit first page of form
				sendClick("//button[@class='button is-primary']", driver);
				
				// Get doc name
				WebElement docName = driver.findElement(By.xpath("//h1[@class='name title is-size-4 m-b-md']"));
				
				// Fill in personal name
				sendText("/html/body/div[5]/div[@class='animation-content modal-content']/div/section//section[@class='tab-content']/div[2]/div[1]/div/input[@type='text']",  driver, "William");
				sendText("/html/body/div[5]/div[@class='animation-content modal-content']/div/section//section[@class='tab-content']/div[2]/div[2]/div/input[@type='text']", driver, "williamtan628@gmail.com");
				sendText("/html/body/div[5]/div[@class='animation-content modal-content']/div/section//section[@class='tab-content']/div[2]/div[3]/div/input[@type='text']", driver, "96414824");
				//sendClick("//button[@class='button is-primary']", driver);
				sendClick("/html/body/div[5]/div[@class='animation-content modal-content']/button[@type='button']", driver);
				writeToFile($, docName.getText(), profession.getText());
				System.out.println("Submitted!" + " ID: " + $ +  " Doctor's name: " + docName.getText()
				+ " Profession: " + profession.getText());
				arr[1]++;
				}
				}
				}
				
				else {
					arr[0]--;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Number of forms submitted: " + arr[1]);
			driver.close();
			driver.quit();
		}
	}

	public static void main(String[] args) {
		initiate();

	}

}

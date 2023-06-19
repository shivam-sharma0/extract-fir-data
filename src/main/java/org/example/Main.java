package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
public class Main {
    public static void main(String[] args) {
        String startDate = "01/01/2023";
        String endDate = "01/02/2023";
        String csvFilename = "maharashtra_police_fir_data.csv";


        List<FIRData> allData = scrapeFIRData(startDate, endDate);
        writeDataToCSV(allData, csvFilename);
        System.out.println("Data extracted and saved to " + csvFilename);
    }

    public static List<FIRData> scrapeFIRData(String startDate, String endDate) {
        List<FIRData> allData = new ArrayList<>();

        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "C:/Users/vashi/Downloads/chromedriver_win32 (1)/chromedriver.exe");

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode, no browser window

        // Create a new instance of ChromeDriver
        WebDriver driver = new ChromeDriver(options);

        try {
            // Navigate to the website
            driver.get("https://citizen.mahapolice.gov.in/Citizen/MH/PublishedFIRs.aspx");

            // Select the date range
            WebElement fromDateInput = driver.findElement(By.cssSelector("#ctl00_cphContainer_txtFromDate"));
            fromDateInput.clear();
            fromDateInput.sendKeys(startDate);

            WebElement toDateInput = driver.findElement(By.cssSelector("#ctl00_cphContainer_txtToDate"));
            toDateInput.clear();
            toDateInput.sendKeys(endDate);

            // Click the Search button
            WebElement searchButton = driver.findElement(By.cssSelector("#ctl00_cphContainer_btnSearch"));
            searchButton.click();

            // Wait for the results to load
            Thread.sleep(2000);

            // Extract the data from the table
            WebElement table = driver.findElement(By.cssSelector("#grvPublishedFIRs"));
            List<WebElement> rows = table.findElements(By.cssSelector("tr"));

            for (int i = 1; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cells = row.findElements(By.cssSelector("td"));

                FIRData dataEntry = new FIRData();
                dataEntry.setSerialNo(cells.get(0).getText());
                dataEntry.setState(cells.get(1).getText());
                dataEntry.setDistrict(cells.get(2).getText());
                dataEntry.setPoliceStation(cells.get(3).getText());
                dataEntry.setYear(cells.get(4).getText());
                dataEntry.setFirNo(cells.get(5).getText());
                dataEntry.setRegistrationDate(cells.get(6).getText());
                dataEntry.setSections(cells.get(7).getText());

                allData.add(dataEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Quit the driver and close the browser
            driver.quit();
        }

        return allData;
    }

    public static void writeDataToCSV(List<FIRData> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("Sr. No.,State,District,Police Station,Year,FIR No.,Registration Date,Sections\n");

            for (FIRData entry : data) {
                writer.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                        entry.getSerialNo(), entry.getState(), entry.getDistrict(), entry.getPoliceStation(),
                        entry.getYear(), entry.getFirNo(), entry.getRegistrationDate(), entry.getSections()));
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
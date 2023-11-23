package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.time.Duration;


public class FilterHandling {
    //For Filter and Filter Category translate() has been used to make the parameter texts to be case independent...
    static String filterCategorySelector = "//legend[contains(@data-testid,'filter-group-name') and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'v1')]";
    static String filterCheckBox = "//span[@class='filter-display-name' and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'v1')]/..//preceding-sibling::span";
    static String filterCheckBoxes = "//div[@role='group' and translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='v1']//span[@class='mat-checkbox-inner-container']";
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.get("https://www.t-mobile.com/tablets");

        selectFilters("Deals", "New", "Special Offer"); //Valid Filter & Filter Category
        selectFilters("Deals", "Samsung"); //Invalid Filter
        selectFilters("Ratings", "Apple", "Samsung"); //Invalid Filter Category
        selectFilters("Brands", "Apple", "Samsung"); //Valid Filter & Filter Category
        selectFilters("Brands", "all"); //Select All Filters Under a Filter Category

        takeElementScreenshot(driver, By.cssSelector("div.product-filters"), "screenshot.png");
        System.out.println("Test Complete...");

        driver.quit();
    }

    /**
     * This method selects the various filters on the search page
     *
     * @param filterCategory - Name of the Filter Category, E.g, Brand, Deals
     * @param filters        - Filter(s) to select
     * @author pabitra.swain.work@gmail.com, pabitra.swain.officework@gmail.com
     */
    public static void selectFilters(String filterCategory, String... filters) {
        String categorySelector = filterCategorySelector.replace("v1", filterCategory.toLowerCase());
        try {
            WebElement category = driver.findElement(By.xpath(categorySelector));
            if (driver.findElement(By.xpath(categorySelector + "//following-sibling::tmo-icon")).getAttribute("data-tmo-icon-name").equals("keyboard_arrow_down")) {
                //Only Expand the Filter Category if already NOT expanded...
                category.click();
                System.out.println("Expanded Filter Category: " + filterCategory);
            }
            if (filters[0].equalsIgnoreCase("all")) {
                for (WebElement filter : driver.findElements(By.xpath(filterCheckBoxes.replace("v1", filterCategory.toLowerCase())))) {
                    String filterName = filter.findElement(By.xpath("following-sibling::span")).getText().trim();
                    if (filter.findElement(By.xpath("input")).getAttribute("aria-checked").equals("false")) {
                        //Only Select the Filter if already NOT selected...
                        filter.click();
                        System.out.println("Selected Filter: " + filterName);
                    } else {
                        System.out.println("Filter '" + filterName + "' is already selected...");
                    }
                }
            } else {
                for (String filter : filters) {
                    try {
                        String filterCheckBoxSelector = filterCheckBox.replace("v1", filter.toLowerCase());
                        WebElement filterCheckBoxInput = driver.findElement(By.xpath(filterCheckBoxSelector + "/input"));
                        if (filterCheckBoxInput.getAttribute("aria-checked").equals("false")) {
                            //Only Select the Filter if already NOT selected...
                            driver.findElement(By.xpath(filterCheckBoxSelector)).click();
                            System.out.println("Selected Filter: " + filter);
                        }
                    } catch (NoSuchElementException e) {
                        //Error Logging Reporting, Printing Exception Can be done here...
                        System.out.println("No Such Filter named '" + filter + "' available under '" + filterCategory + "'... Please check the parameters again...");
                    }
                }
            }
        } catch (NoSuchElementException e) {
            //Error Logging Reporting, Printing Exception Can be done here...
            System.out.println("No Such Category named '" + filterCategory + "' on the page... Please check the parameters again...");
        }
    }

    /**
     * This method takes screenshot of a particular element on webpage
     * @param driver                       WebDriver instance
     * @param byElement                    object of By class
     * @param filePathWithNameAndExtension Screenshot save path with file extension
     * @author pabitra.swain.work@gmail.com, pabitra.swain.officework@gmail.com
     */
    public static void takeElementScreenshot(WebDriver driver, By byElement, String filePathWithNameAndExtension) {
        WebElement element = driver.findElement(byElement);

        //Scroll Element Into View
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true)", element);

        File src = element.getScreenshotAs(OutputType.FILE);
        File destination = new File(filePathWithNameAndExtension);
        try {
            FileUtils.copyFile(src, destination);
            System.out.println("Screenshot Captured...");
        } catch (IOException e) {
            //Error Logging Reporting, Printing Exception Can be done here...
            System.out.println("Couldn't save screenshot...");
        }
    }
}

package ch.egli.bbshop

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.ex.ElementNotFound
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


class BBShopPageTest {
    private val shopPage = BBShopPage()

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpAll() {
            // Configuration.browserSize = "1280x800"
            Configuration.headless = true
        }

        private val logger: Logger = LoggerFactory.getLogger(BBShopPageTest::class.java)
        private const val ALARM_THRESHOLD = 2
        private var isAlarmSet = false
    }

    @BeforeEach
    fun setUp() {
        open("https://shop.buehnenbern.ch/SelectSeats?ret=2&e=3576&bts=bs")
        shopPage.cookieButton.click()
    }

    @Test
    fun checkAvailability() {
        logger.info("##### ${getCurrentTimestamp()} -- START")
        var availability = 0
        while (true) {
            availability = availability("https://shop.buehnenbern.ch/SelectSeats?ret=2&e=3576&bts=bs")
            logger.info("##### ${getCurrentTimestamp()} -- Anzahl freie Plätze  6.12.: $availability")
            Thread.sleep(29_500)

            availability = availability("https://shop.buehnenbern.ch/SelectSeats?ret=2&e=3577&bts=bs")
            logger.info("##### ${getCurrentTimestamp()} -- Anzahl freie Plätze 10.12.: $availability")
            Thread.sleep(29_500)
/*

            availability = availability("https://shop.buehnenbern.ch/SelectSeats?ret=2&e=3780&bts=bs")
            logger.info("##### ${getCurrentTimestamp()} -- Anzahl freie Plätze 05.12.: $availability")
            Thread.sleep(5_000)
*/
        }
        logger.info("##### ${getCurrentTimestamp()} -- END")
    }

    private fun getCurrentTimestamp(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(time) ?: ""
    }

    private fun getFreePlaces(freiePlaetze: String): Int {
        val freePlaces = freiePlaetze.substringAfter("> ")
        return freePlaces.toIntOrNull() ?: 0
    }

    private fun isSoldOut(): Boolean {
        try {
            val size = shopPage.infoElement.size
            if (size != null && shopPage.infoElement.innerText().contains("ist ausverkauft")) {
                return true
            }
        } catch (ex: ElementNotFound) {
            return false
        }
        return false
    }

    private fun availability(url: String): Int {
        open(url)

        var numberOfFreePlaces = -1;

        if (isSoldOut()) {
            numberOfFreePlaces = 0
        } else {
            val freiePlaetzeKat1 = shopPage.kat1.innerText().substringAfter("Freie Plätze: ")
            val freiePlaetzeKat2 = shopPage.kat2.innerText().substringAfter("Freie Plätze: ")
            val freiePlaetzeKat3 = shopPage.kat3.innerText().substringAfter("Freie Plätze: ")
            val freiePlaetzeKat4 = shopPage.kat4.innerText().substringAfter("Freie Plätze: ")
            val freiePlaetzeKat5 = shopPage.kat5.innerText().substringAfter("Freie Plätze: ")
            val freiePlaetzeKat6 = shopPage.kat6.innerText().substringAfter("Freie Plätze: ")

            numberOfFreePlaces = getFreePlaces(freiePlaetzeKat1) + getFreePlaces(freiePlaetzeKat2) + getFreePlaces(freiePlaetzeKat3) +
                    getFreePlaces(freiePlaetzeKat4) + getFreePlaces(freiePlaetzeKat5) + getFreePlaces(freiePlaetzeKat6)
        }
        if (numberOfFreePlaces >= ALARM_THRESHOLD && !isAlarmSet) {
            informMe(url)
        }
        return numberOfFreePlaces
    }

    private fun informMe(url: String) {
        isAlarmSet = true
        val driver = ChromeDriver()
        driver.manage().window().maximize()
        driver.get(url)
        driver.findElement(By.id("btnCookieAccepted")).click()
        driver.findElement(By.id("login")).click()
        //Thread.sleep(1_000)

        driver.findElement(By.id("txt_email")).sendKeys("christian.egli@gmx.net")
        driver.findElement(By.id("txt_password")).sendKeys(shopPage.password)
        //Thread.sleep(1_000)
        val loginButton = driver.findElement(By.id("btn_login"))
        val webDriverWait = WebDriverWait(driver, Duration.ofMillis(1000))
        val webElement: WebElement = webDriverWait.until(ExpectedConditions.elementToBeClickable(loginButton))
        driver.executeScript("arguments[0].click();", webElement)

        driver.get(url)
    }
}

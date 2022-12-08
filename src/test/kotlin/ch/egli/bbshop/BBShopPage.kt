package ch.egli.bbshop

import com.codeborne.selenide.Selectors.byXpath
import com.codeborne.selenide.Selenide.element

// page_url = https://shop.buehnenbern.ch/SelectSeats?ret=2&e=3576&bts=bs
class BBShopPage {
    val infoElement = element(byXpath("//*[@id=\"lblInfo\"]/b"))

    val cookieButton = element(byXpath("//*[@id=\"btnCookieAccepted\"]"))

    val kat1 = element(byXpath("//*[@id=\"divSelectBestSeats\"]/div[2]/div[1]/div[3]"))
    val kat2 = element(byXpath("//*[@id=\"divSelectBestSeats\"]/div[2]/div[2]/div[3]"))
    val kat3 = element(byXpath("//*[@id=\"divSelectBestSeats\"]/div[2]/div[3]/div[3]"))
    val kat4 = element(byXpath("//*[@id=\"divSelectBestSeats\"]/div[2]/div[4]/div[3]"))
    val kat5 = element(byXpath("//*[@id=\"divSelectBestSeats\"]/div[2]/div[5]/div[3]"))
    val kat6 = element(byXpath("//*[@id=\"divSelectBestSeats\"]/div[2]/div[6]/div[3]"))

    val password = "*****"
}
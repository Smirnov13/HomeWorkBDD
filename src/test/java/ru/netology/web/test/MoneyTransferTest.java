package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;
import static ru.netology.web.data.DataHelper.generateValidAmount;

class MoneyTransferTest {
  DashboardPage dashboardPage;
  CardInfo firstCardInfo;
  CardInfo secondCardInfo;
  int firstCardBalance;
  int secondCardBalance;
  @BeforeEach
  void setup() {
    var loginPage = open("http://localhost:9999", LoginPage.class);
    var authInfo = getAuthInfo();
    var verificationPage = loginPage.validLogin(authInfo);
    var verificationCode = getVerificationCode();
    dashboardPage = verificationPage.validVerify(verificationCode);
    firstCardInfo = getFirstCardInfo();
    secondCardInfo = getSecondCardInfo();
    firstCardBalance = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
    secondCardBalance = dashboardPage.getCardBalance(1);
  }

  @Test
  void shouldTransferMoneyFromFirstToSecondCard() {
    var amount = generateValidAmount(firstCardBalance);
    var expectedBalanceFirstCard = firstCardBalance - amount;
    var expectedBalanceSecondCard = secondCardBalance + amount;
    var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
    dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
    dashboardPage.reloadDashboardPage();
    var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
    var actualBalanceSecondCard = dashboardPage.getCardBalance(1);
    assertAll(() -> assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard),
            () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));
  }


  @Test
  void shouldGetErrorMessageIfAmountMoreBalance() {
    var amount = generateInvalidAmount(secondCardBalance);
    var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
    transferPage.makeTransfer(String.valueOf(amount),secondCardInfo);
    transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
    dashboardPage.reloadDashboardPage();
    var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
    var actualBalanceSecondCard = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));
    assertAll(() -> assertEquals(firstCardBalance, actualBalanceFirstCard),
            () -> assertEquals(secondCardBalance, actualBalanceSecondCard));
  }
//  void shouldTransferMoneyBetweenOwnCardsV2() {
//    open("http://localhost:9999");
//    var loginPage = new LoginPageV2();
////    var loginPage = open("http://localhost:9999", LoginPageV2.class);
//    var authInfo = DataHelper.getAuthInfo();
//    var verificationPage = loginPage.validLogin(authInfo);
//    var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
//    verificationPage.validVerify(verificationCode);
//  }

//  @Test
//  void shouldTransferMoneyBetweenOwnCardsV3() {
//    var loginPage = open("http://localhost:9999", LoginPage.class);
//    var authInfo = getAuthInfo();
//    var verificationPage = loginPage.validLogin(authInfo);
//    var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
//    verificationPage.validVerify(verificationCode);
//  }
}


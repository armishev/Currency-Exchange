package main.service;

import main.entity.Currency;
import main.entity.ExchangeCurrency;
import main.entity.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface Service {
    List<Currency> getAllCurrencies() throws SQLException;
    List<ExchangeRate> getAllExchanges() throws SQLException;
    Currency getCurrencyByCode(String code) throws SQLException;
    ExchangeRate getRateByCodes(String codes) throws SQLException;
    Currency addCurrency(Currency currency) throws SQLException;
    ExchangeRate addRateCurrency(ExchangeRate exchangeRate) throws SQLException;
    ExchangeRate updateRateCurrency(String codes, BigDecimal rate) throws SQLException;
    ExchangeCurrency exchangeCurrency(String codes, BigDecimal amount) throws SQLException;
}

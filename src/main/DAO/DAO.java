package main.DAO;

import main.entity.Currency;
import main.entity.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface DAO {
    List<Currency> getAllCurrencies() throws SQLException;
    List<ExchangeRate> getAllExchangeRates() throws SQLException;
    Currency getCurrencyById(int id) throws SQLException;
    Currency getCurrencyByCode(String code) throws SQLException;
    ExchangeRate getRateByCodes(String code1, String code2) throws SQLException;
    Currency addCurrency(Currency currency) throws SQLException;
    ExchangeRate addRateCurrency(ExchangeRate exchangeRate) throws SQLException;
    ExchangeRate updateRateCurrency(ExchangeRate exchangeRate, BigDecimal rate) throws SQLException;
}

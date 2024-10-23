package main.service;

import main.DAO.DAO;
import main.DAO.DAOImpl;
import main.entity.Currency;
import main.entity.ExchangeCurrency;
import main.entity.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class ServiceImpl implements Service {
    final DAO dao = new DAOImpl();

    @Override
    public List<Currency> getAllCurrencies() throws SQLException {
        return dao.getAllCurrencies();
    }

    @Override
    public List<ExchangeRate> getAllExchanges() throws SQLException {
        return dao.getAllExchangeRates();
    }

    @Override
    public Currency getCurrencyByCode(String code) throws SQLException {
        if (code == null || code.isEmpty()) {
            throw new SQLException("Code is null or empty");
        }
        return dao.getCurrencyByCode(code);
    }

    @Override
    public ExchangeRate getRateByCodes(String codes) throws SQLException {
        if (codes == null || codes.isEmpty()) {
            throw new SQLException("Codes is null or empty");
        }
        String currency1 = codes.substring(0, 3);
        String currency2 = codes.substring(3, 6);
        return dao.getRateByCodes(currency1, currency2);
    }

    @Override
    public Currency addCurrency(Currency currency) throws SQLException {
        return dao.addCurrency(currency);

    }

    @Override
    public ExchangeRate addRateCurrency(ExchangeRate exchangeRate) throws SQLException {
        if (exchangeRate.getBaseCurrency().getCode() == null || exchangeRate.getTargetCurrency().getCode() == null) {
            throw new SQLException("Incorrect fields for the exchange rate");
        }
        try {
            exchangeRate.setBaseCurrency(dao.getCurrencyByCode(exchangeRate.getBaseCurrency().getCode()));
            exchangeRate.setTargetCurrency(dao.getCurrencyByCode(exchangeRate.getTargetCurrency().getCode()));
        } catch (SQLException e) {
            if (Objects.equals(e.getMessage(), "Currency not found")) {
                throw new SQLException("One (or both) currency from the currency pair does not exist in the database");
            }
        }
        return dao.addRateCurrency(exchangeRate);
    }

    @Override
    public ExchangeRate updateRateCurrency(String codes, BigDecimal rate) throws SQLException {
        if (codes == null || codes.isEmpty() || rate.compareTo(BigDecimal.ZERO) == 0) {
            throw new SQLException("Incorrect fields");
        }
        String currency1 = codes.substring(0, 3);
        String currency2 = codes.substring(3, 6);
        ExchangeRate exchangeRate = dao.getRateByCodes(currency1, currency2);
        return dao.updateRateCurrency(exchangeRate, rate);
    }

    @Override
    public ExchangeCurrency exchangeCurrency(String codes, BigDecimal amount) throws SQLException {


        ExchangeCurrency exchangeCurrency;
        ExchangeRate exchangeRate = null;
        ExchangeRate rateFromUSDTo1 = null;
        ExchangeRate rateFromUSDTo2 = null;
        String currency1 = codes.substring(0, 3);
        String currency2 = codes.substring(3, 6);
        try {
            exchangeRate = dao.getRateByCodes(currency1, currency2);
        }
        catch (SQLException e) {}
        if (exchangeRate != null) {
                exchangeCurrency = new ExchangeCurrency(exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate(), amount);

            return exchangeCurrency;
        }
        try {
        exchangeRate = dao.getRateByCodes(currency2, currency1);
        }
        catch (SQLException e) {}
        if (exchangeRate != null) {
            BigDecimal invertedRate = BigDecimal.ONE.divide(exchangeRate.getRate(), 4, RoundingMode.HALF_UP);
            exchangeCurrency = new ExchangeCurrency(exchangeRate.getTargetCurrency(), exchangeRate.getBaseCurrency(),invertedRate, amount);
            return exchangeCurrency;
        }
        try {
            rateFromUSDTo1 = dao.getRateByCodes("USD", currency1);
            rateFromUSDTo2 = dao.getRateByCodes("USD", currency2);
        } catch (SQLException e) {
        }
        if (rateFromUSDTo1 != null && rateFromUSDTo2!= null ) {
            BigDecimal invertedRateToUSD = BigDecimal.ONE.divide(rateFromUSDTo1.getRate(), 4, RoundingMode.HALF_UP);
            BigDecimal rateFromCurrency1ToCurrency2 = invertedRateToUSD.multiply(rateFromUSDTo2.getRate());
            exchangeCurrency = new ExchangeCurrency(rateFromUSDTo1.getTargetCurrency(), rateFromUSDTo2.getTargetCurrency(), rateFromCurrency1ToCurrency2, amount);
            return exchangeCurrency;
        }
        throw new SQLException("The conversion method was not found.");



















//        int flag = 0;
//
//        String[] codesToTry = {codes, currency2 + currency1};
//
//        for (String codePair : codesToTry) {
//            try {
//                exchangeRate1 = getRateByCodes(codePair);
//            }catch (SQLException e) {
//
//            }
//            if (exchangeRate1 != null) {
//                break;
//            }
//            flag = 1;
//        }
//        if(exchangeRate1 != null) {
//            BigDecimal exchangeRate = exchangeRate1.getRate();
//            if (flag == 1) {
//                exchangeRate = (new BigDecimal(1)).divide(exchangeRate, 2, RoundingMode.HALF_UP);
//                exchangeCurrency = new ExchangeCurrency(exchangeRate1.getTargetCurrency(), exchangeRate1.getBaseCurrency(), exchangeRate, amount);
//            }else{
//                exchangeCurrency = new ExchangeCurrency(exchangeRate1.getBaseCurrency(), exchangeRate1.getTargetCurrency(), exchangeRate, amount);
//            }
//        }else{
//            try {
//                exchangeRate1 = getRateByCodes("USD" + currency1);
//                exchangeRate2 = getRateByCodes("USD" + currency2);
//            }catch (SQLException e) {
//
//            }
//            if(exchangeRate1 != null && exchangeRate2 != null) {
//                BigDecimal calculateConvertedAmount = (amount.divide(exchangeRate1.getRate(), 2, RoundingMode.UP)).multiply(exchangeRate2.getRate());
//                exchangeCurrency = new ExchangeCurrency(exchangeRate1.getTargetCurrency(), exchangeRate2.getTargetCurrency(), amount, calculateConvertedAmount);
//            }
//
//        }
//
//        if(exchangeCurrency == null) {
//            throw new SQLException("The currency conversion method was not found");
//        }
//
//        return exchangeCurrency;


    }


}

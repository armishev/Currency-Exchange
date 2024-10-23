package main.DAO;

import main.entity.Currency;
import main.entity.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOImpl implements DAO {

    @Override
    public List<Currency> getAllCurrencies() throws SQLException {
        String sql = "select * from Currencies";
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String code = resultSet.getString("Code");
                String fullName = resultSet.getString("FullName");
                String sign = resultSet.getString("Sign");
                currencies.add(new Currency(id, code, fullName, sign));
            }
        }
        return currencies;
    }

    @Override
    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        String sql = "select * from ExchangeRates";
        List<ExchangeRate> rates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                Currency currency1 = getCurrencyById(resultSet.getInt("BaseCurrencyId"));
                Currency currency2 = getCurrencyById(resultSet.getInt("TargetCurrencyId"));
                BigDecimal rate = resultSet.getBigDecimal("Rate");
                rates.add(new ExchangeRate(id, currency1, currency2, rate));
            }
        }
        return rates;
    }


    @Override
    public Currency getCurrencyById(int id) throws SQLException {
        System.out.println("Fetching currency for ID: " + id);
        String sql = "select * from Currencies where ID = ?";
        Currency currency = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = new Currency(
                        resultSet.getInt("ID"),
                        resultSet.getString("Code"),
                        resultSet.getString("FullName"),
                        resultSet.getString("Sign")
                );
            }
        }
        return currency;

    }


    @Override
    public Currency getCurrencyByCode(String code) throws SQLException {
        String sql = "select * from Currencies where code = ?";
        Currency currency;
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = new Currency(
                        resultSet.getInt("ID"),
                        resultSet.getString("Code"),
                        resultSet.getString("FullName"),
                        resultSet.getString("Sign")
                );
            }
            else {
                throw new SQLException("Currency not found");
            }
        }
        return currency;

    }

    @Override
    public ExchangeRate getRateByCodes(String code1, String code2) throws SQLException {
        Currency currency1 = getCurrencyByCode(code1);
        Currency currency2 = getCurrencyByCode(code2);
        String sql = "select * from ExchangeRates where BaseCurrencyId = ? and TargetCurrencyId = ?";
        ExchangeRate exchangeRate;
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, currency1.getId());
            statement.setInt(2, currency2.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                exchangeRate = new ExchangeRate(
                        resultSet.getInt("ID"),
                        currency1,
                        currency2,
                        resultSet.getBigDecimal("Rate")
                );
            }else {
                throw new SQLException("Currency codes are missing");
            }
        }
        return exchangeRate;
    }

    @Override
    public Currency addCurrency(Currency currency) throws SQLException{
        String sql = "insert into Currencies(Code, FullName, Sign) values(?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            System.out.println(3);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                System.out.println(4);
                try(ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        currency.setId(resultSet.getInt(1));
                    }
                }
            }
            }
        return currency;

    }

    @Override
    public ExchangeRate addRateCurrency(ExchangeRate exchangeRate) throws SQLException{
        String sql = "insert into ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) values(?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, exchangeRate.getBaseCurrency().getId());
            statement.setInt(2, exchangeRate.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                System.out.println(4);
                try(ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        exchangeRate.setId(resultSet.getInt(1));
                    }
                }
            }
        }
        return exchangeRate;

    }



    @Override
    public ExchangeRate updateRateCurrency(ExchangeRate exchangeRate, BigDecimal rate) throws SQLException {
        String sql = "update ExchangeRates set Rate = ? where BaseCurrencyId = ? and TargetCurrencyId = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setBigDecimal(1, rate);
            statement.setInt(2, exchangeRate.getBaseCurrency().getId());
            statement.setInt(3, exchangeRate.getTargetCurrency().getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                exchangeRate.setRate(rate);
            }else{
                throw new SQLException("Currency codes are missing in rates");
            }
        }
        return exchangeRate;

    }
}

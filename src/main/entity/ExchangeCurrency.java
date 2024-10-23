package main.entity;

import java.math.BigDecimal;

public class ExchangeCurrency {
    private final Currency currency1;
    private final Currency currency2;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;


    public ExchangeCurrency(Currency currency1, Currency currency2, BigDecimal rate, BigDecimal amount) {
        this.currency1 = currency1;
        this.currency2 = currency2;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = amount.multiply(rate);
    }


    @Override
    public String toString() {
        return "ExchangeCurrency{" +
                "currency1=" + currency1 +
                ", currency2=" + currency2 +
                ", rate=" + rate +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                '}';
    }
}

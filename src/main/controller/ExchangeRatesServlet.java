package main.controller;


import com.google.gson.Gson;
import main.entity.Currency;
import main.entity.ExchangeRate;
import main.service.Service;
import main.service.ServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    Service service = new ServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates;
        try {
            exchangeRates = service.getAllExchanges();

        } catch (SQLException e) {
            resp.sendError(500, "Database error");
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(exchangeRates);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        currency1.setCode(req.getParameter("baseCurrencyCode"));
        currency2.setCode(req.getParameter("targetCurrencyCode"));
        ExchangeRate exchangeRate = new ExchangeRate(0, currency1,
                currency2, new BigDecimal(req.getParameter("rate")));
        try {
            exchangeRate = service.addRateCurrency(exchangeRate);
        } catch (SQLException e) {
            if(e.getMessage().equals("One (or both) currency from the currency pair does not exist in the database")){
                resp.sendError(404, "One (or both) currency from the currency pair does not exist in the database");
                return;
            }else if(e.getMessage().equals("Incorrect fields for the exchange rate")) {
                resp.sendError(400, "Incorrect fields for the exchange rate");
            }else if(e.getErrorCode() == 1062){
                resp.sendError(409, "A currency pair with this code already exists");
            }else {
                resp.sendError(500, "Database error");
            }
            return;
        }
        resp.setStatus(201);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(exchangeRate);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();


    }



}


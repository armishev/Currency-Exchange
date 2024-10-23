package main.controller;


import com.google.gson.Gson;
import main.entity.Currency;
import main.service.Service;
import main.service.ServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    Service service = new ServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies;
        try {
            currencies = service.getAllCurrencies();

        } catch (SQLException e) {
            resp.sendError(500, "Database error");
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(currencies);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency currency = new Currency(0, req.getParameter("code"),
                req.getParameter("name"), req.getParameter("sign"));
        try {
            System.out.println(1111);
            System.out.println(currency.getSign());
            currency = service.addCurrency(currency);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(currency);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();


    }

}


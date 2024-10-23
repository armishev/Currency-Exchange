package main.controller;


import com.google.gson.Gson;
import main.entity.ExchangeCurrency;
import main.service.Service;
import main.service.ServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet{
    Service service = new ServiceImpl();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fromCurrency = req.getParameter("from");
        String toCurrency = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        // Проверка обязательных параметров
        if (fromCurrency == null || toCurrency == null || amountStr == null) {
            sendError(resp, "The fields are incorrect");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            sendError(resp, "Amount is not a number");
            return;
        }

        try {
            ExchangeCurrency exchangeCurrency = service.exchangeCurrency(fromCurrency + toCurrency, amount);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(exchangeCurrency);
            PrintWriter out = resp.getWriter();
            out.print(jsonResponse);
            out.flush();
        } catch (SQLException e) {
            if(e.getMessage().equals("The conversion method was not found.") )
                sendError(resp, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, String errorMessage) throws IOException {
        // Устанавливаем статус ответа 404 и отправляем предопределённое сообщение в формате JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);  // Статус 404
        PrintWriter out = resp.getWriter();
        out.print("{ \"message\": \"" + errorMessage + "\" }");
        out.flush();
    }

}

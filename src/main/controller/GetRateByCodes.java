package main.controller;


import com.google.gson.Gson;
import main.entity.ExchangeRate;
import main.service.Service;
import main.service.ServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class GetRateByCodes extends HttpServlet {
    Service service = new ServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRate exchangeRate;
        try {
            System.out.println(req.getPathInfo().substring(1));
            exchangeRate = service.getRateByCodes(req.getPathInfo().substring(1));
            System.out.println(req.getPathInfo().substring(1));
        } catch (SQLException e) {
            if(e.getMessage().equals("Currency codes are missing")) {
                resp.sendError(404, "Currency codes are missing");
            }
            else if(e.getMessage().equals("Codes is null or empty")){
                resp.sendError(400, "Codes is null or empty");
            }else{
                resp.sendError(500, "Database error");
            }
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(exchangeRate);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp); // Передаёт управление для других методов (GET, POST, PUT, DELETE)
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        // Предполагая, что тип контента application/x-www-form-urlencoded
        Map<String, String> params = new HashMap<>();
        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = URLDecoder.decode(keyValue[1], "UTF-8");
                params.put(key, value);
            }
        }

        String rateParam = params.get("rate");
        if(rateParam == null){
            resp.sendError(400, "Incorrect fields");
            return;
        }
        ExchangeRate exchangeRate;
        try {
            System.out.println(rateParam);
            exchangeRate = service.updateRateCurrency(req.getPathInfo().substring(1), new BigDecimal(rateParam));
        } catch (SQLException e) {
            if(e.getMessage().equals("Currency not found") || e.getMessage().equals("Currency codes are missing")) {
                resp.sendError(404, "Currency(ies) not found in rates");
            }
            else if(e.getMessage().equals("Incorrect fields")){
                resp.sendError(400, "Incorrect fields");
            }else{
                resp.sendError(500, "Database error");
            }
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(exchangeRate);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

}

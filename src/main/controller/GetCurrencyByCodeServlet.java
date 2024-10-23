package main.controller;


import com.google.gson.Gson;
import main.service.Service;
import main.service.ServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import main.entity.Currency;

@WebServlet("/currency/*")
public class GetCurrencyByCodeServlet extends HttpServlet {
    Service service = new ServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency currency;
        try {
            currency = service.getCurrencyByCode(req.getPathInfo().substring(1));
            System.out.println(req.getPathInfo().substring(1));
        } catch (SQLException e) {
            if(e.getMessage().equals("Currency not found")) {
                resp.sendError(404, "Currency not found");
            }
            else if(e.getMessage().equals("Code is null or empty")){
                resp.sendError(400, "Currency code is null or empty");
            }else{
                resp.sendError(500, "Database error");
            }
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

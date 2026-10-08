package com.smartcampus.helpdesk.controller;

import com.smartcampus.helpdesk.dao.CategoryDAO;
import com.smartcampus.helpdesk.model.Category;
import com.smartcampus.helpdesk.model.UserRole;
import com.smartcampus.helpdesk.util.AuthUtil;
import com.smartcampus.helpdesk.util.CategoryXmlExchange;
import com.smartcampus.helpdesk.util.XmlExchangeException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

@WebServlet({"/admin/category-xml", "/admin/categories/export.xml", "/admin/categories/import"})
@MultipartConfig(maxFileSize = 1_048_576, maxRequestSize = 1_100_000)
public class CategoryXmlServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (request.getServletPath().endsWith("export.xml")) {
            exportCategories(response);
            return;
        }
        request.getRequestDispatcher("/admin/category-xml.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Part file = request.getPart("categoriesFile");
        if (file == null || file.getSize() == 0) {
            showError(request, response, "Choose a category XML file to import.");
            return;
        }

        try (InputStream input = file.getInputStream()) {
            List<Category> categories = CategoryXmlExchange.importCategories(input);
            int imported = categoryDAO.importConfiguration(categories);
            response.sendRedirect(request.getContextPath() + "/admin/category-xml?imported=" + imported);
        } catch (XmlExchangeException exception) {
            showError(request, response, "The XML file is invalid or does not match the category schema.");
        } catch (SQLException exception) {
            showError(request, response, "The category configuration could not be imported.");
        }
    }

    private void exportCategories(HttpServletResponse response) throws IOException {
        try {
            byte[] xml = CategoryXmlExchange.exportCategories(categoryDAO.findAll());
            response.setContentType("application/xml");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=helpdesk-categories.xml");
            response.getOutputStream().write(xml);
        } catch (SQLException | XmlExchangeException exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "The category export is temporarily unavailable.");
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/admin/category-xml.jsp").forward(request, response);
    }

    private boolean isAdmin(HttpServletRequest request) {
        return AuthUtil.getRole(request) == UserRole.ADMIN;
    }
}

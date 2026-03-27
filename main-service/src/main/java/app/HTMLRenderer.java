package app;

import java.util.List;

public class HTMLRenderer {

    public static String renderPage(List<Quote> allQuotes, Quote qod) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='en'>");
        sb.append("<head>");
        sb.append("<meta charset='UTF-8'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        sb.append("<title>Quotes Management</title>");
        sb.append("</head>");
        sb.append("<body style='font-family: Arial, sans-serif; margin: 40px;'>");

        sb.append("<h1>Quotes Management</h1>");

        // QOD
        sb.append("<h2>Quote of the Day</h2>");
        sb.append("<div style='padding: 15px; border: 1px solid #ccc; margin-bottom: 30px;'>");

        if (qod == null) {
            sb.append("<p><em>Quote of the day is currently empty.</em></p>");
        } else {
            sb.append("<p><strong>Quote:</strong> ").append(escapeHtml(qod.getQuote())).append("</p>");
            sb.append("<p><strong>Author:</strong> ").append(escapeHtml(qod.getAuthor())).append("</p>");
        }

        sb.append("</div>");

        // Forma
        sb.append("<h2>Add New Quote</h2>");
        sb.append("<form method='POST' action='/save-quote'>");

        sb.append("<div style='margin-bottom: 10px;'>");
        sb.append("<label>Quote:</label><br>");
        sb.append("<input type='text' name='quote' style='width: 400px; padding: 8px;' required>");
        sb.append("</div>");

        sb.append("<div style='margin-bottom: 10px;'>");
        sb.append("<label>Author:</label><br>");
        sb.append("<input type='text' name='author' style='width: 400px; padding: 8px;' required>");
        sb.append("</div>");

        sb.append("<button type='submit' style='padding: 10px 20px;'>Save Quote</button>");
        sb.append("</form>");

        // Lista
        sb.append("<h2 style='margin-top: 40px;'>Saved Quotes</h2>");

        if (allQuotes.isEmpty()) {
            sb.append("<p>No quotes saved yet.</p>");
        } else {
            sb.append("<ul>");
            for (Quote q : allQuotes) {
                sb.append("<li style='margin-bottom: 10px;'>");
                sb.append("\"").append(escapeHtml(q.getQuote())).append("\"");
                sb.append(" - <strong>").append(escapeHtml(q.getAuthor())).append("</strong>");
                sb.append("</li>");
            }
            sb.append("</ul>");
        }

        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
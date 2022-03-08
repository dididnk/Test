package fr.iut.td.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import fr.iut.td.db.ConnectionFactory;
import fr.iut.td.json.JsonBuilder;
import fr.iut.td.log.Logger;

public class Quid extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private ConnectionFactory database;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    try {
      String dbFolder = config.getServletContext().getRealPath("./WEB-INF/db/");
      database = new ConnectionFactory("org.h2.Driver", dbFolder, "sa", "");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  public void destroy() {
    super.destroy();

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doSend(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doSend(req, resp);
  }

  private void doSend(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      String order = req.getParameter("Order");
      if (order == null)
        order = "";
      Logger.info("Order : " + order);

      switch (order) {
      case "ListUser" -> sendListUser(req, resp);
      case "AddUser" -> sendAddUser(req, resp);
      case "DelUser" -> sendDelUser(req, resp);
      case "GetImage" -> sendGetImage(req, resp);
      default -> sendHtml(resp, "<h1>Je ne comprends pas votre requête !</h1>");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException(e);
    }

  }

  private void sendHtml(HttpServletResponse resp, String html) throws IOException {
    resp.setHeader("content-type", "text/html");
    try (
        Writer out = resp.getWriter(); ) {
      out.append(html);
    }
  }

  private void sendJson(HttpServletResponse resp, String json) throws IOException {
    resp.setHeader("content-type", "application/json");
    try (
        Writer out = resp.getWriter(); ) {
      out.append(json);
    }
  }

  private void sendAddUser(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    String birthDate = req.getParameter("birthDate");
    String isDead = req.getParameter("isDead");
    String size = req.getParameter("size");
    try (
        Connection con = database.getConnection();
        PreparedStatement addUser = con
            .prepareStatement(
                "INSERT INTO wuser(created,isDead, size,birthDate, firstName, lastName) VALUES(now(),?,?,? ?,?)"); ) {

      int index = 1;
      addUser.setString(index++, isDead);
      addUser.setString(index++, size);
      addUser.setString(index++, birthDate);
      addUser.setString(index++, firstName);
      addUser.setString(index++, lastName);

      addUser.executeUpdate();
    }

    sendHtml(resp, "OK");
  }

  private void sendDelUser(HttpServletRequest req, HttpServletResponse resp) throws Exception {

    try (
        Connection con = database.getConnection();
        PreparedStatement delUser = con.prepareStatement("DELETE FROM WUser WHERE idWUser=?"); ) {
      delUser.setString(1, req.getParameter("idUser"));
      delUser.executeUpdate();
    }

    sendJson(resp, "{ \"code\":0 }");
  }

  private void sendListUser(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    JsonBuilder jb = new JsonBuilder();

    try (
        Connection con = database.getConnection();
        PreparedStatement usersStmt = con
            .prepareStatement("SELECT idWUser, firstName, lastName, isDead, size, birthDate FROM wuser");
        ResultSet usersRs = usersStmt.executeQuery(); ) {

      jb.begin();
      jb.addField("code", 0);
      jb.beginArray("data");
      while (usersRs.next()) {
        int index = 1;
        jb
            .beginObject()
            .addField("idUser", usersRs.getString(index++))
            .addField("firstName", usersRs.getString(index++))
            .addField("lastName", usersRs.getString(index++))
            .addField("isDead", usersRs.getString(index++))
            .addField("size", usersRs.getString(index++))
            .addField("birthDate", usersRs.getString(index++))
            .endObject();
      }
      jb.endArray();
      jb.end();
      sendJson(resp, jb.toString());
    }
  }

  private void sendListUserHTML(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    StringBuilder sb = new StringBuilder();
    try (
        Connection con = database.getConnection();
        PreparedStatement usersStmt = con
            .prepareStatement("SELECT idWUser, firstName, lastName, isDead, size, birthDate FROM wuser");
        ResultSet usersRs = usersStmt.executeQuery(); ) {

      sb
          .append("<!DOCTIPE HTML>")
          .append("<html>")
          .append("<head>")
          .append("<meta charset=\"UTF-8\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/styles.css\" />")
          .append("</head>")
          .append("<body>")
          .append("  <h1>Liste des utilisateurs</h1>")
          .append("  <table>")
          .append("  <thead>")
          .append("  <tr><th>Id</th><th>Prénom</th><th>Nom</th><th>est mort</th><th>Taille</th><th>Naissance</th></tr>")
          .append("  </thead>")
          .append("  <tbody>");

      while (usersRs.next()) {
        int index = 1;
        sb
            .append("  <tr>")
            .append("    <td>").append(usersRs.getString(index++)).append("</td>")
            .append("    <td>").append(usersRs.getString(index++)).append("</td>")
            .append("    <td>").append(usersRs.getString(index++)).append("</td>")
            .append("    <td>").append(usersRs.getString(index++)).append("</>")
            .append("    <td>").append(usersRs.getString(index++)).append("</td>")
            .append("    <td>").append(usersRs.getString(index++)).append("</td>")
            .append("  </tr>");
      }

      sb
          .append("  </tbody>")
          .append("  </table>")
          .append("</body>")
          .append("</html>");
    }

    sendHtml(resp, sb.toString());
  }

  private void sendGetImage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    //"https://www.lug.com/tmp/smiles/"
    int id = Integer.parseInt(req.getParameter("id")) % 10;

    URL url = new URL("https://www.lug.com/tmp/smiles/" + id + ".png");
    URLConnection urlCon = url.openConnection();
    resp.setContentLength(urlCon.getContentLength());
    resp.setContentType(urlCon.getContentType());

    try (
        BufferedInputStream inputStream = new BufferedInputStream(urlCon.getInputStream());
        OutputStream out = resp.getOutputStream(); ) {
      byte data[] = new byte[1024];
      int nb;
      while ((nb = inputStream.read(data, 0, 1024)) != -1) {
        out.write(data, 0, nb);
      }
    }
  }

}

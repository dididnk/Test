package fr.iut.td.json;

import java.util.Date;

public class JsonBuilder {
  StringBuilder sb = new StringBuilder();

  public JsonBuilder begin() {
    sb.append('{');
    return this;
  }

  public JsonBuilder end() {
    sb.append('}');
    return this;
  }

  public JsonBuilder beginArray(String fieldName) {
    addComaOpt();
    addFieldName(fieldName);
    sb.append('[');
    return this;
  }

  public JsonBuilder endArray() {
    sb.append(']');
    return this;
  }

  public JsonBuilder beginObject() {
    addComaOpt();
    sb.append('{');
    return this;
  }

  public JsonBuilder beginObject(String fieldName) {
    addComaOpt();
    addFieldName(fieldName);
    sb.append('{');
    return this;
  }

  public JsonBuilder endObject() {
    sb.append('}');
    return this;
  }

  public JsonBuilder addField(String name, String value) {
    addComaOpt();
    addFieldName(name);
    sb.append("\"").append(value).append("\"");
    return this;
  }

  private void addComaOpt() {
    char c = sb.charAt(sb.length() - 1);
    if (c == ',')
      return;

    if (c != '{' && c != '[') {
      sb.append(",");
      return;
    }
    if (c == '}' || c == ']')
      sb.append(",");
  }

  private void addFieldName(String name) {
    sb.append('"').append(name).append("\":");
  }

  public JsonBuilder addField(String name, Number value) {
    addComaOpt();
    addFieldName(name);
    sb.append("" + value);
    return this;
  }

  public JsonBuilder addField(String name, Date value) {
    addComaOpt();
    addFieldName(name);
    sb.append("" + value.getTime());
    return this;
  }

  public JsonBuilder addField(String name, boolean value) {
    addComaOpt();
    addFieldName(name);
    sb.append(value);
    return this;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}

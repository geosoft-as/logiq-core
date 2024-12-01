package no.geosoft.logiq.core.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParsingException;

import no.geosoft.cc.util.ISO8601DateParser;

/**
 * A collection of utilities for working with JSON.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public final class JsonUtil
{
  /**
   * Private constructor to prevent client instantiation.
   */
  private JsonUtil()
  {
    assert false;
  }

  /**
   * Check if the specified text is a valid JSON structure.
   *
   * @param text  Text to check. May be null in case false is returned.
   * @return      True if the text is a valid JSON structure, false otherwise.
   */
  public static boolean isValid(String text)
  {
    if (text == null)
      return false;

    try {
      Json.createReader(new StringReader(text)).read();
      return true;
    }
    catch (JsonParsingException exception) {
      return false;
    }
    catch (JsonException exception) {
      return false;
    }
  }

  /**
   * Encode the given string by appropriate escape sequences
   * so that it can be used as a JSON literal.
   * <p>
   * <b>NOTE: </b>The returned value includes surrounding quotes.
   *
   * @param text  Text to encode. Non-null. Without quotes.
   * @return      Encoded text. Never null. Includes surrounding quotes.
   * @throws IllegalArgumentException  If text is null.
   */
  public static String encode(String text)
  {
    if (text == null)
      throw new IllegalArgumentException("text cannot be null");

    // We could scan the text and escape as necessary.
    // Better is to let javax.json fix this, but as there is no public
    // method that does this conversion we add the text to a phony JSON
    // object and then extract it again. Probably not as efficient, but
    // JSON Well Log Format doesn't include many strings anyway.
    return Json.createObjectBuilder().add("x", text).build().get("x").toString();
  }

  /**
   * Return the specified object as a string that can be embedded directly
   * into a JSON string. To make sense, the object should be of numeric type,
   * boolean, date or string. A string type will return with surrounding quotes.
   *
   * @param value  Value to get as JSON string.
   *               May be null, in case "null" will be returned.
   * @return A JSON string representation of value. Never null.
   */
  public static String getAsString(Object value)
  {
    //
    // Null
    //
    if (value == null)
      return "null";

    //
    // Collection
    //
    if (Collection.class.isAssignableFrom(value.getClass())) {
      Collection<?> values = (Collection<?>) value;

      StringBuilder s = new StringBuilder();
      s.append("[");

      boolean isFirst = true;
      for (Object v : values) {
        if (!isFirst)
          s.append(',');

        s.append(JsonUtil.getAsString(v));
        isFirst = false;
      }
      s.append(']');
      return s.toString();
    }

    //
    // JsonSerializable
    //
    if (value instanceof JsonSerializable) {
      return ((JsonSerializable) value).toJson();
    }

    //
    // Date
    //
    if (value instanceof Date) {
      Date date = (Date) value;
      return '\"' + ISO8601DateParser.toString(date) + '\"';
    }

    //
    // Boolean
    //
    if (value instanceof Boolean) {
      boolean v = (Boolean) value;
      return v ? "true" : "false";
    }

    //
    // Number
    //
    if (value instanceof Number) {
      Number number = (Number) value;
      if (value instanceof Double || value instanceof Float)
        return "" + number.doubleValue();
      else
        return "" + number.longValue();
    }

    //
    // String or other
    //
    String text = value.toString();
    return encode(text);
  }

  /**
   * Return the associated Java class for the given JSON value.
   *
   * @param jsonValue  JSON value to get associated Java class for.
   * @return           Associated Java class. Never null.
   * @throws IllegalArgumentException  If jsonValue is null.
   */
  public static Class<?> getClass(JsonValue jsonValue)
  {
    if (jsonValue == null)
      throw new IllegalArgumentException("jsonValue cannot be null");

    if (jsonValue instanceof JsonString)
      return String.class;

    else if (jsonValue instanceof JsonNumber) {
      JsonNumber jsonNumber = (JsonNumber) jsonValue;
      double doubleValue = jsonNumber.doubleValue();
      long longValue = jsonNumber.longValue();
      int intValue = jsonNumber.intValue();

      if (doubleValue != (double) longValue)
        return Double.class;

      if (intValue == (int) longValue)
        return Integer.class;

      return Long.class;
    }

    else if (jsonValue instanceof JsonArray)
      return Collection.class;

    else
      return Object.class;
  }

  /**
   * Return the specified jsonValue as a core Java instance of the
   * given class.
   *
   * @param jsonValue  JSON value to consider. May be null, in case null is returned.
   * @param clazz      Class of instance to return. Non-null.
   * @return           The requested value. Null if input value is null.
   * @throws IllegalArgumentException  If clazz is null.
   */
  public static Object getAsType(JsonValue jsonValue, Class<?> clazz)
  {
    if (clazz == null)
      throw new IllegalArgumentException("clazz cannot be null");

    if (jsonValue == null)
      return null;

    if (jsonValue == JsonValue.NULL)
      return null;

    if (jsonValue == JsonValue.FALSE)
      return Boolean.FALSE;

    if (jsonValue == JsonValue.TRUE)
      return Boolean.TRUE;

    if (clazz == String.class) {
      if (jsonValue instanceof JsonString)
        return ((JsonString) jsonValue).getString();
      else
        return jsonValue.toString();
    }

    if (Number.class.isAssignableFrom(clazz)) {
      if (jsonValue instanceof JsonNumber) {
        JsonNumber jsonNumber = (JsonNumber) jsonValue;
        if (clazz == double.class || clazz == Double.class)
          return (Double) jsonNumber.doubleValue();

        if (clazz == float.class || clazz == Float.class)
          return (float) jsonNumber.doubleValue();

        if (clazz == long.class || clazz == Long.class)
          return (Long) jsonNumber.longValue();

        if (clazz == int.class || clazz == Integer.class)
          return (Integer) jsonNumber.intValue();

        if (clazz == short.class || clazz == Short.class)
          return (short) jsonNumber.intValue();

        if (clazz == byte.class || clazz == Byte.class)
          return (byte) jsonNumber.intValue();

        if (clazz == Number.class)
          return (Number) jsonNumber.doubleValue();
      }

      else if (jsonValue instanceof JsonString) {
        JsonString jsonString = (JsonString) jsonValue;

        String s = jsonValue.toString();


        // TODO: Parse
        assert false;
      }
    }

    return jsonValue;
  }

  /**
   * Return a equivalent <em>pretty</em> version of the specified JSON string.
   * <p>
   * <b>Note: </b>With the javax.json package it is not possible to specify
   * the indentation size. This appear hard coded to 4.
   *
   * @param jsonString  JSON string to get pretty version of. Non-null.
   * @return            The requested pretty version, or the input string in case
   *                    it is not valid JSON in the first place. Never null.
   * @throws IllegalArgumentException  If jsonString is null.
   */
  public static String toPretty(String jsonString)
  {
    if (jsonString == null)
      throw new IllegalArgumentException("jsonString cannot be null");

    StringWriter stringWriter = new StringWriter();
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
      JsonStructure jsonStructure = jsonReader.read();

      Map<String, Object> configuration = new HashMap<>();
      configuration.put(JsonGenerator.PRETTY_PRINTING, true);

      JsonWriterFactory writerFactory = Json.createWriterFactory(configuration);
      JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);
      jsonWriter.write(jsonStructure);
      jsonWriter.close();
    }
    catch(JsonParsingException exception) {
      exception.printStackTrace();

      // Apparently the input was not valid JSON so we just return it as is
      return jsonString;
    }

    return stringWriter.toString();
  }


  /**
   * Add the specified key/value to the given JSON object builder.
   *
   * @param jsonObjectBuilder  JSON object builder to add to. Non-null.
   * @param key                Key to add. Non-null.
   * @param value              Value of key. May be null.
   */
  private static boolean add(JsonObjectBuilder jsonObjectBuilder, String key, Object value)
  {
    assert jsonObjectBuilder != null : "jsonObjectBuilder annot be null";
    assert key != null : "key cannot be null";

    if (value == null)
      jsonObjectBuilder.addNull(key);

    else if (value instanceof BigDecimal)
      jsonObjectBuilder.add(key, (BigDecimal) value);

    else if (value instanceof BigInteger)
      jsonObjectBuilder.add(key, (BigInteger) value);

    else if (value instanceof Boolean)
      jsonObjectBuilder.add(key, (Boolean) value);

    else if (value instanceof Double) {
      Double v = (Double) value;
      if (Double.isFinite(v))
        jsonObjectBuilder.add(key, v);
      else
        jsonObjectBuilder.addNull(key);
    }

    else if (value instanceof Float) {
      Float v = (Float) value;
      if (Float.isFinite(v))
        jsonObjectBuilder.add(key, v.doubleValue());
      else
        jsonObjectBuilder.addNull(key);
    }

    else if (value instanceof Integer)
      jsonObjectBuilder.add(key, (Integer) value);

    else if (value instanceof Long)
      jsonObjectBuilder.add(key, (Long) value);

    else if (value instanceof Short)
      jsonObjectBuilder.add(key, ((Number) value).intValue());

    else if (value instanceof Byte)
      jsonObjectBuilder.add(key, ((Number) value).intValue());

    else if (value instanceof String)
      jsonObjectBuilder.add(key, (String) value);

    else if (value instanceof Date)
      jsonObjectBuilder.add(key, ISO8601DateParser.toString((Date) value));

    else if (value instanceof JsonArray)
      jsonObjectBuilder.add(key, (JsonArray) value);

    else if (value instanceof JsonObject)
      jsonObjectBuilder.add(key, (JsonObject) value);

    else if (value instanceof JsonValue)
      add(jsonObjectBuilder, key, getValue((JsonValue) value));

    else
      return false; // Nothing was added

    return true;
  }

  /**
   * Return the fundamental value of the specified JSON value.
   *
   * @param jsonValue  JSON value to get fundamental value from. Non-null.
   * @return           Requested value. May be null, if jsonValue is of NULL type.
   */
  public static Object getValue(JsonValue jsonValue)
  {
    assert jsonValue != null : "jsonValue cannot be null";

    switch (jsonValue.getValueType()) {
      case ARRAY :
      case OBJECT :
        return jsonValue;

      case NUMBER :
        JsonNumber number = (JsonNumber) jsonValue;
        if (number.isIntegral())
          return number.intValueExact(); // TODO: Handle longs
        else
          return number.doubleValue();

      case STRING :
        return ((JsonString) jsonValue).getString();

      case TRUE :
        return true;

      case FALSE :
        return false;

      case NULL :
        return null;

      default:
        assert false : "Unrecognized value type: " + jsonValue.getValueType();
        return null;
    }
  }

  /**
   * Add the specified value to the given JSON array builder.
   *
   * @param jsonArrayBuilder  JSON array builder to add to. Non-null.
   * @param value             Value to add. May be null.
   */
  private static boolean add(JsonArrayBuilder jsonArrayBuilder, Object value)
  {
    assert jsonArrayBuilder != null : "jsonArrayBuilder annot be null";

    if (value == null)
      jsonArrayBuilder.addNull();

    else if (value instanceof BigDecimal)
      jsonArrayBuilder.add((BigDecimal) value);

    else if (value instanceof BigInteger)
      jsonArrayBuilder.add((BigInteger) value);

    else if (value instanceof Boolean)
      jsonArrayBuilder.add((Boolean) value);

    else if (value instanceof Double) {
      Double v = (Double) value;
      if (Double.isFinite(v))
        jsonArrayBuilder.add(v);
      else
        jsonArrayBuilder.addNull();
    }

    else if (value instanceof Float) {
      Float v = (Float) value;
      if (Float.isFinite(v))
        jsonArrayBuilder.add(v.doubleValue());
      else
        jsonArrayBuilder.addNull();
    }

    else if (value instanceof Integer)
      jsonArrayBuilder.add((Integer) value);

    else if (value instanceof Long)
      jsonArrayBuilder.add((Long) value);

    else if (value instanceof Short)
      jsonArrayBuilder.add(((Number) value).intValue());

    else if (value instanceof Byte)
      jsonArrayBuilder.add(((Number) value).intValue());

    else if (value instanceof String)
      jsonArrayBuilder.add((String) value);

    else if (value instanceof Date)
      jsonArrayBuilder.add(ISO8601DateParser.toString((Date) value));

    else if (value instanceof JsonArray)
      jsonArrayBuilder.add((JsonArray) value);

    else if (value instanceof JsonObject)
      jsonArrayBuilder.add((JsonObject) value);

    else if (value instanceof JsonValue)
      add(jsonArrayBuilder, getValue((JsonValue) value));

    else
      return false; // Nothing was added

    return true;
  }
}

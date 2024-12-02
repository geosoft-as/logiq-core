package no.geosoft.logiq.core.jsonrpc;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import no.geosoft.cc.util.Indentation;
import no.geosoft.cc.util.Counter;

import no.geosoft.logiq.core.json.JsonUtil;

/**
 * Models a request message according to the JSON-RPC version 2.0.
 * <p>
 * JSON-RPC requests have the following generic structure:
 * <br>
 * <pre>
 *   {
 *     "jsonrpc": "2.0",
 *     "method": &lt;method&gt;,
 *     "params": [&lt;parameter1&gt;, &lt;parameter2&gt;, ...],
 *     "id": &lt;id&gt;
 *   }
 * </pre>
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public final class Request
{
  /** Name of method to invoke. */
  private final String method_;

  /** Parameter values to be used during the invocation of the method. */
  private final List<Object> params_ = new ArrayList<>();

  /**
   * ID of this request message, or ID of the corresponding request
   * message of this response message.
   */
  private final long id_;

  /** Time the message was created. */
  private final long time_ = System.currentTimeMillis();

  /**
   * Create a request message.
   * <p>
   * Use this constructor if the message ID must be controlled
   * from outside.
   *
   * @param method  Name of the method to invoke. Non-null.
   * @param params  Method parameters to apply. Non-null.
   * @param id      Message ID.
   * @throws IllegalArgumentException  If method or params is null.
   */
  public Request(String method, List<Object> params, long id)
  {
    if (method == null)
      throw new IllegalArgumentException("method cannot be null");

    if (params == null)
      throw new IllegalArgumentException("params cannot be null");

    method_ = method;
    params_.addAll(params);
    id_ = id;
  }

  /**
   * Create a request message.
   * <p>
   * Use this constructor if the the message ID does not need to be
   * controlled from the outside. In this case it a unique ID will
   * be auto-created.
   *
   * @param method  Name of the method to invoke. Non-null.
   * @param params  Method parameters to apply. Non-null.
   * @throws IllegalArgumentException  If method or params is null.
   */
  public Request(String method, Object... params)
  {
    if (method == null)
      throw new IllegalArgumentException("method cannot be null");

    if (params == null)
      throw new IllegalArgumentException("params cannot be null");

    method_ = method;

    for (Object param : params)
      params_.add(param);
    id_ = Counter.get();
  }

  /**
   * Create a new message instance from the specified JSON string.
   *
   * @param jsonString  JSON string to create instance from. Non-null.
   * @throws IllegalArgumentException  If jsonString is null.
   * @throws JsonParsingException      If jsonString is not a valid JSON string or
   *                                   if it doesn't constitute a valid instance.
   */
  public Request(String jsonString)
    throws JsonParsingException
  {
    if (jsonString == null)
      throw new IllegalArgumentException("jsonString cannot be null");

    JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
    JsonObject jsonObject = jsonReader.readObject();

    //
    // Method
    //
    method_ = jsonObject != null ? jsonObject.getString("method") : null;
    if (method_ == null)
      throw new JsonParsingException("method must be present", null);

    //
    // Params
    //
    JsonArray params = jsonObject.getJsonArray("params");
    if (params != null) {
      for (int i = 0; i < params.size(); i++) {
        JsonValue jsonValue = params.get(i);
        Class<?> paramType = JsonUtil.getClass(jsonValue);
        Object param = JsonUtil.getAsType(jsonValue, paramType);
        params_.add(param);
      }
    }

    //
    // ID
    //
    JsonNumber id = jsonObject.getJsonNumber("id");
    if (id == null)
      throw new JsonParsingException("id must be present", null);

    id_ = id.longValue();
  }

  /**
   * Return method of this message.
   *
   * @return  Method of this message. Never null.
   */
  public String getMethod()
  {
    return method_;
  }

  /**
   * Return the parameters of this message.
   *
   * @return  Parameters of this message. Never null.
   */
  public List<Object> getParams()
  {
    return Collections.unmodifiableList(params_);
  }

  /**
   * Get a specific parameter of this message.
   * Convenient if the caller knows the parameters exactly.
   *
   * @param paramNo  Parameter number to get. [0,&gt;.
   * @return         The requested parameter, or null if doesn't exist.
   * @throws IllegalArgumentException  If parameterNo is &lt; 0.
   */
  public Object getParam(int paramNo)
  {
    if (paramNo < 0)
      throw new IllegalArgumentException("Invalid paramNo: " + paramNo);

    return paramNo < params_.size() ? params_.get(paramNo) : null;
  }


  /**
   * Return ID of this request message or the ID of the correspoding
   * request message of this response message.
   *
   * @return  ID of this message.
   */
  public long getId()
  {
    return id_;
  }

  /**
   * Return exact time the message was created.
   * In any normal case this is very close to the time when it was sent.
   *
   * @return  The time the message was created. Never null.
   */
  public Date getTime()
  {
    return new Date(time_);
  }

  /**
   * Return a JSON string representation of this message.
   *
   * @return  A JSON string representation of this message. Never null.
   */
  public String toJson()
  {
    StringBuilder s = new StringBuilder();
    s.append('{');

    //
    // JSON-RPC version
    //
    s.append("\"jsonrpc\":\"2.0\",");

    //
    // Method
    //
    s.append("\"method\":\"" + method_ + "\",");

    //
    // Params
    //
    if (params_.size() > 0) {
      s.append("\"params\":[");
      for (int i = 0; i < params_.size(); i++) {
        if (i > 0)
          s.append(',');

        Object param = params_.get(i);
        s.append(JsonUtil.getAsString(param));
      }
      s.append("],");
    }

    //
    // ID
    //
    s.append("\"id\":" + id_);

    s.append('}');

    return s.toString();
  }

  /**
   * Return a <em>pretty</em> JSON representation of this message
   * suitable for logging etc.
   * <p>
   * <b>Note:</b> The parts of the message may be <em>clipped</em>
   * in order to keep the volume down so the returned string is typically
   * not valid JSON.
   *
   * @param maxLength  Max length of each component of the output.
   * @return           A pretty JSON representation of this message. Never null.
   */
  public String toPretty(int maxLength)
  {
    Indentation indentation = new Indentation(2);

    StringBuilder s = new StringBuilder();
    s.append(indentation);
    s.append("{\n");

    indentation = indentation.push();

    //
    // version
    //
    s.append(indentation);
    s.append("\"jsonrpc\": \"2.0\",\n");

    //
    // method
    //
    s.append(indentation);
    s.append("\"method\": \"" + method_ + "\",\n");

    //
    // Params
    //
    if (params_.size() > 0) {
      s.append(indentation);
      s.append("\"params\": [");

      StringBuilder parameters = new StringBuilder();
      for (int i = 0; i < params_.size(); i++) {
        if (i > 0)
          parameters.append(',');
        Object param = params_.get(i);
        parameters.append(JsonUtil.getAsString(param));
      }

      s.append(Response.clip(parameters.toString(), maxLength));
      s.append("],\n");
    }

    //
    // ID
    //
    s.append(indentation);
    s.append("\"id\": " + id_ + "\n");
    s.append('}');

    return s.toString();
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return toPretty(60);
  }

  public static void main(String[] arguments)
  {
    String s = "test";
    List<Object> objects = new ArrayList<>();
    objects.add("test1");
    objects.add("test2");
    int c = 100;

    Request r = new Request(s, objects, c);

    System.out.println(r.toJson());
  }
}

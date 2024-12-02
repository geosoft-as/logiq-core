package no.geosoft.logiq.core.jsonrpc;

import java.io.StringReader;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import no.geosoft.cc.util.Indentation;

import no.geosoft.logiq.core.json.JsonUtil;

/**
 * Models a response message according to the JSON-RPC version 2.0.
 * <p>
 * JSON-RPC responses have the following generic structure:
 * <br>
 * <pre>
 *   {
 *     "jsonrpc": "2.0",
 *     "result": &lt;result object&gt;,
 *     "error": {
 *       "code": &lt;error code&gt;,
 *       "message": &lt;error message&gt;,
 *       "data": &lt;additional error information&gt;
 *     },
 *     "id": &lt;id&gt;
 *   }
 * </pre>

 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public final class Response
{
  /**
   * Model a response error according to the JSON-RPC version 2.0.
   */
  public final static class Error
  {
    /** A code that indicates the error type that occurred. */
    private final int code_;

    /** A short description of the error. */
    private final String message_;

    /** A primitive or structured value that contains additional information about the error. */
    private final Object data_;

    /**
     * Create a new error instance.
     *
     * @param code     Error code.
     * @param message  Error message. Non-null.
     * @param data     Additional error data. Null if N/A.
     */
    public Error(int code, String message, Object data)
    {
      assert message != null : "message cannot be null";

      code_ = code;
      message_ = message;
      data_ = data;
    }

    /**
     * Create a new error instance.
     *
     * @param code     Error code.
     * @param message  Error message. Non-null.
     */
    public Error(int code, String message)
    {
      this(code, message, null);
    }

    /**
     * Create a new error instance.
     *
     * @param errorType The predefined error type. Non-null.
     * @param data      Additional error data. Null if N/A.
     */
    public Error(ErrorType errorType, Object data)
    {
      this(errorType.getCode(), errorType.getMessage(), data);
    }

    /**
     * Return the error code of this error.
     *
     * @return  Code of this error.
     */
    public int getCode()
    {
      return code_;
    }

    /**
     * Return the error message of this error.
     *
     * @return  Error message of this error. Never null.
     */
    public String getMessage()
    {
      return message_;
    }

    /**
     * Return the additional error data associated wit this error.
     *
     * @return Error data. Null if N/A.
     */
    public Object getData()
    {
      return data_;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
      return code_ + " " + message_;
    }
  }

  /** The result of the corresponding request message. Null if an error occurred. */
  private final Object result_;

  /** Any error the occurred on the corresponding request. null on success. */
  private final Error error_;

  /** ID of the corresponding request message. Null if it was not detected in the request. */
  private final Long id_;

  /** Time the message was created. */
  private final long time_ = System.currentTimeMillis();

  /**
   * Create a new success response message.
   *
   * @param result  The message result. May be null.
   * @param id      ID of the corresponding request message.
   */
  public Response(Object result, long id)
  {
    result_ = result;
    error_ = null;
    id_ = id;
  }

  /**
   * Create a new error response message.
   *
   * @param errorType  The message error type. Non-null.
   * @param errorData  Any data associated with the error. Null if N/A.
   * @param id         ID of the corresponding request message.
   *                   Null if not detectable in the request.
   */
  public Response(ErrorType errorType, Object errorData, Long id)
  {
    if (errorType == null)
      throw new IllegalArgumentException("errorType cannot be null");

    result_ = null;
    error_ = new Error(errorType, errorData);
    id_ = id;
  }

  /**
   * Create a new response message from the specified JSON string.
   *
   * @param jsonString  JSON string to create instance from. Non-null.
   * @throws IllegalArgumentException  If jsonString is null.
   * @throws JsonParsingException      If jsonString is not a valid JSON string or
   *                                   if it doesn't constitute a valid instance.
   */
  public Response(String jsonString)
    throws JsonParsingException
  {
    if (jsonString == null)
      throw new IllegalArgumentException("jsonString cannot be null");

    JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
    JsonObject jsonObject = jsonReader.readObject();

    //
    // result
    //
    JsonValue result = jsonObject.get("result");
    Class<?> resultType = JsonUtil.getClass(result);
    result_ = JsonUtil.getAsType(result, resultType);

    //
    // error
    //
    JsonObject errorObject = jsonObject.getJsonObject("error");
    Error error = null;
    if (errorObject != null) {
      JsonNumber errorCode = errorObject.getJsonNumber("code");
      String errorMessage = errorObject.getString("message");
      Object errorData = errorObject.getJsonObject("data");
      error = new Error(errorCode.intValue(), errorMessage, errorData);
    }
    error_ = error;

    //
    // id
    //
    JsonNumber id = jsonObject.getJsonNumber("id");
    id_ = id != null ? id.longValue() : null;
  }

  /**
   * Return result of this response message.
   *
   * @return  Result of this message. Never null.
   */
  public Object getResult()
  {
    return result_;
  }

  /**
   * Return error of this response message.
   *
   * @return  Error of this message. Never null.
   */
  public Error getError()
  {
    return error_;
  }

  /**
   * Return ID of this request message or the ID of the correspoding
   * request message of this response message.
   *
   * @return  ID of this message.
   */
  public Long getId()
  {
    return id_;
  }

  /**
   * Return exact time the message was created.
   * In any normal case this is very close to the time when it was received.
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
    // result
    //
    if (error_ == null) {
      s.append("\"result\":");
      s.append(JsonUtil.getAsString(result_));
      s.append(',');
    }

    //
    // error
    //
    if (error_ != null) {
      s.append("\"error\":{");
      s.append("\"code\":" + error_.getCode() + ",");
      s.append("\"message\":\"" + error_.getMessage() + "\"");
      if (error_.getData() != null) {
        s.append(",");
        s.append("\"data\":" + JsonUtil.getAsString(error_.getData()));
      }
      s.append("},");
    }

    //
    // id
    //
    s.append("\"id\":" + id_);
    s.append('}');

    return s.toString();
  }

  /**
   * Return a clipped version of the specified string, including a
   * statement of much was clipped.
   *
   * @param s       String to clip. Non-null.
   * @param length  Approximate length of returning string. [0,&gt;.
   * @return        Requested string. Never null.
   */
  static String clip(String s, int length)
  {
    assert s != null : "s cannot be null";
    assert length >= 0 : "invalid length";

    int stringLength = s.length();

    // If the string is already smaller the length (+ some extra space that
    // we will anyway add in order to say whats missing) we return it all.
    if (stringLength < length + 13)
      return s;

    int nMissing = stringLength - length;
    return s.substring(0, length) + "... (" + nMissing + " more)";
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
    // result
    //
    if (error_ == null) {
      s.append(indentation);
      s.append("\"result\": ");

      String result = JsonUtil.getAsString(result_);
      s.append(clip(result, maxLength) + "\n");
    }

    //
    // error
    //
    if (error_ != null) {
      s.append(indentation);
      s.append("\"error\": \"{\n");

      String errorData = error_.getData() != null ? JsonUtil.getAsString(error_.getData()) : null;

      Indentation i2 = indentation.push();
      s.append(i2 + "\"code\": " + error_.getCode() + ",\n");
      s.append(i2 + "\"message\": \"" + error_.getMessage() + "\"");
      if (errorData != null) {
        s.append(",\n");
        s.append(i2 + "\"data\": " + clip(errorData, maxLength));
      }
      s.append("\n");
      s.append(indentation + "},\n");
    }

    //
    // id
    //
    s.append(indentation + "\"id\": " + id_ + "\n");
    s.append('}');

    return s.toString();
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return toPretty(100000); // TODO: Reduce when stable
  }
}

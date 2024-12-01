package no.geosoft.logiq.core.jsonrpc;

/**
 * List error types used with the JSON-RPC. This includes the codes
 * included in the standard as well as additional codes defined for
 * the present appliance.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public enum ErrorType
{
  /** Invalid JSON was received by the server. */
  PARSE_ERROR(-32700, "Parse error"),

  /** The JSON sent is not a valid Request object. */
  INVALID_REQUEST(-32600, "Invalid request"),

  /** The method does not exist / is not available. */
  METHOD_NOT_FOUND(-32601, "Method not found"),

  /** Invalid method parameter(s): */
  INVALID_PARAMS(-32602, "Invalid params"),

  /** Internal JSON-RPC error. */
  INTERNAL_ERROR(-32603, "Internal error"),

  /** LogIQ: Database error. */
  LOGIQ_DATABASE_ERROR(-32001, "LogIQ - Database error"),

  /** LogIQ: Invalid login. */
  LOGIQ_INVALID_LOGIN(-32002, "LogIQ - Invalid login"),

  /** LogIQ: Invalid JSON Well Log Format. */
  LOGIQ_INVALID_FORMAT(-32003, "LogIQ - Invalid format"),

  /** LogIQ: Incompatible JSON Well Log Format. */
  LOGIQ_INCOMPATIBLE_FORMAT(-32004, "LogIQ - Incompatible format"),

  /** LogIQ: Unknown instance. */
  LOGIQ_UNKNOWN_INSTANCE(-32005, "LogIQ - Unknown instance"),

  /** LogIQ: Illegal access. */
  LOGIQ_ILLEGAL_ACCESS(-32006, "LogIQ - Illegal access");

  /** A number that indicates the error type that occurred. */
  private final int code_;

  /** A short description of the error. */
  private final String message_;

  /**
   * Create a new error type instance.
   *
   * @param code     The code associated with this error type.
   * @param message  The message associated with this error type. Non-null.
   */
  private ErrorType(int code, String message)
  {
    assert message != null : "message cannot be null";

    code_ = code;
    message_ = message;
  }

  /**
   * Return the code associated with this error type.
   *
   * @return  The error code.
   */
  public int getCode()
  {
    return code_;
  }

  /**
   * Return the message associated with this error type.
   *
   * @return  The error message. Never null.
   */
  public String getMessage()
  {
    return message_;
  }

  /**
   * Return error type from associated code.
   *
   * @param code  Code to get error type from.
   * @return      The requested error type, or null if code is an unknwon.
   */
  public static ErrorType get(int code)
  {
    for (ErrorType errorType : ErrorType.values()) {
      if (errorType.code_ == code)
        return errorType;
    }

    // Not found
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return code_ + " " + message_;
  }
}

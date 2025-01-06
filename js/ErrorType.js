"use strict"

/**
 * List error types used with the JSON-RPC. This includes the codes
 * included in the standard as well as additional codes defined for
 * the present appliance.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export const ErrorType = Object.freeze({
    PARSE_ERROR: { code: -32700, message: "Parse error" },
    INVALID_REQUEST: { code: -32600, message: "Invalid request" },
    METHOD_NOT_FOUND: { code: -32601, message: "Method not found" },
    INVALID_PARAMS: { code: -32602, message: "Invalid params" },
    INTERNAL_ERROR: { code: -32603, message: "Internal error" },
    LOGIQ_DATABASE_ERROR: { code: -32001, message: "LogIQ - Database error" },
    LOGIQ_INVALID_LOGIN: { code: -32002, message: "LogIQ - Invalid login" },
    LOGIQ_INVALID_FORMAT: { code: -32003, message: "LogIQ - Invalid format" },
    LOGIQ_INCOMPATIBLE_FORMAT: { code: -32004, message: "LogIQ - Incompatible format" },
    LOGIQ_UNKNOWN_INSTANCE: { code: -32005, message: "LogIQ - Unknown instance" },
    LOGIQ_ILLEGAL_ACCESS: { code: -32006, message: "LogIQ - Illegal access" }
  });


"use strict"

import { ErrorType } from "./ErrorType.js";
import { Error } from "./Error.js";

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
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class Response
{
  /** Method call result. May be null if n/A or an error occured. */
  #result_;

  /** The error object in case an error occured. Null if not. */
  #error_;

  /** The corresponding response ID. Never null. */
  #id_;

  /** Time of the response. */
  #time_ = new Date();

  /**
   * Create a Response instance from the JSON string response.
   *
   * @param {string} jsonString  The string holding the response. Non-null.
   * @throws TypeError  If jsonString is null or not a string.
   */
  constructor(jsonString)
  {
    if (typeof jsonString != "string" || jsonString == null)
      throw new TypeError("Invalid jsonString: " + jsonString);

    let jsonObject = JSON.parse(jsonString);

    // result
    this.#result_ = jsonObject.result;

    // error
    let errorObject = jsonObject.error;
    this.#error_ = errorObject ? new Error(errorObject.code, errorObject.message,  errorObject.data) : null;

    // id
    this.#id_ = jsonObject.id;
  }

  /**
   * Return the result of the method call.
   *
   * @return {object}  The result of the method call. Null if N/A or an error occured.
   */
  getResult()
  {
    return this.#result_;
  }

  /**
   * Return any error of the method call.
   *
   * @return {Error}  Any error occuring during the method call. Null if none.
   */
  getError()
  {
    return this.#error_;
  }

  /**
   * Return the corresponding request ID.
   *
   * @return {string}  The corresponding request ID. Never null.
   */
  getId()
  {
    return this.#id_;
  }

  /**
   * Return the time this response instance was created.
   *
   * @return  The time this response instance was created. Never null.
   */
  getTime()
  {
    return this.#time_;
  }

  /**
   * Return a string representation of this response.
   *
   * @return  A string representation of this response. Never null.
   */
  toString()
  {
    return "Response " + this.#id_;
  }
}

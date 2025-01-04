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
  #result_;

  #error_;

  #id_;

  #time_ = new Date();

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

  getResult()
  {
    return this.#result_;
  }

  getError()
  {
    return this.#error_;
  }

  getId()
  {
    return this.#id_;
  }

  toString()
  {
    return "Response " + this.#id_;
  }
}


let text = "{\"jsonrpc\":\"2.0\", \"result\":{\"data\":12.2}, \"id\":101}";
let r = new Response(text);
console.log(r.toString());

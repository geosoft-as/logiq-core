"use strict"

import { Counter } from "./Counter.js";

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
export class Request
{
  /** Name of method to be invoked. Non-null. */
  #method_;

  /** List of parameters to be used during the invocation of the method. Non-null. */
  #params_;

  /** Message ID. Non-null. */
  #id_;

  /**
   * Create a request message.
   * <p>
   * Use this constructor if the message ID must be controlled
   * from outside.
   *
   * @param {string} method  Name of the method to invoke. Non-null.
   * @param {array}  params  Method parameters to apply. Non-null.
   * @param {string} id      Message ID. Undefined to auto-generate.
   * @throws {TypeError}  If method or params is null.
   */
  constructor(method, params, id)
  {
    if (typeof method !== "string" || method === null)
      throw new TypeError("Invalid method: " + method);

    if (!Array.isArray(params))
      throw new TypeError("Invalid parms: " + params);

    this.#method_ = method;
    this.#params_ = params;
    this.#id_ = id === undefined ? Counter.get() : id;
  }

  /**
   * Return method of this request.
   *
   * @return {string}  Method of this request. Never null.
   */
  getMethod()
  {
    return this.#method_;
  }

  /**
   * Return the parameter list of this request.
   *
   * @return {array} The parameter list of this request. Never null.
   */
  getParams()
  {
    return this.#params_;
  }

  /**
   * Return ID of this request message or the ID of the corresponding
   * request message of this response message.
   *
   * @return {string}  ID of this message.
   */
  getId()
  {
    return this.#id_;
  }

  /**
   * Return a JSON string representation of this request.
   *
   * @return {string}  A JSON string representation of this request. Never null.
   */
  toJson()
  {
    let jsonObject = {
      jsonrpc: "2.0",
      method: this.#method_
    };

    // Add params if they exist
    if (this.#params_.length > 0) {
      jsonObject.params = this.#params_.map(param => param);
    }

    // Add ID
    jsonObject.id = this.#id_;

    // Convert the object to JSON string
    return JSON.stringify(jsonObject);
  }

  /**
   * Return a <em>pretty</em> JSON representation of this message
   * suitable for logging etc.
   * <p>
   * <b>Note:</b> The parts of the message may be <em>clipped</em>
   * in order to keep the volume down so the returned string is typically
   * not valid JSON.
   *
   * @param {number} maxLength - Max length of each component of the output. [0,&gt;.
   * @return {string} A pretty JSON representation of this message. Never null.
   */
  toPretty(maxLength)
  {
    // TODO
    return this.toJson();
  }

  /**
   * Return a string representation of this instance.
   *
   * @returns {string} A string representing this instance. Never null.
   */
  toString()
  {
    return toJson();
  }
}


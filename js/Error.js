"use strict"

import { ErrorType } from "./ErrorType.js";

/**
 * Model a response error according to the JSON-RPC version 2.0.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class Error
{
  /** {number} A code that indicates the error type that occurred. */
  #code_;

  /** {string} A short description of the error. */
  #message_;

  /** {object} A primitive or structured value that contains additional information about the error. */
  #data_;

  /**
   * Create a new error instance.
   *
   * @param {number} code    - Error code.
   * @param {string} message - Error message. Non-null.
   * @param {object} data    - Additional error data. Null if N/A.
   */
  constructor(code, message, data)
  {
    if (typeof code != "number" || code == null)
      throw new TypeError("Invalid code: " + code);

    if (typeof message != "string" || message == null)
      throw new TypeError("Invalid message: " + code);

    this.#code_ = code;
    this.#message_ = message;
    this.#data_ = data;
  }

  /**
   * Create a new error instance from a given error type.
   *
   * @param {ErrorType} errorType - The error type to create error from. Non-null.
   * @param {object} data         - Additional error data. Null if N/A.
   */
  static fromType(errorType, data)
  {
    return new Error(errorType.code, errorType.message, data);
  }

  /**
   * Return the error code of this error.
   *
   * @return {number}  Code of this error.
   */
  getCode()
  {
    return this.#code_;
  }

  /**
   * Return the error message of this error.
   *
   * @return {string}  Error message of this error. Never null.
   */
  getMessage()
  {
    return this.#message_;
  }

  /**
   * Return the additional error data associated wit this error.
   *
   * @return {object}  Error data. Null if N/A.
   */
  getData()
  {
    return this.#data_;
  }

  /**
   * Return a string representation of this instance.
   *
   * @return {string}  A string representation of this instance. Never null.
   */
  toString()
  {
    return this.#code_ + " " + this.#message_;
  }
}

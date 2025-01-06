"use strict"

import { ErrorType } from "./ErrorType.js";

export class Error
{
  #code_;

  #message_;

  #data_;

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

  static fromType(errorType, data)
  {
    return new Error(errorType.code, errorType.message, data);
  }

  getCode()
  {
    return this.#code_;
  }

  getMessage()
  {
    return this.#message_;
  }

  getData()
  {
    return this.#data_;
  }

  toString()
  {
    return this.#code_ + " " + this.#message_;
  }
}

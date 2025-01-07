"use strict"

import { WebSocketClient } from "./WebSocketClient.js";
import { Request } from "./Request.js";

/**
 * <b>NOTE:</b> This is not a web socket server. It is a web socket client that
 * <em>represents</em> the remote web socket server in this application.
 * <p>
 * The is really an ultra-thin wrapper of the WebSocketClient. Currently that object
 * could be used directly, but we might think that the present class can get some
 * higer level logic that might be useful.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class LogiqServer
{
  /** {string} URI of the LogIQ server. */
  #uri_;

  /** The back-end web socket client. */
  #webSocketClient_;

  /**
   * Create a new LogiqServer instance, i.e. an instance that <em>represents</em>
   * the LogIQ server in the client prohgram.
   *
   * @param {string} uri  Web socket URI. Non-null.
   * @throws TypeError  If uri is null or of wrong type.
   */
  constructor(uri)
  {
    if (typeof uri != "string" || uri == null)
      throw new TypeError("Invalid uri: " + uri);

    this.#uri_ = uri;
    this.#webSocketClient_ = null;
  }

  /**
   * Return URI of the LogIQ server.
   *
   * @return {string}  URI of the LogIQ server. Never null.
   */
  getUri()
  {
    return this.#uri_;
  }

  /**
   * Check if the connection to the LogIQ server is currently open.
   *
   * @return {boolean}  True if the web socket connection to the LogIQ server is currently open,
   *                    false otherwise.
   */
  isOpen()
  {
    return this.#webSocketClient_ != null && this.#webSocketClient_.isOpen();
  }

  /**
   * Send the given request to the LogIQ server.
   *
   * @param {Request} request  Request to send. Non-null.
   * @throws TypeError  If request is null or of wrong type.
   */
  send(request)
  {
    if (!(request instanceof Request) || request == null)
      throw new TypeError("Invalid request: " + request);

    if (this.#webSocketClient_ == null || !this.#webSocketClient_.isOpen()) {
      if (this.#webSocketClient_ != null)
        console.log("Connection was closed. Reopening...");

      this.#webSocketClient_ = new WebSocketClient(this);
    }

    this.#webSocketClient_.send(request);
  }

  /**
   * Return a string representation of this instance.
   *
   * @return {string}  A string representation of this instance. Never null.
   */
  toString()
  {
    return this.#uri_;
  }
}

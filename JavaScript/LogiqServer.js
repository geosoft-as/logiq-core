"use strict"

import { WebSocketClient } from "./WebSocketClient.js";
import { Request } from "./Request.js";

/**
 * <b>NOTE:</b> This is not a web socket server. It is a web socket client that
 * <em>represents</em> the remote web socket server in this application.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
class LogiqServer
{
  /** {string} URI of the LogIQ server. */
  #uri_;

  #username_;

  #password_;

  #webSocketClient_;

  constructor(uri, username, password)
  {
    if (typeof uri != "string" || uri == null)
      throw new TypeError("Invalid uri: " + uri);

    if (typeof username != "string" || username == null)
      throw new TypeError("Invalid username: " + username);

    if (typeof password != "string" || password == null)
      throw new TypeError("Invalid password: " + password);

    this.#uri_ = uri;
    this.#username_ = username;
    this.#password_ = password;

    this.#webSocketClient_ = null;
  }

  /**
   * Return URI of the LogIQ server.
   *
   * @return {string} URI of the LogIQ server. Never null.
   */
  getUri()
  {
    return this.#uri_;
  }

  /**
   * Return username for the session.
   *
   * @return {string} Username for the session. Never null.
   */
  getUsername()
  {
    return this.#usernamne_;
  }

  /**
   * Return password for the session.
   *
   * @return {string} Password for the session. Never null.
   */
  getPassword()
  {
    return this.#password_;
  }

  /**
   * Check if the connection to the LogIQ server is currently open.
   *
   * @return {boolean}  True if the web socket connection to the LogIQ server is currently open,
   *                    false otherwise.
   */
  isOpen()
  {
    return this.#webScoket != null && this.#webSocket_.readyState === WebSocket.OPEN;
  }

  /**
   * Send the given request to the LogIQ server.
   *
   * @param {object} request - Request to send. Non-null.
   * @throws TypeError  If the sending fails for some reason.
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


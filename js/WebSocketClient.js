"use strict"

import { EventManager } from "./EventManager.js";
import { Request } from "./Request.js";
import { Response } from "./Response.js";

/**
 * A web socket client instance, a convenience front-end to the WebSocket type.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class WebSocketClient
{
  /** {string} Web socket URI. */
  #uri_;

  /** The low-level web socket instance. */
  #webSocket_;

  /** {Request[]} Requests made before the web socket was opened. */
  #requestQueue_ = [];

  /**
   * Create a web socket client instance.
   *
   * @param {string} uri - The web socket URI. Non-null.
   */
  constructor(uri)
  {
    if (uri == null)
      throw new TypeError("uri cannot be null");

    this.#uri_ = uri;
    this.#requestQueue_ = [];

    this.#webSocket_ = new WebSocket(uri);
    this.#webSocket_.onopen = this.onOpen.bind(this);
    this.#webSocket_.onclose = this.onClose.bind(this);
    this.#webSocket_.onmessage = this.onMessage.bind(this);
    this.#webSocket_.onerror = this.onError.bind(this);
  }

  /**
   * Called when the connection is opened.
   */
  onOpen()
  {
    console.log("WebSocket connection opened");

    EventManager.getInstance().notify("WebSocketConnectionOpened", this.#uri_)

    // Send any queued messages
    this.#requestQueue_.forEach(request => this.#webSocket_.send(request.toJson()));
    this.#requestQueue_ = []; // Clear the queue
  }

  /**
   * Called when the connection is closed.
   */
  onClose()
  {
    console.log("WebSocket connection closed");
    EventManager.getInstance().notify("WebSocketConnectionClosed", this.#uri_);
  }

  /**
   * Called when a message is received.
   */
  onMessage(event)
  {
    console.log("Received message: ", event.data);
    const response = new Response(event.data);
    EventManager.getInstance().notify("WebSocketResponseReceived", this.#uri_, response);
  }

  /**
   * Called when an error occurs.
   */
  onError(error)
  {
    console.log("WebSocket error: " + error);
  }

  /**
   * Check if the connection is open.
   *
   * @return {boolean}  True if the connection is open, false otherwise.
   */
  isOpen()
  {
    return this.#webSocket_.readyState == WebSocket.OPEN;
  }

  /**
   * Send a message through the web socket.
   *
   * @param {Request}  Request to send. Non-null.
   */
  send(request)
  {
    if (request == null || !(request instanceof Request))
      throw new TypeError("Invalid request: " + request);

    console.log("Send: " + request.toJson());

    // Send right away if the web socket is open
    if (this.isOpen()) {
      this.#webSocket_.send(request.toJson());
      EventManager.getInstance().notify("WebSocketRequestSent", this, request);
    }

    // Otherwise queue the message and send it as soon as the socket opens
    else {
      this.#requestQueue_.push(request);
    }
  }
}

package no.geosoft.logiq.core.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import no.geosoft.cc.util.TextUtil;
import no.geosoft.cc.event.EventManager;

import no.geosoft.logiq.core.jsonrpc.Request;
import no.geosoft.logiq.core.jsonrpc.Response;

/**
 * A websocket client instance.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient
{
  /** The logger instance. */
  private static final Logger logger_ = Logger.getLogger(WebSocketClient.class.getName());

  /**
   * Create a websocket client.
   *
   * @param webSocketUri  URI to server web socket. Non-null.
   */
  public WebSocketClient(URI webSocketUri)
  {
    super(webSocketUri);
  }

  /** {@inheritDoc} */
  @Override
  public void onOpen(ServerHandshake serverHandshake)
  {
    logger_.log(Level.INFO, "Connection open " + getRemoteSocketAddress() +
                " - " + serverHandshake.getHttpStatusMessage() +
                " (" + serverHandshake.getHttpStatus() + ")");
    EventManager.getInstance().notify("LogIqConnectionOpened", this);
  }

  /** {@inheritDoc} */
  @Override
  public void onClose(int exitCode, String closeReason, boolean isRemote)
  {
    logger_.log(Level.INFO, "Connection closed " + getRemoteSocketAddress() +
                " - " + closeReason + " (" + exitCode + ")");
    EventManager.getInstance().notify("LogIqConnectionClosed", this);
  }

  /** {@inheritDoc} */
  @Override
  public void onMessage(String message)
  {
    logger_.log(Level.INFO, "Response: " + message);

    Response response = new Response(message);
    EventManager.getInstance().notify("LogIqResponseReceived", this, response);
  }

  /** {@inheritDoc} */
  @Override
  public void onError(Exception exception)
  {
    logger_.log(Level.WARNING, "Web socket error: " + getURI(), exception);
  }

  /**
   * Send the specified request to the server.
   *
   * @param message  Request to send. Non-null.
   * @throws IOException  If sending failed for some reason.
   */
  public void send(Request request)
    throws IOException
  {
    if (request == null)
      throw new IllegalArgumentException("request cannot be null");

    try {
      send(request.toJson());

      EventManager.getInstance().notify("LogIqRequestSent", this, request);
    }
    catch (WebsocketNotConnectedException exception) {
      throw new IOException("Unable to send message: " + TextUtil.truncate(request.toJson(), 40), exception);
    }
  }
}

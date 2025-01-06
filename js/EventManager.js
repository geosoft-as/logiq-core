"use strict"

/**
 * Class for managing events.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class EventManager
{
  static #instance_ = new EventManager();

  #listeners_ = new Map();

  /**
   * Create a new event manager.
   */
  constructor()
  {
    // Nothing
  }

  /**
   * Return ths sole instance of this class.
   */
  static getInstance()
  {
    if (!EventManager.instance_)
      EventManager.#instance_ = new EventManager();

    return EventManager.#instance_;
  }

  /**
   * Add listener for the specified event.
   *
   * @param {string} eventName  Name of event, Non-null.
   * @param {object} eventListener  Event listener. Non-null.
   */
  addListener(eventName, eventListener)
  {
    if (!eventName)
      throw new TypeError("eventName cannot be null");

    if (!eventListener)
      throw new TypeError("eventListener cannot be null");

    // Get the listener set for the event, create if not present
    if (!this.#listeners_.has(eventName))
      this.#listeners_.set(eventName, []);

    const listeners = this.#listeners_.get(eventName);

    // Ensure the listener is not already added
    if (!listeners.some((listener) => listener === eventListener))
      listeners.push(eventListener);
  }

  /**
   * Remove the specified event listener.
   *
   * @param {object} eventListener  Event listener to remove.
   * @param {string} eventName      Name of event to remove from, or null
   *                                to remove from all.
   */
  removeListener(eventListener, eventName = null)
  {
    if (!eventListener)
      throw new TypeError("eventListener cannot be null");

    for (let name of this.#listeners_.keys()) {
      if (eventName == null || eventName == name) {
        const listeners = this.#listeners_.get(name);
        const index = listeners.indexOf(eventListener);
        if (index !== -1)
          listeners.splice(index, 1);
      }
    }
  }

  notify(eventName, source, data = null)
  {
    if (!eventName)
      throw new TypeError("eventName cannot be null");

    const listeners = this.#listeners_.get(eventName);
    if (!listeners)
      return;

    listeners.forEach(listener => listener.update(eventName, source, data));
  }

  cleanListenerList(eventName)
  {
    const listeners = this.listeners_.get(eventName);
    if (!listeners)
      return;

    listeners.forEach(listener => listeners.delete(listener));
  }
}


class F
{
  update(eventName, source, data)
  {
    console.log(eventName + " " + source);
  }
}


let f = new F();
let e = EventManager.getInstance();
e.addListener("Test", f);
e.addListener("Test2", f);
e.removeListener(f);
e.notify("Test2", "hei");


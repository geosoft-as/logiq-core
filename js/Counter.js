"use strict"

/**
 * A universal, session specific counter instance staring at 1.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class Counter
{
  // Static counter starting at 1
  static #counter_ = 1;

  // Private constructor to prevent instantiation
  constructor()
  {
    throw new Error("This constructor should never be called");
  }

  // Static method to get and increment the counter
  static get()
  {
    return this.#counter_++;
  }
}

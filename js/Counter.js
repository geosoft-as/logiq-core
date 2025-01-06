"use strict"

/**
 * A universal, session specific counter instance staring at 1.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
export class Counter
{
  /** Static counter starting at 1. */
  static #counter_ = 1;

  /**
   * Constructor that will fail when called. This class should not be instantiated.
   *
   * @throws Error  Always.
   */
  constructor()
  {
    throw new Error("This constructor should never be called");
  }

  /**
   * Return current counter. Every call will return a unique value.
   *
   * @return {number}  Current counter. [1,&gt;.
   */
  static get()
  {
    return this.#counter_++;
  }
}

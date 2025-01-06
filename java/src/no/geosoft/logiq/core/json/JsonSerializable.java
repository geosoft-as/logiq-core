package no.geosoft.logiq.core.json;

/**
 * Common interface for classes that are able to serialize
 * its content to JSON.
 *
 * @author <a href="mailto:jacob.dreyer@fluxens.com">Jacob Dreyer</a>
 */
public interface JsonSerializable
{
  /**
   * Return a JSON string representation of this object.
   *
   * @return  A JSON string representation of this object. Never null.
   */
  public String toJson();
}

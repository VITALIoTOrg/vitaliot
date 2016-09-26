package net.atos.ari.vital.external;

public class ThingInformation
{
  private String thingID;
  private String unit;
  private boolean is_input;
  private boolean is_digital;
  private String manufacturer;
  private String location;
  private String serial;
  private boolean is_output;
  private String maximum_response_time;
  private String computational_cost;
  private String protocol;
  private String type;
  private byte[] tags;
  
  public String getProtocol()
  {
    return this.protocol;
  }
  
  public void setProtocol(String protocol)
  {
    this.protocol = protocol;
  }
  
  public String getComputational_cost()
  {
    return this.computational_cost;
  }
  
  public void setComputational_cost(String computational_cost)
  {
    this.computational_cost = computational_cost;
  }
  
  public boolean isIs_output()
  {
    return this.is_output;
  }
  
  public void setIs_output(boolean is_output)
  {
    this.is_output = is_output;
  }
  
  public String getMaximum_response_time()
  {
    return this.maximum_response_time;
  }
  
  public void setMaximum_response_time(String maximum_response_time)
  {
    this.maximum_response_time = maximum_response_time;
  }
  
  public String getSerial()
  {
    return this.serial;
  }
  
  public void setSerial(String serial)
  {
    this.serial = serial;
  }
  
  public String getThingID()
  {
    return this.thingID;
  }
  
  public void setThingID(String thingID)
  {
    this.thingID = thingID;
  }
  
  public String getUnit()
  {
    return this.unit;
  }
  
  public void setUnit(String unit)
  {
    this.unit = unit;
  }
  
  public String getLocation()
  {
    return this.location;
  }
  
  public void setLocation(String location)
  {
    this.location = location;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public byte[] getTags()
  {
    return this.tags;
  }
  
  public void setTags(byte[] tags)
  {
    this.tags = tags;
  }
  
  public boolean isIs_input()
  {
    return this.is_input;
  }
  
  public void setIs_input(boolean is_input)
  {
    this.is_input = is_input;
  }
  
  public boolean isIs_digital()
  {
    return this.is_digital;
  }
  
  public void setIs_digital(boolean is_digital)
  {
    this.is_digital = is_digital;
  }
  
  public String getManufacturer()
  {
    return this.manufacturer;
  }
  
  public void setManufacturer(String manufacturer)
  {
    this.manufacturer = manufacturer;
  }
}

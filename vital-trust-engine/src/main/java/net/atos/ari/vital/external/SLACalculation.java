package net.atos.ari.vital.external;

public class SLACalculation
{
  private String thingServiceId;
  private int QoSparamsFulfill;
  private int QoSparamsNoFulfill;
  
  public String getThingServiceId()
  {
    return this.thingServiceId;
  }
  
  public void setThingServiceId(String thingServiceId)
  {
    this.thingServiceId = thingServiceId;
  }
  
  public int getQoSparamsFulfill()
  {
    return this.QoSparamsFulfill;
  }
  
  public void setQoSparamsFulfill(int qoSparamsFulfill)
  {
    this.QoSparamsFulfill = qoSparamsFulfill;
  }
  
  public int getQoSparamsNoFulfill()
  {
    return this.QoSparamsNoFulfill;
  }
  
  public void setQoSparamsNoFulfill(int qoSparamsNoFulfill)
  {
    this.QoSparamsNoFulfill = qoSparamsNoFulfill;
  }
}

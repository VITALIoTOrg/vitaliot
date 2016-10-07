package net.atos.ari.vital.external;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract interface QoSManagerInternalIF
{
  public abstract String getTemplate();
  
  public abstract void createAgreement(String paramString);
  
  public abstract Map<String, Double> registerServiceQoSPUSH(String paramString, int paramInt, ArrayList<ArrayList<String>> paramArrayList);
  
  @Deprecated
  public abstract Map<String, Double> registerServiceQoSPUSH(String paramString, int paramInt, ArrayList<String> paramArrayList, ArrayList<ArrayList<String>> paramArrayList1);
  
  @Deprecated
  public abstract Map<String, Double> registerServiceQoSPUSH(String paramString, ArrayList<String> paramArrayList, ArrayList<ArrayList<String>> paramArrayList1);
  
  public abstract List<String> registerServiceQoSPULL(String paramString, ArrayList<ArrayList<String>> paramArrayList);
  
  @Deprecated
  public abstract List<String> registerServiceQoSPULL(String paramString, ArrayList<String> paramArrayList, ArrayList<ArrayList<String>> paramArrayList1);
  
  public abstract void unregisterServiceQoS(String paramString);
  
  public abstract boolean writeThingsServicesQoS(ArrayList<String> paramArrayList);
  
  public abstract boolean modifyThingsServicesQoS(ArrayList<String> paramArrayList);
  
  public abstract boolean thingRemoved(String paramString);
  
  @Deprecated
  public abstract ArrayList<SLACalculation> calculateSLA(ArrayList<String> paramArrayList);
  
  public abstract SLACalculation calculateSLA(String paramString);
  
  public abstract SLACalculation calculateSLAPush(String paramString, int paramInt);
  
  public abstract SLACalculation failureSLA(String paramString);
  
  public abstract boolean getMeasurementSLAMonitoring(String paramString1, String paramString2);
  
  @Deprecated
  public abstract boolean getMeasurementSLAMonitoring(String paramString, int paramInt);
  
  @Deprecated
  public abstract boolean getMeasurementSLAMonitoring(ArrayList<String> paramArrayList);
  
  public abstract String getGatewayId();
  
  public abstract void setGatewayId(String paramString);
  
  
  public abstract Map<String, Double> getBatteryLevels();
  
  public abstract void reachable(String paramString);
  
  public abstract void unreachable(String paramString);
}

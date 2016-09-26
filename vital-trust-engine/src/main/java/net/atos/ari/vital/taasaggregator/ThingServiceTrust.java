/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package net.atos.ari.vital.taasaggregator;

import java.util.Date;

public class ThingServiceTrust 
{
	private String IoTSystem_id;	
	private double trustScore;
	private Date timeS;
	//private Date dateCalculated; /* EGO */
	
	public ThingServiceTrust (String id, double qos, Date dateCalculated)
	{
		IoTSystem_id = id;	
		trustScore = qos;
		this.timeS = dateCalculated;
	}
	
	public String getIoTSystem_id ()
	{
		return IoTSystem_id;
	}
	
	
	public double getTrustScore ()
	{
		return trustScore;
	}

	public Date getTimeS() {
		return timeS; //"{\n\t\"$date\": " + dateCalculated + "\n }";
	}
	
	
}

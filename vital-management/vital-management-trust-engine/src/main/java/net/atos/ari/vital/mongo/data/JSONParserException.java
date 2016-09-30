/**

Copyright 2016 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author Contact:
@author Elena Garrido Ostermann. Atos Research and Innovation, Atos SPAIN SA
@email elena.garrido@atos.net
**/
package net.atos.ari.vital.mongo.data;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JSONParserException extends Exception {
	 
	private static final long serialVersionUID = -751061601062337918L;

	public JSONParserException(String message){
		super(message);
	}

	public JSONParserException(String s, Throwable t){
		super(s + " - " +serialize(t));
	}
	
	static private String serialize(Throwable t){
		StringWriter errors = new StringWriter();
		t.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

}

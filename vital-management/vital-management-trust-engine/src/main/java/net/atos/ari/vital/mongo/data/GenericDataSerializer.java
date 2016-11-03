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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericDataSerializer<T> {

	public String serialize(T object) throws JSONParserException{
		ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new JSONParserException("Error processing object", e);
		}
	}
}

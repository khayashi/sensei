/**
 * This software is licensed to you under the Apache License, Version 2.0 (the
 * "Apache License").
 *
 * LinkedIn's contributions are made under the Apache License. If you contribute
 * to the Software, the contributions will be deemed to have been made under the
 * Apache License, unless you expressly indicate otherwise. Please do not make any
 * contributions that would be inconsistent with the Apache License.
 *
 * You may obtain a copy of the Apache License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, this software
 * distributed under the Apache License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License for the specific language governing permissions and limitations for the
 * software governed under the Apache License.
 *
 * © 2012 LinkedIn Corp. All Rights Reserved.  
 */

package com.senseidb.gateway.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.javaapi.producer.ProducerData;
import kafka.message.Message;
import kafka.producer.ProducerConfig;

import com.senseidb.gateway.kafka.DefaultJsonDataSourceFilter;

public class PrimeKafkaUtil {

  static Charset UTF8 = Charset.forName("UTF-8");
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception{
    File f = new File("/home/jwang/github/search-perf/data/cars1m.json");
    
    Properties props = new Properties();
    props.put("zk.connect", "localhost:2181");
    props.put("serializer.class", "kafka.serializer.DefaultEncoder");

    ProducerConfig producerConfig = new ProducerConfig(props);
    Producer<String,Message> kafkaProducer = new Producer<String,Message>(producerConfig);
    String topic = "perfTopic";
    List<ProducerData<String, Message>> msgList = new ArrayList<ProducerData<String, Message>>();
   
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),UTF8));
    
    int batchSize = 10000;
    int count = 0;
    while(true){
      String line = reader.readLine();
      if (line==null) break;
      count++;
      System.out.println(count+" msgs pushed.");
      Message m = new Message(line.getBytes(DefaultJsonDataSourceFilter.UTF8));
      ProducerData<String,Message> msg = new ProducerData<String,Message>(topic,m);
      msgList.add(msg);

      if (msgList.size()>batchSize){
        kafkaProducer.send(msgList);
        msgList.clear();
      }
    }

    if (msgList.size()>0){
      kafkaProducer.send(msgList);
    }
  }

}

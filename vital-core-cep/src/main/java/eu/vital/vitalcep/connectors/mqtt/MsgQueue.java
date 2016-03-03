package eu.vital.vitalcep.connectors.mqtt;


import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

public class MsgQueue{

    Logger logger;

    LinkedBlockingQueue<MqttMsg> queue;
    MessageProcessor msgProcessor;
    Receiver receiver;

    public MsgQueue (MessageProcessor msgProcessor){
            logger = Logger.getLogger(this.getClass());

            queue = new LinkedBlockingQueue<>();
            this.msgProcessor = msgProcessor;

            //important, this is the key to be able to manage big amount of messages and several CEP instances
            receiver = new Receiver();
            receiver.start();
    }



    public void insertMsg (MqttMsg msg){
            try{
                    queue.put (msg);
            }catch (Exception e){
                    logger.error (e,e);

            }
    }

    public Object take (){
            try {
                    return queue.take();
            } catch (InterruptedException e) {
                    logger.error (e,e);
                    return null;
            }
    }

    public class Receiver extends Thread{
        @Override
        public void run(){
            try{
                    while(true){
                            msgProcessor.processMsg((MqttMsg)queue.take());
                    }
            }catch(Exception e){
                    logger.error(e,e);
            }
        }

    }

}

package none.mrgoodbody.qalcosonicw1reader;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;

import java.util.Arrays;

public class w1NFCCallback implements NfcAdapter.ReaderCallback {
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    private static final String SELECT_APDU_HEADER = "FFCA0000";
    private static final byte[] SELECT_OK_SW = {-112, 0};
    private static final String TAG = "LoyaltyCardReader";
    private static final byte flag = 34;
    public int M_ID;
    public int M_Manuf;
    int checkPsw;
    public int command;
    byte[] commandArr;
    byte[] commandDate;
    byte[] commandDayArch;
    byte[] commandDeviceNb;
    byte[] commandError;
    byte[] commandFlow;
    byte[] commandREQ_UD2;
    byte[] commandREQ_UD2AdditionalData;
    byte[] commandGetLoraMac;
    byte[] commandGetMode;
    byte[] commandGetState;
    byte[] commandLastArch;

    byte[] commandMonthArch;
    byte[] commandRadioActive;
    byte[] commandRadioMode;
    byte[] commandRadioMode2;
    byte[] commandRadioS1;
    byte[] commandSND_NKE;
    byte[] commandSelect;
    byte[] commandSelectAll;
    byte[] commandSelectHourArch;
    byte[] commandTemperature;
    byte[] commandTestMode;
byte[] commandDefault;
    byte[] commandUserArch;
    byte[] commandVolume;
    byte[] commandYearArch;
    byte[] commandresetLoraState;
    byte[] commandTestingArch = new byte[]{104, 4, 4, 104, 115, -2, 80, (byte)144};
    byte[] commandStartupArch = new byte[]{104, 4, 4, 104, 115, -2, 80, (byte)144};
    byte[] locamac1 = new byte[]{0x68, 0x0A, 0x0A, 0x68, 0x73, (byte)0xFE, (byte)0xA1, 0x0F, 0x00,0x01, 0x33, 0x16, 0x00, 0x00};
byte[] lorapass= new byte[]{16,123,-2};
    MainActivity act;

    NfcAdapter adapter;
    NfcAdapter.OnTagRemovedListener tagRemovedListener=new w1NFCTagRemovedListener();
    w1NFCCallback(NfcAdapter adapter,MainActivity act){
        this.adapter=adapter;
        this.act=act;
        this.M_Manuf = 0;
        this.M_ID = 0;
//standard befehle
        this.commandSelect = new byte[]{104, 4, 4, 104, 115, -2, 80};
        this.commandSelectAll = ArrayCombine(commandSelect,new byte[]{0});
        this.commandSelectHourArch = ArrayCombine(commandSelect,new byte[]{96});
        this.commandDayArch =ArrayCombine(commandSelect,new byte[]{48});
        this.commandYearArch = ArrayCombine(commandSelect,new byte[]{32});
        this.commandLastArch =ArrayCombine(commandSelect,new byte[]{80});
        this.commandUserArch = ArrayCombine(commandSelect,new byte[]{16});
        this.commandMonthArch = ArrayCombine(commandSelect,new byte[]{64});
        this.commandSND_NKE = new byte[]{16, 64, -2};
        this.commandREQ_UD2 = new byte[]{16, 123, -2};
        this.commandREQ_UD2AdditionalData = new byte[]{16, 91, -2};

        //Axioma spezial

        this.commandDate =           new byte[]{7, 17, 16, 0, 0, 73, 22};
        this.commandVolume =         new byte[]{4, 23, 16, 0, 0, 76, 22};
        this.commandFlow =           new byte[]{2, 24, 16, 0, 0, 75, 22};
        this.commandTemperature =    new byte[]{2, 15, 16, 0, 0, 66, 22};
        this.commandError =          new byte[]{2, 25, 16, 0, 0, 76, 22};
        this.commandDeviceNb =       new byte[]{64, 0, 16, 15, 0, -30, 22};
        this.commandRadioMode =      new byte[]{-128, 44, 16, 15, 0, -84, 22};
        this.commandRadioMode2 =     new byte[]{83, 0, 15, 16, 71, -84, 22};
        this.commandGetState =       new byte[]{-128, 44, 16, 0, 0, -84, 22};
        this.commandGetLoraMac =     new byte[]{0, 0, 16, 0, 0, -84, 22};
        this.commandGetMode =        new byte[]{1, 16, 16, 0, 0, 66, 22};
        this.commandDefault = new byte[]{104,10,10,104,115,-2,-95,15,0};

        this.commandTestMode = new byte[]{104, 11, 11, 104, 115, -2, -94, 15, 0, 18, 16, 0, 0, 16, 55};

        this.commandresetLoraState =  new byte[]{-94, 15, 0, 51, 16, 0, 0};
        this.commandRadioActive = new byte[]{-94, 15, 0, 52, 16, 0, 0};
        this.commandRadioS1 = new byte[]{-94, 15, 0, 26, 16, 0, 0, 1};


    }
    private byte[] ArrayCombine(byte[] array1,byte[] array2){

        byte[] concatenatedArray = new byte[array1.length + array2.length];

        int i = 0;
        for (; i < array1.length; i++) {
            concatenatedArray[i] = array1[i];
        }
        for (int j = 0; j < array2.length; i++, j++) {
            concatenatedArray[i] = array2[j];
        }
        return concatenatedArray;
    }
    private byte[] leserResetAbfrage(byte [] tagID){
        return ArrayCombine(nfcHead((byte) 173,tagID),new byte[]{13});
    }
    private byte[] leserSoftReset(byte [] tagID){
        return ArrayCombine(nfcHead((byte) 174,tagID),new byte[]{13,1});
    }
    private byte[] leserHardReset(byte [] tagID){
        return ArrayCombine(nfcHead((byte) 174,tagID),new byte[]{13,0});
    }

    private byte[] messageLength(byte[] tagID){
        return nfcHead((byte) 171,tagID);
    }
    private byte[]sende(byte[] data,NfcV tag){
     try {
         return tag.transceive(data);
     }catch (Exception e){
         return new byte[]{(byte)0xff,(byte)0xff};
     }

    }
    private byte[] sendKommando(byte[] message,byte[] tagID){
        if (message[0] == 104) {
            message = summe(message, 4);
        }else {
            message = summe(message, 1);

        }
        act.meldungZeigen(MBusParser.parse(message).toString());
        byte laenge=(byte) (message.length-1);
        return ArrayCombine(nfcHead((byte) 170,tagID),ArrayCombine(new byte[]{laenge},message));
    }

    private byte[] sendMessage(byte[] message,byte[] tagID){
        act.meldungZeigen(MBusParser.parse(message).toString());
        byte laenge=(byte) (message.length-1);
        return ArrayCombine(nfcHead((byte) 170,tagID),ArrayCombine(new byte[]{laenge},message));
    }
    private byte[] getValue(byte laenge,byte[] tagID){
        return ArrayCombine(nfcHead((byte) 172,tagID),new byte[]{0,laenge});
    }
private void ausf(byte[] cmd,byte[] tagID,NfcV w1){
        try {
            sende(leserSoftReset(tagID),w1);
            act.meldungZeigen("MessageSend:");
    byte[] ret=new byte[]{(byte)0xff,(byte) 0xff};
    int i=0;
            while ( ret[0]!=0 && ret[1]!=(byte)0xe5) {
        if(cmd[0]==104||cmd[0]==16){

                ret = sende(sendKommando(cmd, tagID), w1);
                if(i++>4) break;

        }else {

                ret = sende(sendMessage(ArrayCombine(this.commandDefault, cmd), tagID), w1);

            }
                if(i++>4) break;
        }

        printData(ret);

    act.meldungZeigen("Message Length:");
    ret=new byte[]{(byte)0xf,(byte) 0xf};
    while (ret[0] > 0 ) {
        java.lang.Thread.sleep(300);
        ret = sende(messageLength(tagID), w1);
    }
            byte size=ret[1];
    printData(ret);
    ret=new byte[]{(byte)0xf,(byte) 0xf};
            act.meldungZeigen("GetValue:");

    while (ret[0] > 0 ) {
        ret = sende(getValue(size, tagID), w1);
        printData(ret);
        act.meldungZeigen(MBusParser.parse(Arrays.copyOfRange(ret,1,ret.length-1)).toString());
    }
    act.meldungZeigen("Ende");
            act.meldungZeigen("");
        }catch (Exception e){
            e.printStackTrace();
        }
}
    private byte[]nfcHead(byte kommando,byte[] tagID){
        return new byte[]{34,kommando,2,tagID[0], tagID[1], tagID[2], tagID[3], tagID[4], tagID[5], tagID[6], tagID[7]};
    }
    private byte[]nfcHead(byte kommando,byte[] tagID,byte laenge){
        return ArrayCombine(nfcHead(kommando,tagID),new byte[]{laenge});
    }
    @Override
    public void onTagDiscovered(final Tag tag) {

        //meldungZeigen("NFC Tag erkannt!",Toast.LENGTH_SHORT);
       byte[] tagID= tag.getId();
       printData(tagID);
        NfcV w1= NfcV.get(tag);

       if (w1==null) {
       return;
       }

        try {
            byte[] tagIDBytes = new byte[]{};
            byte[] ant;
            byte[] msg = new byte[]{34, -86, 2};//170
            byte[] writeDynamic = new byte[]{34, -82, 2};//174
            byte[] readDynamic = new byte[]{34, -83, 2};//173

            byte[] ret;
            w1.connect();
ret=sende(leserResetAbfrage(tagID),w1);
printData(ret);
if(ret[0]==0 &&ret[1]==1) {
    printData(sende(leserHardReset(tagID), w1));
}

           // act.meldungZeigen("Send Message:");
          //  cmd = ArrayCombine(cmdArray, this.commandVolume);

           // byte[][] comm = new byte[][] {commandSelect,commandGetData1,commandGetData2,commandDate,commandGetData1,commandGetData2,commandFlow,commandGetData1,commandGetData2,commandTemperature,commandGetData1,commandGetData2,commandVolume,commandGetData1,commandGetData2};
            byte[][] cmdlist = new byte[][] {commandSND_NKE,commandSelectAll, commandREQ_UD2,commandSND_NKE,commandYearArch, commandREQ_UD2,commandSND_NKE,commandMonthArch,commandREQ_UD2
            };
            for (byte[] cmd:  cmdlist ) {
                ausf(cmd, tagID, w1);
            }
            {

            }
            /*
            int length = cmd.length - 1;
            if(0==1){
            act.meldungZeigen("writeDynamic:");
            //tagid,w1,1,13 writeDynamic
            ant = w1.transceive(ArrayCombine(ArrayCombine(writeDynamic, tagIDBytes), new byte[]{13, 1}));
            printData(ant);
            //readDynamic
            act.meldungZeigen("readDynamic:");
            ant = w1.transceive(ArrayCombine(ArrayCombine(readDynamic, tagIDBytes), new byte[]{13}));
            printData(ant);
            //reset

            act.meldungZeigen("reset:");
            byte[] reset = summe(this.commandResetApp, 1);
            reset = ArrayCombine(new byte[]{(byte) (reset.length - 1)}, reset);
            reset = ArrayCombine(tagIDBytes, reset);
            reset = ArrayCombine(msg, reset);
            for (int a = 0; a < 5; a++) {
                ant = w1.transceive(reset);
                printData(ant);
                java.lang.Thread.sleep(300);
                //select
            }
            act.meldungZeigen("select:");
            byte[] select = summe(this.commandSelect, 4);
            select = ArrayCombine(new byte[]{(byte) (select.length - 1)}, select);
            select = ArrayCombine(tagIDBytes, select);
            select = ArrayCombine(msg, select);
            for (int a = 0; a < 5; a++) {
                ant = w1.transceive(select);

                printData(ant);

                java.lang.Thread.sleep(300);
            }
            act.meldungZeigen("gdata1:");
            byte[] gData1 = summe(this.commandGetData1, 1);
            gData1 = ArrayCombine(new byte[]{(byte) (gData1.length - 1)}, gData1);
            gData1 = ArrayCombine(tagIDBytes, gData1);
            gData1 = ArrayCombine(msg, gData1);
            for (int a = 0; a < 5; a++) {
                ant = w1.transceive(gData1);
                printData(ant);
                java.lang.Thread.sleep(300);

            }
            int i = 0;
            while (i++ < 5) {
                ant = w1.transceive(ArrayCombine(new byte[]{34, -85, 2}, tagIDBytes));
                printData(ant);
                if (ant[0] <= 0) {
                    break;
                }
            }
            byte abfrage = ant[1];
            i = 0;
            while (i++ < 5) {
                //MEssage
                ant = w1.transceive(ArrayCombine(ArrayCombine(new byte[]{34, -84, 2}, tagIDBytes), new byte[]{0, abfrage}));
                printData(ant);
                if (ant[0] <= 0) {
                    break;
                }
            }
//Volume
/// /////////////////////////////////////////////////////////
            act.meldungZeigen("volume:");
            cmd = ArrayCombine(cmdArray, this.commandVolume);
             length = cmd.length - 1;
            cmd = ArrayCombine(ArrayCombine(ArrayCombine(msg, tagIDBytes), new byte[]{(byte) length}), cmd);
            ant = w1.transceive(cmd);
            printData(ant);
            java.lang.Thread.sleep(300);
            //Messagelength
            i = 0;
            while (i++ < 5) {
                ant = w1.transceive(ArrayCombine(new byte[]{34, -85, 2}, tagIDBytes));
                printData(ant);
                if (ant[0] <= 0) {
                    break;
                }
            }
            i = 0;
            abfrage = ant[1];
            while (i++ < 5) {
                //MEssage
                ant = w1.transceive(ArrayCombine(ArrayCombine(new byte[]{34, -84, 2}, tagIDBytes), new byte[]{0, abfrage}));
                printData(ant);
                if (ant[0] <= 0) {
                    break;
                }
            }
            if (ant.length > 12) {
                act.meldungZeigen(Float.toString(((ant[12] << 24) | (ant[11] << 16) | (ant[10] << 8) | ant[9]) / 1000.0f));
            }
            /// /////////////////////////////////////////////////////////
            act.meldungZeigen("flow:");
            cmd = ArrayCombine(cmdArray, this.commandFlow);
            length = cmd.length - 1;
            cmd = ArrayCombine(ArrayCombine(ArrayCombine(msg, tagIDBytes), new byte[]{(byte) length}), cmd);
            ant = w1.transceive(cmd);
            printData(ant);
            java.lang.Thread.sleep(300);
            //Messagelength
            i = 0;
            while (i++ < 5) {
                ant = w1.transceive(ArrayCombine(new byte[]{34, -85, 2}, tagIDBytes));
                printData(ant);
                if (ant[0] <= 0) {
                    break;
                }
            }
            abfrage = ant[1];
            i = 0;
            while (i++ < 5) {
                //MEssage
                ant = w1.transceive(ArrayCombine(ArrayCombine(new byte[]{34, -84, 2}, tagIDBytes), new byte[]{0, abfrage}));
                printData(ant);
                if (ant[0] <= 0) {
                    break;
                }
            }
            if (ant.length > 10) {
                act.meldungZeigen(Float.toString(((ant[10] << 8) | ant[9]) / 1000.0f));
            }
        }*/
        } catch (Exception e) {
           e.printStackTrace();
        }


        /*MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {


          //      knoeppeZeigen(resp);


            }
        });
    */
        adapter.ignore(tag,1000,tagRemovedListener,null);
    }
    private byte[]summe(byte[] cmd,int start){
        byte sum=0;
        while(start<cmd.length){
            sum+=cmd[start++];

        }
 return ArrayCombine(cmd,new byte[]{(byte) (sum & 0xff),22});
    }
    private void printData(byte[] data){

        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : data) {
            hexBuilder.append(String.format("%02x", b)); // Jedes Byte in 2-stellige Hex-Form bringen
        }
        String hexString = hexBuilder.toString();
        act.meldungZeigen(hexString);

        System.out.println("Hex-String: " + hexString); // Ausgabe: 010aff
    }

}

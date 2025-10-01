package none.mrgoodbody.qalcosonicw1reader;

import static none.mrgoodbody.qalcosonicw1reader.Helper.*;

import java.util.Arrays;


public class MBusParser {
    public static MBusTelegram parse(byte[] paket){
        if(!checkValidity(paket))return new MBusTelegram();
        switch (paket[0]){
            case 0x68:
           return parseLongFrame(paket);
            case 0x10:
                return parseShortFrame(paket);
            case (byte)0xe5:
                return new MBusTelegram(true);

        }
        return new MBusTelegram();
    }

    public static MBusTelegram parse(String paket){
        return parse(hexStringToByteArray(paket));
    }
    public static boolean checkValidity(byte[] paket){
        if (paket.length<1)return false;
        switch (paket[0]){
            case 0x68:
                return checkLongFrame(paket);
            case 0x10:
                return checkShortFrame(paket);
            case (byte)0xe5:
                return paket.length == 1;
            default:
                return false;
        }
    }
    private static MBusTelegram parseShortFrame(byte[] paket) {
    return new MBusTelegram(paket[1],(byte)0xff,paket[2],paket[3]);
    }
    private static MBusTelegram parseLongFrame(byte[] paket) {
    if(paket.length==9)return parseControlFrame(paket);
    return new MBusTelegram(paket[4],paket[6],paket[5],paket[paket.length-2],Arrays.copyOfRange(paket, 7, paket.length-2));
    }
    private static MBusTelegram parseControlFrame(byte[] paket) {
        return new MBusTelegram(paket[4],paket[6],paket[5],paket[7]);
    }
    private static boolean checkShortFrame(byte[] paket){
        if(paket.length!=5)return false;
        if(paket[paket.length-1]!=0x16)return false;
        return calculateChecksum(Arrays.copyOfRange(paket, 0, 3)) == paket[3];
    }
    private static boolean checkLongFrame(byte[] paket){
        if (paket.length==9) return checkControlFrame(paket);
        if (paket.length<10) return false;
        if(paket[paket.length-1]!=0x16)return false;
        if(paket[1]!=paket[2])return false;
        if(paket[3]!=0x68) return false;
        if(paket.length-6!=paket[1])return false;
        return calculateChecksum(Arrays.copyOfRange(paket, 0, paket.length-2)) == paket[paket.length-2];


    }
    private static boolean checkControlFrame(byte[] paket){
        if(paket[1]!=0x03 || paket[2]!=0x03)return false;
        if(paket[3]!=0x68) return false;
        if(paket[paket.length-1]!=0x16)return false;
        return calculateChecksum(Arrays.copyOfRange(paket, 0, 7)) == paket[7];

    }
    public static byte calculateChecksum(byte[] data){
        byte chksum=0;
        int start=1;
        if(data[0]==0x68)start=4;
        for(int i=start;i<data.length;i++){
            chksum+=data[i];
        }
        return (byte)(chksum & 0xff);

    }
}

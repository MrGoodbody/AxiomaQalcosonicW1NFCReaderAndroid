package none.mrgoodbody.qalcosonicw1reader;

import java.util.Arrays;
import java.util.HexFormat;

public class MBusTelegram {
    byte c_field;
    byte address;
    byte ci_field;
    byte checksum;
    byte[] data = new byte[]{};
    boolean validity = false;

    public MBusTelegram() {

    }

    public MBusTelegram(boolean validity) {
        this.validity = validity;
        this.c_field = 0xf;

    }

    public boolean isValid() {
        return this.validity;
    }

    public MBusTelegram(byte c_field, byte ci_field, byte address) {
        this.validity = true;
        this.c_field = c_field;
        this.ci_field = ci_field;
        this.address = address;
    }

    public MBusTelegram(byte c_field, byte ci_field, byte address, byte checksum) {
        this.validity = true;
        this.c_field = c_field;
        this.ci_field = ci_field;
        this.address = address;
        this.checksum = checksum;
    }

    public MBusTelegram(byte c_field, byte ci_field, byte address, byte checksum, byte[] data) {
        this.validity = true;
        this.c_field = c_field;
        this.ci_field = ci_field;
        this.address = address;
        this.checksum = checksum;
        this.data = data;
    }

    public String toString() {
        if (!this.validity) return  "invalid paket";
        String returnString = "Adresse: " + String.valueOf(Byte.toUnsignedInt(this.address)) + "\r\n";
        returnString += "C-Feld: " + String.valueOf(this.c_field) + "\r\n";
        returnString += "\tRichtung: " + ((this.c_field >>> 6) & 1) + "\r\n";
        returnString += "\tFCB/ACD: " + ((this.c_field >>> 5) & 1) + "\r\n";
        returnString += "\tFCV/DFC: " + ((this.c_field >>> 4) & 1) + "\r\n";
        byte cf = (byte) (this.c_field & 0xf);
        returnString += "\tF(3-0): " + ((this.c_field) & 0xf) + "\r\n";
        returnString += "\t\t";
        switch (cf) {
            case 0:
                returnString += "SND_NKE";
                break;
            case 3:
                returnString += "SND_UD";
                break;
            case 11:
                returnString += "REQ_UD2";
                break;
            case 10:
                returnString += "REQ_UD1";
                break;
            case 8:
                returnString += "RSP_UD";
                break;
            case 15:
                return "E5 paket received";
            default:
                returnString += "Uknown: " + String.valueOf(cf);
        }

        returnString += "\r\n";

        if (ci_field != (byte) 0xff) {

            returnString += "CI-Feld: " + String.valueOf(Byte.toUnsignedInt(this.ci_field)) + "\r\n";
            returnString += "\t";
            switch (ci_field) {
                case 0x55:
                case 0x51:
                    returnString += "data send";
                    break;
                case 0x56:
                case 0x52:
                    returnString += "selection of slaves";
                    break;
                case 0x50:
                    returnString += "application reset";
                    break;
                case 0x54:
                    returnString += "synchronize";
                    break;
                case 0x70:
                    returnString += "report of general aplication errors";
                    break;
                case 0x71:
                    returnString += "report of alarmstatus";
                    break;
                case 0x76:
                case 0x72:
                    returnString += "variable data respond";
                    break;
                case 0x73:
                case 0x77:
                    returnString += "fixed data respond";
                    break;
                case (byte) 0xa1:
                    returnString += "Axioma read Register";
                    break;
                case (byte) 0xa2:
                    returnString += "Axioma write Register";
                    break;
            }
            returnString += "\r\n";


            if (data.length != 0) {
                if (ci_field == 0x72 || ci_field == 0x76) {
                    returnString += "\t\tZählernummer: " + String.valueOf(data[3] >>> 4 & 0xf) + String.valueOf(data[3] & 0x0f) + String.valueOf(data[2] >>> 4 & 0xf) + String.valueOf(data[2] & 0x0f) + String.valueOf(data[1] >>> 4 & 0xf) + String.valueOf(data[1] & 0x0f) + String.valueOf(data[0] >>> 4 & 0xf) + String.valueOf(data[0] & 0x0f) + "\r\n";
                    returnString += "\t\tHersteller: " + String.valueOf((int) ((data[4] << 8) | data[5])) + "\r\n";
                    returnString += "\t\tVersion: " + String.valueOf(data[6]) + "\r\n";
                    returnString += "\t\tMedium: " + String.valueOf(data[7]) + "\r\n";
                    returnString += "\t\t\t" + mediumLookup(data[7]) + "\r\n";
                    returnString += "\t\tZugriffnr.: " + String.valueOf(Byte.toUnsignedInt(data[8])) + "\r\n";
                    returnString += "\t\tStatus: " + String.valueOf(data[9]) + "\r\n";
                    if (data[9] != 0) {
                        returnString += "\t\t\tStatus: \r\n";

                        if ((data[9] & 1) == 1) {
                            returnString += "\t\t\t\tApplication busy\r\n";
                        }
                        if (((data[9] >>> 1) & 1) == 1) {
                            returnString += "\t\t\t\tApplication error\r\n";
                        }
                        if (((data[9] >>> 2) & 1) == 1) {
                            returnString += "\t\t\t\tPower low\r\n";
                        }
                        if (((data[9] >>> 3) & 1) == 1) {
                            returnString += "\t\t\t\tPermanent error\r\n";
                        }
                        if (((data[9] >>> 4) & 1) == 1) {
                            returnString += "\t\t\t\tTemporary error\r\n";
                        }
                    }

                    if (data[10] != 0 || data[11] != 0) {
                        returnString += "\t\tSignature: " + String.valueOf((data[10] << 8) | data[11]) + "\r\n";
                    }

                    if(data.length > 12) {
                        returnString += "Datablock: \r\n" + parseDataBlock(Arrays.copyOfRange(data, 12, data.length));
                    }
//todo: fixed length paket /incomplete
                } else if (ci_field == 0x73 || ci_field == 0x77) {
                    if (data.length != 16) {
                        returnString += "\r\nInvalid Data: " + Helper.byteArrayToHexString(this.data) + "\r\n";

                    } else
                        returnString += "\t\tZählernummer: " + String.valueOf(data[3] >>> 4 & 0xf) + String.valueOf(data[3] & 0x0f) + String.valueOf(data[2] >>> 4 & 0xf) + String.valueOf(data[2] & 0x0f) + String.valueOf(data[1] >>> 4 & 0xf) + String.valueOf(data[1] & 0x0f) + String.valueOf(data[0] >>> 4 & 0xf) + String.valueOf(data[0] & 0x0f) + "\r\n";
                    returnString += "\t\tZugriffnr.: " + String.valueOf(data[4]);
                    returnString += "\t\tStatus: " + String.valueOf(data[5]);
                    returnString += "\t\t\tStatus: \r\n";
                    returnString += "\t\t\t\t";
                    if ((data[5] & 1) == 0) {
                        returnString += "BCD Werte";
                    } else {
                        returnString += "Vorzeichen Binärwerte";
                    }
                    returnString += "\r\n\t\t\t\t";
                    if (((data[5] >>> 1) & 1) == 0) {
                        returnString += "Batterie OK";
                    } else {
                        returnString += "Batterie niedrig";
                    }
                    returnString += "\r\n\t\t\t\t";
                    if (((data[5] >>> 2) & 1) == 0) {
                        returnString += "Kein persistierender Fehler";
                    } else {
                        returnString += "Persistierender Fehler";
                    }
                    if (((data[5] >>> 3) & 1) == 0) {
                        returnString += "Kein temporärer Fehler";
                    } else {
                        returnString += "Temporärer Fehler";
                    }


                } else {

                }
                //      returnString += "\r\nData: " + HexFormat.of().formatHex(this.data) + "\r\n";
            }
        }

        return returnString;


    }

    private String mediumLookup(byte medium) {
        switch (medium) {
            case 0x0:
                return "Other";
            case 0x1:
                return "Oil";
            case 0x2:
                return "Electricity";
            case 0x3:
                return "Gas";
            case 0x4:
                return "Heat (outlet)";
            case 0x5:
                return "Steam";
            case 0x6:
                return "Hot Water";
            case 0x7:
                return "Water";
            case 0x8:
                return "Heat Cost Allocator";
            case 0x9:
                return "Compressed Air";
            case 0xa:
                return "Cooling load meter(outlet)";
            case 0xb:
                return "Cooling load meter(inlet)";
            case 0xc:
                return "Heat inlet";
            case 0xd:
                return "Heat/Cooling load meter";
            case 0xe:
                return "Bus/System";
            case 0xf:
                return "uknown";
            case 0x16:
                return "Cold Water";
            case 0x17:
                return "Dual Water";
            case 0x18:
                return "Pressure";
            case 0x19:
                return "A/D Converter";
            default:
                return "reserved";
        }

    }

    private String parseDataBlock(byte[] data) {
        //todo: incomplete/sometimes wrong
        int blockIndex = 0;
        String retString="";
      //  System.out.println(HexFormat.of().formatHex(data));
        CounterEntry[] entrys = new CounterEntry[]{};
        while (blockIndex < data.length) {
            CounterEntry acEntry = new CounterEntry();
            acEntry.dif=Byte.toUnsignedInt(data[blockIndex]);
            acEntry.dataField = (acEntry.dif & 0xf);
            acEntry.functionField = ((acEntry.dif >>> 4) & 3);
//System.out.println("DIF: "+data[blockIndex]);

            while ((data[blockIndex] >>> 7) >= 1) {

                blockIndex++;


            }
            blockIndex++;
            acEntry.vif =  Byte.toUnsignedInt(data[blockIndex ]);


       //     System.out.println("Geschiftet"+(Byte.toUnsignedInt(data[blockIndex]) >>> 7));
            while ((data[blockIndex] >>> 7) >= 1) {

                blockIndex++;


            }
            blockIndex++;
           // System.out.println(blockIndex);
            acEntry.value = Arrays.copyOfRange(data, blockIndex, blockIndex + acEntry.getLength());
            entrys = Arrays.copyOf(entrys, entrys.length + 1);
            entrys[entrys.length - 1] = acEntry;
            blockIndex += acEntry.getLength() ;
           // System.out.println("Block index: "+blockIndex);
           //System.out.println(acEntry.toString());
            retString+="\t"+acEntry.toString()+" "+acEntry.getUnit()+"\r\n";
        }
        return retString;
        //int year=((cutData[2] >>>7| ));

/*return HexFormat.of().formatHex(data)+"\r\n"+
        "DF=" + acEntry.dataField + "\r\nFF=" + acEntry.functionField +
        "\r\nVIF="+String.valueOf(acEntry.vif) +
        "\r\nData=" + HexFormat.of().formatHex(cutData);*/


    }

}

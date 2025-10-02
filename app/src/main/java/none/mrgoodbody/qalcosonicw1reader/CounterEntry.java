package none.mrgoodbody.qalcosonicw1reader;

public class CounterEntry {
    int functionField=0;
    int dataField=0;
    int dif=0;
    byte[] value;
    int length=0;
    boolean isTime=false;
boolean isDIF=true;
int storageNumber=0;
int tariff=0;
int vif=0;
String unit="";
    public int getLength() {
        switch (dataField){
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
            case 5:
                return 4;
            case 6:
                return 6;
            case 7:
                return 8;
            case 8:
                return 0;
            case 9:
                return 1;
            case 10:
                return 2;
            case 11:
                return 3;
            case 12:
                return 4;
            case 13:
                return 4;
            case 14:
                return 6;
            case 15:
                return 8;
            default:
                return 0;

        }

    }
    public String getUnit(){
        switch ((vif >>>4)&0x7){
            case 0:
                if((vif & 0b1000) ==0){
                    return "Wh (energy)";
                }else {return "J (energy)";}
            case 1:
                if((vif & 0b1000) ==0){
                    return "m³ (volume)";
                }else {return "kg (mass)";}
            case 2:
                //todo: incomplete
                if((vif & 0b1000) ==0){

                    return "minutes (on time or operating time)";
                }else {return "W (Power)";}

            case 3:
                if((vif & 0b1000) ==0){
                    return "J/h (Power)";
                }else {return "m³/h (Volume flow)";}
            case 4:
                if((vif & 0b1000) ==0){
                    return "m³/min (volume flow external)";
                }else {return "m³/s (volume flow external)";}
            case 5:

                if((vif & 0b1000) ==0){
                    return "kg/h (mass flow)";
                }else {return "°C (Flow/Return temperature)";}
            case 6:
                //todo: incomplete
                if((vif & 0b1000) ==0){

                    return "K (temperature difference) or °C(external temperature)";

                }else {
                    if((vif & 0b100) ==0){
                        return "bar (pressure)";
                    }else {
                        return "time or time & date (time point)";}

                }
            default:
                return "unknown";

        }
    }
 private float getMultiplyer(){
        switch ((vif >>>4)&0x7){
            case 0:
                if((vif & 0b1000) ==0){
                    return (float) Math.pow(10,(vif & 0b111)-3);
                }else {return (float)Math.pow(10,(vif & 0b111));}
            case 1:
                if((vif & 0b1000) ==0){
                    return (float) Math.pow(10,(vif & 0b111)-6);
                }else {return (float)Math.pow(10,(vif & 0b111)-3);}
            case 2:
                if((vif & 0b1000) ==0){
                    return 1f/60;
                }else {return (float)Math.pow(10,(vif & 0b111)-3);}

            case 3:
                if((vif & 0b1000) ==0){
                    return (float) Math.pow(10,(vif & 0b111));
                }else {return (float)Math.pow(10,(vif & 0b111)-6);}
            case 4:
                if((vif & 0b1000) ==0){
                    return (float) Math.pow(10,(vif & 0b111)-7);
                }else {return (float)Math.pow(10,(vif & 0b111)-9);}
            case 5:
                if((vif & 0b1000) ==0){
                    return (float) Math.pow(10,(vif & 0x111)-3);
                }else {return (float)Math.pow(10,(vif & 0b11)-3);}
            case 6:
                if((vif & 0b1000) ==0){

                        return (float) Math.pow(10,(vif & 0b11)-3);

                }else {
                    if((vif & 0b100) ==0){
                        return (float) Math.pow(10,(vif & 0b11)-3);
                    }else {
                        isTime=true;
                        return 1.0f;}

                }
            default:
                return 1.0f;

        }
 }
    public float getValue() {
        int binaryValue=0;
float multiply=getMultiplyer();
//multiply=1f;
        String bcdValue="";
        for(int i =0; i<value.length;i++){
            if(i==value.length-1){
                binaryValue |= ((value[i])) << (i * 8);
            }else {
                binaryValue |= (Byte.toUnsignedInt(value[i])) << (i * 8);
            }
            //System.out.println(Integer.toBinaryString(binaryValue));
        }
        for(int i =0; i<value.length;i++){
            bcdValue+=String.valueOf(((value[i] >>> 8) & 0xf) )+String.valueOf((value[i]  & 0xf));
        }
        switch (dataField){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return binaryValue*multiply;
            case 9:

            case 10:

            case 11:

            case 12:

            case 13:

            case 14:
                return Float.parseFloat(bcdValue)*multiply;

            default:
                return 0.0f;

        }

    }

    @Override
    public String toString() {
      //  String retString="DIF:"+ byteArray2HexString(new byte[]{(byte) (this.dif & 0x7f)})+",VIF:"+ byteArray2HexString(new byte[]{(byte) (this.vif & 0x7f)})+ " = "+ Integer.toBinaryString(this.vif) + " = " + getValue();
        String retString=String.valueOf(getValue());
        if(isTime)return parseDateTime(this.value);
        return retString;
    }
    private String parseDateTime(byte[] data) {
        int zeit = data[3] << 24 | data[2] << 16 | data[1] << 8 | data[0];

        int min = zeit & 0x3F;
        int h = (zeit >>> 8 & 0x1F);
        int day = (zeit >>> 16 & 0x1F);
        int mon = (zeit >>> 24 & 0xF);
        int year = ((zeit >>> 21) & 0x7) | ((zeit >>> 25) & 0x78);
        String retString="Zeit: "+h+":"+min+" "+day+"."+mon+"."+year;
        if (data[0]>>>7==0){
            retString+=" Zeit gültig!";
        }
        if ((data[2] & 1) ==0){
            retString+=" Standardzeit!";
        }
        return retString;
    }

}

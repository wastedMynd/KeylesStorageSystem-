package inc.wastedmynd.keylesstorage.SerialCommunication;

/**
 * monitor wieght sensor; parcel0  Raspberry Pi sends 'd', micro controller transmits back a '0' meaning these no parcel and '1' these a parcel
 *                       parcel1  Raspberry Pi sends 'e', micro controller transmits back a '0' meaning these no parcel and '1' these a parcel
 *                      parcel2  Raspberry Pi sends 'f', micro controller transmits back a '0' meaning these no parcel and '1' these a parcel
 */
public enum ParcelStatus {

    //monitor wieght sensor; parcel0  Raspberry Pi sends 'd'
    storage_unit_0('d'),
    storage_unit_1('e'),
    storage_unit_2('f');

    private char mCommand;
    private ParcelResponse parcelResponse;

    public char getCommand() {
        return mCommand;
    }

    public ParcelResponse getParcelResponse() {
        return parcelResponse;
    }

    public void setParcelResponse(ParcelResponse parcelResponse) {
        this.parcelResponse = parcelResponse;
    }

    ParcelStatus(char command)
    {
        mCommand = command;
        parcelResponse = ParcelResponse.empty;
    }


    public enum ParcelResponse
    {
        //micro controller transmits back a '0' meaning these no parcel and '1' these a parcel
        empty('p'),
        containing('P');

        private char parcelResponse;

        public char getParcelResponse() {
            return parcelResponse;
        }

        ParcelResponse(char parcelResponse) { this.parcelResponse = parcelResponse;}

        public boolean isStorageEmpty()
        {
            return (this == empty);
        }
    }
}

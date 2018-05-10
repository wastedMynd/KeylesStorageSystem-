package inc.wastedmynd.keylesstorage.SerialCommunication;

/***
 * monitor door; door0  Raspberry Pi sends 'g', micro controller transmits back a '0' meaning the door is open and '1' door is closed
 *              door1  Raspberry Pi sends 'h', micro controller transmits back a '0' meaning the door is open and '1' door is closed
 *              door2  Raspberry Pi sends 'i', micro controller transmits back a '0' meaning the door is open and '1' door is closed
 **/
public enum  DoorStatus {
    //monitor door; door0  Raspberry Pi sends 'g',
    storage_unit_0('g'),
    storage_unit_1('h'),
    storage_unit_2('i');

    private char mCommand;
    private DoorResponses doorResponses;

    public char getCommand() {
        return mCommand;
    }

    public DoorResponses getDoorResponses() {
        return doorResponses;
    }

    public void setDoorResponses(DoorResponses doorResponses) {
        this.doorResponses = doorResponses;
    }

    DoorStatus(char command)
    {
        mCommand = command;
        doorResponses = DoorResponses.closed;
    }



    public enum DoorResponses
    {
        //micro controller transmits back a '0' meaning the door is open and '1' door is closed
        closed('D'),
        open('d');

        private char doorResponse;

        public char getDoorResponse() {
            return doorResponse;
        }

        DoorResponses(char doorResponse) { this.doorResponse= doorResponse;}

        public boolean doorClosed()
        {
            return  (this == closed);
        }
    }
}

package inc.wastedmynd.keylesstorage.SerialCommunication;

/**
locking and unlocking; lock0  Raspberry Pi sends 'a',followed by 'l' unlocks and 'L' to lock
                       lock1  Raspberry Pi sends 'b',followed by 'l' unlocks and 'L' to lock
                       lock1  Raspberry Pi sends 'c',followed by 'l' unlocks and 'L' to lock
*/
public enum  LockAccess
{
        storage_unit_0('a'),
        storage_unit_1('b'),
        storage_unit_2('c');

        private char mCommand;
        public LockStatus lockStatus;

        public char getCommand() {
            return mCommand;
        }

        public LockStatus getLockStatus() { return lockStatus;}

        public void setLockStatus(LockStatus lockStatus) { this.lockStatus = lockStatus; }

        LockAccess(char command)
        {
            mCommand = command;
            setLockStatus(LockStatus.lock);
        }

        public enum LockStatus
        {
            lock('L'),
            unlock('l');

            private char mCommand;
            public char getCommand()
            {
                return mCommand;
            }

            LockStatus(char command)
            {
                mCommand = command;
            }
        }
}

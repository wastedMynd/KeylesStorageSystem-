package inc.wastedmynd.keylesstorage.data;

import inc.wastedmynd.keylesstorage.SerialCommunication.DoorStatus;
import inc.wastedmynd.keylesstorage.SerialCommunication.LockAccess;
import inc.wastedmynd.keylesstorage.SerialCommunication.ParcelStatus;

public class StorageUnit {

    private final int unit;
    private Token token;
    private DoorStatus doorStatus;
    private LockAccess lockAccess;
    private ParcelStatus parcelStatus;

    //region getters & setters


    public int getUnit() {
        return unit;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public DoorStatus getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(DoorStatus doorStatus) {
        this.doorStatus = doorStatus;
    }

    public LockAccess getLockAccess() {
        return lockAccess;
    }

    public void setLockAccess(LockAccess lockAccess) {
        this.lockAccess = lockAccess;
    }

    public ParcelStatus getParcelStatus() {
        return parcelStatus;
    }

    public void setParcelStatus(ParcelStatus parcelStatus) {
        this.parcelStatus = parcelStatus;
    }
    //endregion


    public StorageUnit(Token token) {
        this.unit = token.getStorageUnit();
        this.token = token;
        getToken().setStorageUnit(unit);

        setLockAccess(LockAccess.values()[unit]);
        setParcelStatus(ParcelStatus.values()[unit]);
        setDoorStatus(DoorStatus.values()[unit]);
    }
}

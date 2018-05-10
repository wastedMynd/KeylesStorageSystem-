package inc.wastedmynd.keylesstorage.data;

import android.text.Html;
import android.text.Spanned;

import java.util.Objects;

public class Token {

    public static final int INVALID_TOKEN = -1;

    private int id;
    private int storageUnit;
    private String passPhrase;
    private String userMacAddress;
    private long timeStamp;


    public Token(int storageUnit) {
        this.storageUnit = storageUnit;
    }

    public  Token(int storageUnit, String passPhrase)
    {
        setStorageUnit(storageUnit);
        setPassPhrase(passPhrase);
    }

    public Token() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStorageUnit() {
        return storageUnit;
    }

    public Token setStorageUnit(int storageUnit) {
        this.storageUnit = storageUnit;
        return this;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    public String getUserMacAddress() {

        if(this.userMacAddress==null) return "";
        else
        return userMacAddress;
    }

    public void setUserMacAddress(String userMacAddress) {
        this.userMacAddress = userMacAddress;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isValid() { return  (getStorageUnit() > INVALID_TOKEN) && !(getPassPhrase().isEmpty()) ; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return storageUnit == token.storageUnit;
    }

    @Override
    public int hashCode() {

        return Objects.hash(storageUnit);
    }

    @Override
    public String toString() {
        StringBuilder tokenStr = new StringBuilder("Storage Unit");
        tokenStr.append("\n");
        Spanned spanned = Html.fromHtml("<b>"+String.valueOf(getStorageUnit())+"</b>");
        tokenStr.append(spanned);
        return tokenStr.toString();
    }
}

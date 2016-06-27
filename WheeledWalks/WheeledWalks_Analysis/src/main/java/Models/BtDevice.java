package Models;

import java.util.List;

/**
 * Created by David on 19/05/2016.
 */
public class BtDevice {
    private String name;
    private String address;
    private String alias;

    public BtDevice() {
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAlias() {
        return alias;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

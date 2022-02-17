package sample;

import java.io.Serializable;

/** Κλαση που αναπαρτιστα τους χρηστες */

public class User implements Serializable {
    private String name;
    private String surname;
    private String email;
    private String username;
    private byte[] salt;
    private byte[] cryptoPassword;

    public User(String name, String surname, String email, String username, byte[] salt, byte[] cryptoPassword) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.salt = salt;
        this.cryptoPassword = cryptoPassword;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getCryptoPassword() {
        return cryptoPassword;
    }
}

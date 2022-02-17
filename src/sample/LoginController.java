package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;

import javax.crypto.*;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final String privateKeyFromRSAEncryption = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFKYyN+JskGEDxL1AVyETSMc+g3Fwv8N78Z95BuvNKA6QPUcBoncRhBvAPQ4JHNgX2hCtX/WIhAxDeOzdi5UPAFRcoNy5kdLEPlMc4zL0qPiUtpo0CZ8712snWD9fstdYUYzz8a4xRnNxVkimbZL+N1q2cMc4oCdPldxJU67UcHY2MbnkVCaG2jugjC1tsvBu6eg8aDfC2PlmIv6FtRnjB5iCEeVMRPuyXH6/+jkAf71IfeFAsxswidyufJPA24qWPtxtzMHJTXFRHmnqGbeW0kOgL/3ucFCHwyM8ssfbKojeONIGyqaH47G/Gjp04I39S+6o5rlQG2W9kmGEtxPXTAgMBAAECggEAB7mjV9EmXu+xRMyz3q+hnngZTYG7L2TP6uCzuih4WMDhUaoyeV89t0tJvfjyo+L0cZq1UQN4H+YnolzZF7mpNomCsGiTHmzOoNLGw/rMtpioW3+gCpWvIgAxWWPN6QObic9ACHzMxSdOeQBjvYoYusfLhpY1GYmIa3V+8MJD3X+a4YIRQgI2NolM17QeIsrIHCWcVR/l37J7i+sgKfOaXnmN3nKpl63fWEI4cmO9hpTKpE1TfW25y2/85t0UIfyolpaCNveRvRGDUDgMAvOUpEnI1LARlIIM3hVGq2FUwcB8mCeXZXXsYIGb6EAg5lPJiTC+qSIrxp/UPmQd8z4sYQKBgQD+OTbF9JYDN+DsyE6180X/r3x7hMTvs0HbyczKyu5b9UYLnXdO0m8aK+GwtNLBJF8rpmtGcETNKLg3hlu+/3VK0N6Q7TgxlrsvFA3YJTYWOyHpPD65gEn/0u01b49PCjGWE8YnzCpDMDaxhxQvo1L2vthUUyK1nGLWBvsecp3vGQKBgQCGF8Qa0H7qh96jWZhMITGA+tl0KA447OCIFpIkiIEHhFCbB4+utZ+K5weLbygtWnsx2IgAhzueNQN5w0Y61zjuQWXi5kU7+we8LhI1GTL9Z36KkCFLssZnmGTovljqm5epHGv1GXJ0CEXxS7r9gi82gFK/fKRWC/RRkurwoL/lywKBgQC+4Mh+c3mbKv6H6pImzWT2aJWnzHVtoINHekp4zUuQ4iVYnT+ygBAJb39ChVJk0GFgzdBoD+2ouPUwQ4JpczdnGFK0MFjr6sfavkzyyGXG5Vk93Q0fjPKC0aVnZL8OUpIOpAQ5Z3MoBXBbdRez+QZW285j6hP6llQjbRRptRdeKQKBgAOQcif8TsNRJTvWT9QGNdil0k3iGq6srTMw1mWeMz/N5o6YXttX/IumpG6yX7EE7K1gggzxi0YjRppf9Gfv/JVoq5qiYEi93XDFELrUlqRhsd1hA2GNuPRb+qKtHJPv2tIl7UCwkng/GAzX5HqEkVizhH+Ogpe+7ZmVfCU1QKSNAoGBALHA9qw2+4AZyMuKFzsoOAE8sIYTaMNL5fYiCSYoU7XscFvLO6j/bFajRlnaBFxXdUnzh65vM9kKyuF7MahlkFR67/3n5Z2j95HhcBw1Ki7QYDKchboYXo6MwUYVR4U4k2/e2rG8O1v3B2DOB4ZhcSDQF23KtLncIE97YEJ2KwKj";
    private Key publicKey;
    private Key privateKey;
    private ArrayList<User> users = new ArrayList<>();


    @FXML
    private Pane signinPane;

    @FXML
    private TextField textfieldUsername;

    @FXML
    private PasswordField textfieldPassword;

    @FXML
    private Button btnSignin;

    @FXML
    private Button btnSignup;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Pane signupPane;

    @FXML
    private ImageView backBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField nameField;

    @FXML
    private Button getstartedBtn;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorSignUpLabel;


    /**
     * Functions
     *
     * @param url
     * @param resourceBundle
     */

    /**
     * Η συναρτηση initialize στο javafx απο οτι καταλαβαμε τρεχει πρωτα αυτη για να κανει μερικα πραγματα που θελοπουμε και μετα τα γραφικα στην ουσια αρχικοποιη η φορτωνη τα αρχεια και αλλα πραγματα που θελουμε για να δουλευουν σωστα τα γραφικα
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadFromFileUsers();
        dimiourgiaKleidiwn();
    }

    /**
     * Απλα αλλαζει το login panel με το sign up panel
     *
     * @param mouseEvent
     */
    @FXML
    public void handlerMouseEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().equals(backBtn)) {
            signinPane.toFront();
        }
    }

    /**
     * Action Event function που ελενχει αν ο χρηστης ειναι εγγεγραμενος στο συστημα και αναλογα την απαντηση του εμφανιζει το καταλληλο μηνυμα η του εμφανιζει το MainFrame
     *
     * @param event
     * @throws IOException
     */
    public void handlerButtonActionSignIn(ActionEvent event) {
        try {

            if (event.getSource().equals(btnSignin)) {
                if (textfieldUsername.getText().equals("") || textfieldPassword.getText().equals("")) {
                    errorMessageLabel.setText("Please Fill all fields");
                } else {

                    String username = textfieldUsername.getText();
                    String password = textfieldPassword.getText();

                    if (verifyUser(username, password)) {
                        errorMessageLabel.setText("Ok");
                        byte[] salt = null;
                        for (User user : users) {
                            if (user.getUsername().equals(username))
                                salt = user.getSalt();
                        }

                        // Με αυτον τον τροπο μπορουμε να παρουμε τα γραγικα απο MainFrame χωρις να χρειαστει να δημιουργισουμε νεο παραθυρο
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFrame.fxml"));
                        AnchorPane pane = loader.load();

                        MainFrameController test = loader.getController();
                        test.setMainFrameController(privateKey, username, salt);

                        Scene mainScene = new Scene(pane);
                        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        mainStage.setScene(mainScene);
                        mainStage.show();


                    } else {
                        errorMessageLabel.setText("Invalid Username or Password");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    /**
     * Συναρτηση που ελενγχει αν το username υπαρχει ειδη.Καθε χρηστης πρεπει να εχει διαφορετικο username
     *
     * @param username
     * @return
     */
    public boolean userExists(String username) {
        for (User x : users) {
            if (x.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Η συναρτηση αυτη ελεγχει αν ο χρηστης συμπληρωσε ολα τα πεδια για την εγγραφη του και στην συνεχεια αν το username που εβαλε δεν υπαρχει στο συστημα τοτε
     * δημιουργει εναν φακελο με το username του, δημιουργει το salt me χωρο 16 bytes και τους αρχικοποιει με τυχαιους αριθμους-χαρακτηρες.
     * Στην συνεχεια χρησημοποιει την hash function και το κρυπτογραφουμε
     * Τελος αποθηκευουμε τα στοιχεια του σε αρχειο και δημιουργουμε το συμμετρικο κλειδι το οποιο θα αποθηκευτει στον φακελο του
     * Και κανουμε clear τα πεδια
     */

    public void handlerButtonActionSignUp(ActionEvent event) {
        if (event.getSource().equals(btnSignup)) {
            signupPane.toFront();
        }
        if (event.getSource().equals(getstartedBtn)) {

            if (nameField.getText().equals("") || surnameField.getText().equals("") || emailField.getText().equals("") || usernameField.getText().equals("") || passwordField.getText().equals("") || confirmPasswordField.getText().equals("")) {
                errorSignUpLabel.setText("Please fill all fields");
            } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                errorSignUpLabel.setText("The passwords does not match");
            } else {

                if (userExists(usernameField.getText())) {
                    errorSignUpLabel.setText("This Username exists");
                } else {

                    ObjectOutputStream addUserToFile = null;
                    User newUserToAdd = null;

                    try {

                        String password = passwordField.getText();
                        String username = usernameField.getText();
                        //dimiourgia neou fakelou gia ton xristi
                        File dir = new File(username);
                        dir.mkdir();

                        //dimiourgia tuxaiou Salt gia ton xristi
                        SecureRandom random = new SecureRandom();
                        byte[] salt = new byte[16];
                        random.nextBytes(salt);

                        //hasharisma kwdikou
                        MessageDigest dig = MessageDigest.getInstance("SHA-256");
                        //perasma tou salt sto antikeimeno messageDigest
                        dig.update(salt);
                        byte hashedPassword[] = dig.digest(password.getBytes());

                        /** --------------RSA---------------- */
                        Cipher userRSAPassword;
                        userRSAPassword = Cipher.getInstance("RSA");
                        userRSAPassword.init(Cipher.ENCRYPT_MODE, publicKey);
                        //teliki diadikasia kryptografisis
                        byte encPass[] = userRSAPassword.doFinal(hashedPassword);

                        newUserToAdd = new User(nameField.getText(), surnameField.getText(), emailField.getText(), username, salt, encPass);
                        users.add(newUserToAdd);

                        addUserToFile = new ObjectOutputStream(new FileOutputStream("UserFile.dat"));
                        addUserToFile.writeObject(users);

                        //Δημιουργια Συμμετρικου Κλειδιου
                        createSymmetricKey(username);

                        signinPane.toFront();
                        System.out.println("Nice User Created");

                        nameField.clear();
                        surnameField.clear();
                        emailField.clear();
                        usernameField.clear();
                        passwordField.clear();
                        passwordField.clear();
                        confirmPasswordField.clear();


                    } catch (NoSuchAlgorithmException ex) {
                        ex.printStackTrace();
                    } catch (NoSuchPaddingException ex) {
                        ex.printStackTrace();
                    } catch (InvalidKeyException ex) {
                        ex.printStackTrace();
                    } catch (IllegalBlockSizeException ex) {
                        ex.printStackTrace();
                    } catch (BadPaddingException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            //kleisimo arxeiou
                            if (addUserToFile != null)
                                addUserToFile.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }

        }

    }


    /**
     * Συναρτηση createSymmetricKey Δημιουργει το συμμετρικο κλειδι
     * Αρχικα καλουμε το KeyGenerator για να του πουμε τι ειδους αλγοριθμου θα χρησημοποιησουμε και με το SecretKey κανουμε παραγωγη του κλειδιου
     * Στην συνεχεια το κρυπτογραφουμε με το RSA και το αποθηκευουμε στο αρχειο του χρηστη
     *
     * @return
     */

    public void createSymmetricKey(String username) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {

        //dimiourgia neou kleidiou tipou AES-256
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        //orismos megethous kleidiou
        generator.init(256);

        //paragwgi neou kleidiou
        SecretKey symmetricKey = generator.generateKey();

        // kruptografisi kleidiou me to dimosio tis efarmogis
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);
        // teliki diadikasia kryptografisis
        byte encText[] = rsa.doFinal(symmetricKey.getEncoded());

        //eggrafi tou summetrikou kleidiou tu xristi sto arxeio
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(username + "//Key.txt"));
        output.writeObject(encText);
        //kleisimo arxeiou
        output.close();


    }


    /**
     * Αποθηκευουμε σε μια λιστα τους χρηστες για ποιο γρηγορη αποκρηση του συστηματος και δικη μας
     */

    public void loadFromFileUsers() {
        try {
            File file = new File("UserFile.dat");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                //diavasma twn xristwn apo to arxeio
                ObjectInputStream input = new ObjectInputStream(new FileInputStream("UserFile.dat"));
                users = (ArrayList<User>) input.readObject();
                input.close();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Για να επιβεβαιβσουμε οτι ο χρηστης ειναι εγγεγραμενος στο συστημα απλα ψαχνουμε το username
     * αν υπαρχει μεσα στην λιστα και στην συνεχεια κανουμε την διαδικασια τις κρυπτογραφησης του κλειδιου
     * Αν το κλειδι ειναι ιδιο με αυτο που εχουμε αποθηκευσει τοτε επεστρεψε true
     */

    public boolean verifyUser(String username, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                byte[] salt = user.getSalt();

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(salt);

                byte hashedPassword[] = digest.digest(password.getBytes());

                byte encryptedPassword[] = user.getCryptoPassword();

                Cipher rsa = Cipher.getInstance("RSA");
                rsa.init(Cipher.DECRYPT_MODE, privateKey);

                byte decodePassword[] = rsa.doFinal(encryptedPassword);

                if (Arrays.equals(decodePassword, hashedPassword)) {
                    System.out.println("Welcome to the system");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * ==================================================================================================
     */


    public void dimiourgiaKleidiwn() {
        File publicfile = new File("publicKey.txt");
        if (!publicfile.exists()) {
            try {

                // Δημιουργια ζευγη κλειδιων
                KeyPairGenerator keypairGen = KeyPairGenerator.getInstance("RSA");
                // Μεγεθος κλειδιου
                keypairGen.initialize(2048, new SecureRandom());
                // Κλαση που απλα κραταει τα ζευγη  API 'This class is a simple holder for a key pair (a public key and a private key). It does not enforce any security, and, when initialized, should be treated like a PrivateKey.'
                KeyPair keypair = keypairGen.generateKeyPair();
                // Αποθηκευση δημουσιου και ιδιοτικου κλειδιου το καθε ενα ξεχωριστα
                Key publick = keypair.getPublic();
                Key privatek = keypair.getPrivate();

                //elegxos an ta kleidia einai se antistoixia
                //kriptografisi
                /*Cipher rsa = Cipher.getInstance("RSA");
                rsa.init(Cipher.ENCRYPT_MODE, publick);
                byte encText[] = rsa.doFinal("Xaris".getBytes());

                Cipher rsa2 = Cipher.getInstance("RSA");
                rsa.init(Cipher.DECRYPT_MODE, privatek);
                byte decText[] = rsa.doFinal(encText);
                System.out.println(new String(decText, "UTF-8"));*/

                /** https://www.devglan.com/java8/rsa-encryption-decryption-java
                 * Το base64 είναι ομάδα ομοειδών κωδικοποιήσεων ψηφιακών δεδομένων σε κείμενο. Η κωδικοποίηση αυτή αντιστοιχεί τα μπάιτ ενός αρχείου σε ένα υποσύνολο του ASCII που αποτελείται μόνο από εκτυπώσιμους χαρακτήρες.
                 * Κάθε ψηφίο ενός κειμένου, κωδικοποιημένου με Base64, αντιστοιχεί σε 6 δυαδικά ψηφία των δυαδικών δεδομένων και επομένως η κωδικοποίηση ενός μπάιτ απαιτεί τουλάχιστον 2 ψηφία.
                 * Πάντα κωδικοποιούνται τρία συνεχόμενα μπάιτ, δηλαδή τα τρία μπάιτ, που αποτελούνται από 8 μπιτ (σύνολο 24 μπιτ), μετατρέπονται σε 4 συνεχόμενα «ψηφία» του συστήματος Base64.
                 */

                // Δημιουργια encoder για εγγραφη κλειδιου στο αρχειο.
                Base64.Encoder encoder = Base64.getEncoder();
                // Για να αποθηκευσουμε το ιδιοτικο κλειδι στην αρχη απλα το εκτυπωνουμε για να το παρουμε
                System.out.println(encoder.encodeToString(privatek.getEncoded()));

                // Δημιουργια FileWriter αντικειμενου και αποθηκευση του κλειδιου publick
                FileWriter writeFile = new FileWriter(publicfile);
                // Μετατροπη σε base64
                writeFile.write(encoder.encodeToString(publick.getEncoded()));
                writeFile.close();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        /**
         * Αυτο εδω θα χρησημοποιησετε κυριος
         * Επειδη υπαρχη μονο μια γραμμη στο αρχειο το καλουμε μια φορα με readLine και το κανουμε Decode
         * Το ιδιωτικο κλειδι απο μονο του ειναι δημιουργημενο σε PKCS#8 format kai to public se  X.509 format.
         * Οποτε αφου το κανουμε Decode απο το base64 πρεπει να τα κανουμε format to καθενα στο δικο του
         * Το KeyFactory ειναι η κλαση που κανει convert-generate τα κρυπτογραφημενα κλειδια σε key specifications
         */
        else {
            try {
                FileReader reader = new FileReader(publicfile);
                BufferedReader br = new BufferedReader(reader);
                String publick = br.readLine();
                System.out.println(publick);

                // metatropi tou dimosiou kleidiou apo to arxeio keimenou se kleidi
                Base64.Decoder decoder = Base64.getDecoder();
                // anaktisi twn bytes tou String kleidiou
                byte[] encodePublic = decoder.decode(publick.getBytes("utf-8"));
                // dimiourgia enos spec typou X509
                X509EncodedKeySpec spec = new X509EncodedKeySpec(encodePublic);
                // dimiourgia factory gia tin paragogi tou dimosiou kleidiou
                KeyFactory kf = KeyFactory.getInstance("RSA");
                publicKey = kf.generatePublic(spec);

                // metatropi tou idiwtikou kleidiou apo tin stathera se kleidi
                byte[] encodePrivate = decoder.decode(privateKeyFromRSAEncryption.getBytes("utf-8"));
                // dimiourgia sepc gia tin paragwgi tou idiwtikou kleidiou
                PKCS8EncodedKeySpec spec2 = new PKCS8EncodedKeySpec(encodePrivate);
                privateKey = kf.generatePrivate(spec2);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            } catch (InvalidKeySpecException ex) {
                ex.printStackTrace();
            }

        }
    }


}










package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFrameController implements Initializable {
    // ObservableList την χρησημοποιουμε στο javafx για να μπορουμε να εμφανισουμε και να αρχικοποιησουμε τα γραφικα με τα στοιχεια με απλη arraylist δεν γινεται
    private ObservableList<String> typesOfCardsList = FXCollections.observableArrayList("Master Card", "Visa", "American Express", "Maestro");
    private Key privateKey;
    private String username;
    private byte salt[];
    private ArrayList<byte[]> creditCardArrayList = new ArrayList<byte[]>();
    private Label tableLabelDisplay = new Label("Add Type Of Card To Display");
    private CreditCard saveCardForUpdate = null;

    /**
     * Φορτωνουμε τα ID απο το fxml αρχειο
     */
    @FXML
    private TableView<CreditCard> tableViewCards;

    @FXML
    private TextField cardNameField, cardNumberField, cvvField, cardNumberFindField, cardNameFieldUpdateOrDelete, cardNumberFieldUpdateOrDelete, cvvFieldUpdateOrDelete;

    @FXML
    private DatePicker cardExpirationDateField, cardExpirationDateFieldUpdateOrDelete;

    @FXML
    private Button addCardButton, searchCardTypeButton, searchCardTypeButtonForUpdate, menuButtonAddCard, menuButtonViewCard, menuButtonUpdateOrDeleteCard, UpdateBtn, DeleteBtn, CancelBtn, modifyBtn, logoutBtn;

    @FXML
    private ChoiceBox<String> choiceBoxField, choiceBoxFieldSearch, choiceBoxFieldUpdate, choiceBoxFieldUpdateOrDelete;

    @FXML
    private TableColumn<CreditCard, String> typeViewCol, nameViewCol, numberViewCol, dateViewCol, cvvViewCol;

    @FXML
    private Label errorMessageMainLabel, modifyNotFoundLabel, integrityErrorLabel;

    @FXML
    private BorderPane borderPaneMain;

    @FXML
    private Pane viewCardPane, AddCardPane, updateOrDeletePane;


    /**
     * Δεν μπορουμε να θεσουμε Constructor στους Controllers με αυτα που εχουμε κανει οποτε για να παρουμε τα στοιχεια που θελουμε απο το LoginController
     * Δημιουργουμε μια καπως παραξενη συναρτηση-Constructor η οποια δουλευει
     * Και Κανουμε load τις καρτες απο το αρχειο του user
     *
     * @param privateKey
     * @param username
     * @param salt
     */
    public void setMainFrameController(Key privateKey, String username, byte salt[]) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException {
        this.privateKey = privateKey;
        this.username = username;
        this.salt = salt;
        loadCreditCardsFromFile();
        verifyDigitalSign();
    }

    /**
     * Η συναρτηση initialize στο javafx απο οτι καταλαβαμε τρεχει πρωτα αυτη για να κανει μερικα πραγματα που θελοπουμε και μετα τα γραφικα στην ουσια αρχικοποιη η φορτωνη τα αρχεια και αλλα πραγματα που θελουμε για να δουλευουν σωστα τα γραφικα
     * Σε αυτην την περιπτωση αυτο που θελουμε ειναι να θεσουμε τα choiceBoxField με το typesOfCardsList για να μπορεσει ο χρηστης να επιλεξει τον τυπο καρτας στην συνεχεια.
     *
     * @param url
     * @param resourceBundle
     */
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choiceBoxField.setItems(typesOfCardsList);
        choiceBoxFieldSearch.setItems(typesOfCardsList);
        choiceBoxFieldUpdate.setItems(typesOfCardsList);
        choiceBoxFieldUpdateOrDelete.setItems(typesOfCardsList);
        choiceBoxField.setValue("Visa");
        choiceBoxFieldSearch.setValue("Visa");
        choiceBoxFieldUpdate.setValue("Visa");

        // Με αυτον τον τροπο μπορουμε να πουμε στον πινακα τη στοιχεια θα αποθυκευσεις σε καθε column
        typeViewCol.setCellValueFactory(new PropertyValueFactory("type"));
        nameViewCol.setCellValueFactory(new PropertyValueFactory("name"));
        numberViewCol.setCellValueFactory(new PropertyValueFactory("number"));
        dateViewCol.setCellValueFactory(new PropertyValueFactory("datePicker"));
        cvvViewCol.setCellValueFactory(new PropertyValueFactory("cvv"));

        tableViewCards.setPlaceholder(tableLabelDisplay);


    }

    /**
     * Η συναρτηση αυτη το μονο που κανει ειναι να αλλαζει τα Panel αναλογα με το κουμπι που παταει ο χρηστης
     * Για την αποσυνδεση Δημιουργει ενα OptionPane h Alert στο javafx και αναλογα με την απαντηση παει στο LoginController η οχι
     */

    public void changeMenuPaneAction(ActionEvent event) {
        if (event.getSource().equals(menuButtonAddCard)) {
            borderPaneMain.setCenter(AddCardPane);
        }
        if (event.getSource().equals(menuButtonViewCard)) {
            borderPaneMain.setCenter(viewCardPane);
        }
        if (event.getSource().equals(menuButtonUpdateOrDeleteCard)) {
            borderPaneMain.setCenter(updateOrDeletePane);
        }
        if (event.getSource().equals(logoutBtn)) {
            ButtonType yesBtn = new ButtonType("YES");
            ButtonType noBtn = new ButtonType("NO");
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Disconnect?", yesBtn, noBtn);
            a.setHeaderText("Disconnect");
            a.setTitle("CONFIRMATION");
            a.setResizable(true);
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == yesBtn) {
                try {
                    SaveToFileIntegrityMechanism();
                    username = null;
                    privateKey = null;
                    //Με αυτον τον τροπο μπορουμε να παμε πισω στην αρχη
                    AnchorPane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));
                    Scene mainScene = new Scene(pane);
                    Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    mainStage.setScene(mainScene);
                    mainStage.show();
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

            } else if (result.get() == noBtn) {
                System.out.println("Disconnect canceled");
            }

        }
    }

    /**
     * ------------------ Κρυπτογραφηση και Αποκρυπτογραφηση Καρτων Για Προσθηκη-Τροποποιηση Μαζι Με Γραφικα -------------------------------------------
     */

    /**
     * addUserCreditCardEncryptedToListAndFile Αρχικα ελενχει αν συμπληρωθηκαν ολα τα πεδια σωστα και στην συνεχεια
     * αποκρυπτογραφουμε καθε καρτα και ελενχουμε αν ο αριθμος της καρτας με ταδε πεδιο υπαρχει στην λιστα.
     * Αυτο το κανουμε γιατι θα εχουμε προβλημα στην Update αν εχουμε δυο ιδιες καρτες με ιδιο τυπο παντα θα παρουμε το πρωτο που θα συναντησει.
     * Τελος δημιουργουμε το αντικειμενο της καρτας και το κανουμε κρυπτογραγηση(Εχουμε βαλει στην encryptAndAddCreditCard να  αποθηκευει το Object στο αρχειο και την λιστα).
     * Και καθαριζουμε και με εναν αλλον τροπο εκτος την clear τα στοιχεια.
     */
    public void addUserCreditCardEncryptedToListAndFile(ActionEvent event) {
        try {
            SecretKey userSymmetricKey = decryptSymmetricKeyForUse();
            if (event.getSource().equals(addCardButton)) {
                if (cardNameField.getText().equals("") || cardNumberField.getText().equals("") || cardExpirationDateField.getValue() == null || cvvField.getText().equals("")) {
                    errorMessageMainLabel.setText("Please Fill All Fields");
                } else if (!validateCreditCardNumber(cardNumberField.getText())) {
                    errorMessageMainLabel.setText("Invalid Credit Card Number");
                } else if (!validateCVV(cvvField.getText())) {
                    errorMessageMainLabel.setText("Invalid CVV/CVC2");
                } else {
                    for (byte[] card : creditCardArrayList) {
                        CreditCard creditCard = decryptCreditCard(card, userSymmetricKey);
                        if (creditCard.getType().equals(choiceBoxField.getValue()) && (creditCard.getNumber().equals(cardNumberField.getText()))) {
                            errorMessageMainLabel.setText("This type and number already exists!");
                            return;
                        }
                    }
                    CreditCard creditCard = new CreditCard(choiceBoxField.getValue(), cardNameField.getText(), cardNumberField.getText(), cardExpirationDateField.getValue().toString(), cvvField.getText());
                    encryptAndAddCreditCard(creditCard);
                    errorMessageMainLabel.setText("Card Added Successfully!");
                    cardNameField.setText("");
                    cardNumberField.setText("");
                    cardExpirationDateField.setValue(null);
                    cvvField.setText("");

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
     * Εδω μπορουμε να δουμε τις ολες τις καρτες απο καθε ειδος.
     * Αναλογα με το searchType κανουμε κρυπτογραφηση τις καρτες για να μπορεσει να τις δει ο χρηστης
     * ObservableList ειναι η λιστα που θα αποθυκευσει τις καρτες και με το tableViewCards μπορουμε να θεσουμε τα αντικειμενα στα γραφικα
     */
    public void searchCreditCardTypeAction(ActionEvent event) {
        try {
            String searchType = choiceBoxFieldSearch.getValue();
            SecretKey userSymmetricKey = decryptSymmetricKeyForUse();
            ObservableList<CreditCard> observableListCard = FXCollections.observableArrayList();

            if (event.getSource().equals(searchCardTypeButton)) {
                for (byte[] card : creditCardArrayList) {
                    CreditCard creditCard = decryptCreditCard(card, userSymmetricKey);
                    if (creditCard.getType().equals(searchType)) {
                        observableListCard.add(creditCard);
                    }
                }
                tableViewCards.setItems(observableListCard);
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
     * UpdateUserCreditCardEncryptedToListAndFile ειναι λιγο-πολυ μεγαλη αλλα δεν εχει κατι δυσκολο τα πιο πολλα ειναι ενεργοποιηση και απενεργοποιηση Buttons/TextField
     */
    public void UpdateUserCreditCardEncryptedToListAndFile(ActionEvent event) {
        try {
            // Αρχικα αποθηκευουμε σε συμβολοσειρες τα πεδια που συμπληρωσε ο χρηστης
            String searchType = choiceBoxFieldUpdate.getValue();
            String searchNumber = cardNumberFindField.getText();
            SecretKey userSymmetricKey = decryptSymmetricKeyForUse(); // Το χρειαζομαστε για decrypt καρτων


            // Μετα ελαγχουμε αδου κανουμε decrypt τις καρτες και βλεπουμε αν υπαρχει η σιγκεκριμενη αν ναι τοτε θα ενεργοποιηθει το Modify Button
            // Αλλιως απλα εμφανιζει μυνημα δεν βρεθηκε
            if (event.getSource().equals(searchCardTypeButtonForUpdate)) {
                boolean check = false;
                for (byte[] card : creditCardArrayList) {
                    CreditCard creditCard = decryptCreditCard(card, userSymmetricKey);
                    if (creditCard.getType().equals(searchType) && creditCard.getNumber().equals(searchNumber)) {
                        choiceBoxFieldUpdateOrDelete.setValue(creditCard.getType());
                        cardNameFieldUpdateOrDelete.setText(creditCard.getName());
                        cardNumberFieldUpdateOrDelete.setText(creditCard.getNumber());
                        cvvFieldUpdateOrDelete.setText(creditCard.getCvv());
                        System.out.println(creditCard.getDatePicker());
                        cardExpirationDateFieldUpdateOrDelete.setValue(LOCAL_DATE(creditCard.getDatePicker()));
                        modifyBtn.setDisable(false);
                        check = true;
                    }
                }
                if (check == false)
                    modifyNotFoundLabel.setText("Card Not Found");
            }
            // Στην συνεχεια κανουμε editable τα πεδια της καρτας που θελουμε να κανουμε Update
            if (event.getSource().equals(modifyBtn)) {
                choiceBoxFieldUpdateOrDelete.setDisable(false);
                cardNameFieldUpdateOrDelete.setDisable(false);
                cardNumberFieldUpdateOrDelete.setDisable(false);
                cvvFieldUpdateOrDelete.setDisable(false);
                cardExpirationDateFieldUpdateOrDelete.setDisable(false);
                DeleteBtn.setDisable(false);
                UpdateBtn.setDisable(false);
                CancelBtn.setDisable(false);
                searchCardTypeButtonForUpdate.setDisable(true);
                modifyBtn.setDisable(true);
                choiceBoxFieldUpdate.setDisable(true);
                cardNumberFindField.setDisable(true);

            }
            // Αφου και αν κανουμε αλλαγη στα στοιχεια της καρτας ελεγχουμε οπως και στην προσθηκη νεας καρτας τα πεδια αν ειναι ορθα αν ειναι τοτε ακολουθει η else
            if (event.getSource().equals(UpdateBtn)) {
                if (cardNameFieldUpdateOrDelete.getText().equals("") || cardNumberFieldUpdateOrDelete.getText().equals("") || cvvFieldUpdateOrDelete.getText().equals("")) {
                    modifyNotFoundLabel.setText("Please Fill All Fields");
                } else if (!validateCreditCardNumber(cardNumberFieldUpdateOrDelete.getText())) {
                    modifyNotFoundLabel.setText("Invalid Credit Card Number");
                } else if (!validateCVV(cvvFieldUpdateOrDelete.getText())) {
                    modifyNotFoundLabel.setText("Invalid CVV/CVC2");
                } else {

                    // Δημιουργει ενα Alert Box για να πουμε στον χρηστη αν οντως θελει να τα αλλαξει
                    ButtonType yesBtn = new ButtonType("YES");
                    ButtonType noBtn = new ButtonType("NO");
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Update This Card?", yesBtn, noBtn);
                    a.setHeaderText("Update CreditCard");
                    a.setTitle("CONFIRMATION");
                    a.setResizable(true);
                    Optional<ButtonType> result = a.showAndWait();

                    // Αν ναι τοτε βρησκουμε την καρτα ξανα απο το αρχειο και την κανουμε remove(Μπορουσαμε να κανουμε την αλλαγη μεσω setters αλλα το σκεφτηκα πολυ μετα και το αφησα οπως ειναι.)
                    if (result.get() == yesBtn) {
                        for (byte[] card : creditCardArrayList) {
                            CreditCard creditCard = decryptCreditCard(card, userSymmetricKey);
                            if (creditCard.getType().equals(searchType) && (creditCard.getNumber().equals(searchNumber))) {
                                saveCardForUpdate = creditCard; // Σε περιπτωση που η αλλαγμενη καρτα υπαρχει στην λιστα τοτε να εχουμε την δυνατοτητα να την βαλουμε πισω
                                creditCardArrayList.remove(card);
                                break;
                            }
                        }
                        // Ελεγχουμε αν η τροποποιημενη καρτα υπαρχει ειδη με τον ιδιο αριθμο και τυπο αν ναι τοτε βαζουμε πισω την καρτα και αναφερουμε το αποτελεσμα στο Label
                        for (byte[] card : creditCardArrayList) {
                            CreditCard creditCard = decryptCreditCard(card, userSymmetricKey);
                            if (creditCard.getType().equals(choiceBoxFieldUpdateOrDelete.getValue()) && (creditCard.getNumber().equals(cardNumberFieldUpdateOrDelete.getText()))) {
                                modifyNotFoundLabel.setText("There is already credit card with the same type and number");
                                encryptAndAddCreditCard(saveCardForUpdate);
                                return;
                            }
                        }
                        // Αλλιως δημιουργουμε την Updated καρτα και κανουμε clear kai disable ta fields
                        CreditCard updatedCard = new CreditCard(choiceBoxFieldUpdateOrDelete.getValue(), cardNameFieldUpdateOrDelete.getText(), cardNumberFieldUpdateOrDelete.getText(), cardExpirationDateFieldUpdateOrDelete.getValue().toString(), cvvFieldUpdateOrDelete.getText());
                        encryptAndAddCreditCard(updatedCard);
                        saveCardForUpdate = null;
                        choiceBoxFieldUpdateOrDelete.setDisable(true);
                        cardNameFieldUpdateOrDelete.setDisable(true);
                        cardNumberFieldUpdateOrDelete.setDisable(true);
                        cvvFieldUpdateOrDelete.setDisable(true);
                        cardExpirationDateFieldUpdateOrDelete.setDisable(true);
                        choiceBoxFieldUpdateOrDelete.setValue(null);
                        cardNameFieldUpdateOrDelete.setText("None");
                        cardNumberFieldUpdateOrDelete.setText("None");
                        cvvFieldUpdateOrDelete.setText("None");
                        cardExpirationDateFieldUpdateOrDelete.setValue(null);
                        UpdateBtn.setDisable(true);
                        DeleteBtn.setDisable(true);
                        CancelBtn.setDisable(true);
                        searchCardTypeButtonForUpdate.setDisable(false);
                        choiceBoxFieldUpdate.setDisable(false);
                        cardNumberFindField.setDisable(false);
                        modifyNotFoundLabel.setText("Card Updated Successfully!");

                    } else if (result.get() == noBtn) {
                        modifyNotFoundLabel.setText("Update Stopped");
                        modifyNotFoundLabel.setText("");

                    }

                }
            }
            // To ιδιο και με την delete σε αυτην την περιπτωση ειναι πιο ευκολο γιατι ενα remove θα κανουμε μονο
            if (event.getSource().equals(DeleteBtn)) {

                ButtonType yesBtn = new ButtonType("YES");
                ButtonType noBtn = new ButtonType("NO");
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete This Card?", yesBtn, noBtn);
                a.setHeaderText("Delete CreditCard");
                a.setTitle("CONFIRMATION");
                a.setResizable(true);
                Optional<ButtonType> result = a.showAndWait();
                if (result.get() == yesBtn) {
                    for (byte[] card : creditCardArrayList) {
                        CreditCard creditCard = decryptCreditCard(card, userSymmetricKey);
                        if (creditCard.getType().equals(searchType) && (creditCard.getNumber().equals(searchNumber))) {
                            creditCardArrayList.remove(card);
                            saveToFileCreditCards();
                            break;
                        }
                    }
                    choiceBoxFieldUpdateOrDelete.setDisable(true);
                    cardNameFieldUpdateOrDelete.setDisable(true);
                    cardNumberFieldUpdateOrDelete.setDisable(true);
                    cvvFieldUpdateOrDelete.setDisable(true);
                    cardExpirationDateFieldUpdateOrDelete.setDisable(true);
                    choiceBoxFieldUpdateOrDelete.setValue(null);
                    cardNameFieldUpdateOrDelete.setText("None");
                    cardNumberFieldUpdateOrDelete.setText("None");
                    cvvFieldUpdateOrDelete.setText("None");
                    UpdateBtn.setDisable(true);
                    DeleteBtn.setDisable(true);
                    CancelBtn.setDisable(true);
                    searchCardTypeButtonForUpdate.setDisable(false);
                    choiceBoxFieldUpdate.setDisable(false);
                    cardNumberFindField.setDisable(false);
                    modifyNotFoundLabel.setText("Card Deleted Successfully!");

                } else if (result.get() == noBtn) {
                    modifyNotFoundLabel.setText("Update Stopped");
                    modifyNotFoundLabel.setText("");

                }

            }
            if (event.getSource().equals(CancelBtn)) {
                choiceBoxFieldUpdateOrDelete.setDisable(true);
                cardNameFieldUpdateOrDelete.setDisable(true);
                cardNumberFieldUpdateOrDelete.setDisable(true);
                cvvFieldUpdateOrDelete.setDisable(true);
                cardExpirationDateFieldUpdateOrDelete.setDisable(true);
                choiceBoxFieldUpdateOrDelete.setValue(null);
                cardNameFieldUpdateOrDelete.setText("None");
                cardNumberFieldUpdateOrDelete.setText("None");
                cvvFieldUpdateOrDelete.setText("None");
                UpdateBtn.setDisable(true);
                DeleteBtn.setDisable(true);
                CancelBtn.setDisable(true);
                searchCardTypeButtonForUpdate.setDisable(false);
                choiceBoxFieldUpdate.setDisable(false);
                cardNumberFindField.setDisable(false);
                modifyNotFoundLabel.setText("Card Modification Canceled!");
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


    /** ------------------ Τελος Κρυπτογραφηση και Αποκρυπτογραφηση Καρτων Για Προσθηκη-Τροποποιηση Μαζι Με Γραφικα ------------------------------------------- */

    /*===============================================================================================================================================*/


    /**
     * --------------------------------------------- Το Σημαντικο **Αλγοριθμοι Κρυπτογραφιας** --------------------------------------------------------------------
     **/

    /**
     * Απλα καλουμε μεσο Cipher τον RSA καλουμε το DECRYPT_MODE που γινεται με το privateKey, διαβαζουμε το αρχειο με το συμμετρικο κλειδι και με το doFinal
     * το αποκρυπτογραφουμε.
     * Στην συνεχεια Δημιουργουμε το SecretKeySpec που αυτο που κανει ειναι να δημιουργει εναν πινακα byte χωρις να χρειαζεται να περασει απο το SecretKeyFactory
     */
    public SecretKey decryptSymmetricKeyForUse() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException {
        ObjectInputStream symmetricKeyFile = new ObjectInputStream(new FileInputStream(username + "//Key.txt"));

        /**--------RSA---------*/
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte encryptedSymmetricKey[] = (byte[]) symmetricKeyFile.readObject();
        byte decodeSymmetricKey[] = rsa.doFinal(encryptedSymmetricKey);

        SecretKey userSymmetricKey = new SecretKeySpec(decodeSymmetricKey, 0, decodeSymmetricKey.length, "AES");

        symmetricKeyFile.close();

        return userSymmetricKey;
    }


    /**
     * Η συναρτηση αυτη αρχικα καλει την decryptSymmetricKeyForUse και δημιουργει εναν πινακα με 16 bytes που ειναι ο buffer με το IV
     * Μετα δημιουργουμε το IvParameterSpec που δεν καταλαβαινω τι κανει αλλα το θελει ο AES
     * Και στην συνεχεια μεσο του Cipher καλουμε τον AES σε ENCRYPT_MODE για να κρυπτογραφησουμε την καρτα αφου πρωτα την μετατρεψουμε στο byte[] και την προσθετουμε
     * αντιστοιχα στο αρχειο και λιστα.
     */
    public void encryptAndAddCreditCard(CreditCard creditCard) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException, InvalidAlgorithmParameterException {
        SecretKey userSymmetricKey = decryptSymmetricKeyForUse();

        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, userSymmetricKey, ivspec);
        byte[] convertedToByteCreditCard = convertCreditCardToBytes(creditCard);
        byte[] creditCardToByteEncrypted = cipher.doFinal(convertedToByteCreditCard);
        creditCardArrayList.add(creditCardToByteEncrypted);

        ObjectOutputStream writeToFileCards = new ObjectOutputStream(new FileOutputStream(username + "//CreditCards.txt"));
        writeToFileCards.writeObject(creditCardArrayList);
        writeToFileCards.close();

    }

    /**
     * Παρομοια με την απο πανω συναρτηση encryptAndAddCreditCard αντι για ENCRYPT_MODE καλουμε DECRYPT_MODE το userSymmetricKey το περανουμε απο την μεθοδο που την υλοποιει
     * Και επιστρεφουμε την καρτα.
     */
    public CreditCard decryptCreditCard(byte[] card, SecretKey userSymmetricKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        CreditCard creditCard = null;

        //dimiourgia tou initialization vector gia to prwto block
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, userSymmetricKey, ivspec);
        byte[] creditCardToByteDecrypted = cipher.doFinal(card);
        creditCard = convertBytesToCreditCard(creditCardToByteDecrypted);

        return creditCard;

    }


    /** ------------------------- Τελος **Αλγοριθμοι Κρυπτογραφιας** --------------------------------------------------------------------*/


    /*===============================================================================================================================================*/


    /**
     * ---------------------------------------------DigitalSign--------------------------------------------------------------------
     **/
    /**
     * Η παρακατω συναρτησεις ειναι αντιγραφη επικολληση τον προηγουμενων συναρτησεων δεν αλλαζει σχεδον τιποτα
     * Ακολουθει την διαδικασια του σχηματος που μας δωσατε για να κρυπτογραφησουμε και αποκρυπτογραφησουμε το Digital Signature
     * Και για να την δημιουργισουμε μεσω hash function
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws ClassNotFoundException
     * @throws InvalidAlgorithmParameterException
     */

    public void verifyDigitalSign() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException, InvalidAlgorithmParameterException, IOException {
        File file = new File(username + "//digitalSignWithAES.txt");
        byte[] digitalSignAES = null;
        if (file.exists()) {
            try {
                ObjectInputStream load = new ObjectInputStream(new FileInputStream(username + "//digitalSignWithAES.txt"));
                digitalSignAES = (byte[]) load.readObject();
                load.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (digitalSignAES != null) {
                SecretKey userSymmetricKey = decryptSymmetricKeyForUse();

                //dimiourgia tou initialization vector gia to prwto block
                byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, userSymmetricKey, ivspec);
                String digitalSignDecryptedAES = new String(cipher.doFinal(digitalSignAES));
                String digitalSignCurrent = new String(digitalSignFunction());
                System.out.println(digitalSignDecryptedAES);
                System.out.println(digitalSignCurrent);
                if (digitalSignCurrent.equals(digitalSignDecryptedAES)) {
                    integrityErrorLabel.setTextFill(Color.GREEN);
                    integrityErrorLabel.setText("Status: Integrity OK");
                } else
                    integrityErrorLabel.setText("Status: Your Credit Cards Are Lost Something Modified Card File");
            }


        }

    }

    public byte[] digitalSignFunction() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //hasharisma kwdikou
        MessageDigest dig = MessageDigest.getInstance("SHA3-256");
        //perasma tou salt sto antikeimeno messageDigest
        dig.update(salt);

        byte[] byteCards = new byte[creditCardArrayList.size()];
        creditCardArrayList.toArray(new byte[][]{byteCards});


        byte hashedCards[] = dig.digest(byteCards);

        dig.update(username.getBytes());
        byte usernameDigestCards[] = dig.digest(hashedCards);

        /** --------------RSA---------------- */
        Cipher userRSADigitalSign;
        userRSADigitalSign = Cipher.getInstance("RSA");
        userRSADigitalSign.init(Cipher.ENCRYPT_MODE, privateKey);
        byte digitalSign[] = userRSADigitalSign.doFinal(usernameDigestCards);

        return digitalSign;

    }


    public void SaveToFileIntegrityMechanism() throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException, ClassNotFoundException, InvalidAlgorithmParameterException {


        byte digitalSign[] = digitalSignFunction();
        SecretKey userSymmetricKey = decryptSymmetricKeyForUse();

        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, userSymmetricKey, ivspec);
        byte digitalSignWithAES[] = cipher.doFinal(digitalSign);

        ObjectOutputStream writeToFileCards = new ObjectOutputStream(new FileOutputStream(username + "//digitalSignWithAES.txt"));
        writeToFileCards.writeObject(digitalSignWithAES);
        writeToFileCards.close();

    }

    /**
     * ---------------------------------------------End DigitalSign--------------------------------------------------------------------
     **/


    /*===============================================================================================================================================*/


    /**
     * H function convertCreditCardToBytes χρειαζεται γιατι στην κρυπτογραφηση μονο byte μπορει να δεχτει to doFinal το βρηκαμε ετοιμο σχεδον στο stackOverflow
     */
    //https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
    public byte[] convertCreditCardToBytes(CreditCard creditCard) {
        byte[] data = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(creditCard);
            oos.flush();
            data = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Και το αναποδο για να μπορεσουμε να διαβασουμε τα στοιχεια του Object μετα
     */
    public CreditCard convertBytesToCreditCard(byte[] card) {
        CreditCard creditCard = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(card);
            ObjectInputStream ois = new ObjectInputStream(bis);
            creditCard = (CreditCard) ois.readObject();
            bis.close();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return creditCard;
    }


    /**
     * Η συναρτηση απλα βλεπει αν αυτα που εδωσε ο χρηστης στο πεδιο του αριθμου της καρτας ειναι 16 χαρακτηρες και συκγεκριμενα Integers χαρακτηρες
     */
    public boolean validateCreditCardNumber(String input) {
        if (input.length() == 16) {
            for (int i = 0; i < input.length(); i++) {
                if (!isInteger(input.substring(i, i + 1))) {
                    return false;
                }
            }
            return true;
        } else
            return false;
    }


    /**
     * Η συναρτηση απλα βλεπει αν αυτα που εδωσε ο χρηστης στο πεδιο ειναι 3 εως 4 χαρακτηρες και συκγεκριμενα Integers χαρακτηρες
     */
    public boolean validateCVV(String input) {
        if (input.length() <= 4 && input.length() >= 3) {
            for (int i = 0; i < input.length(); i++) {
                if (!isInteger(input.substring(i, i + 1))) {
                    return false;
                }
            }
            return true;
        } else
            return false;
    }

    /**
     * Η συναρτηση που ελεγχει αν ειναι Integer Η κατι αλλο οι χαρακτηρες
     */
    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Η συναρτηση αυτη ειναι για το DatePicker οταν θελουμε να παρουμε την ημερομηνια απο την καρατα για να την κανουμε Update
     */
    public LocalDate LOCAL_DATE(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }


    /**
     * H Δυο αυτες συναρτησης απλως αποθυκευουν τις καρτες σε αρχειο και το αναποδο φορτωνουν τις καρτες στην λιστα
     */

    public void loadCreditCardsFromFile() {
        File file = new File(username + "//CreditCards.txt");
        if (file.exists()) {
            try {
                ObjectInputStream load = new ObjectInputStream(new FileInputStream(username + "//CreditCards.txt"));
                creditCardArrayList = (ArrayList<byte[]>) load.readObject();
                load.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToFileCreditCards() {
        ObjectOutputStream writeToFileCards = null;
        try {
            writeToFileCards = new ObjectOutputStream(new FileOutputStream(username + "//CreditCards.txt"));
            writeToFileCards.writeObject(creditCardArrayList);
            writeToFileCards.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}



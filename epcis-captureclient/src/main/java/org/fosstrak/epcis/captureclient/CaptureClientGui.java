package org.accada.epcis.captureclient;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.accada.epcis.captureclient.CaptureEvent.BizTransaction;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * GUI class for the EPCIS Capture Interface Client. Implements the GUI and the
 * creation of XML from the GUI data.
 * 
 * @author David Gubler
 */
public class CaptureClientGui extends WindowAdapter implements ActionListener {

    /**
     * The default URL for the service.
     */
    // private final String defaultUrl =
    // "http://demo.accada.org/EPCIS-Capture-v0.2.0";
    private String defaultUrl = "http://localhost:8080/epcis-repository/capture";

    /**
     * The various tooltips.
     */
    private final String toolTipDate = "Format is ISO 8601, i.e. YYYY-MM-DDThh:mm:ss.SSS";
    private final String toolTipUri = "URI";
    private final String toolTipUris = "One or multiple URIs, separated by spaces";
    private final String toolTipInteger = "Integer number";
    private final String toolTipOptional = ". This field is optional";
    private final String toolTipBizTransType = "Business Transaction Type";
    private final String toolTipBizTransID = "Business Transactio ID";

    /**
     * The possible values for the "actions" parameter.
     */
    private final String[] actions = { "ADD", "OBSERVE", "DELETE" };

    /**
     * The four possible event types, in human readable form.
     */
    private final String[] eventTypes = { "Object event", "Aggregation event",
                                         "Quantity event", "Transaction event" };

    /**
     * The four possible event types, used for XML creation.
     */
    private final String[] xmlEventNode = { "ObjectEvent", "AggregationEvent",
                                           "QuantityEvent", "TransactionEvent" };

    /**
     * Holds all the examples.
     */
    private CaptureClientGuiExampleEvents exampleEvents = new CaptureClientGuiExampleEvents();

    /*
     * These lists hold the input fields for the BizTransactions. The lists are
     * modified by the user to allow for as many arguments as the user wants.
     * Objects may be deleted from these lists by pressing the "-" Button.
     */
    private ArrayList<JTextField> mwBizTransTypeFields;
    private ArrayList<JTextField> mwBizTransIDFields;
    private ArrayList<JButton> mwBizTransButtons;

    /* main window */
    private JFrame mainWindow;
    private JPanel mwMainPanel;
    private JPanel mwConfigPanel;
    private JPanel mwEventTypePanel;
    private JPanel mwEventDataPanel;
    private JPanel mwEventDataInputPanel;
    private JPanel mwEventDataExamplesPanel;
    private JPanel mwButtonPanel;
    private JLabel mwServiceUrlLabel;

    private JTextField mwServiceUrlTextField;
    private JComboBox mwEventTypeChooserComboBox;
    private JCheckBox mwShowDebugWindowCheckBox;

    /* the BizTransaction field */
    private JPanel mwBizTransactionPanel;
    private JButton mwBizTransactionPlus;

    /* the event time field */
    private JLabel mwEventTimeLabel;
    private JTextField mwEventTimeTextField;

    /* the action type drop-down box */
    private JLabel mwActionLabel;
    private JComboBox mwActionComboBox;

    /* fields for the various URIs */
    private JLabel mwBizStepLabel;
    private JTextField mwBizStepTextField;
    private JLabel mwDispositionLabel;
    private JTextField mwDispositionTextField;
    private JLabel mwReadPointLabel;
    private JTextField mwReadPointTextField;
    private JLabel mwBizLocationLabel;
    private JTextField mwBizLocationTextField;
    private JLabel mwBizTransactionLabel;
    private JTextField mwBizTransactionTextField;

    /* associated EPCs for object events */
    private JLabel mwEpcListLabel;
    private JTextField mwEpcListTextField;

    /* parent EPC field for aggregation events */
    private JLabel mwParentIDLabel;
    private JTextField mwParentIDTextField;

    /* associated EPCs for aggregation events */
    private JLabel mwChildEPCsLabel;
    private JTextField mwChildEPCsTextField;

    /* EPC class for quantity events */
    private JLabel mwEpcClassLabel;
    private JTextField mwEpcClassTextField;

    /* quantity for quantity events */
    private JLabel mwQuantityLabel;
    private JTextField mwQuantityTextField;

    /* buttons */
    private JButton mwFillInExampleButton;
    private JButton mwGenerateEventButton;

    /** example selection window. */
    private JFrame exampleWindow;

    private JPanel ewMainPanel;
    private JPanel ewListPanel;
    private JPanel ewButtonPanel;
    private JList ewExampleList;
    private JScrollPane ewExampleScrollPane;
    private JButton ewOkButton;

    /* debug window */
    private JFrame debugWindow;

    private JTextArea dwOutputTextArea;
    private JScrollPane dwOutputScrollPane;
    private JPanel dwButtonPanel;
    private JButton dwClearButton;

    public CaptureClientGui() {
        initWindow();
    }

    public CaptureClientGui(String address) {
        this.defaultUrl = address;
        initWindow();
    }

    private void initWindow() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        mainWindow = new JFrame("EPCIS capture interface client");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);

        mwMainPanel = new JPanel();
        mwMainPanel.setLayout(new BoxLayout(mwMainPanel, BoxLayout.PAGE_AXIS));
        mwMainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mwConfigPanel = new JPanel(new GridBagLayout());
        mwMainPanel.add(mwConfigPanel);
        mwEventTypePanel = new JPanel();
        mwMainPanel.add(mwEventTypePanel);
        mwEventDataPanel = new JPanel();
        mwEventDataPanel.setLayout(new BoxLayout(mwEventDataPanel,
                BoxLayout.PAGE_AXIS));
        mwMainPanel.add(mwEventDataPanel);
        mwButtonPanel = new JPanel();
        mwMainPanel.add(mwButtonPanel);

        mwConfigPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Configuration"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mwEventTypePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Event type"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mwEventDataPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Event data"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mwServiceUrlLabel = new JLabel("Capture interface URL: ");
        mwServiceUrlTextField = new JTextField(defaultUrl, 40);
        mwShowDebugWindowCheckBox = new JCheckBox("Show debug window", false);
        mwShowDebugWindowCheckBox.addActionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 0);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        mwConfigPanel.add(mwServiceUrlLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        mwConfigPanel.add(mwServiceUrlTextField, c);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        mwConfigPanel.add(mwShowDebugWindowCheckBox, c);

        mwEventTypeChooserComboBox = new JComboBox(eventTypes);
        mwEventTypeChooserComboBox.addActionListener(this);
        mwEventTypePanel.add(mwEventTypeChooserComboBox);

        mwGenerateEventButton = new JButton("Generate event");
        mwGenerateEventButton.addActionListener(this);
        mwButtonPanel.add(mwGenerateEventButton);

        /* instanciate all event data input fields and descriptions */
        mwEventTimeLabel = new JLabel("occured");

        /* set default to something simple and ISO 8601 compatible */
        Date now = new Date();
        SimpleDateFormat dateTime = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS");
        mwEventTimeTextField = new JTextField(dateTime.format(now));

        mwEventTimeTextField.setToolTipText(toolTipDate);

        mwActionLabel = new JLabel("action");
        mwActionComboBox = new JComboBox(actions);

        mwBizStepLabel = new JLabel("business step");
        mwBizStepTextField = new JTextField();
        mwBizStepTextField.setToolTipText(toolTipUri + toolTipOptional);

        mwDispositionLabel = new JLabel("disposition");
        mwDispositionTextField = new JTextField();
        mwDispositionTextField.setToolTipText(toolTipUri + toolTipOptional);

        mwReadPointLabel = new JLabel("read point");
        mwReadPointTextField = new JTextField();
        mwReadPointTextField.setToolTipText(toolTipUri + toolTipOptional);

        mwBizLocationLabel = new JLabel("business location");
        mwBizLocationTextField = new JTextField();
        mwBizLocationTextField.setToolTipText(toolTipUri + toolTipOptional);

        mwBizTransactionLabel = new JLabel("business transaction");
        mwBizTransactionTextField = new JTextField();

        mwEpcListLabel = new JLabel("EPCs");
        mwEpcListTextField = new JTextField();
        mwEpcListTextField.setToolTipText(toolTipUris);

        mwParentIDLabel = new JLabel("parent object");
        mwParentIDTextField = new JTextField();
        mwParentIDTextField.setToolTipText(toolTipUri);

        mwChildEPCsLabel = new JLabel("child EPCs");
        mwChildEPCsTextField = new JTextField();
        mwChildEPCsTextField.setToolTipText(toolTipUris + toolTipOptional);

        mwEpcClassLabel = new JLabel("EPC class");
        mwEpcClassTextField = new JTextField();
        mwChildEPCsTextField.setToolTipText(toolTipUri);

        mwQuantityLabel = new JLabel("quantity");
        mwQuantityTextField = new JTextField();
        mwChildEPCsTextField.setToolTipText(toolTipInteger);

        mwBizTransTypeFields = new ArrayList<JTextField>();
        mwBizTransIDFields = new ArrayList<JTextField>();
        mwBizTransButtons = new ArrayList<JButton>();

        mwBizTransactionPanel = new JPanel(new GridBagLayout());
        ImageIcon tempImageIcon = getImageIcon("new10.gif");
        mwBizTransactionPlus = new JButton(tempImageIcon);
        mwBizTransactionPlus.setMargin(new Insets(0, 0, 0, 0));
        mwBizTransactionPlus.addActionListener(this);

        addBizTransactionRow();

        mwEventDataInputPanel = new JPanel(new GridBagLayout());
        mwEventDataExamplesPanel = new JPanel(new BorderLayout());
        mwEventDataPanel.add(mwEventDataInputPanel);
        mwEventDataPanel.add(mwEventDataExamplesPanel);

        eventDataPanelObjectEvent();
        mwFillInExampleButton = new JButton("Fill in example");
        mwFillInExampleButton.addActionListener(this);
        mwEventDataExamplesPanel.add(mwFillInExampleButton, BorderLayout.EAST);

        /* draw window */
        mainWindow.getContentPane().add(mwMainPanel);
        mainWindow.pack();
        mainWindow.setVisible(true);

        createDebugWindow();
    }

    /**
     * Show user input elements for object event.
     */
    private void eventDataPanelObjectEvent() {
        mwEventDataInputPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 5, 0);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        mwEventDataInputPanel.add(mwEventTimeLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEventTimeTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        mwEventDataInputPanel.add(mwEpcListLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEpcListTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        mwEventDataInputPanel.add(mwActionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwActionComboBox, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 3;
        mwEventDataInputPanel.add(mwBizStepLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizStepTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 4;
        mwEventDataInputPanel.add(mwDispositionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwDispositionTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 5;
        mwEventDataInputPanel.add(mwReadPointLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwReadPointTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 6;
        mwEventDataInputPanel.add(mwBizLocationLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizLocationTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 7;
        mwEventDataInputPanel.add(mwBizTransactionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizTransactionPanel, c);
    }

    /**
     * Show user input elements for aggregation event.
     */
    private void eventDataPanelAggregationEvent() {
        mwEventDataInputPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 5, 0);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        mwEventDataInputPanel.add(mwEventTimeLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEventTimeTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        mwEventDataInputPanel.add(mwParentIDLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwParentIDTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        mwEventDataInputPanel.add(mwChildEPCsLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwChildEPCsTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 3;
        mwEventDataInputPanel.add(mwActionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwActionComboBox, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 4;
        mwEventDataInputPanel.add(mwBizStepLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizStepTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 5;
        mwEventDataInputPanel.add(mwDispositionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwDispositionTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 6;
        mwEventDataInputPanel.add(mwReadPointLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwReadPointTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 7;
        mwEventDataInputPanel.add(mwBizLocationLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizLocationTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 8;
        mwEventDataInputPanel.add(mwBizTransactionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizTransactionPanel, c);
    }

    /**
     * Show user input elements for quantity event.
     */
    private void eventDataPanelQuantityEvent() {
        mwEventDataInputPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 5, 0);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        mwEventDataInputPanel.add(mwEventTimeLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEventTimeTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        mwEventDataInputPanel.add(mwEpcClassLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEpcClassTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        mwEventDataInputPanel.add(mwQuantityLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwQuantityTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 3;
        mwEventDataInputPanel.add(mwBizStepLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizStepTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 4;
        mwEventDataInputPanel.add(mwDispositionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwDispositionTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 5;
        mwEventDataInputPanel.add(mwReadPointLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwReadPointTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 6;
        mwEventDataInputPanel.add(mwBizLocationLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizLocationTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 7;
        mwEventDataInputPanel.add(mwBizTransactionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizTransactionPanel, c);
    }

    /**
     * Show user input elements for transaction events.
     */
    private void eventDataPanelTransactionEvent() {
        mwEventDataInputPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 5, 0);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        mwEventDataInputPanel.add(mwEventTimeLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEventTimeTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        mwEventDataInputPanel.add(mwParentIDLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwParentIDTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        mwEventDataInputPanel.add(mwEpcListLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwEpcListTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 3;
        mwEventDataInputPanel.add(mwActionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwActionComboBox, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 4;
        mwEventDataInputPanel.add(mwBizStepLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizStepTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 5;
        mwEventDataInputPanel.add(mwDispositionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwDispositionTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 6;
        mwEventDataInputPanel.add(mwReadPointLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwReadPointTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 7;
        mwEventDataInputPanel.add(mwBizLocationLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizLocationTextField, c);

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 8;
        mwEventDataInputPanel.add(mwBizTransactionLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        mwEventDataInputPanel.add(mwBizTransactionPanel, c);
    }

    /**
     * Sets up the window used to show the debug output.
     */
    private void createDebugWindow() {
        debugWindow = new JFrame("Debug output");
        debugWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        debugWindow.addWindowListener(this);
        debugWindow.setLocation(500, 100);
        debugWindow.setSize(500, 300);

        dwOutputTextArea = new JTextArea();
        dwOutputScrollPane = new JScrollPane(dwOutputTextArea);
        debugWindow.add(dwOutputScrollPane);

        dwButtonPanel = new JPanel();
        debugWindow.add(dwButtonPanel, BorderLayout.AFTER_LAST_LINE);

        dwClearButton = new JButton("Clear");
        dwClearButton.addActionListener(this);
        dwButtonPanel.add(dwClearButton);
    }

    /**
     * Sets up the window used to show the list of examples. Can only be open
     * once.
     */
    private void createExampleWindow() {
        if (exampleWindow != null) {
            exampleWindow.setVisible(true);
            return;
        }
        exampleWindow = new JFrame("Choose example");
        exampleWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        ewMainPanel = new JPanel();
        ewMainPanel.setLayout(new BoxLayout(ewMainPanel, BoxLayout.PAGE_AXIS));
        exampleWindow.add(ewMainPanel);

        ewListPanel = new JPanel();
        ewListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        ewListPanel.setLayout(new BoxLayout(ewListPanel, BoxLayout.PAGE_AXIS));

        ewButtonPanel = new JPanel();
        ewButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        ewButtonPanel.setLayout(new BoxLayout(ewButtonPanel,
                BoxLayout.LINE_AXIS));

        ewMainPanel.add(ewListPanel);
        ewMainPanel.add(ewButtonPanel);

        ewExampleList = new JList();
        ewExampleScrollPane = new JScrollPane(ewExampleList);
        ewListPanel.add(ewExampleScrollPane);
        ewExampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String[] exampleList = new String[exampleEvents.examples.size()];
        for (int i = 0; i < exampleEvents.examples.size(); i++) {
            exampleList[i] = ((CaptureEvent) exampleEvents.examples.get(i)).getDescription();
        }
        ewExampleList.setListData(exampleList);

        ewOkButton = new JButton("Fill in");
        ewOkButton.addActionListener(this);
        ewButtonPanel.add(Box.createHorizontalGlue());
        ewButtonPanel.add(ewOkButton);
        ewButtonPanel.add(Box.createHorizontalGlue());

        exampleWindow.pack();
        exampleWindow.setVisible(true);
    }

    /**
     * Event dispatcher. Very simple events may be processed directly within
     * this method.
     * 
     * @param e
     *            for the Action
     */
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == mwEventTypeChooserComboBox) {
            mwEventTypeChooserComboBoxChanged();
            return;
        }
        if (e.getSource() == mwGenerateEventButton) {
            mwGenerateEventButtonPressed();
            return;
        }
        if (e.getSource() == mwFillInExampleButton) {
            createExampleWindow();
            return;
        }
        if (e.getSource() == ewOkButton) {
            ewOkButtonPressed();
            return;
        }
        if (e.getSource() == dwClearButton) {
            dwOutputTextArea.setText("");
        }
        if (e.getSource() == mwShowDebugWindowCheckBox) {
            debugWindow.setVisible(mwShowDebugWindowCheckBox.isSelected());
            return;
        }
        if (e.getSource() == mwBizTransactionPlus) {
            addBizTransactionRow();
        }
        // check, if it is a JButton and second, if it's name starts with
        // "removeBizTransNumber<Number>".
        if (((JButton) e.getSource()).getName() != null
                && ((JButton) e.getSource()).getName().startsWith(
                        "removeBizTransNumber")) {
            removeBizTransactionRow((JButton) e.getSource());
        }
    }

    /**
     * The user pushed the Ok button in the example window. Apply the chosen
     * example to the GUI.
     */
    private void ewOkButtonPressed() {
        int selected = ewExampleList.getSelectedIndex();
        if (selected >= 0) {
            CaptureEvent ex = (CaptureEvent) exampleEvents.examples.get(selected);

            if (ex.getEventTime() == null) {
                /* set eventTime to current time */
                java.util.Date now = new java.util.Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                /* set default to something simple and ISO 8601 compatible */
                mwEventTimeTextField.setText(cal.get(Calendar.YEAR) + "-"
                        + cal.get(Calendar.MONTH) + "-"
                        + cal.get(Calendar.DAY_OF_MONTH) + "T"
                        + cal.get(Calendar.HOUR_OF_DAY) + ":"
                        + cal.get(Calendar.MINUTE) + ":"
                        + cal.get(Calendar.SECOND));
            } else {
                mwEventTimeTextField.setText(ex.getEventTime());
            }
            if (ex.getAction() >= 0 && ex.getAction() <= 3) {
                mwActionComboBox.setSelectedIndex(ex.getAction());
            }
            mwBizStepTextField.setText(ex.getBizStep());
            mwDispositionTextField.setText(ex.getDisposition());
            mwReadPointTextField.setText(ex.getReadPoint());
            mwBizLocationTextField.setText(ex.getBizLocation());

            ArrayList<BizTransaction> bizTrans = new ArrayList<BizTransaction>();
            bizTrans = ex.getBizTransaction();
            // erase all what has been.
            mwBizTransTypeFields = new ArrayList<JTextField>();
            mwBizTransIDFields = new ArrayList<JTextField>();
            mwBizTransButtons = new ArrayList<JButton>();
            int i = 0;
            for (BizTransaction transaction : bizTrans) {
                addBizTransactionRow();
                mwBizTransTypeFields.get(i).setText(
                        transaction.getBizTransType());
                mwBizTransIDFields.get(i).setText(transaction.getBizTransID());
                i++;
            }
            drawBizTransaction();

            mwEpcListTextField.setText(ex.getEpcList());
            mwChildEPCsTextField.setText(ex.getChildEPCs());
            mwParentIDTextField.setText(ex.getParentID());
            mwEpcClassTextField.setText(ex.getEpcClass());
            if (ex.getQuantity() >= 0) {
                mwQuantityTextField.setText((new Integer(ex.getQuantity())).toString());
            }
            exampleWindow.setVisible(false);
            mwEventTypeChooserComboBox.setSelectedIndex(ex.getType());
        }
    }

    /**
     * The user changed the type of event. Update GUI accordingly.
     */
    private void mwEventTypeChooserComboBoxChanged() {
        /* show the corresponding input mask */
        switch (mwEventTypeChooserComboBox.getSelectedIndex()) {
        case 0:
            eventDataPanelObjectEvent();
            break;
        case 1:
            eventDataPanelAggregationEvent();
            break;
        case 2:
            eventDataPanelQuantityEvent();
            break;
        case 3:
            eventDataPanelTransactionEvent();
            break;
        default:
        }
        /* update graphics */
        mainWindow.pack();
    }

    /**
     * The user pushed the Generate event-button. This method converts the data
     * from the user interface to XML, POSTs it to the server and displays the
     * answer to the user. It also does some simple client-side checks to see if
     * all necessary fields are filled.
     */
    private void mwGenerateEventButtonPressed() {
        dwOutputTextArea.setText("");
        /* used later for user interaction */
        JFrame frame = new JFrame();

        try {
            /* DOM-tree stuff */
            Document document = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            /* temporary element variable */
            Element element = null;

            /* create empty document and fetch root */
            document = impl.createDocument("urn:epcglobal:epcis:xsd:1",
                    "epcis:EPCISDocument", null);
            Element root = document.getDocumentElement();

            Date now = new Date();
            SimpleDateFormat dateTime = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS");
            root.setAttribute("creationDate", dateTime.format(now));
            root.setAttribute("xmlns:xsi",
                    "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xmlns:epcis", "urn:epcglobal:epcis:xsd:1");
            root.setAttribute("schemaVersion", "1.0");
            element = document.createElement("EPCISBody");
            root.appendChild(element);
            root = element;
            element = document.createElement("EventList");
            root.appendChild(element);
            root = element;
            element = document.createElement(xmlEventNode[mwEventTypeChooserComboBox.getSelectedIndex()]);
            root.appendChild(element);
            root = element;

            /* eventTime */
            if (!addEventTime(document, root)) {
                JOptionPane.showMessageDialog(frame,
                        "Please specify the event time "
                                + "(i.e. 2005-07-18T17:33:20.231)", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // record Time is set by the capture-Interface

            if (xmlEventNode[mwEventTypeChooserComboBox.getSelectedIndex()].equals("ObjectEvent")) {
                if (!addEpcList(document, root)) {
                    JOptionPane.showMessageDialog(frame,
                            "Please specify at least one EPC", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addAction(document, root);
                addBizStep(document, root);
                addDisposition(document, root);
                addReadPoint(document, root);
                addBizLocation(document, root);
                addBizTransactionList(document, root);
            } else if (xmlEventNode[mwEventTypeChooserComboBox.getSelectedIndex()].equals("AggregationEvent")) {
                if (!addParentId(document, root)
                        && !mwActionComboBox.getSelectedItem().equals("OBSERVE")) {
                    JOptionPane.showMessageDialog(frame,
                            "Because action is OBSERVE, it's required to "
                                    + "specify a ParentID", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!addChildEpcList(document, root)) {
                    JOptionPane.showMessageDialog(frame,
                            "Please specify at least one EPC", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addAction(document, root);
                addBizStep(document, root);
                addDisposition(document, root);
                addReadPoint(document, root);
                addBizLocation(document, root);
                addBizTransactionList(document, root);
            } else if (xmlEventNode[mwEventTypeChooserComboBox.getSelectedIndex()].equals("QuantityEvent")) {
                if (!addEpcClass(document, root)) {
                    JOptionPane.showMessageDialog(frame,
                            "Please specify an EPC class (URI)", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!addQuantity(document, root)) {
                    JOptionPane.showMessageDialog(frame,
                            "Please specify a quantity value (integer number)",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addBizStep(document, root);
                addDisposition(document, root);
                addReadPoint(document, root);
                addBizLocation(document, root);
                addBizTransactionList(document, root);
            } else if (xmlEventNode[mwEventTypeChooserComboBox.getSelectedIndex()].equals("TransactionEvent")) {
                if (!addBizTransactionList(document, root)) {
                    JOptionPane.showMessageDialog(frame,
                            "Please specify at least one business "
                                    + "transaction (ID, Type)", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addParentId(document, root);
                if (!addEpcList(document, root)) {
                    JOptionPane.showMessageDialog(frame,
                            "Please specify at least one EPC", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addAction(document, root);
                addBizStep(document, root);
                addDisposition(document, root);
                addReadPoint(document, root);
                addBizLocation(document, root);
            }

            DOMSource domsrc = new DOMSource(document);

            StringWriter out = new StringWriter();
            StreamResult streamResult = new StreamResult(out);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();

            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.transform(domsrc, streamResult);

            String eventXml = out.toString();
            String postData = "event=" + eventXml;

            dwOutputTextArea.append("sending HTTP POST data:\n");
            dwOutputTextArea.append(postData);

            /* connect the service, write out xml and get response */
            URL serviceUrl = new URL(mwServiceUrlTextField.getText());
            HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
            connection.setDoOutput(true);

            OutputStream outStream = connection.getOutputStream();
            outStream.write(postData.getBytes());
            outStream.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String response = "Server responds: 200 OK\n";
                String line;
                while ((line = rd.readLine()) != null) {
                    response = response + line + "\n";
                }
                JOptionPane.showMessageDialog(frame, response, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream()));
                String response = "Server responds: "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage() + "\n";
                String line;
                while ((line = rd.readLine()) != null) {
                    response = response + line + "\n";
                }
                JOptionPane.showMessageDialog(frame, response, "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (MalformedURLException mfue) {
            JOptionPane.showMessageDialog(frame, mfue.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            mfue.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(frame, ioe.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            ioe.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        } catch (TransformerException te) {
            JOptionPane.showMessageDialog(frame, te.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            te.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        } catch (ParserConfigurationException pce) {
            JOptionPane.showMessageDialog(frame, pce.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            pce.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            StringWriter detailed = new StringWriter();
            PrintWriter pw = new PrintWriter(detailed);
            e.printStackTrace(pw);
            dwOutputTextArea.append(detailed.toString());
        }
    }

    /**
     * Adds the Quantity to the XML-File. Possible for the QuantityEvent
     * (required)
     * 
     * @param frame
     *            where the Error-Message can be displaied
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addQuantity(final Document document, final Element root) {
        Element element;
        try {
            Integer n = new Integer(mwQuantityTextField.getText());
            element = document.createElement("quantity");
            element.appendChild(document.createTextNode(n.toString()));
            root.appendChild(element);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Adds the ChildEPCList to the XML-File. Possible for the QuantityEvent
     * (required)
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addEpcClass(final Document document, final Element root) {
        if (!mwEpcClassTextField.getText().equals("")) {
            Element element;
            element = document.createElement("epcClass");
            element.appendChild(document.createTextNode(mwEpcClassTextField.getText()));
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the ChildEPCList to the XML-File. Possible for - AggregationEvent
     * (required) - TransactionEvent (required)
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addChildEpcList(final Document document, final Element root) {
        if (!mwChildEPCsTextField.getText().equals("")) {
            Element element;
            element = document.createElement("childEPCs");
            Element epcNode = null;
            String[] epcs = mwChildEPCsTextField.getText().split(" ");
            for (String epc : epcs) {
                epcNode = document.createElement("epc");
                epcNode.appendChild(document.createTextNode(epc));
                element.appendChild(epcNode);
            }
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the BusinessTransaction List to the XML-File. Possible for -
     * ObjectEvent (optional) - AggregationEvent (optional) - TransactionEvent
     * (at least one)
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addBizTransactionList(final Document document,
            final Element root) {
        if (mwBizTransIDFields != null && mwBizTransIDFields.size() > 0
                && !mwBizTransIDFields.get(0).getText().equals("")) {
            Element element;
            element = document.createElement("bizTransactionList");
            Element bizNode = null;
            int i = 0;
            for (JTextField j : mwBizTransIDFields) {
                if (!j.getText().equals("")
                        && !mwBizTransTypeFields.get(i).getText().equals("")) {
                    bizNode = document.createElement("bizTransaction");
                    bizNode.appendChild(document.createTextNode(j.getText()));
                    bizNode.setAttribute("type",
                            mwBizTransTypeFields.get(i).getText());
                    element.appendChild(bizNode);
                }
                i++;
            }
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the Business Location to the XML-File. Optional for all events.
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addBizLocation(final Document document, final Element root) {
        if (!mwBizLocationTextField.getText().equals("")) {
            Element element;
            element = document.createElement("bizLocation");
            Element bizId = document.createElement("id");
            bizId.appendChild(document.createTextNode(mwBizLocationTextField.getText()));
            element.appendChild(bizId);
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the ReadPoint to the XML-File. Optional for all events.
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addReadPoint(final Document document, final Element root) {
        if (!mwReadPointTextField.getText().equals("")) {
            Element element;
            element = document.createElement("readPoint");
            Element pointId = document.createElement("id");
            pointId.appendChild(document.createTextNode(mwReadPointTextField.getText()));
            element.appendChild(pointId);
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the Disposition to the XML-File. Optional for all events.
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addDisposition(final Document document, final Element root) {
        if (!mwDispositionTextField.getText().equals("")) {
            Element element;
            element = document.createElement("disposition");
            element.appendChild(document.createTextNode(mwDispositionTextField.getText()));
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the Business Step to the XML-File. Optional for all events.
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addBizStep(final Document document, final Element root) {
        if (!mwBizStepTextField.getText().equals("")) {
            Element element;
            element = document.createElement("bizStep");
            element.appendChild(document.createTextNode(mwBizStepTextField.getText()));
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the action to the XML-File. Required for all Events except the
     * QuantityEvent.
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private void addAction(final Document document, final Element root) {
        Element element;
        element = document.createElement("action");
        element.appendChild(document.createTextNode((String) mwActionComboBox.getSelectedItem()));
        root.appendChild(element);
    }

    /**
     * Adds all the EPC's to the XML-File. Possible for - AggregationEvent
     * (required) - TransactionEvent (required).
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addEpcList(final Document document, final Element root) {
        if (!mwEpcListTextField.getText().equals("")) {
            Element element;
            element = document.createElement("epcList");
            Element epcNode = null;
            StringTokenizer st = new StringTokenizer(
                    mwEpcListTextField.getText());
            while (st.hasMoreTokens()) {
                epcNode = document.createElement("epc");
                epcNode.appendChild(document.createTextNode(st.nextToken()));
                element.appendChild(epcNode);
            }
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the Parent ID to the XML-File. Possible for - AggregationEvent
     * (optional when action is OBSERVE, required otherwise) - TransactionEvent
     * (Optional).
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addParentId(final Document document, final Element root) {
        if (!mwParentIDTextField.getText().equals("")) {
            Element element;
            element = document.createElement("parentID");
            element.appendChild(document.createTextNode(mwParentIDTextField.getText()));
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the Time of the Event to the XML-File. Required for all Events.
     * 
     * @param document
     *            the DOM-Tree where is has to inserted
     * @param root
     *            the element, where it has to be added
     * @return if the value had been set in the GUI
     */
    private Boolean addEventTime(final Document document, final Element root) {
        if (!mwEventTimeTextField.getText().equals("")) {
            Element element;
            element = document.createElement("eventTime");
            element.appendChild(document.createTextNode(mwEventTimeTextField.getText()));
            root.appendChild(element);
            element = document.createElement("eventTimeZoneOffset");
            element.appendChild(document.createTextNode("+01:00"));
            root.appendChild(element);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Event handler for window manager closing events. Overrides the default,
     * empty method.
     * 
     * @param e
     *            for the WindowEvent
     */
    public void windowClosing(WindowEvent e) {
        if (e.getSource() == debugWindow) {
            mwShowDebugWindowCheckBox.setSelected(false);
            return;
        }
    }

    /**
     * Adds another row at the end of the Business Transactions.
     * 
     * @param i
     */
    private void addBizTransactionRow() {
        JTextField bizTransID = new JTextField();
        bizTransID.setToolTipText(toolTipBizTransID);
        JTextField bizTransType = new JTextField();
        bizTransType.setToolTipText(toolTipBizTransType);

        ImageIcon tempDelIcon = getImageIcon("delete10.gif");
        JButton minus = new JButton(tempDelIcon);

        minus.setMargin(new Insets(0, 0, 0, 0));
        minus.addActionListener(this);

        mwBizTransTypeFields.add(bizTransType);
        mwBizTransIDFields.add(bizTransID);
        mwBizTransButtons.add(minus);

        drawBizTransaction();
    }

    /**
     * Removes a row from the Business Transactions.
     * 
     * @param button
     *            which generated the event
     */
    private void removeBizTransactionRow(final JButton button) {
        int toRemove = Integer.parseInt(button.getName().substring(
                button.getName().length() - 1, button.getName().length()));
        mwBizTransTypeFields.remove(toRemove);
        mwBizTransIDFields.remove(toRemove);
        mwBizTransButtons.remove(toRemove);

        drawBizTransaction();
    }

    /**
     * After having added or deleted a Row from the BusinessTransactions, it has
     * to be redrawn.
     */
    private void drawBizTransaction() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 5, 0);

        mwBizTransactionPanel.removeAll();

        int i = 0;
        c.weightx = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        for (JButton j : mwBizTransButtons) {
            // every name from minus-Buttons has
            // to start with "removeBizTransNumber<Number>"
            j.setName("removeBizTransNumber" + i);
            c.gridy = i;
            mwBizTransactionPanel.add(j, c);
            i++;
        }

        i = 0;
        c.weightx = 1;
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        for (JTextField j : mwBizTransIDFields) {
            c.gridy = i;
            mwBizTransactionPanel.add(j, c);
            i++;
        }
        i = 0;
        c.weightx = 1;
        c.gridx = 2;
        c.fill = GridBagConstraints.BOTH;
        for (JTextField j : mwBizTransTypeFields) {
            c.gridy = i;
            mwBizTransactionPanel.add(j, c);
            i++;
        }

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = i + 1;
        c.fill = GridBagConstraints.NONE;
        mwBizTransactionPanel.add(mwBizTransactionPlus, c);

        // in case of having no BusinessTransactionfields,
        // we need to insert two "null"-elements that the
        // plus-Button is left-aligned.
        if (i == 0) {
            c.weightx = 1;
            c.gridx = 1;
            c.gridy = i + 1;
            c.fill = GridBagConstraints.BOTH;
            mwBizTransactionPanel.add(new JPanel(), c);
            c.weightx = 1;
            c.gridx = 2;
            c.gridy = i + 1;
            mwBizTransactionPanel.add(new JPanel(), c);
        }

        mainWindow.pack();
    }

    /**
     * Loads ImageIcon from either JAR or filesystem.
     */
    private ImageIcon getImageIcon(String filename) {
        // try loading image from JAR (Web Start environment)
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("gui/" + filename);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            // try loading image from filesystem - hack as we
            // can be called in either Eclipse or shell environment
            ImageIcon ii;
            ii = new ImageIcon("./tools/capturingGUI/media/" + filename);
            if (ii.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) ii = new ImageIcon(
                    "./gui/" + filename);
            return ii;
        }
    }

    /**
     * Tries to set a look-and-feel that matches the operating system and
     * creates the GUI.
     * 
     * @param args
     *            for the input-Arguments
     */
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (args != null && args.length > 0) {
            // a default url is given
            new CaptureClientGui(args[0]);
        } else {
            new CaptureClientGui();
        }
    }
}

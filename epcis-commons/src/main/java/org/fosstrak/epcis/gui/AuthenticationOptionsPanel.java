/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.epcis.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.fosstrak.epcis.utils.AuthenticationType;


/**
 * The AuthenticationConfigurationPanel is a pull down list of authentication modes and
 * widgets to accept parameters relevant to those choices. It is displayed
 * inline beneath the "URL" text field in the capture and query clients. It has two subcomponents
 * (BasicOptionsPanel and CertificateOptionsPanel) that contain inputs relevant
 * to the parameters for those authentication methods.
 * 
 * @author Sean Wellington
 */
public class AuthenticationOptionsPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -6085494400041808090L;

    private static final Map<String, AuthenticationType> authTypes = new LinkedHashMap<String, AuthenticationType>();
    static {
    	authTypes.put("None", AuthenticationType.NONE);
    	authTypes.put("Basic", AuthenticationType.BASIC);
    	authTypes.put("X.509 Certificate", AuthenticationType.HTTPS_WITH_CLIENT_CERT);
    }
    
    private AuthenticationOptionsChangeListener helper;

    private JLabel authTypeLabel;
    private JComboBox authTypeSelector;
    private OptionsPanel selectedOptionsPanel;

    private Map<AuthenticationType, OptionsPanel> allOptionsPanels = new HashMap<AuthenticationType, OptionsPanel>();

    public AuthenticationOptionsPanel(AuthenticationOptionsChangeListener helper) {
        super(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.helper = helper;

        authTypeLabel = new JLabel("Authentication Mode:");
        add(authTypeLabel);

        authTypeSelector = new JComboBox(authTypes.keySet().toArray());
        authTypeSelector.addActionListener(this);
        add(authTypeSelector);

        OptionsPanel noneOptions = new NoneOptionsPanel();
        allOptionsPanels.put(AuthenticationType.NONE, noneOptions);
        add((JPanel) noneOptions);

        OptionsPanel basicOptions = new BasicOptionsPanel();
        allOptionsPanels.put(AuthenticationType.BASIC, basicOptions);
        add((JPanel) basicOptions);

        OptionsPanel certOptions = new CertificateOptionsPanel();
        allOptionsPanels.put(AuthenticationType.HTTPS_WITH_CLIENT_CERT, certOptions);
        add((JPanel) certOptions);

        selectedOptionsPanel = noneOptions;
    }

    public void actionPerformed(ActionEvent e) {
        if (authTypeSelector == e.getSource()) {
            selectedOptionsPanel.setVisible(false);
            AuthenticationType at = authTypes.get(authTypeSelector.getSelectedItem());
            selectedOptionsPanel = allOptionsPanels.get(at);
            selectedOptionsPanel.setVisible(true);
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, selectedOptionsPanel.isComplete()));
        }
    }

    public Object[] getAuthenticationOptions() {
        return selectedOptionsPanel.getAuthenticationOptions();
    }

    private interface OptionsPanel {

    	public Object[] getAuthenticationOptions();

        public void setVisible(boolean visible);

        public boolean isComplete();
    }

    private class NoneOptionsPanel extends JPanel implements OptionsPanel {

        private static final long serialVersionUID = -3875349682626806242L;

        public Object[] getAuthenticationOptions() {
            return new Object[] { AuthenticationType.NONE };
        }

        public boolean isComplete() {
            return true;
        }
    }

    private class BasicOptionsPanel extends JPanel implements DocumentListener, OptionsPanel {

        private static final long serialVersionUID = -8162893711119565008L;

        private JLabel userNameLabel;
        private JTextField userNameInput;
        private JLabel passwordLabel;
        private JPasswordField passwordInput;

        private BasicOptionsPanel() {
            super(new FlowLayout(FlowLayout.LEFT, 10, 0));

            userNameLabel = new JLabel("Username:");
            add(userNameLabel);

            userNameInput = new JTextField(10);
            userNameInput.getDocument().addDocumentListener(this);
            add(userNameInput);

            passwordLabel = new JLabel("Password:");
            add(passwordLabel);

            passwordInput = new JPasswordField(10);
            passwordInput.getDocument().addDocumentListener(this);
            add(passwordInput);

            setVisible(false);
        }

        public Object[] getAuthenticationOptions() {
            return new Object[] {
                    AuthenticationType.BASIC, userNameInput.getText(), new String(passwordInput.getPassword()) };
        }

        public void changedUpdate(DocumentEvent e) {
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
        }

        public void insertUpdate(DocumentEvent e) {
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
        }

        public void removeUpdate(DocumentEvent e) {
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
        }

        public boolean isComplete() {
            String userName = userNameInput.getText();
            char[] password = passwordInput.getPassword();
            return (userName != null && userName.length() > 0) && (password != null && password.length > 0);
        }
    }

    private class CertificateOptionsPanel extends JPanel implements OptionsPanel, ActionListener, DocumentListener {

        private static final long serialVersionUID = 761325536394318272L;

        private JLabel keyStoreLabel;
        private JTextField keyStoreInput;
        private JButton loadKeyStoreButton;
        private JFileChooser fileChooser;
        private JLabel passwordLabel;
        private JPasswordField passwordInput;

        CertificateOptionsPanel() {
            super(new FlowLayout(FlowLayout.LEFT, 10, 0));

            keyStoreLabel = new JLabel("Key Store:");
            add(keyStoreLabel);

            keyStoreInput = new JTextField(23);
            keyStoreInput.setEnabled(false);
            add(keyStoreInput);

            loadKeyStoreButton = new JButton("Open");
            loadKeyStoreButton.addActionListener(this);
            add(loadKeyStoreButton);

            passwordLabel = new JLabel("Password:");
            add(passwordLabel);

            passwordInput = new JPasswordField(10);
            passwordInput.getDocument().addDocumentListener(this);
            add(passwordInput);

            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                public boolean accept(File pathname) {
                    String s = pathname.getName();
                    return pathname.isDirectory() || s.endsWith(".p12") || s.endsWith(".jks");
                }

                public String getDescription() {
                    return "Key Store Files (*.jks; *.p12)";
                }
            });

            setVisible(false);
        }

        public Object[] getAuthenticationOptions() {
            return new Object[] {
                    AuthenticationType.HTTPS_WITH_CLIENT_CERT, keyStoreInput.getText(),
                    new String(passwordInput.getPassword()) };
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loadKeyStoreButton) {
                int returnCode = fileChooser.showOpenDialog(getRootPane());
                if (returnCode == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    keyStoreInput.setText(f.toString());
                    helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
                }
            }
        }

        public boolean isComplete() {
            String filename = keyStoreInput.getText();
            char[] password = passwordInput.getPassword();
            return (filename != null && filename.length() > 0) && (password != null && password.length > 0);
        }

        public void changedUpdate(DocumentEvent e) {
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
        }

        public void insertUpdate(DocumentEvent e) {
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
        }

        public void removeUpdate(DocumentEvent e) {
            helper.configurationChanged(new AuthenticationOptionsChangeEvent(this, isComplete()));
        }
    }
}
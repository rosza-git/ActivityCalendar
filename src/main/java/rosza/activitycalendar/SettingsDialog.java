/**
 * Settings dialog
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.MatteBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;
import rosza.xcomponents.JButtonX;
import rosza.xcomponents.JDialogX;
import rosza.xcomponents.JScrollBarX;
import rosza.xcomponents.JSpinnerX;
import rosza.xcomponents.JTabbedPaneX;

public class SettingsDialog extends JDialogX {
  // UI variables
  private JTabbedPane    settingsTabbedPane;
  private JPanel         storagePanel;
  private JPanel         categoriesPanel;
  private JPanel         emailPanel;
  private ButtonGroup    storageRadioGroup;
  private ButtonGroup    emailRadioGroup;
  private JRadioButton   xmlRadio;
  private JRadioButton   dbRadio;
  private JLabel         dbUserLabel;
  private JTextField     dbUserTextField;
  private JPasswordField dbPasswordField;
  private JLabel         dbPasswordLabel;
  private JLabel         dbServerLabel;
  private JLabel         dbServerPortLabel;
  private JTextField     dbServerTextField;
  private JTextField     dbServerPortTextField;
  private JButtonX       saveStorageButton;
  private JLabel         categoriesLabel;
  private JScrollPane    categoryScrollPane;
  private CategoryTree   categoryTree;
  private final Category categoryTreeElements = XMLUtil.getCategories();
  private JButtonX       addCategoryButton;
  private JButtonX       modifyCategoryButton;
  private JButtonX       removeCategoryButton;
  private JLabel         newCategoryLabel;
  private JTextField     newCategoryTextField;
  private JColorChooser  categoryColor;
  private JButtonX       closeButton;
  private JLabel         emailAddressLabel;
  private JLabel         emailHostLabel;
  private JLabel         emailPortLabel;
  private JLabel         emailUserLabel;
  private JLabel         emailPasswordLabel;
  private JTextField     emailAddressTextField;
  private JTextField     emailUserTextField;
  private JCheckBox      emailAuthCheckBox;
  private JTextField     emailHostTextField;
  private JTextField     emailPortTextField;
  private JPasswordField emailPasswordField;
  private JLabel         emailProtocolLabel;
  private JRadioButton   emailTLSRadioButton;
  private JRadioButton   emailSMTPSRadioButton;
  private JButtonX       emailSaveButton;

  // Properties variable
  private Properties props;

  // Variable for encryption
  private final StrongTextEncryptor textEncryptor;      // http://technofes.blogspot.in/2011/10/orgjasyptexceptionsencryptionoperationn.html

  // Create settings dialog
  public SettingsDialog(Frame frame, Component locationComp, String title, boolean modal) {
    super(frame, title, modal);

    textEncryptor = new StrongTextEncryptor();
    textEncryptor.setPassword(Constant.SALT);
    props = ActivityCalendar.getSettings();

    createUI(locationComp);
    setFieldValues();
  }

  // Show the dialog.
  public void showDialog() {
    setVisible(true);
  }

  // Create UI
  @SuppressWarnings("unchecked")
  private void createUI(Component locationComp) {
    emailRadioGroup       = new ButtonGroup();
    storageRadioGroup     = new ButtonGroup();
    settingsTabbedPane    = new JTabbedPane();
    storagePanel          = new JPanel();
    emailPanel            = new JPanel();
    xmlRadio              = new JRadioButton();
    dbRadio               = new JRadioButton();
    dbServerLabel         = new JLabel();
    dbServerPortLabel     = new JLabel();
    dbUserLabel           = new JLabel();
    dbPasswordLabel       = new JLabel();
    dbServerTextField     = new JTextField();
    dbServerPortTextField = new JTextField();
    dbUserTextField       = new JTextField();
    dbPasswordField       = new JPasswordField();
    saveStorageButton     = new JButtonX("save");
    categoriesPanel       = new JPanel();
    categoriesLabel       = new JLabel();
    categoryScrollPane    = new JScrollPane();
    categoryTree          = new CategoryTree(categoryTreeElements);
    newCategoryTextField  = new JTextField();
    newCategoryLabel      = new JLabel();
    addCategoryButton     = new JButtonX("add");
    removeCategoryButton  = new JButtonX("remove");
    modifyCategoryButton  = new JButtonX("modify");
    categoryColor         = new JColorChooser();
    emailPanel            = new JPanel();
    emailAddressLabel     = new JLabel();
    emailUserLabel        = new JLabel();
    emailPasswordLabel    = new JLabel();
    emailAddressTextField = new JTextField();
    emailUserTextField    = new JTextField();
    emailAuthCheckBox     = new JCheckBox();
    emailHostLabel        = new JLabel();
    emailPortLabel        = new JLabel();
    emailHostTextField    = new JTextField();
    emailPortTextField    = new JTextField();
    emailPasswordField    = new JPasswordField();
    emailProtocolLabel    = new JLabel();
    emailTLSRadioButton   = new JRadioButton();
    emailSMTPSRadioButton = new JRadioButton();
    emailSaveButton       = new JButtonX("save");
    closeButton           = new JButtonX("close");

    categoryColor.setPreviewPanel(new JPanel());
    // remove chooser panel, except "RGB"
    AbstractColorChooserPanel[] colorPanel = categoryColor.getChooserPanels();
    for(AbstractColorChooserPanel accp : colorPanel) {
      if(!accp.getDisplayName().equals("RGB")) {
         categoryColor.removeChooserPanel(accp);
      }
      else {
        for(Component component : accp.getComponents()) {
          JComponent comp = (JComponent)component;
          for(Component c : comp.getComponents()) {
            if(c instanceof JSpinner) {
              ((JSpinner)c).setUI(new JSpinnerX());
            }
          }
        }
      }
    }

    categoryColor.getSelectionModel().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        Color color = new Color(categoryColor.getColor().getRGB());
        newCategoryTextField.setBackground(color);
      }
    });

    storageRadioGroup.add(xmlRadio);
    storageRadioGroup.add(dbRadio);

    xmlRadio.setText("XML");
    xmlRadio.setOpaque(false);
    xmlRadio.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        storageRadioActionPerformed(e);
      }
    });

    dbRadio.setText("database");
    dbRadio.setOpaque(false);
    dbRadio.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        storageRadioActionPerformed(e);
      }
    });

    dbServerLabel.setHorizontalAlignment(JLabel.RIGHT);
    dbServerLabel.setText("server:");
    dbServerLabel.setFont(dbServerLabel.getFont().deriveFont(Font.BOLD));

    dbServerTextField.getDocument().addDocumentListener(databaseListener);

    dbServerPortLabel.setHorizontalAlignment(JLabel.RIGHT);
    dbServerPortLabel.setText("port:");
    dbServerPortLabel.setFont(dbServerPortLabel.getFont().deriveFont(Font.BOLD));

    dbServerPortTextField.getDocument().addDocumentListener(databaseListener);

    dbUserLabel.setHorizontalAlignment(JLabel.RIGHT);
    dbUserLabel.setText("user:");
    dbUserLabel.setFont(dbUserLabel.getFont().deriveFont(Font.BOLD));

    dbUserTextField.getDocument().addDocumentListener(databaseListener);

    dbPasswordLabel.setHorizontalAlignment(JLabel.RIGHT);
    dbPasswordLabel.setText("password:");
    dbPasswordLabel.setFont(dbPasswordLabel.getFont().deriveFont(Font.BOLD));

    dbPasswordField.getDocument().addDocumentListener(databaseListener);

    saveStorageButton.setText("save");
    saveStorageButton.setFont(saveStorageButton.getFont().deriveFont(Font.BOLD));
    saveStorageButton.setEnabled(enableStorageSaveButton());
    saveStorageButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveStorageButtonActionPerformed(e);
      }
    });

    storagePanel.setOpaque(true);
    GroupLayout storagePanelLayout = new GroupLayout(storagePanel);
    storagePanel.setLayout(storagePanelLayout);
    storagePanelLayout.setHorizontalGroup(
      storagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(storagePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(storagePanelLayout.createSequentialGroup()
            .addComponent(xmlRadio)
            .addGap(18, 18, 18)
            .addComponent(dbRadio)
          )
          .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(saveStorageButton)
            .addGroup(storagePanelLayout.createSequentialGroup()
              .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(dbServerLabel)
                .addComponent(dbServerPortLabel)
                .addComponent(dbUserLabel)
                .addComponent(dbPasswordLabel)
              )
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(dbServerTextField, GroupLayout.Alignment.LEADING)
                .addComponent(dbServerPortTextField, GroupLayout.Alignment.LEADING)
                .addComponent(dbUserTextField, GroupLayout.Alignment.LEADING)
                .addComponent(dbPasswordField, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
              )
            )
          )
        )
        .addContainerGap(124, Short.MAX_VALUE)
      )
    );
    storagePanelLayout.setVerticalGroup(
      storagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(storagePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(xmlRadio)
          .addComponent(dbRadio)
        )
        .addGap(18, 18, 18)
        .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(dbServerLabel)
          .addComponent(dbServerTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(dbServerPortLabel)
          .addComponent(dbServerPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(dbUserLabel)
          .addComponent(dbUserTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(storagePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(dbPasswordLabel)
          .addComponent(dbPasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(saveStorageButton)
        .addContainerGap(168, Short.MAX_VALUE)
      )
    );

    categoriesLabel.setHorizontalAlignment(JLabel.RIGHT);
    categoriesLabel.setText("categories:");
    categoriesLabel.setFont(categoriesLabel.getFont().deriveFont(Font.BOLD));

    categoryTree.setSelectionPath(categoryTree.getPathForRow(1));
    categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        categoryTreeSelectionChanged(e);
      }
    });

    categoryScrollPane.setViewportView(categoryTree);
    categoryScrollPane.getVerticalScrollBar().setUI(new JScrollBarX());
    categoryScrollPane.getHorizontalScrollBar().setUI(new JScrollBarX());
    categoryScrollPane.setBorder(new MatteBorder(1, 1, 1, 1, Constant.BG_DARKER_BLUE));

    newCategoryTextField.setText("");
    newCategoryTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        addCategoryButton.setEnabled(enableAddCategoryButton());
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        addCategoryButton.setEnabled(enableAddCategoryButton());
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        addCategoryButton.setEnabled(enableAddCategoryButton());
      }
    });

    newCategoryLabel.setHorizontalAlignment(JLabel.RIGHT);
    newCategoryLabel.setText("new name:");
    newCategoryLabel.setFont(newCategoryLabel.getFont().deriveFont(Font.BOLD));

    addCategoryButton.setText("add");
    addCategoryButton.setEnabled(false);
    addCategoryButton.setFont(addCategoryButton.getFont().deriveFont(Font.BOLD));
    addCategoryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        categoryAddButtonActionPerformed(e);
      }
    });

    removeCategoryButton.setText("remove");
    removeCategoryButton.setEnabled(false);
    removeCategoryButton.setFont(removeCategoryButton.getFont().deriveFont(Font.BOLD));
    removeCategoryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        categoryRemoveButtonActionPerformed(e);
      }
    });

    modifyCategoryButton.setText("modify");
    modifyCategoryButton.setEnabled(false);
    modifyCategoryButton.setFont(modifyCategoryButton.getFont().deriveFont(Font.BOLD));
    modifyCategoryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        categoryModifyButtonActionPerformed(e);
      }
    });

    categoriesPanel.setOpaque(true);
    GroupLayout categoriesPanelLayout = new GroupLayout(categoriesPanel);
    categoriesPanel.setLayout(categoriesPanelLayout);
    categoriesPanelLayout.setHorizontalGroup(
      categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(categoriesPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(categoriesPanelLayout.createSequentialGroup()
            .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
              .addComponent(categoriesLabel)
              .addComponent(newCategoryLabel)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addGroup(categoriesPanelLayout.createSequentialGroup()
                .addComponent(newCategoryTextField, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
              )
              .addGroup(categoriesPanelLayout.createSequentialGroup()
                .addComponent(addCategoryButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modifyCategoryButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(removeCategoryButton)
              )
              .addComponent(categoryScrollPane)
            )
          )
          .addComponent(categoryColor, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addContainerGap()
      )
    );
    categoriesPanelLayout.setVerticalGroup(
      categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(categoriesPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(categoryScrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
          .addComponent(categoriesLabel)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(removeCategoryButton)
          .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(addCategoryButton)
            .addComponent(modifyCategoryButton)
          )
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(newCategoryTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(newCategoryLabel)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(categoryColor, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      )
    );

    emailRadioGroup.add(this.emailTLSRadioButton);
    emailRadioGroup.add(this.emailSMTPSRadioButton);

    emailAddressLabel.setHorizontalAlignment(JLabel.RIGHT);
    emailAddressLabel.setText("from:");
    emailAddressLabel.setFont(emailAddressLabel.getFont().deriveFont(Font.BOLD));

    emailAddressTextField.setInputVerifier(new EmailVerifier());
    emailAddressTextField.getDocument().addDocumentListener(emailListener);

    emailUserLabel.setHorizontalAlignment(JLabel.RIGHT);
    emailUserLabel.setText("username:");
    emailUserLabel.setFont(emailUserLabel.getFont().deriveFont(Font.BOLD));

    emailUserTextField.getDocument().addDocumentListener(emailListener);

    emailPasswordLabel.setHorizontalAlignment(JLabel.RIGHT);
    emailPasswordLabel.setText("password:");
    emailPasswordLabel.setFont(emailPasswordLabel.getFont().deriveFont(Font.BOLD));

    emailPasswordField.getDocument().addDocumentListener(emailListener);

    emailAuthCheckBox.setText("authentication");
    emailAuthCheckBox.setOpaque(false);

    emailHostLabel.setHorizontalAlignment(JLabel.RIGHT);
    emailHostLabel.setText("smtp host:");
    emailHostLabel.setFont(emailHostLabel.getFont().deriveFont(Font.BOLD));

    emailHostTextField.getDocument().addDocumentListener(emailListener);

    emailPortLabel.setHorizontalAlignment(JLabel.RIGHT);
    emailPortLabel.setText("port:");
    emailPortLabel.setFont(emailPortLabel.getFont().deriveFont(Font.BOLD));

    emailPortTextField.getDocument().addDocumentListener(emailListener);

    emailProtocolLabel.setText("protocol:");
    emailProtocolLabel.setFont(emailProtocolLabel.getFont().deriveFont(Font.BOLD));

    emailRadioGroup.add(emailTLSRadioButton);
    emailRadioGroup.add(emailSMTPSRadioButton);

    emailTLSRadioButton.setText("TLS");
    emailTLSRadioButton.setOpaque(false);
    emailTLSRadioButton.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        emailSaveButton.setEnabled(enableEmailSaveButton());
      }
    });

    emailSMTPSRadioButton.setText("SMTPS");
    emailSMTPSRadioButton.setOpaque(false);
    emailSMTPSRadioButton.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        emailSaveButton.setEnabled(enableEmailSaveButton());
      }
    });

    emailSaveButton.setText("save");
    emailSaveButton.setFont(emailSaveButton.getFont().deriveFont(Font.BOLD));
    emailSaveButton.setEnabled(enableEmailSaveButton());
    emailSaveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveEmailButtonActionPerformed(e);
      }
    });

    emailPanel.setOpaque(true);
    GroupLayout emailPanelLayout = new GroupLayout(emailPanel);
    emailPanel.setLayout(emailPanelLayout);
    emailPanelLayout.setHorizontalGroup(
      emailPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(emailPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(GroupLayout.Alignment.TRAILING, emailPanelLayout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(emailSaveButton))
          .addGroup(emailPanelLayout.createSequentialGroup()
            .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
              .addComponent(emailAddressLabel)
              .addComponent(emailPortLabel)
              .addComponent(emailHostLabel)
              .addComponent(emailPasswordLabel)
              .addComponent(emailUserLabel)
              .addComponent(emailProtocolLabel)
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addComponent(emailAddressTextField)
              .addComponent(emailUserTextField)
              .addComponent(emailHostTextField)
              .addComponent(emailPortTextField)
              .addComponent(emailPasswordField)
              .addGroup(emailPanelLayout.createSequentialGroup()
                .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(emailAuthCheckBox)
                  .addGroup(emailPanelLayout.createSequentialGroup()
                    .addComponent(emailTLSRadioButton)
                    .addGap(18, 18, 18)
                    .addComponent(emailSMTPSRadioButton)
                  )
                )
                .addGap(0, 126, Short.MAX_VALUE)
              )
            )
          )
        )
        .addContainerGap()
      )
    );
    emailPanelLayout.setVerticalGroup(
      emailPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(emailPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(emailAddressLabel)
          .addComponent(emailAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(emailUserLabel)
          .addComponent(emailUserTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(emailPasswordLabel)
          .addComponent(emailPasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addGap(18, 18, 18)
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(emailHostLabel)
          .addComponent(emailHostTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(emailPortLabel)
          .addComponent(emailPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(emailPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(emailProtocolLabel)
          .addComponent(emailTLSRadioButton)
          .addComponent(emailSMTPSRadioButton)
        )
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(emailAuthCheckBox)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
        .addComponent(emailSaveButton)
        .addContainerGap()
      )
    );

    closeButton.setText("close");
    closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD));
    closeButton.addActionListener(actionListener);
    closeButton.setActionCommand(Constant.CLOSE_DIALOG);

    settingsTabbedPane.setUI(new JTabbedPaneX());
    settingsTabbedPane.setTabPlacement(JTabbedPane.TOP);
    settingsTabbedPane.setFont(settingsTabbedPane.getFont().deriveFont(Font.BOLD));
    settingsTabbedPane.addTab("storage", storagePanel);
    settingsTabbedPane.addTab("categories", categoriesPanel);
    settingsTabbedPane.addTab("e-mail", emailPanel);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(settingsTabbedPane)
          .addComponent(closeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        )
        .addContainerGap()
      )
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(settingsTabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(closeButton)
        .addContainerGap()
      )
    );

    pack();
    setLocationRelativeTo(locationComp);
  }

  // Set field values from properties
  private void setFieldValues() {
    try {
      // Get storage settings
      if(props.getProperty(Constant.PROPS_STORAGE).equals(Constant.PROPS_XML_STORAGE)) {
        xmlRadio.setSelected(true);
        dbRadio.setSelected(false);

        dbServerPortTextField.setEnabled(false);
        dbServerTextField.setEnabled(false);
        dbUserTextField.setEnabled(false);
        dbPasswordField.setEnabled(false);
      }
      else {
        xmlRadio.setSelected(false);
        dbRadio.setSelected(true);

        dbServerTextField.setEnabled(true);
        dbServerTextField.setText(props.getProperty(Constant.PROPS_DB_SERVER));

        dbServerPortTextField.setEnabled(true);
        dbServerPortTextField.setText(props.getProperty(Constant.PROPS_DB_SERVER_PORT));

        dbUserTextField.setEnabled(true);
        dbUserTextField.setText(props.getProperty(Constant.PROPS_DB_USERNAME));

        dbPasswordField.setEnabled(true);
        String pwd = props.getProperty(Constant.PROPS_DB_PASSWORD);
        try {
          dbPasswordField.setText(textEncryptor.decrypt(pwd));
        }
        catch(EncryptionOperationNotPossibleException e) {
          dbPasswordField.setText(pwd);
        }
        catch(Exception e) {
          JOptionPane.showMessageDialog(this, "Error in jaspyt!\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    catch(NullPointerException e) {
      xmlRadio.setSelected(true);
      dbRadio.setSelected(false);
      dbServerPortTextField.setEnabled(false);
      dbPasswordField.setEnabled(false);
      dbServerTextField.setEnabled(false);
      dbUserTextField.setEnabled(false);
    }
    saveStorageButton.setEnabled(enableStorageSaveButton());
    // Get e-mail settings
    try {
      emailAddressTextField.setText(props.getProperty(Constant.PROPS_EMAIL_ADDRESS));
      emailHostTextField.setText(props.getProperty(Constant.PROPS_EMAIL_SMTP_HOST));
      emailPortTextField.setText(props.getProperty(Constant.PROPS_EMAIL_SMTP_PORT));
      emailUserTextField.setText(props.getProperty(Constant.PROPS_EMAIL_USERNAME));
      String pwd = props.getProperty(Constant.PROPS_EMAIL_PASSWORD);
      try {
        emailPasswordField.setText(textEncryptor.decrypt(pwd));
      }
      catch(EncryptionOperationNotPossibleException e) {
        emailPasswordField.setText(pwd);
      }
      catch(Exception e) {
        JOptionPane.showMessageDialog(this, "Error in jaspyt!\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
      }
      if(props.getProperty(Constant.PROPS_EMAIL_AUTHENTICATION) == null) {
        emailAuthCheckBox.setSelected(false);
      }
      else {
        emailAuthCheckBox.setSelected(props.getProperty(Constant.PROPS_EMAIL_AUTHENTICATION).equals("true"));
      }
      switch(props.getProperty(Constant.PROPS_EMAIL_PROTOCOL)) {
        case Constant.PROPS_EMAIL_SMTPS:
          emailSMTPSRadioButton.setSelected(true);
          break;
        case Constant.PROPS_EMAIL_TLS:
          emailTLSRadioButton.setSelected(true);
          break;
      }
    }
    catch(NullPointerException e) {
    }
  }

  // E-mail save button state
  private boolean enableEmailSaveButton() {
    boolean state = true;
    state &= !emailAddressTextField.getText().equals("");
    state &= !emailUserTextField.getText().equals("");
    state &= emailPasswordField.getPassword().length > 0;
    state &= !emailHostTextField.getText().equals("");  
    state &= !emailPortTextField.getText().equals("");
    state &= (emailTLSRadioButton.isSelected() | emailSMTPSRadioButton.isSelected());

    return state;
  }

  // Storage save button state
  private boolean enableStorageSaveButton() {
    if(xmlRadio.isSelected()) {
      return true;
    }
    boolean state = true;
    state &= !dbServerTextField.getText().equals("");
    state &= !dbServerPortTextField.getText().equals("");
    state &= !dbUserTextField.getText().equals("");  
    state &= dbPasswordField.getPassword().length > 0;

    return state;
  }

  // Add category button state
  private boolean enableAddCategoryButton() {
    return !(newCategoryTextField.getText().equals("") && (categoryTree.getSelected().getID() == 0));
  }

  // Button handlers
  private void categoryAddButtonActionPerformed(ActionEvent e) {
    int id = Category.getLastID((Category)categoryTree.getModel().getRoot(), 0);
    Category c = new Category(++id, newCategoryTextField.getText(), categoryColor.getColor(), false);
    Category.addCategory(c, categoryTree.getSelected());
    XMLUtil.createCategoriesXML(categoryTreeElements);
    categoryTree.updateUI();
  }

  private void categoryModifyButtonActionPerformed(ActionEvent e) {
    Category.modifiyCategory(categoryTree.getSelected(), newCategoryTextField.getText(), categoryColor.getColor());
    XMLUtil.createCategoriesXML(categoryTreeElements);
    categoryTree.updateUI();
  }

  private void categoryRemoveButtonActionPerformed(ActionEvent e) {
    Category c = categoryTree.getSelected();
    if(c.isPredefined()) {
      JOptionPane.showMessageDialog(this, "Removing pre-defined categories are not allowed! (" + c.getName() + ")", "Error", JOptionPane.ERROR_MESSAGE);
    }
    else if(c.hasSubCategory()) {
      int reply = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove '" + c.getName() + "' and its subcategories?", "Question", JOptionPane.YES_NO_OPTION);
      if(reply == JOptionPane.YES_OPTION) {
        categoryTreeElements.removeCategory(c);
        XMLUtil.createCategoriesXML(categoryTreeElements);
        categoryTree.updateUI();
      }
    }
    else {
      int reply = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove '" + c.getName() + "'?", "Question", JOptionPane.YES_NO_OPTION);
      if(reply == JOptionPane.YES_OPTION) {
        categoryTreeElements.removeCategory(c);
        XMLUtil.createCategoriesXML(categoryTreeElements);
        categoryTree.updateUI();
      }
    }
  }

  private void saveStorageButtonActionPerformed(ActionEvent e) {
    if(props == null) {
      props = new Properties();
    }

    if(dbRadio.isSelected()) {
      props.setProperty(Constant.PROPS_STORAGE, Constant.PROPS_DB_STORAGE);
      props.setProperty(Constant.PROPS_DB_SERVER, dbServerTextField.getText());
      props.setProperty(Constant.PROPS_DB_SERVER_PORT, dbServerPortTextField.getText());
      props.setProperty(Constant.PROPS_DB_USERNAME, dbUserTextField.getText());
      String pwd = new String(dbPasswordField.getPassword());
      try {
        props.setProperty(Constant.PROPS_DB_PASSWORD, textEncryptor.encrypt(pwd));
      }
      catch(EncryptionOperationNotPossibleException ex) {
        JOptionPane.showMessageDialog(this, "Error in jaspyt!\n" + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else {
      props.setProperty(Constant.PROPS_STORAGE, Constant.PROPS_XML_STORAGE);
    }

    ActivityCalendar.saveSettings(props);
  }

  private void saveEmailButtonActionPerformed(ActionEvent e) {
    if(props == null) {
      props = new Properties();
    }

    props.setProperty(Constant.PROPS_EMAIL_ADDRESS, emailAddressTextField.getText());
    props.setProperty(Constant.PROPS_EMAIL_USERNAME, emailUserTextField.getText());
    String pwd = new String(emailPasswordField.getPassword());
    try {
      props.setProperty(Constant.PROPS_EMAIL_PASSWORD, textEncryptor.encrypt(pwd));
    }
    catch(EncryptionOperationNotPossibleException ex) {
      JOptionPane.showMessageDialog(this, "Error in jaspyt!\n" + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    props.setProperty(Constant.PROPS_EMAIL_SMTP_HOST, emailHostTextField.getText());
    props.setProperty(Constant.PROPS_EMAIL_SMTP_PORT, emailPortTextField.getText());
    String protocol;
    if(emailSMTPSRadioButton.isSelected()) {
      protocol = Constant.PROPS_EMAIL_SMTPS;
    }
    else {
      protocol = Constant.PROPS_EMAIL_TLS;
    }
    props.setProperty(Constant.PROPS_EMAIL_PROTOCOL, protocol);
    String auth = emailAuthCheckBox.isSelected() ? "true" : "false";
    props.setProperty(Constant.PROPS_EMAIL_AUTHENTICATION, auth);

    ActivityCalendar.saveSettings(props);
  }

  // Radio button events
  private void storageRadioActionPerformed(ActionEvent e) {
    if(xmlRadio.isSelected()) {
      this.dbServerTextField.setEnabled(false);
      this.dbServerPortTextField.setEnabled(false);
      this.dbUserTextField.setEnabled(false);
      this.dbPasswordField.setEnabled(false);
    }
    else {
      this.dbServerTextField.setEnabled(true);
      this.dbServerPortTextField.setEnabled(true);
      this.dbUserTextField.setEnabled(true);
      this.dbPasswordField.setEnabled(true);
    }
    saveStorageButton.setEnabled(enableStorageSaveButton());
  }

  // Category-tree selection changed
  private void categoryTreeSelectionChanged(TreeSelectionEvent e) {
    if(categoryTree.getSelected() == null) {
      return;
    }
    int id = categoryTree.getSelected().getID();
    if(id == 0) {
      categoryTree.setSelectionPath(categoryTree.getPathForRow(id + 1));
    }
    else {
      newCategoryTextField.setText(categoryTree.getSelected().toString());
      categoryColor.setColor(categoryTree.getSelected().getColor());
      newCategoryTextField.setBackground(categoryTree.getSelected().getColor());
      modifyCategoryButton.setEnabled(true);
      removeCategoryButton.setEnabled(true);
    }
  }

  // Action listener
  ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if(null != e.getActionCommand()) {
        switch (e.getActionCommand()) {
          case Constant.CLOSE_DIALOG:
            setVisible(false);
            dispose();
            break;
        }
      }
    }
  };

  // Document listener for e-mail fields
  DocumentListener emailListener = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      emailSaveButton.setEnabled(enableEmailSaveButton());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      emailSaveButton.setEnabled(enableEmailSaveButton());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      emailSaveButton.setEnabled(enableEmailSaveButton());
    }
  };

  // Document listener for database fields
  DocumentListener databaseListener = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      saveStorageButton.setEnabled(enableStorageSaveButton());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      saveStorageButton.setEnabled(enableStorageSaveButton());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      saveStorageButton.setEnabled(enableStorageSaveButton());
    }
  };
}

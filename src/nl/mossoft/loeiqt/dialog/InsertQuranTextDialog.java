/*
 * This file is part of QuranLO
 *
 * Copyright (C) 2020 <mossie@mossoft.nl>
 *
 * QuranLO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */

package nl.mossoft.loeiqt.dialog;

import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XNumericField;
import com.sun.star.awt.XProgressBar;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XController;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.ControlCharacter;
import com.sun.star.text.XParagraphCursor;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import nl.mossoft.loeiqt.helper.DialogHelper;
import nl.mossoft.loeiqt.helper.DocumentHelper;
import nl.mossoft.loeiqt.helper.FileHelper;
import nl.mossoft.loeiqt.helper.QuranReader;

public class InsertQuranTextDialog implements XDialogEventHandler {

  private static final String ARABIC = "Arabic";
  private static final String DIALOG_ALL_AYAT_CHECKBOX_ID = "AllAyatCheckBoxID";
  private static final String DIALOG_ARABIC_CHECKBOX_ID = "ArabicCheckBoxID";
  private static final String DIALOG_ARABIC_FONT_GROUPBOX_ID = "ArabicFontGroupBoxID";
  private static final String DIALOG_ARABIC_FONT_LABEL_ID = "ArabicFontLabelID";
  private static final String DIALOG_ARABIC_FONT_LISTBOX_ID = "ArabicFontListBoxID";
  private static final String DIALOG_ARABIC_FONTSIZE_LABEL_ID = "ArabicFontSizeLabelID";
  private static final String DIALOG_ARABIC_FONTSIZE_NUMERICFIELD_ID =
      "ArabicFontSizeNumericFieldID";
  private static final String DIALOG_ARABIC_LISTBOX_ID = "ArabicListBoxID";
  private static final String DIALOG_AYAT_FROM_NUMERIC_FIELD_ID = "AyatFromNumericFieldID";
  private static final String DIALOG_AYAT_TO_NUMERIC_FIELD_ID = "AyatToNumericFieldID";
  private static final String DIALOG_LINE_BY_LINE_CHECKBOX_ID = "LineByLineCheckBoxID";
  private static final String DIALOG_LINE_BY_LINE_LABEL_ID = "LineBylineLabelID";
  private static final String DIALOG_MISCELLANEOUS_GROUPBOX_ID = "MiscellaneousGroupBoxID";
  private static final String DIALOG_OK_BUTTON_ID = "OkButtonID";
  private static final String DIALOG_SURAH_LISTBOX_ID = "SurahListBoxID";
  private static final String DIALOG_TRANSLATION_CHECKBOX_ID = "TranslationCheckBoxID";
  private static final String DIALOG_TRANSLATION_FONT_GROUPBOX_ID = "TranslationFontGroupBoxID";
  private static final String DIALOG_TRANSLATION_FONT_LABEL_ID = "TranslationFontLabelID";
  private static final String DIALOG_TRANSLATION_FONT_LISTBOX_ID = "TranslationFontListBoxID";
  private static final String DIALOG_TRANSLATION_FONTSIZE_LABEL_ID = "TranslationFontSizeLabelID";
  private static final String DIALOG_TRANSLATION_FONTSIZE_NUMERICFIELD_ID =
      "TranslationFontSizeNumericFieldID";
  private static final String DIALOG_TRANSLATION_LISTBOX_ID = "TranslationListBoxID";
  private static final String DIALOG_TRANSLITERATION_CHECKBOX_ID = "TransliterationCheckBoxID";
  private static final String DIALOG_TRANSLITERATION_LISTBOX_ID = "TransliterationListBoxID";
  private static final String DIALOG_WRITE_SURAH_PROGRESSBAR = "WriteSurahProgressBar";
  private static final String LEFT_PARENTHESIS = new String(Character.toChars(0xFD3E));
  private static final String ON_ALL_AYAT_CHECKBUTTON_PRESSED = "onAllAyatCheckButtonPressed";
  private static final String ON_ARABIC_CHECKBUTTON_PRESSED = "onArabicCheckButtonPressed";
  private static final String ON_ARABIC_FONT_SELECTED = "onArabicFontSelected";
  private static final String ON_ARABIC_FONT_SIZE_TEXT_CHANGED = "onArabicFontSizeTextChanged";
  private static final String ON_ARABIC_VERSION_SELECTED = "onArabicVersionSelected";
  private static final String ON_AYAT_FROM_TEXT_CHANGED = "onAyatFromTextChanged";
  private static final String ON_AYAT_TO_TEXT_CHANGED = "onAyatToTextChanged";
  private static final String ON_LINE_BY_LINE_CHECKBUTTON_PRESSED =
      "onLineByLineCheckButtonPressed";
  private static final String ON_OK_BUTTON_PRESSED = "onOkButtonPressed";
  private static final String ON_SURAH_SELECTED = "onSurahSelected";
  private static final String ON_TRANSLATION_CHECKBUTTON_PRESSED =
      "onTranslationCheckButtonPressed";
  private static final String ON_TRANSLATION_FONT_SELECTED = "onTranslationFontSelected";
  private static final String ON_TRANSLATION_FONT_SIZE_TEXT_CHANGED =
      "onTranslationFontSizeTextChanged";
  private static final String ON_TRANSLATION_VERSION_SELECTED = "onTranslationVersionSelected";
  private static final String ON_TRANSLITERATION_CHECKBUTTON_PRESSED =
      "onTransliterationCheckButtonPressed";
  private static final String ON_TRANSLITERATION_VERSION_SELECTED =
      "onTransliterationVersionSelected";
  private static final String RIGHT_PARENTHESIS = new String(Character.toChars(0xFD3F));
  private static final String TRANSLITERATION = "Transliteration";

  /**
   * Convert boolean to short.
   *
   * @param b the boolean
   * @return the short
   */
  private static short boolean2Short(final boolean b) {
    return (short) (b ? 1 : 0);
  }

  /**
   * get the codepoint for zero in the font.
   *
   * @param fontname the fontname
   * @return base
   */
  private static int getFontNumberBase(final String fontname) {
    final Map<String, Integer> fontmap = new LinkedHashMap<>();

    fontmap.put("Al Qalam Quran Majeed", 0x06F0);
    fontmap.put("Al Qalam Quran Majeed 1", 0x06F0);
    fontmap.put("Al Qalam Quran Majeed 2", 0x06F0);
    fontmap.put("Noto Nastaliq Urdu", 0x0660);
    fontmap.put("KFGQPC Uthmanic Script HAFS", 0x0030);
    fontmap.put("me_quran", 0x0660);
    fontmap.put("Scheherazade", 0x0660);
    fontmap.put("Scheherazade quran", 0x0660);

    if (fontmap.containsKey(fontname)) {
      return fontmap.get(fontname);
    } else {
      return 0x0030;

    }
  }

  /**
   * Transforms the listbox item description of a languguage listbox into a language.
   *
   * @param item the listbox item
   * @return the language
   */
  private static String getItemLanguague(final String item) {
    final String[] itemsSelected = item.split("[(]");
    return itemsSelected[0].trim();
  }

  /**
   * Transforms the listbox item description of a languguage listbox into a text version.
   *
   * @param item the listbox item
   * @return the version
   */
  private static String getItemVersion(final String item) {
    final String[] itemsSelected = item.split("[(]");
    return itemsSelected[1].replace(")", " ").trim().replace(" ", "_");
  }

  private static short getLanguageWritingMode(final String language) {
    final Map<String, Short> directionmap = new LinkedHashMap<>();

    directionmap.put("Arabic", com.sun.star.text.WritingMode2.RL_TB);
    directionmap.put("English", com.sun.star.text.WritingMode2.LR_TB);
    directionmap.put("Dutch", com.sun.star.text.WritingMode2.LR_TB);
    directionmap.put("Indonesian", com.sun.star.text.WritingMode2.LR_TB);
    directionmap.put("Urdu", com.sun.star.text.WritingMode2.RL_TB);

    if (directionmap.containsKey(language)) {
      return directionmap.get(language);
    } else {
      return com.sun.star.text.WritingMode2.LR_TB;
    }
  }

  /**
   * Returns the string representation of a number based on language and font.
   * 
   * @param n number between 0-9
   * @param language language
   * @param fontname fontname
   * @return number string
   */
  public static String numToAyatNumber(long n, final String language, final String fontname) {
    final int base = getFontNumberBase(fontname);

    final StringBuilder as = new StringBuilder();
    while (n > 0) {
      as.append(Character.toChars(base + (int) (n % 10)));
      n = n / 10;
    }
    return as.reverse().toString();
  }

  /**
   * Convert short to boolean.
   *
   * @param s the short
   * @return the boolean
   */
  private static boolean short2Boolean(final short s) {
    return s != 0;
  }

  private final Map<String, Method> actionsMap = new LinkedHashMap<>();
  private String defaultArabicFontName;
  private double defaultArabicFontSize;
  private String defaultTranslationFontName;
  private double defaultTranslationFontSize;
  private final XComponentContext dlgContext;
  private final XDialog dlgDialog;
  private boolean selectedAllAyatInd = true;
  private String selectedArabicFontName = "";
  private double selectedArabicFontSize;
  private boolean selectedArabicInd = false;
  private String selectedArabicLanguage = "";
  private String selectedArabicVersion = "";
  private long selectedAyatFrom = 1;
  private long selectedAyatTo = 7;
  private boolean selectedLineByLineInd = true;
  private boolean selectedLineNumberInd = true;
  private int selectedSurahNo = 1;
  private String selectedTranslationFontName = "";
  private double selectedTranslationFontSize;
  private boolean selectedTranslationInd = false;
  private String selectedTranslationLanguage = "";
  private String selectedTranslationVersion = "";
  private boolean selectedTransliterationInd = false;
  private String selectedTransliterationLanguage = "";
  private String selectedTransliterationVersion = "";
  private final String[] supportedActions =
      new String[] {ON_ALL_AYAT_CHECKBUTTON_PRESSED, ON_ARABIC_CHECKBUTTON_PRESSED,
          ON_ARABIC_VERSION_SELECTED, ON_ARABIC_FONT_SELECTED, ON_ARABIC_FONT_SIZE_TEXT_CHANGED,
          ON_AYAT_FROM_TEXT_CHANGED, ON_AYAT_TO_TEXT_CHANGED, ON_LINE_BY_LINE_CHECKBUTTON_PRESSED,
          ON_TRANSLATION_FONT_SELECTED, ON_TRANSLATION_FONT_SIZE_TEXT_CHANGED, ON_OK_BUTTON_PRESSED,
          ON_SURAH_SELECTED, ON_TRANSLATION_CHECKBUTTON_PRESSED, ON_TRANSLATION_VERSION_SELECTED,
          ON_TRANSLITERATION_CHECKBUTTON_PRESSED, ON_TRANSLITERATION_VERSION_SELECTED};


  /**
   * Constructor.
   *
   * @param context the dialig context
   */
  public InsertQuranTextDialog(final XComponentContext context) {
    dlgDialog = DialogHelper.createDialog("InsertQuranTextDialog.xdl", context, this);
    dlgContext = context;
  }

  /**
   * Overrides the callHandlerMethod to handle the actions.
   */
  @Override
  public boolean callHandlerMethod(final XDialog dialog, final Object eventObject,
      final String methodName) throws WrappedTargetException {
    try {
      if (actionsMap.containsKey(methodName)) {
        actionsMap.get(methodName).invoke(this);
        return true;
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Gets a ayat for the selected Surah.
   *
   * @param surahno the surah number
   * @param ayahno the ayat number
   * @param language the language to be used
   * @param version the text version to be used
   * @param fontname the font used to write the text
   * @return the ayat
   *
   */
  private String getAyahLine(final int surahno, final long ayahno, final String language,
      final String version, String fontName) {

    final QuranReader qr = new QuranReader(language, version, dlgContext);

    String line = qr.getAyahNoOfSuraNo(surahno, ayahno);
    if (selectedLineNumberInd) {
      if (getLanguageWritingMode(language) == com.sun.star.text.WritingMode2.RL_TB) {
        line = line + " " + RIGHT_PARENTHESIS + numToAyatNumber(ayahno, language, fontName)
            + LEFT_PARENTHESIS + " ";
      } else {
        line = "(" + numToAyatNumber(ayahno, language, fontName) + ") " + line;
      }
    }
    return line;
  }

  /**
   * Gets the text Bismillah in the language from a text version.
   *
   * @param language the language to be used
   * @param version the text version to be used
   * @return the text
   */
  private String getBismillah(final String language, final String version) {
    final QuranReader qr = new QuranReader(language, version, dlgContext);
    return qr.getBismillah();
  }

  /**
   * Get the default font for Arabic.
   *
   * @return the fontsize
   */
  private String getDefaultArabicFontName() {
    return defaultArabicFontName;
  }

  /**
   * Get the default fontsize for Arabic.
   *
   * @return the fontsize
   */
  private double getDefaultArabicFontsize() {
    return defaultArabicFontSize;
  }

  /**
   * Get the default font for non Arabic.
   *
   * @return the fontsize
   */
  private String getDefaultTranslationFontName() {
    return defaultTranslationFontName;
  }

  /**
   * Get the default fontsize for the Non-Arabic.
   *
   * @return the fontsize
   */
  private double getDefaultTranslationFontSize() {
    return defaultTranslationFontSize;
  }

  /**
   * Get default settings from LibreOffice.
   */
  private void getLoDocumentDefaults() {
    final XTextDocument textDoc = DocumentHelper.getCurrentDocument(dlgContext);
    final XController controller = textDoc.getCurrentController();
    final XTextViewCursorSupplier textViewCursorSupplier =
        DocumentHelper.getCursorSupplier(controller);
    final XTextViewCursor textViewCursor = textViewCursorSupplier.getViewCursor();
    final XText text = textViewCursor.getText();
    final XTextCursor textCursor = text.createTextCursorByRange(textViewCursor.getStart());
    final XParagraphCursor paragraphCursor =
        UnoRuntime.queryInterface(XParagraphCursor.class, textCursor);
    final XPropertySet paragraphCursorPropertySet = DocumentHelper.getPropertySet(paragraphCursor);

    try {
      defaultArabicFontSize =
          (float) paragraphCursorPropertySet.getPropertyValue("CharHeightComplex");
    } catch (UnknownPropertyException | WrappedTargetException e) {
      defaultArabicFontSize = 10;
    }
    try {
      defaultArabicFontName =
          (String) paragraphCursorPropertySet.getPropertyValue("CharFontNameComplex");
    } catch (UnknownPropertyException | WrappedTargetException e) {
      defaultArabicFontName = "No Default set";
    }
    try {
      defaultTranslationFontName =
          (String) paragraphCursorPropertySet.getPropertyValue("CharFontName");
    } catch (UnknownPropertyException | WrappedTargetException e) {
      defaultTranslationFontName = "No Default set";
    }
    try {
      defaultTranslationFontSize =
          (float) paragraphCursorPropertySet.getPropertyValue("CharHeight");
    } catch (UnknownPropertyException | WrappedTargetException e) {
      defaultTranslationFontSize = 10;
    }
  }

  /**
   * Get the available Quran text files.
   *
   * @return list of files
   */
  private List<String> getQuranTxtFiles() {
    final File path = FileHelper.getFilePath("resources/quran", dlgContext);
    final List<String> fns = Arrays.asList(path.list());
    Collections.sort(fns);
    return fns;
  }

  /**
   * Override to provide the supported actions.
   */
  @Override
  public String[] getSupportedMethodNames() {
    return supportedActions;
  }

  /**
   * Handler for the All Ayat CheckButtun.
   */
  @SuppressWarnings("unused")
  private void handleAllAyatCheckButtonPressed() {
    final XCheckBox dlgAllAyatCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_ALL_AYAT_CHECKBOX_ID);
    dlgAllAyatCheckbox.setState(boolean2Short(!selectedAllAyatInd));
    selectedAllAyatInd = short2Boolean(dlgAllAyatCheckbox.getState());

    DialogHelper.enableButton(dlgDialog, DIALOG_AYAT_FROM_NUMERIC_FIELD_ID, !selectedAllAyatInd);
    DialogHelper.enableButton(dlgDialog, DIALOG_AYAT_TO_NUMERIC_FIELD_ID, !selectedAllAyatInd);

    if (selectedAllAyatInd) {
      final XNumericField dlgAyatFromNumericField =
          DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_FROM_NUMERIC_FIELD_ID);
      dlgAyatFromNumericField.setValue(1);
      selectedAyatFrom = (long) Math.round(dlgAyatFromNumericField.getValue());

      final XNumericField dlgAyatToNumericField =
          DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_TO_NUMERIC_FIELD_ID);
      dlgAyatToNumericField.setValue(QuranReader.getSurahSize(selectedSurahNo));
      selectedAyatTo = (long) Math.round(dlgAyatToNumericField.getValue());
    }
  }

  /**
   * Handler for Arabic CheckButton.
   */
  @SuppressWarnings("unused")
  private void handleArabicCheckButtonPressed() {
    final XCheckBox dlgArabicCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_ARABIC_CHECKBOX_ID);

    selectedArabicInd = short2Boolean(dlgArabicCheckbox.getState());

    DialogHelper.enableComponent(dlgDialog, DIALOG_ARABIC_LISTBOX_ID, selectedArabicInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_ARABIC_FONT_GROUPBOX_ID, selectedArabicInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_ARABIC_FONT_LABEL_ID, selectedArabicInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_ARABIC_FONT_LISTBOX_ID, selectedArabicInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_ARABIC_FONTSIZE_LABEL_ID, selectedArabicInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_ARABIC_FONTSIZE_NUMERICFIELD_ID,
        selectedArabicInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_OK_BUTTON_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_MISCELLANEOUS_GROUPBOX_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_LINE_BY_LINE_CHECKBOX_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_LINE_BY_LINE_LABEL_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
  }

  /**
   * Handler for Arabic Font ListBox.
   */
  @SuppressWarnings("unused")
  private void handleArabicFontSelected() {
    final XListBox dlgArabicFontListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_ARABIC_FONT_LISTBOX_ID);

    selectedArabicFontName = dlgArabicFontListBox.getSelectedItem();
  }

  /**
   * Handler for Arabic Fontsize NumericField.
   */
  @SuppressWarnings("unused")
  private void handleArabicFontSizeTextChanged() {
    final XNumericField dlgArabicFrontSize =
        DialogHelper.getNumericField(dlgDialog, DIALOG_ARABIC_FONTSIZE_NUMERICFIELD_ID);

    selectedArabicFontSize = dlgArabicFrontSize.getValue();
  }

  /**
   * Handler for Arabic Version ListBox.
   */
  @SuppressWarnings("unused")
  private void handleArabicVersionSelected() {
    final XListBox dlgArabicListBox = DialogHelper.getListBox(dlgDialog, DIALOG_ARABIC_LISTBOX_ID);

    selectedArabicLanguage =
        InsertQuranTextDialog.getItemLanguague(dlgArabicListBox.getSelectedItem());
    selectedArabicVersion =
        InsertQuranTextDialog.getItemVersion(dlgArabicListBox.getSelectedItem());
  }

  /**
   * Handler for Ayat From NumericField.
   */
  @SuppressWarnings("unused")
  private void handleAyatFromTextChanged() {
    final XNumericField dlgAyatFromNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_FROM_NUMERIC_FIELD_ID);

    if (Math.round(dlgAyatFromNumericField.getValue()) >= selectedAyatTo) {
      dlgAyatFromNumericField.setValue(selectedAyatTo);
    }

    selectedAyatFrom = (long) Math.round(dlgAyatFromNumericField.getValue());
  }

  /**
   * Handler for Ayat To NumericField.
   */
  @SuppressWarnings("unused")
  private void handleAyatToTextChanged() {
    final XNumericField dlgAyatToNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_TO_NUMERIC_FIELD_ID);

    if (Math.round(dlgAyatToNumericField.getValue()) <= selectedAyatFrom) {
      dlgAyatToNumericField.setValue(selectedAyatFrom);
    } else if (Math.round(dlgAyatToNumericField.getValue()) >= QuranReader
        .getSurahSize(selectedSurahNo)) {
      dlgAyatToNumericField.setValue(QuranReader.getSurahSize(selectedSurahNo));
    }

    selectedAyatTo = (long) Math.round(dlgAyatToNumericField.getValue());
  }


  /**
   * Handler for Line By Line CheckButton.
   */
  @SuppressWarnings("unused")
  private void handleLineByLineCheckButtonPressed() {
    final XCheckBox dlgLineByLineCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_LINE_BY_LINE_CHECKBOX_ID);

    selectedLineByLineInd = short2Boolean(dlgLineByLineCheckbox.getState());
  }

  /**
   * Handler for Ok Button.
   */
  @SuppressWarnings("unused")
  private void handleOkButtonPressed() {
    writeSurah(selectedSurahNo);
    dlgDialog.endExecute();
  }

  /**
   * Handler for Surah listbox.
   */
  @SuppressWarnings("unused")
  private void handleSurahSelected() {
    final XListBox dlgSurahListBox = DialogHelper.getListBox(dlgDialog, DIALOG_SURAH_LISTBOX_ID);

    selectedSurahNo = dlgSurahListBox.getSelectedItemPos() + 1;

    final XNumericField dlgAyatFromNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_FROM_NUMERIC_FIELD_ID);
    dlgAyatFromNumericField.setValue(1);
    selectedAyatFrom = (long) Math.round(dlgAyatFromNumericField.getValue());

    final XNumericField dlgAyatToNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_TO_NUMERIC_FIELD_ID);
    dlgAyatToNumericField.setValue(QuranReader.getSurahSize(selectedSurahNo));
    selectedAyatTo = (long) Math.round(dlgAyatToNumericField.getValue());
  }

  /**
   * Handler for Translation CheckButton.
   */
  @SuppressWarnings("unused")
  private void handleTranslationCheckButtonPressed() {
    final XCheckBox dlgTranslationCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_TRANSLATION_CHECKBOX_ID);

    selectedTranslationInd = short2Boolean(dlgTranslationCheckbox.getState());

    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_LISTBOX_ID, selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONT_GROUPBOX_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONT_LABEL_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONT_LISTBOX_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONTSIZE_LABEL_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONTSIZE_NUMERICFIELD_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_OK_BUTTON_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_MISCELLANEOUS_GROUPBOX_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_LINE_BY_LINE_CHECKBOX_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_LINE_BY_LINE_LABEL_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
  }

  /**
   * Handler for Non Arabic Font ListBox.
   */
  @SuppressWarnings("unused")
  private void handleTranslationFontSelected() {
    final XListBox dlgTranslationFontListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_TRANSLATION_FONT_LISTBOX_ID);

    selectedTranslationFontName = dlgTranslationFontListBox.getSelectedItem();
  }

  /**
   * Handler for Non Arabic Fontsize NumericField.
   */
  @SuppressWarnings("unused")
  private void handleTranslationFontSizeTextChanged() {
    final XNumericField dlgTranslationFrontSize =
        DialogHelper.getNumericField(dlgDialog, DIALOG_TRANSLATION_FONTSIZE_NUMERICFIELD_ID);

    selectedTranslationFontSize = dlgTranslationFrontSize.getValue();
  }

  /**
   * Handler for Translation Version ListBox.
   */
  @SuppressWarnings("unused")
  private void handleTranslationVersionSelected() {
    final XListBox dlgTranslationListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_TRANSLATION_LISTBOX_ID);

    selectedTranslationLanguage =
        InsertQuranTextDialog.getItemLanguague(dlgTranslationListBox.getSelectedItem());
    selectedTranslationVersion =
        InsertQuranTextDialog.getItemVersion(dlgTranslationListBox.getSelectedItem());
  }

  /**
   * Handler for Transliteration CheckButton.
   */
  @SuppressWarnings("unused")
  private void handleTransliterationCheckButtonPressed() {
    final XCheckBox dlgTransliterationCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_TRANSLITERATION_CHECKBOX_ID);

    selectedTransliterationInd = short2Boolean(dlgTransliterationCheckbox.getState());

    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLITERATION_LISTBOX_ID,
        selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONT_GROUPBOX_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONT_LABEL_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONT_LISTBOX_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONTSIZE_LABEL_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_TRANSLATION_FONTSIZE_NUMERICFIELD_ID,
        selectedTransliterationInd || selectedTranslationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_OK_BUTTON_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_MISCELLANEOUS_GROUPBOX_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_LINE_BY_LINE_CHECKBOX_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
    DialogHelper.enableComponent(dlgDialog, DIALOG_LINE_BY_LINE_LABEL_ID,
        selectedArabicInd || selectedTranslationInd || selectedTransliterationInd);
  }

  /**
   * Handler for Transliteration Version ListBox.
   */
  @SuppressWarnings("unused")
  private void handleTransliterationVersionSelected() {
    final XListBox dlgTransliterationListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_TRANSLITERATION_LISTBOX_ID);

    selectedTransliterationLanguage =
        InsertQuranTextDialog.getItemLanguague(dlgTransliterationListBox.getSelectedItem());
    selectedTransliterationVersion =
        InsertQuranTextDialog.getItemVersion(dlgTransliterationListBox.getSelectedItem());
  }

  /**
   * Iniializes the all ayat checkbox.
   */
  private void initializeAllAyatCheckBox() {
    final XCheckBox dlgAllAyatCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_ALL_AYAT_CHECKBOX_ID);
    dlgAllAyatCheckbox.setState(InsertQuranTextDialog.boolean2Short(true));
    selectedAllAyatInd = InsertQuranTextDialog.short2Boolean(dlgAllAyatCheckbox.getState());
  }

  /**
   * Iniializes the Arabic checkbox.
   */
  private void initializeArabicCheckBox() {
    final XCheckBox dlgArabicCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_ARABIC_CHECKBOX_ID);
    dlgArabicCheckbox.setState(InsertQuranTextDialog.boolean2Short(true));
    selectedArabicInd = InsertQuranTextDialog.short2Boolean(dlgArabicCheckbox.getState());
  }

  /**
   * Iniializes the listbox with all the fonts that support Arabic characters.
   */
  private void initializeArabicFontListBox() {
    final XListBox dlgArabicFontListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_ARABIC_FONT_LISTBOX_ID);

    final Locale locale = new Locale.Builder().setScript("ARAB").build();
    final String[] fonts =
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(locale);
    for (int i = 0; i < fonts.length; i++) {

      if (new Font(fonts[i], Font.PLAIN, 10).canDisplay(0x0627)) { // If Alif -> Arabic support
        dlgArabicFontListBox.addItem(fonts[i], (short) i);
        if (fonts[i].equals(getDefaultArabicFontName())) {
          dlgArabicFontListBox.selectItemPos((short) i, true);
        }
      }
    }
    dlgArabicFontListBox.selectItem(getDefaultArabicFontName(), true);
    selectedArabicFontName = dlgArabicFontListBox.getSelectedItem();
  }

  /**
   * Initialize the Arabic Fontsize NumericField.
   */
  private void initializeArabicFontSize() {
    final XNumericField dlgArabicFontSizeNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_ARABIC_FONTSIZE_NUMERICFIELD_ID);

    dlgArabicFontSizeNumericField.setValue(getDefaultArabicFontsize());
    selectedArabicFontSize = getDefaultArabicFontsize();
  }

  /**
   * Initialize the Arabic LineNumber CheckBox.
   */
  private void initializeArabicLineNumberCheckBox() {
    selectedLineNumberInd = true;
  }

  /**
   * Iniializes the listbox with all the Arabic versions of the Qur'an.
   */
  private void initializeArabicListBox() {
    final XListBox dlgArabicListBox = DialogHelper.getListBox(dlgDialog, DIALOG_ARABIC_LISTBOX_ID);

    final List<String> fns = getQuranTxtFiles();

    int k = 0;
    for (final String fn : fns) {
      final String[] parts = fn.split("[.]");
      if (parts[1].equals(ARABIC)) {
        dlgArabicListBox.addItem(parts[1] + " (" + parts[2].replace("_", " ") + ")", (short) k++);
      }
    }
    if (dlgArabicListBox.getItemCount() > 0) {
      dlgArabicListBox.selectItemPos((short) 0, true);
      selectedArabicLanguage =
          InsertQuranTextDialog.getItemLanguague(dlgArabicListBox.getSelectedItem());
      selectedArabicVersion =
          InsertQuranTextDialog.getItemVersion(dlgArabicListBox.getSelectedItem());
    }
  }

  /**
   * Initialize the Ayat From NumericField.
   */
  private void initializeAyatFrom() {
    final XNumericField dlgAyatFromNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_FROM_NUMERIC_FIELD_ID);

    dlgAyatFromNumericField.setValue(1);
  }

  /**
   * Initialize the Ayat To NumericField.
   */
  private void initializeAyatTo() {
    final XNumericField dlgAyatToNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_AYAT_TO_NUMERIC_FIELD_ID);

    dlgAyatToNumericField.setValue(QuranReader.getSurahSize(selectedSurahNo));
  }

  /**
   * Initializes the dialog.
   */
  private void initializeDialog() {
    getLoDocumentDefaults();

    initializeDialogActions();

    initializeSurahListBox();
    initializeAllAyatCheckBox();
    initializeAyatTo();
    initializeAyatFrom();
    initializeArabicCheckBox();
    initializeArabicListBox();
    initializeTranslationCheckBox();
    initializeTranslationListBox();
    initializeTransliterationCheckBox();
    initializeTransliterationListBox();
    initializeArabicFontListBox();
    initializeArabicFontSize();
    initializeArabicLineNumberCheckBox();
    initializeTranslationFontListBox();
    initializeTranslationFontSize();
    initializeLineByLineCheckBox();
    initializeWriteSurahProgressBar();
  }

  /**
   * Initialize the dialog actions.
   */
  private void initializeDialogActions() {
    try {
      for (final String action : supportedActions) {
        actionsMap.put(action,
            InsertQuranTextDialog.class.getDeclaredMethod("handle".concat(action.substring(2))));
      }
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
  }

  /**
   * Iniializes the Arabic checkbox.
   */
  private void initializeLineByLineCheckBox() {
    final XCheckBox dlgLineByLineCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_LINE_BY_LINE_CHECKBOX_ID);
    dlgLineByLineCheckbox.setState(InsertQuranTextDialog.boolean2Short(true));
    selectedLineByLineInd = InsertQuranTextDialog.short2Boolean(dlgLineByLineCheckbox.getState());
  }

  /**
   * Initializes the listbox with all the surah names of the Qur'an.
   */
  private void initializeSurahListBox() {
    final XListBox dlgSurahListBox = DialogHelper.getListBox(dlgDialog, DIALOG_SURAH_LISTBOX_ID);

    for (int i = 0; i < 114; i++) {
      dlgSurahListBox.addItem(QuranReader.getSurahName(i + 1) + " (" + (i + 1) + ")", (short) i);
    }
    dlgSurahListBox.selectItemPos((short) 0, true);
    selectedSurahNo = dlgSurahListBox.getSelectedItemPos() + 1;
  }

  /**
   * Iniializes the Translation checkbox.
   */
  private void initializeTranslationCheckBox() {
    final XCheckBox dlgTranslationCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_TRANSLATION_CHECKBOX_ID);
    dlgTranslationCheckbox.setState(InsertQuranTextDialog.boolean2Short(false));
    selectedTranslationInd = InsertQuranTextDialog.short2Boolean(dlgTranslationCheckbox.getState());
  }

  /**
   * Initializes the listbox with all the fonts that support Latin characters.
   */
  private void initializeTranslationFontListBox() {
    final XListBox dlgTranslationFontListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_TRANSLATION_FONT_LISTBOX_ID);

    final Locale locale = new Locale.Builder().setScript("LATN").build();
    final String[] fonts =
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(locale);
    for (int i = 0; i < fonts.length; i++) {
      dlgTranslationFontListBox.addItem(fonts[i], (short) i);
      if (fonts[i].equals(getDefaultTranslationFontName())) {
        dlgTranslationFontListBox.selectItemPos((short) i, true);
      }
    }
    dlgTranslationFontListBox.selectItem(getDefaultTranslationFontName(), true);
    selectedTranslationFontName = dlgTranslationFontListBox.getSelectedItem();
  }

  /**
   * Initialize the Non Arabic Fontsize NumericField.
   */
  private void initializeTranslationFontSize() {
    final XNumericField dlgTranslationFontSizeNumericField =
        DialogHelper.getNumericField(dlgDialog, DIALOG_TRANSLATION_FONTSIZE_NUMERICFIELD_ID);

    dlgTranslationFontSizeNumericField.setValue(getDefaultTranslationFontSize());
    selectedTranslationFontSize = getDefaultTranslationFontSize();
  }

  /**
   * Iniializes the listbox with all the Translation versions of the Qur'an.
   */
  private void initializeTranslationListBox() {
    final XListBox dlgTranslationListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_TRANSLATION_LISTBOX_ID);

    final List<String> fns = getQuranTxtFiles();

    int k = 0;
    for (final String fn : fns) {
      final String[] parts = fn.split("[.]");
      if (!parts[1].equals(ARABIC)) {
        dlgTranslationListBox.addItem(parts[1] + " (" + parts[2].replace("_", " ") + ")",
            (short) k++);
      }
    }
    if (dlgTranslationListBox.getItemCount() > 0) {
      dlgTranslationListBox.selectItemPos((short) 0, true);
      selectedTranslationLanguage =
          InsertQuranTextDialog.getItemLanguague(dlgTranslationListBox.getSelectedItem());
      selectedTranslationVersion =
          InsertQuranTextDialog.getItemVersion(dlgTranslationListBox.getSelectedItem());
    }
  }

  /**
   * Iniializes the transliteration checkbox.
   */
  private void initializeTransliterationCheckBox() {
    final XCheckBox dlgTransliterationCheckbox =
        DialogHelper.getCheckBox(dlgDialog, DIALOG_TRANSLITERATION_CHECKBOX_ID);
    dlgTransliterationCheckbox.setState(InsertQuranTextDialog.boolean2Short(false));
    selectedTransliterationInd =
        InsertQuranTextDialog.short2Boolean(dlgTransliterationCheckbox.getState());
  }

  /**
   * Iniializes the listbox with all the Transliteration versions of the Qur'an.
   */
  private void initializeTransliterationListBox() {
    final XListBox dlgTransliterationListBox =
        DialogHelper.getListBox(dlgDialog, DIALOG_TRANSLITERATION_LISTBOX_ID);

    final List<String> fns = getQuranTxtFiles();

    int k = 0;
    for (final String fn : fns) {
      final String[] parts = fn.split("[.]");
      if (parts[1].equals(TRANSLITERATION)) {
        dlgTransliterationListBox.addItem(parts[1] + " (" + parts[2].replace("_", " ") + ")",
            (short) k++);
      }
    }
    if (dlgTransliterationListBox.getItemCount() > 0) {
      dlgTransliterationListBox.selectItemPos((short) 0, true);
      selectedTransliterationLanguage =
          InsertQuranTextDialog.getItemLanguague(dlgTransliterationListBox.getSelectedItem());
      selectedTransliterationVersion =
          InsertQuranTextDialog.getItemVersion(dlgTransliterationListBox.getSelectedItem());
    }
  }

  /**
   * Initialize the ProgressBar.
   */
  private void initializeWriteSurahProgressBar() {
    final XProgressBar dlgWriteSurahProgressBar =
        DialogHelper.getProgressBar(dlgDialog, DIALOG_WRITE_SURAH_PROGRESSBAR);

    dlgWriteSurahProgressBar.setRange(0, 100);
    dlgWriteSurahProgressBar.setValue(0);
  }

  /**
   * Show the dialog.
   */
  public void show() {
    initializeDialog();
    dlgDialog.execute();
  }

  /**
   * Write Bismillah lines.
   *
   * @param text the text
   * @param paragraphCursor the paragraph
   */
  private void writeBismillahSurahLineByLIne(final XText text,
      final XParagraphCursor paragraphCursor) {
    try {
      if (selectedArabicInd) {
        writeParagraph(text, paragraphCursor,
            getBismillah(selectedArabicLanguage, selectedArabicVersion), selectedArabicLanguage,
            selectedArabicFontName, selectedArabicFontSize);
      }
      if (selectedTranslationInd) {
        writeParagraph(text, paragraphCursor,
            getBismillah(selectedTranslationLanguage, selectedTranslationVersion),
            selectedTranslationLanguage, selectedTranslationFontName, selectedTranslationFontSize);
      }
      if (selectedTransliterationInd) {
        writeParagraph(text, paragraphCursor,
            getBismillah(selectedTransliterationLanguage, selectedTransliterationVersion),
            selectedTranslationLanguage, selectedTranslationFontName, selectedTranslationFontSize);
      }
    } catch (com.sun.star.lang.IllegalArgumentException | UnknownPropertyException
        | PropertyVetoException | WrappedTargetException e) {
      e.printStackTrace();
    }
  }

  private void writeParagraph(final XText text, final XParagraphCursor paragraphCursor,
      final String paragraph, final String language, final String fontName, final double fontSize)
      throws UnknownPropertyException, PropertyVetoException, WrappedTargetException {

    paragraphCursor.gotoEndOfParagraph(false);
    text.insertControlCharacter(paragraphCursor, ControlCharacter.PARAGRAPH_BREAK, false);

    final XPropertySet paragraphCursorPropertySet = DocumentHelper.getPropertySet(paragraphCursor);

    if (getLanguageWritingMode(language) == com.sun.star.text.WritingMode2.LR_TB) {
      paragraphCursorPropertySet.setPropertyValue("ParaAdjust",
          com.sun.star.style.ParagraphAdjust.LEFT);
      paragraphCursorPropertySet.setPropertyValue("WritingMode",
          com.sun.star.text.WritingMode2.LR_TB);
      paragraphCursorPropertySet.setPropertyValue("CharFontName", fontName);
      paragraphCursorPropertySet.setPropertyValue("CharHeight", fontSize);
    } else {
      paragraphCursorPropertySet.setPropertyValue("ParaAdjust",
          com.sun.star.style.ParagraphAdjust.RIGHT);
      paragraphCursorPropertySet.setPropertyValue("WritingMode",
          com.sun.star.text.WritingMode2.RL_TB);
      paragraphCursorPropertySet.setPropertyValue("CharFontNameComplex", fontName);
      paragraphCursorPropertySet.setPropertyValue("CharHeightComplex", fontSize);
    }
    text.insertString(paragraphCursor, paragraph, false);
  }

  /**
   * Write the selected surah text.
   *
   * @param surahNumber the surah number
   */
  public void writeSurah(final int surahNumber) {
    final XTextDocument textDoc = DocumentHelper.getCurrentDocument(dlgContext);
    final XController controller = textDoc.getCurrentController();
    final XTextViewCursorSupplier textViewCursorSupplier =
        DocumentHelper.getCursorSupplier(controller);
    final XTextViewCursor textViewCursor = textViewCursorSupplier.getViewCursor();
    final XText text = textViewCursor.getText();
    final XTextCursor textCursor = text.createTextCursorByRange(textViewCursor.getStart());
    final XParagraphCursor paragraphCursor =
        UnoRuntime.queryInterface(XParagraphCursor.class, textCursor);
    final XPropertySet paragraphCursorPropertySet = DocumentHelper.getPropertySet(paragraphCursor);

    try {
      paragraphCursorPropertySet.setPropertyValue("CharFontName", selectedTranslationFontName);
      paragraphCursorPropertySet.setPropertyValue("CharFontNameComplex", selectedArabicFontName);
      paragraphCursorPropertySet.setPropertyValue("CharHeight", selectedTranslationFontSize);
      paragraphCursorPropertySet.setPropertyValue("CharHeightComplex", selectedArabicFontSize);

      final long from = (selectedAllAyatInd) ? 1 : selectedAyatFrom;
      final long to =
          (selectedAllAyatInd) ? QuranReader.getSurahSize(surahNumber) + 1 : selectedAyatTo + 1;

      if (selectedLineByLineInd) {
        writeSurahLineByLine(surahNumber, text, paragraphCursor, from, to);
      } else {
        writeSurahAsOneBlock(surahNumber, text, paragraphCursor, from, to);
      }
 
      paragraphCursorPropertySet.setPropertyValue("CharFontName", selectedTranslationFontName);
      paragraphCursorPropertySet.setPropertyValue("CharFontNameComplex", selectedArabicFontName);
      paragraphCursorPropertySet.setPropertyValue("CharHeight", selectedTranslationFontSize);
      paragraphCursorPropertySet.setPropertyValue("CharHeightComplex", selectedArabicFontSize);
   
    } catch (com.sun.star.lang.IllegalArgumentException | UnknownPropertyException
        | PropertyVetoException | WrappedTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write the selected Surah as one block.
   *
   * @param surahNumber the surah number
   * @param text the text
   * @param paragraphCursor the paragraph
   * @param from start ayat
   * @param to last ayat
   */
  private void writeSurahAsOneBlock(final int surahNumber, final XText text,
      final XParagraphCursor paragraphCursor, final long from, final long to) {
    final XProgressBar dlgWriteSurahProgressBar =
        DialogHelper.getProgressBar(dlgDialog, DIALOG_WRITE_SURAH_PROGRESSBAR);
    if (selectedArabicInd) {
      writeSurahTextBlock(surahNumber, text, paragraphCursor, from, to, selectedArabicLanguage,
          selectedArabicVersion, selectedArabicFontName, selectedArabicFontSize,
          dlgWriteSurahProgressBar);
    }
    if (selectedTranslationInd) {
      writeSurahTextBlock(surahNumber, text, paragraphCursor, from, to, selectedTranslationLanguage,
          selectedTranslationVersion, selectedTranslationFontName, selectedTranslationFontSize,
          dlgWriteSurahProgressBar);
    }
    if (selectedTransliterationInd) {
      writeSurahTextBlock(surahNumber, text, paragraphCursor, from, to,
          selectedTransliterationLanguage, selectedTransliterationVersion,
          selectedTranslationFontName, selectedArabicFontSize, dlgWriteSurahProgressBar);
    }
  }

  /**
   * Write the selected Surah Line By Line.
   *
   * @param surahNumber the surah number
   * @param text the text
   * @param paragraphCursor the paragraph of the text
   * @param from start ayat
   * @param to last ayat
   */
  private void writeSurahLineByLine(final int surahNumber, final XText text,
      final XParagraphCursor paragraphCursor, final long from, final long to) {
    try {

      final XProgressBar dlgWriteSurahProgressBar =
          DialogHelper.getProgressBar(dlgDialog, DIALOG_WRITE_SURAH_PROGRESSBAR);
      if ((from == 1) && (surahNumber != 1 && surahNumber != 9)) {
        writeBismillahSurahLineByLIne(text, paragraphCursor);
      }

      for (long l = from; l < to; l++) {
        dlgWriteSurahProgressBar.setValue((int) (100 * l / (to - from + 1)));
        if (selectedArabicInd) {
          writeParagraph(text, paragraphCursor,
              getAyahLine(surahNumber, l, selectedArabicLanguage, selectedArabicVersion,
                  selectedArabicFontName),
              selectedArabicLanguage, selectedArabicFontName, selectedArabicFontSize);
        }
        if (selectedTranslationInd) {
          writeParagraph(text, paragraphCursor,
              getAyahLine(surahNumber, l, selectedTranslationLanguage, selectedTranslationVersion,
                  selectedTranslationFontName),
              selectedTranslationLanguage, selectedTranslationFontName,
              selectedTranslationFontSize);
        }
        if (selectedTransliterationInd) {
          writeParagraph(text, paragraphCursor,
              getAyahLine(surahNumber, l, selectedTransliterationLanguage,
                  selectedTransliterationVersion, selectedTranslationFontName),
              selectedTransliterationLanguage, selectedTranslationFontName,
              selectedTranslationFontSize);
        }
      }
    } catch (com.sun.star.lang.IllegalArgumentException | UnknownPropertyException
        | PropertyVetoException | WrappedTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write the Text block.
   *
   * @param surahNumber the surah number
   * @param text the text
   * @param paragraphCursor the paragraph
   * @param from start ayat
   * @param to last ayat
   * @param language the language of the text
   * @param languageVersion the text version for the language
   * @param dlgWriteSurahProgressBar the progressBar
   */
  private void writeSurahTextBlock(final int surahNumber, final XText text,
      final XParagraphCursor paragraphCursor, final long from, final long to, final String language,
      final String languageVersion, final String fontName, final double fontSize,
      final XProgressBar dlgWriteSurahProgressBar) {
    try {
      final StringBuilder lb = new StringBuilder();
      for (long l = from; l < to; l++) {
        if ((l == 1) && (surahNumber != 1 && surahNumber != 9)) {
          lb.append(getBismillah(language, languageVersion));
          lb.append("\n");
        }
        dlgWriteSurahProgressBar.setValue((int) (100 * l / (to - from + 1)));
        lb.append(getAyahLine(surahNumber, l, language, languageVersion, fontName));
        lb.append(" ");
      }
      writeParagraph(text, paragraphCursor, lb.toString() + "\n", language, fontName, fontSize);
    } catch (com.sun.star.lang.IllegalArgumentException | UnknownPropertyException
        | PropertyVetoException | WrappedTargetException e) {
      e.printStackTrace();
    }
  }


}

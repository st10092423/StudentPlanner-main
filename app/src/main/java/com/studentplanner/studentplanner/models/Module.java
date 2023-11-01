package com.studentplanner.studentplanner.models;

import android.content.ContentValues;
import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.interfaces.Searchable;
import com.studentplanner.studentplanner.tables.ModuleTable;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class Module implements Searchable {
    private int moduleID;
    private String moduleCode;
    private String moduleName;
    private TextInputLayout txtModuleCode;
    private TextInputLayout txtModuleName;


    public Module(TextInputLayout txtModuleCode, TextInputLayout txtModuleName) {
        this.txtModuleCode = txtModuleCode;
        this.txtModuleName = txtModuleName;
    }

    public TextInputLayout getTxtModuleCode() {
        return txtModuleCode;
    }

    public void setTxtModuleCode(TextInputLayout txtModuleCode) {
        this.txtModuleCode = txtModuleCode;
    }

    public void setTxtModuleName(TextInputLayout txtModuleName) {
        this.txtModuleName = txtModuleName;
    }

    public TextInputLayout getTxtModuleName() {
        return txtModuleName;
    }


    public int getModuleID() {
        return moduleID;
    }

    public Module(String moduleCode, String moduleName) {
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
    }

    public Module(int moduleID, String moduleCode, String moduleName) {
        this.moduleID = moduleID;
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public static List<String> populateDropdown(List<Module> list) {
        return list.stream().map(Module::getModuleDetails).toList();
    }

    public String getModuleDetails() {
        return String.format(Locale.ENGLISH, "%s %s", moduleCode.toUpperCase(), WordUtils.capitalizeFully(moduleName));
    }

    private static List<Module> defaultModules() {
        List<Module> moduleList = new ArrayList<>();


        return moduleList;
    }

    public static void addDefaultModules(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        defaultModules().forEach(m -> db.addModule(new Module(m.moduleCode, m.getModuleName())));
    }


    public static List<Module> sortList(List<Module> list) {
        return !list.isEmpty() ? list.stream().sorted(Comparator.comparing(module -> module.getModuleName().toLowerCase())).toList() : list;
    }

    @Override
    public String searchText() {
        return getModuleDetails();
    }

    public static ContentValues contentValues(Module module) {
        ContentValues cv = new ContentValues();
        cv.put(ModuleTable.COLUMN_MODULE_C0DE, module.getModuleCode());
        cv.put(ModuleTable.COLUMN_MODULE_NAME, module.getModuleName());
        return cv;
    }


    @Override
    public String toString() {
        return "Module{" +
                "moduleCode='" + moduleCode + '\'' +
                ", moduleName='" + moduleName + '\'' +
                '}';
    }
}

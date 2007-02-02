// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.rowgenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.SystemException;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTable;
import org.talend.core.model.process.AbstractExternalNode;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.Problem;
import org.talend.core.model.temp.ECodePart;
import org.talend.designer.codegen.ICodeGeneratorService;
import org.talend.designer.rowgenerator.external.data.ExternalRowGeneratorData;
import org.talend.designer.rowgenerator.shadow.RowGenPreivewCodeMain;
import org.talend.designer.rowgenerator.utils.problems.ProblemsAnalyser;

/**
 * This is the external component of rowGenerator component. <br/>
 * 
 * $Id: RowGeneratorComponent.java,v 1.12 2007/02/02 03:34:13 pub Exp $
 * 
 */
public class RowGeneratorComponent extends AbstractExternalNode {

    public static final String COLUMN_NAME = "SCHEMA_COLUMN";

    public static final String ARRAY = "ARRAY";

    private RowGenMain rowGeneratorMain;

    private List<IMetadataTable> metadataListOut;

    private ExternalRowGeneratorData externalData;

    private RowGenPreivewCodeMain codeGenMain;

    private String number;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.AbstractExternalNode#initialize()
     */
    public void initialize() {
        initRowGeneratorMain();
        rowGeneratorMain.loadInitialParamters();
        codeGenMain = new RowGenPreivewCodeMain(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IExternalComponent#getPersistentData()
     */
    public Object getExternalData() {
        return null;
    }

    private void initRowGeneratorMain() {
        rowGeneratorMain = new RowGenMain(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IExternalComponent#open(org.eclipse.swt.widgets.Display)
     */
    public int open(final Display display) {
        initRowGeneratorMain();
        Shell shell = rowGeneratorMain.createUI(display);
        while (!shell.isDisposed()) {
            try {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            } catch (Throwable e) {
                if (RowGenMain.isStandAloneMode()) {
                    e.printStackTrace();
                } else {
                    ExceptionHandler.process(e);
                }
            }
        }
        if (RowGenMain.isStandAloneMode()) {
            display.dispose();
        }
        rowGeneratorMain.loadModelFromInternalData();
        return rowGeneratorMain.getMapperDialogResponse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IExternalComponent#open()
     */
    public int open(final Composite parent) {
        return open(parent.getDisplay());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IExternalComponent#setPersistentData(java.lang.Object)
     */
    public void setExternalData(Object externalData) {
        // this.externalData = (ExternalRowGeneratorData) externalData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.INode#getGeneratedCode()
     */
    public String getGeneratedCode() {
        try {
            ICodeGeneratorService service = RowGeneratorPlugin.getDefault().getCodeGeneratorService();
            return service.createCodeGenerator().generateComponentCode(this, ECodePart.MAIN);
        } catch (SystemException e) {
            ExceptionHandler.process(e);
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.INode#getMetadataList()
     */
    public List<IMetadataTable> getMetadataList() {
        return this.metadataListOut;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.INode#setMetadataList(java.util.List)
     */
    public void setMetadataList(List<IMetadataTable> metadataTablesOut) {
        this.metadataListOut = metadataTablesOut;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.AbstractExternalNode#setExternalXmlData(java.io.InputStream)
     */
    public void loadDataIn(InputStream in, Reader stringReader) throws IOException, ClassNotFoundException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.AbstractExternalNode#getProblems()
     */
    @Override
    public List<Problem> getProblems() {
        initRowGeneratorMain();
        ProblemsAnalyser problemsAnalyser = new ProblemsAnalyser(rowGeneratorMain.getMapperManager());
        return problemsAnalyser.checkProblems(externalData);
    }

    public RowGenMain getRowGeneratorMain() {
        return this.rowGeneratorMain;
    }

    public RowGenPreivewCodeMain getCodeGenMain() {
        if (codeGenMain == null) {
            this.codeGenMain = new RowGenPreivewCodeMain(this);
        }
        return this.codeGenMain;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.AbstractExternalNode#renameMetadataColumnName(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    protected void renameMetadataColumnName(String conectionName, String oldColumnName, String newColumnName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IExternalNode#loadDataOut(java.io.OutputStream, java.io.Writer)
     */
    public void loadDataOut(OutputStream out, Writer writer) throws IOException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IExternalNode#renameInputConnection(java.lang.String, java.lang.String)
     */
    public void renameInputConnection(String oldName, String newName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IExternalNode#renameOutputConnection(java.lang.String, java.lang.String)
     */
    public void renameOutputConnection(String oldName, String newName) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.AbstractNode#isMultipleMethods()
     */
    @Override
    public Boolean isMultipleMethods() {
        return new Boolean(true);
    }

    @SuppressWarnings("unchecked")
    public String getNumber() {
        boolean end = false;
        List<IElementParameter> eps = (List<IElementParameter>) this.getElementParameters();
        for (int i = 0; i < eps.size() && !end; i++) {
            IElementParameter parameter = eps.get(i);
            if ("__NB_ROWS__".indexOf(parameter.getVariableName()) != -1) {
                this.number = (String) parameter.getValue();
                end = true;
            }
        }
        return this.number;
    }

    @SuppressWarnings("unchecked")
    public void setNumber(String number) {
        this.number = number;
        List<IElementParameter> eps = (List<IElementParameter>) this.getElementParameters();
        boolean end = false;
        for (int i = 0; i < eps.size() && !end; i++) {
            IElementParameter parameter = eps.get(i);
            if ("__NB_ROWS__".indexOf(parameter.getVariableName()) != -1) {
                parameter.setValue(this.number);
                end = true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMapList() {
        List<Map<String, Object>> map = null;
        List<IElementParameter> eps = (List<IElementParameter>) this.getElementParameters();
        boolean end = false;
        for (int i = 0; i < eps.size() && !end; i++) {
            IElementParameter parameter = eps.get(i);
            if (parameter.getField() == EParameterFieldType.TABLE) {
                map = (List<Map<String, Object>>) parameter.getValue();
                end = true;
            }
        }
        return map;
    }

    public void setColumnValue(String columnName, String value) {
        List<Map<String, Object>> map = getMapList();
        boolean isAdd = true;
        for (int i = 0; i < map.size(); i++) {
            Map<String, Object> line = map.get(i);
            if (line.get(COLUMN_NAME).equals(columnName)) {
                line.put(ARRAY, value);
                isAdd = false;
            }
        }
        if (isAdd) {
            Map<String, Object> l = new HashMap<String, Object>();
            l.put(COLUMN_NAME, columnName);
            l.put(ARRAY, value);
            map.add(l);
        }
        reinitColumnValues(map);
    }

    public void reinitColumnValues(List<Map<String, Object>> map) {
        MetadataTable tableExt = (MetadataTable) this.getMetadataList().get(0);
        List<String> columnNames = new ArrayList<String>();
        for (IMetadataColumn column : tableExt.getListColumns()) {
            columnNames.add(column.getLabel());
        }
        if (map.size() > tableExt.getListColumns().size()) {
            for (int i = 0; i < map.size(); i++) {
                Map<String, Object> line = map.get(i);
                if (!columnNames.contains(line.get(COLUMN_NAME))) {
                    map.remove(line);
                }
            }
        }
    }

    public String getColumnValue(IMetadataColumn ext) {
        String arrayValue = "";
        List<Map<String, Object>> map = getMapList();
        for (int i = 0; i < map.size(); i++) {
            Map<String, Object> line = map.get(i);
            if (line.get(COLUMN_NAME).equals(ext.getLabel())) {
                arrayValue = (String) line.get(ARRAY);
            }
        }
        return arrayValue;
    }

    @SuppressWarnings("unchecked")
    public void setTableElementParameter(List<Map<String, Object>> epsl) {
        List<IElementParameter> eps = (List<IElementParameter>) this.getElementParameters();
        boolean end = false;
        for (int i = 0; i < eps.size() && !end; i++) {
            IElementParameter parameter = eps.get(i);
            if (parameter.getField() == EParameterFieldType.TABLE) {
                List<Map<String, Object>> tableValues = epsl;
                ArrayList newValues = new ArrayList();
                for (Map<String, Object> map : tableValues) {
                    Map<String, Object> newMap = new HashMap<String, Object>();
                    newMap.putAll(map);
                    newValues.add(newMap);
                }
                parameter.setValue(newValues);
            }
        }
    }

}

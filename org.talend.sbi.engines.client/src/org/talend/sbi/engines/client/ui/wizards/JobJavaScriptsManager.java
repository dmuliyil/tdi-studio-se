// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.sbi.engines.client.ui.wizards;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ProcessItem;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 */
public class JobJavaScriptsManager extends org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobJavaScriptsManager {

    @Override
    protected List<URL> getLauncher(boolean needLauncher, boolean setParameterValues, boolean needContext, IProcess jobProcess,
            ProcessItem process, String contextName, String environment, int statisticPort, int tracePort, String... codeOptions) {
        String tmpFold = getTmpFolder();
        File fileTemp = new File(tmpFold);
        if (!fileTemp.exists()) {
            fileTemp.mkdir();
        }
        List<URL> toReturn = super.getLauncher(needLauncher, setParameterValues, needContext, jobProcess, process, contextName,
                environment, statisticPort, tracePort, codeOptions);
        // File spagobi = new File(tmpFold + "/" + "spagobi.xml");
        try {
            toReturn.add(new GenerateSpagoBIXML(fileTemp, process, contextName).getResult());
        } catch (Exception e) {
            // e.printStackTrace();
            ExceptionHandler.process(e);
        }
        return toReturn;
    }

}

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
package org.talend.designer.dbmap.mysql;

import org.talend.designer.dbmap.DbMapComponent;
import org.talend.designer.dbmap.language.generation.DbGenerationManager;
import org.talend.designer.dbmap.mysql.language.MysqlGenerationManager;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id: MapperComponent.java 1782 2007-02-03 07:57:38Z bqian $
 * 
 */
public class MysqlMapperComponent extends DbMapComponent {

    DbGenerationManager generationManager = new MysqlGenerationManager();

    /**
     * DOC amaumont OracleMapperComponent constructor comment.
     */
    public MysqlMapperComponent() {
        super();
    }

    /**
     * Getter for generationManager.
     * 
     * @return the generationManager
     */
    public DbGenerationManager getGenerationManager() {
        return this.generationManager;
    }

}

// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.dbmap.utils;

import java.util.HashSet;
import java.util.Set;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.talend.commons.utils.data.text.StringHelper;
import org.talend.designer.dbmap.language.IDbLanguage;
import org.talend.designer.dbmap.model.tableentry.TableEntryLocation;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id: DataMapExpressionParser.java 1782 2007-02-03 07:57:38Z bqian $
 * 
 */
public class DataMapExpressionParser {

    // private String expression;
    private Perl5Matcher matcher = new Perl5Matcher();

    private Perl5Compiler compiler = new Perl5Compiler();

    private Set<TableEntryLocation> resultList = new HashSet<TableEntryLocation>();

    private Pattern pattern;

    private PatternMatcherInput patternMatcherInput;

    private String locationPattern;

    private IDbLanguage language;

    public DataMapExpressionParser(IDbLanguage language) {
        super();
        this.language = language;
        setLocationPattern(language.getLocationPattern());
    }

    /**
     * DOC amaumont Comment method "setRegexpPattern".
     * 
     * @param regexpPattern
     */
    private void setLocationPattern(String locationPattern) {
        this.locationPattern = locationPattern;
    }

    public TableEntryLocation[] parseTableEntryLocations(String expression) {
        resultList.clear();
        if (expression != null) {
            matcher.setMultiline(true);

            String[] expressSplit = expression.split("  ");//$NON-NLS-N$
            for (int i = 0; i < expressSplit.length; i++) {
                String str = expressSplit[i];
                String[] split = str.split("\\.");//$NON-NLS-N$
                int length = split.length;

                if (patternMatcherInput == null) {
                    patternMatcherInput = new PatternMatcherInput(str);
                } else {
                    patternMatcherInput.setInput(str);
                }

                if (length == 2) {
                    // for table name without schema
                    pattern = recompilePatternIfNecessary(locationPattern);

                    while (matcher.contains(patternMatcherInput, pattern)) {
                        MatchResult matchResult = matcher.getMatch();
                        resultList.add(new TableEntryLocation(matchResult.group(1), matchResult.group(2)));
                    }
                } else {
                    // for table name with schema
                    String patternStr = "\\s*(\\w+)\\s*";//$NON-NLS-N$
                    pattern = recompilePatternIfNecessary(patternStr + "." + patternStr + "." + patternStr);//$NON-NLS-N$ //$NON-NLS-N$
                    while (matcher.contains(patternMatcherInput, pattern)) {
                        MatchResult matchResult = matcher.getMatch();
                        resultList.add(new TableEntryLocation(matchResult.group(1) + "." + matchResult.group(2), matchResult //$NON-NLS-N$
                                .group(3)));
                    }
                }

            }

        }
        return resultList.toArray(new TableEntryLocation[0]);
    }

    private Pattern recompilePatternIfNecessary(String regexpPattern) {
        if (pattern == null || !regexpPattern.equals(pattern.getPattern())) {
            try {
                pattern = compiler.compile(regexpPattern);
            } catch (MalformedPatternException e) {
                throw new RuntimeException(e);
            }
        }
        return pattern;
    }

    public String addTablePrefixToColumnName(String expression, TableEntryLocation[] locations) {
        String returnedExpression = expression;
        for (TableEntryLocation location : locations) {
            recompilePatternIfNecessary(StringHelper.replacePrms(language.getSubstPatternForPrefixColumnName(), new Object[] {
                    location.tableName, location.columnName }));
            if (returnedExpression != null) {
                matcher.setMultiline(true);
                Perl5Substitution substitution = new Perl5Substitution(
                        language.getPrefixTableRegexp() + "$1" //$NON-NLS-1$
                                + language.getPrefixFieldRegexp() + "$1__$2" + language.getSuffixFieldRegexp(), Perl5Substitution.INTERPOLATE_ALL); //$NON-NLS-1$
                returnedExpression = Util.substitute(matcher, pattern, substitution, returnedExpression, Util.SUBSTITUTE_ALL);
            }
        }
        return returnedExpression;
    }

    public String replaceLocation(String expression, TableEntryLocation oldLocation, TableEntryLocation newLocation) {
        String returnedExpression = expression;
        String tempPattern = StringHelper.replacePrms(language.getSubstPatternForReplaceLocation(), new Object[] {
                oldLocation.tableName, oldLocation.columnName });
        recompilePatternIfNecessary(tempPattern);
        if (returnedExpression != null) {
            matcher.setMultiline(true);
            Perl5Substitution substitution = new Perl5Substitution(language.getPrefixTableRegexp()
                    + "$1" + newLocation.tableName + "$2" //$NON-NLS-1$ //$NON-NLS-2$
                    + language.getSuffixTableRegexp()
                    + "$3" + language.getPrefixFieldRegexp() + "$4" + newLocation.columnName + "$5" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + language.getSuffixFieldRegexp(), Perl5Substitution.INTERPOLATE_ALL);
            returnedExpression = Util.substitute(matcher, pattern, substitution, returnedExpression, Util.SUBSTITUTE_ALL);
        }
        return returnedExpression;
    }

}
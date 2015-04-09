package org.talend.designer.webservice.ws.wsdlinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.xml.schema.Schema;
import org.talend.designer.webservice.ws.wsdlutil.ServiceHelperConfiguration;

/**
 * 
 * @author gcui
 */
public class ServiceInfo {

    /** server name */
    private String serverName;

    private String serverNameSpace;

    private List<PortNames> portNames;

    /** WSDL URI */
    private String wsdluri;//

    private String endpoint;

    private String targetnamespace;

    private Schema wsdlType;

    private ServiceHelperConfiguration authConfig;

    /** The list of operations that this service defines. */
    List<OperationInfo> operations = new ArrayList<OperationInfo>();

    public Schema getWsdlType() {
        return wsdlType;
    }

    public void setWsdlTypes(Schema wsdlType) {
        this.wsdlType = wsdlType;
    }

    public List<OperationInfo> getOperation() {
        return operations;
    }

    public Iterator<OperationInfo> getOperations() {
        return operations.iterator();
    }

    public void addOperation(OperationInfo operation) {
        operations.add(operation);
    }

    @Override
    public String toString() {
        return getServerName();
    }

    public String getTargetnamespace() {
        return targetnamespace;
    }

    public void setTargetnamespace(String targetnamespace) {
        this.targetnamespace = targetnamespace;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getWsdlUri() {
        return wsdluri;
    }

    public void setWsdlUri(String wsdluri) {
        this.wsdluri = wsdluri;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String name) {
        this.serverName = name;
    }

    public String getServerNameSpace() {
        return this.serverNameSpace;
    }

    public void setServerNameSpace(String serverNameSpace) {
        this.serverNameSpace = serverNameSpace;
    }

    public List<PortNames> getPortNames() {
        return this.portNames;
    }

    public void setPortNames(List<PortNames> portNames) {
        this.portNames = portNames;
    }

    public ServiceHelperConfiguration getAuthConfig() {
        return this.authConfig;
    }

    public void setAuthConfig(ServiceHelperConfiguration authConfig) {
        this.authConfig = authConfig;
    }

}
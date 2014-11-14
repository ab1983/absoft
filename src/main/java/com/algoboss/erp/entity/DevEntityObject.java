/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algoboss.erp.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.*;

import com.algoboss.erp.face.GerLoginBean;

/**
 *
 * @author Agnaldo
 */
@NamedQueries({
    @NamedQuery(name = "findAllDevEntityObject",
            query = "select u from DevEntityObject u")
})
@Entity
@Table(name = "dev_entity_object")
public class DevEntityObject extends GenericEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "DevEntityObject", sequenceName = "sequence_dev_entity_object", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DevEntityObject")
    @OrderBy
    @Column(name = "entity_object_id")
    private Long entityObjectId;
    @Temporal(TemporalType.DATE)
    @Column(name = "registration_date")
    private Date registrationDate = new Date();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "entity_object_id")
    @OrderBy
    private List<DevEntityPropertyValue> entityPropertyValueList = new ArrayList();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_class_id")
    private DevEntityClass entityClass;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="parent_id")    
    private DevEntityPropertyValue propertyParent;    
    @OneToOne(fetch=FetchType.LAZY)   
    @JoinColumn(name="user_id")    
    protected SecUser user;
    @Transient
    private Map<String,DevEntityPropertyValue> entityPropertyValueMap = new HashMap<String,DevEntityPropertyValue>();

    public DevEntityObject() {
    }
    public DevEntityObject(DevEntityClass entityClass) {
        if (entityClass != null) {
            this.entityClass = entityClass;
            loadProperties(this, entityClass.getEntityPropertyDescriptorList());
        }

    }
    public DevEntityObject(DevEntityObject obj) {
        if (obj != null) {
            //this.entityObjectId = obj.entityObjectId;
            //this.registrationDate = obj.registrationDate;
            this.entityClass = obj.entityClass;
            for (DevEntityPropertyValue devEntityPropertyValue : obj.entityPropertyValueList) {
                this.entityPropertyValueList.add(new DevEntityPropertyValue(devEntityPropertyValue));
            }
        }
    }
    public boolean isEmpty(){
    	boolean hasValue = false;
        for (DevEntityPropertyValue propVal : this.getEntityPropertyValueList()) {
        	Object val = propVal.getVal();   
			if(val instanceof List){
				hasValue = !((List)val).isEmpty();   
			}else{
				hasValue = val!=null;    							    							
			}        		
        }
        return hasValue;
    }
    public DevEntityPropertyValue getPropObj(String propName) {	
        return $(this.getEntityClass().getName()+"."+propName);
    }
    
    public Object getProp(String propName) {
    	DevEntityPropertyValue propObj = getPropObj(propName);
    	if(propObj==null){
    		throw new IllegalArgumentException("Property "+propName+" not found in "+this.getEntityClass().getName()+".");
    	}
        return getPropObj(propName).getVal();
    }

    public String $date(String attr, String format) {
    	 DevEntityObject entObj = this;
        String dtFmt = "";
        try {
            Date date = (Date) entObj.$(attr).getVal();
            dtFmt = new SimpleDateFormat(format).format(date);
        } catch (Exception e) {
        }
        return dtFmt;
    }    
    
    public DevEntityPropertyValue $(String attr) {
    	//long timeIni = System.nanoTime();
    	DevEntityObject entObj = this;
        DevEntityPropertyValue valReturn = new DevEntityPropertyValue();
        try {
            //SETAR PROPRIEDADES DA ENTITY CLASS NO ENTITY OBJECT
            if (entObj != null && entObj.getEntityClass() != null) {
                if (!entObj.getEntityClass().getEntityPropertyDescriptorList().isEmpty()) {
                    String[] attrArray = attr.split(Pattern.quote("."));
                    DevEntityObject obj = entObj;
                    for (int i = 1; i < attrArray.length; i++) {
                    	loadProperties(obj, obj.getEntityClass().getEntityPropertyDescriptorList());
                        String string = attrArray[i];
                        valReturn = findNode(obj, string);
                        if (valReturn != null) {
                            if (valReturn.getPropertyObject() != null && !valReturn.getPropertyObject().getEntityPropertyValueList().isEmpty()) {
                                obj = valReturn.getPropertyObject();
                            } else if (valReturn.getObjectParent() != null && !valReturn.getObjectParent().getEntityPropertyValueList().isEmpty()) {
                                obj = valReturn.getObjectParent();
                            } else if (valReturn.getEntityObjectMapped() != null) {
                                obj = valReturn.getEntityObjectMapped();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Time getVal("+attr+"): "+BigDecimal.valueOf((System.nanoTime()-timeIni)/1000000000d).setScale(6,BigDecimal.ROUND_UP));
        return valReturn;
    }
    
    private void loadProperties(DevEntityObject entObj, List<DevEntityPropertyDescriptor> entityPropertyDescriptorList){
    	if(entObj!=null){
	        for (DevEntityPropertyDescriptor prop : entityPropertyDescriptorList) {
	            DevEntityPropertyValue value = null;
	            if(!entObj.entityPropertyValueMap.containsKey(prop.getPropertyName())){
		            for (DevEntityPropertyValue propVal : entObj.getEntityPropertyValueList()) {
		                if (propVal.getEntityPropertyDescriptor().getPropertyName().equals(prop.getPropertyName())) {
		                    value = propVal;
		                    break;
		                }
		            }
		            if (value == null) {
		                value = new DevEntityPropertyValue();
		                value.setEntityPropertyDescriptor(prop);
		                entObj.getEntityPropertyValueList().add(value);
		                //entObj.putEntityPropertyValueMap(entObj, value);
		            }
	                entObj.entityPropertyValueMap.put(prop.getPropertyName(), value);
	        		if (prop.getPropertyClass()!=null) {
	        			DevEntityClass entCls = prop.getPropertyClass();
	        			List<DevEntityPropertyDescriptor> _EntityPropertyDescriptorList = entCls.getEntityPropertyDescriptorList();
	        			DevEntityObject entObj2 = new DevEntityObject();
	        			entObj2.setEntityClass(entCls);        
	        			value.setEntityObjectMapped(entObj2);
	        			loadProperties(entObj2 , _EntityPropertyDescriptorList);
	        		}	                
	            }
	        }    	
    	}
    }
    
    private DevEntityPropertyValue findNode(DevEntityObject obj, String attr) {
        try {
        	DevEntityPropertyValue val = obj.getEntityPropertyValueMap().get(attr);
        	if(val!=null){
        		return val;
        	}
            if (obj.getPropertyParent() != null
                    && obj.getPropertyParent().getEntityPropertyDescriptor().getEntityClassParent() != null && obj.getPropertyParent().getEntityPropertyDescriptor().getEntityClassParent().getName().equals(attr)) {
                if (obj.getPropertyParent().getObjectParent() != null) {
                    return obj.getPropertyParent();
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    
    public Long getEntityObjectId() {
        return entityObjectId;
    }

    public void setEntityObjectId(Long entityObjectId) {
        this.entityObjectId = entityObjectId;
    }
    //@OrderBy("entityPropertyDescriptor.entityPropertyDescriptorId")
    public List<DevEntityPropertyValue> getEntityPropertyValueList() {
        return entityPropertyValueList;
    }

    public void setEntityPropertyValueList(List<DevEntityPropertyValue> entityPropertyValueList) {
        this.entityPropertyValueList = entityPropertyValueList;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public DevEntityClass getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(DevEntityClass entityClass) {
        this.entityClass = entityClass;
    }

    public DevEntityPropertyValue getPropertyParent() {
        return propertyParent;
    }

    public void setPropertyParent(DevEntityPropertyValue propertyParent) {
        this.propertyParent = propertyParent;
    }

    public SecUser getUser() {
		return user;
	}
	public void setUser(SecUser user) {
		this.user = user;
	}    
    
    public Map<String, DevEntityPropertyValue> getEntityPropertyValueMap() {
    	if(this.entityPropertyValueMap.isEmpty()){
    		for (DevEntityPropertyValue val : this.getEntityPropertyValueList()) {
    			putEntityPropertyValueMap(this, val);		
    		}        		
    	}        	
		return entityPropertyValueMap;
	}

	public void setEntityPropertyValueMap(Map<String, DevEntityPropertyValue> entityPropertyValueMap) {
		this.entityPropertyValueMap = entityPropertyValueMap;
	}
	
	public void putEntityPropertyValueMap(DevEntityObject obj, DevEntityPropertyValue val){
		obj.entityPropertyValueMap.put(val.getEntityPropertyDescriptor().getPropertyName(), val);		
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DevEntityObject other = (DevEntityObject) obj;
        if (this.entityObjectId != other.entityObjectId && (this.entityObjectId == null || !this.entityObjectId.equals(other.entityObjectId))) {
            return false;
        }
        if (this.entityObjectId == other.entityObjectId && this.entityObjectId == null && !this.registrationDate.equals(other.registrationDate)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.entityObjectId != null ? this.entityObjectId.hashCode() : this.registrationDate.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        String objectStr = "";
        for (DevEntityPropertyValue devEntityPropertyValue : entityPropertyValueList) {
            if (devEntityPropertyValue.getEntityPropertyDescriptor().isPropertyIdentifier()) {
                if (!objectStr.isEmpty()) {
                    objectStr += " - ";
                }
                objectStr += devEntityPropertyValue;
            }
        }
        return objectStr;
        //return "DevEntityObject{" + "entityObjectId = " + entityObjectId + ", registrationDate=" + registrationDate + ", entityPropertyValueList=" + entityPropertyValueList + '}';               
    }

	@Override
	public void updateProperty() {
        this.setUser(getFacadeWithJNDI(GerLoginBean.class).getUser());
		super.updateProperty();
	}
    
    
}

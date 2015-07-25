/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algoboss.erp.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import com.algoboss.erp.dao.BaseDao;
import com.algoboss.erp.entity.DevComponentContainer;
import com.algoboss.erp.entity.DevEntityClass;
import com.algoboss.erp.entity.DevEntityObject;
import com.algoboss.erp.entity.DevEntityPropertyDescriptor;
import com.algoboss.erp.entity.DevEntityPropertyDescriptorConfig;
import com.algoboss.erp.entity.DevPrototypeComponentChildren;
import com.algoboss.erp.entity.DevPrototypeComponentProperty;
import com.algoboss.erp.entity.DevRequirement;
import com.algoboss.erp.face.AdmAlgoappBean;
import com.algoboss.erp.face.AdmAlgodevBean;
import com.algoboss.erp.face.BaseBean;
import com.algoboss.integration.small.business.SmallUtil;

/**
 *
 * @author Agnaldo
 */
public class AlgodevUtil {
	public static UIComponent elementPanel;
	public static Map<String, String> dictionary = new HashMap<>();
	public static List<DevEntityClass> entityClassList = new ArrayList<DevEntityClass>();
	public static Map<String, DevEntityClass> entityClassMap = new LinkedHashMap<String, DevEntityClass>();

	private static void generateEntityClassMap() {
		for (Iterator iterator = entityClassList.iterator(); iterator.hasNext();) {
			DevEntityClass devEntityClass = (DevEntityClass) iterator.next();
			entityClassMap.put(AlgoUtil.normalizerName(devEntityClass.getName().substring(devEntityClass.getName().indexOf("_")+1)), devEntityClass);
		}
	}

	public static String formatDescription(String description) {
		char[] charArray = description.toCharArray();
		StringBuilder sb = new StringBuilder();
		int len = charArray.length;
		String separator = ",";
		boolean addSeparator = false;
		Boolean isString = true;		
		for (int i = 0; i < len; i++) {
			char c = charArray[i];
			String cStr = "";
			if (c == ';' || c == ',' || /*c == '.' ||*/ c == '\n' || c == '\t' || c == '\r') {
				addSeparator = true;
				continue;
			}
			if(addSeparator){
				sb.append(separator);
				addSeparator = false;
			}
			if (!cStr.isEmpty()) {
				isString = false;
			} else {
				if (!(!isString && c == ' ')) {
					sb.append(String.valueOf(c));
					isString = true;
				}
			}							
		}
		return sb.toString();
	}

	public static String getPreffixCmd(String str) {
		char[] strArray = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strArray.length; i++) {
			if (!Character.isLetterOrDigit(strArray[i]) && !String.valueOf(strArray[i]).matches("-|~") && strArray[i] != Character.toChars(0)[0]) {
				sb.append(strArray[i]);
			} else {
				return sb.toString();
			}
		}
		return "";
	}

	public static List<DevEntityPropertyDescriptorConfig> generateConfig(String configStr) {
		List<DevEntityPropertyDescriptorConfig> configList = new ArrayList();
		if (configStr.endsWith("]")) {
			String[] configStrArray = configStr.replaceAll("\\[|\\]", "").split(",");
			for (int i = 0; i < configStrArray.length; i++) {
				String configStrItem = configStrArray[i];
				String[] sa = configStrItem.split("=");
				DevEntityPropertyDescriptorConfig config = new DevEntityPropertyDescriptorConfig();
				config.setConfigName(sa[1].toLowerCase());
				config.setConfigValue(sa[2].toLowerCase());
				configList.add(config);

			}
		}
		return configList;
	}

	public static DevEntityClass arrayToEntity(Object[] strArray, DevEntityClass entity) {
		DevEntityClass entityRet = new DevEntityClass();
		List<DevEntityPropertyDescriptor> entityPropertyDescriptorlistTemp = new ArrayList();
		List<DevEntityPropertyDescriptor> entityPropertyDescriptorlistTemp2 = new ArrayList();
		if (entity != null) {
			entityRet = entity;
			if (entity.getEntityPropertyDescriptorList() != null && !entity.getEntityPropertyDescriptorList().isEmpty()) {
				entityPropertyDescriptorlistTemp.addAll(entity.getEntityPropertyDescriptorList());
			}
		}
		for (int i = 0; i < strArray.length; i++) {
			String key = "";
			List children = new ArrayList<Object>();
			if (strArray[i] instanceof Map) {
				Map<String, List> map = (Map) strArray[i];
				for (Map.Entry<String, List> mape : map.entrySet()) {
					key = mape.getKey();
					children = mape.getValue();

				}
			} else {
				key = String.valueOf(strArray[i]).toUpperCase();

			}

			String propType = "STRING";
			DevEntityPropertyDescriptorConfig config = new DevEntityPropertyDescriptorConfig();
			List<DevEntityPropertyDescriptorConfig> configList = new ArrayList();
			String preffix = getPreffixCmd(key);
			String typeConfig = dictionary.get(preffix);
			if (typeConfig == null) {
				throw new IllegalArgumentException("Preffix '" + preffix + "' not found in dictionary.");
			}
			key = key.replace(preffix, "").replace(Character.toChars(0)[0], ' ');
			if (typeConfig.endsWith("]")) {
				String[] sa = typeConfig.replaceAll("\\Q:[\\E|\\]|=", ";").split(";");
				propType = sa[0];
				config.setConfigName(sa[1]);
				config.setConfigValue(sa[2]);
				configList.add(config);
			} else {
				propType = typeConfig;
			}
			String configStr = "";
			if (key.startsWith("~")) {
				key = key.replace("~", "");
				configStr = "[list,display=none]";
			}
			if (key.startsWith("-")) {
				key = key.replace("-", "");
				configStr = "[list,display=none,form,display=none]";
			}
			if (!configStr.isEmpty()) {
				configList.addAll(generateConfig(configStr));
			}
			/*
			 * propType = getPreffixCmd(string); if (string.startsWith("_")) {
			 * propType = "STRING"; string = string.replace("_", "");
			 * config.setConfigName("length"); config.setConfigValue("long");
			 * configList.add(config); } else if (string.startsWith("#")) {
			 * propType = "INTEGER"; string = string.replace("#", ""); } else if
			 * (string.startsWith("$")) { propType = "FLOAT"; string =
			 * string.replace("$", ""); } else if (string.startsWith("%")) {
			 * propType = "DATE"; string = string.replace("%", ""); } else if
			 * (string.startsWith("@")) { propType = "DATE"; string =
			 * string.replace("@", ""); config.setConfigName("pattern");
			 * config.setConfigValue("HH:mm"); configList.add(config); } else if
			 * (string.startsWith("!")) { propType = "BOOLEAN"; string =
			 * string.replace("!", ""); } else if (string.startsWith("*")) {
			 * propType = "FILE"; string = string.replace("*", ""); } else if
			 * (string.startsWith(")")) { propType = "ONEINTERNALOBJECT"; string
			 * = string.replace(")", ""); } else if (string.startsWith("))")) {
			 * propType = "ENTITYCHILDREN"; string = string.replace("))", ""); }
			 * else if (string.startsWith("(")) { propType = "ENTITYCLASS";
			 * string = string.replace("(", ""); } else if
			 * (string.startsWith("((")) { propType = "MANYEXTERNALOBJECT";
			 * string = string.replace("((", ""); }
			 */
			DevEntityPropertyDescriptor entityPropertyDescriptorAux = null;
			for (int j = 0; j < entityPropertyDescriptorlistTemp.size(); j++) {
				DevEntityPropertyDescriptor devEntityPropertyDescriptor = entityPropertyDescriptorlistTemp.get(j);
				if (AlgoUtil.normalizerName(devEntityPropertyDescriptor.getPropertyLabel()).equals(AlgoUtil.normalizerName(key))) {
					entityPropertyDescriptorAux = devEntityPropertyDescriptor;
					break;
				}
			}
			if (entityPropertyDescriptorAux == null) {
				entityPropertyDescriptorAux = new DevEntityPropertyDescriptor();
			}
			if (!children.isEmpty()) {
				if (entityPropertyDescriptorAux.getPropertyClass() == null || entityPropertyDescriptorAux.getPropertyClass().getEntityPropertyDescriptorList().isEmpty()) {
					entityPropertyDescriptorAux.setPropertyClass(new DevEntityClass());
					entityPropertyDescriptorAux.getPropertyClass().setLabel(key);
				}
				arrayToEntity(children.toArray(), entityPropertyDescriptorAux.getPropertyClass());
			} else {
				if (isPropertyClass(propType)) {
					generateEntityClassMap();
					entityPropertyDescriptorAux.setPropertyClass(entityClassMap.get(AlgoUtil.normalizerName(key)));
				}
			}
			entityPropertyDescriptorlistTemp2.add(entityPropertyDescriptorAux);
			entityPropertyDescriptorAux.setPropertyLabel(key);
			entityPropertyDescriptorAux.setPropertyType(propType);
			if (i == 0) {
				entityPropertyDescriptorAux.setPropertyRequired(true);
				entityPropertyDescriptorAux.setPropertyIdentifier(true);
			}
			entityPropertyDescriptorAux.setEntityPropertyDescriptorConfigList(configList);
		}
		if (!entityPropertyDescriptorlistTemp2.isEmpty()) {
			entityRet.getEntityPropertyDescriptorList().clear();
			entityRet.getEntityPropertyDescriptorList().addAll(entityPropertyDescriptorlistTemp2);
		}
		return entityRet;
	}

	public static String entityToString(DevEntityClass ent) {
		StringBuilder stringRet = new StringBuilder();
		List<DevEntityPropertyDescriptor> entityPropertyDescriptorlistTemp = ent.getEntityPropertyDescriptorList();
		for (int i = 0; i < entityPropertyDescriptorlistTemp.size(); i++) {
			DevEntityPropertyDescriptor devEntityPropertyDescriptor = entityPropertyDescriptorlistTemp.get(i);
			String strConfig = "";
			List<DevEntityPropertyDescriptorConfig> entityPropertyDescriptorConfigList = devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList();
			if (entityPropertyDescriptorConfigList != null && !entityPropertyDescriptorConfigList.isEmpty()) {
				DevEntityPropertyDescriptorConfig config = entityPropertyDescriptorConfigList.get(0);
				if (config.getConfigName() != null && config.getConfigValue() != null) {
					strConfig = ":[".concat(config.getConfigName()).concat("=").concat(config.getConfigValue()).concat("]");
				}
			}
			String strCmd = devEntityPropertyDescriptor.getPropertyType().concat(strConfig);
			String translatedVal = dictionary.get(strCmd);
			if (translatedVal != null) {
				if (!stringRet.toString().isEmpty()) {
					stringRet.append(",");
				}
				translatedVal = translatedVal.concat(devEntityPropertyDescriptor.getPropertyLabel());
				if (devEntityPropertyDescriptor.getPropertyClass() != null) {
					stringRet.append("{".concat(translatedVal));
					stringRet.append(":[".concat(entityToString(devEntityPropertyDescriptor.getPropertyClass())).concat("]"));
					stringRet.append("}");
				} else {
					stringRet.append(translatedVal);
				}
			} else {
				new IllegalArgumentException("Command not found: " + strCmd);
			}
		}
		return stringRet.toString();
	}

	public static String entityToCss(DevEntityClass ent) {
		StringBuilder stringRet = new StringBuilder();
		List<DevEntityPropertyDescriptor> entityPropertyDescriptorlistTemp = ent.getEntityPropertyDescriptorList();
		for (int i = 0; i < entityPropertyDescriptorlistTemp.size(); i++) {
			DevEntityPropertyDescriptor devEntityPropertyDescriptor = entityPropertyDescriptorlistTemp.get(i);
			String strConfig = "";
			// List<DevEntityPropertyDescriptorConfig>
			// entityPropertyDescriptorConfigList =
			// devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList();
			// if (entityPropertyDescriptorConfigList!=null &&
			// !entityPropertyDescriptorConfigList.isEmpty()) {
			// DevEntityPropertyDescriptorConfig config =
			// entityPropertyDescriptorConfigList.get(0);
			// if(config.getConfigName()!=null &&
			// config.getConfigValue()!=null){
			// strConfig =
			// ":[".concat(config.getConfigName()).concat("=").concat(config.getConfigValue()).concat("]");
			// }
			// }
			String strCmd = devEntityPropertyDescriptor.getPropertyType().concat(strConfig);
			// String translatedVal = dictionary.get(strCmd);

			if (devEntityPropertyDescriptor.getPropertyClass() != null) {
				// stringRet.append("{".concat(translatedVal));
				stringRet.append(":[".concat(entityToString(devEntityPropertyDescriptor.getPropertyClass())).concat("]"));
				// stringRet.append("}");
			} else {
				// stringRet.append(translatedVal);
			}
		}
		return stringRet.toString();
	}

	public static List<UIComponent> generateComponentList(DevComponentContainer componentContainer) {
		List<UIComponent> elementList = new ArrayList();
		if (componentContainer != null) {
			List<DevPrototypeComponentChildren> children = componentContainer.getPrototypeComponentChildrenList();
			if(children!=null){
				for (DevPrototypeComponentChildren object : children) {
					UIComponent comp = ComponentFactory.componentDeserializer(object);
					elementList.add(comp);
				}
			}
		}
		return elementList;
	}

	public static void translateCmd() {
		dictionary = new HashMap();
		dictionary.put("STRING", "");
		dictionary.put("STRING:[length=long]", "_");
		dictionary.put("STRING:[length=short]", "");
		dictionary.put("INTEGER", "+");
		dictionary.put("FLOAT", "$");
		dictionary.put("DATE", "%");
		dictionary.put("DATE:[pattern=HH:mm]", "@");
		dictionary.put("BOOLEAN", "!");
		dictionary.put("FILE", "*");
		dictionary.put("ONEINTERNALOBJECT", "´");
		dictionary.put("ENTITYCHILDREN", "´´");
		dictionary.put("ENTITYCLASS", "`");
		dictionary.put("MANYEXTERNALOBJECT", "``");
		Map<String, String> dictionaryAux = new HashMap();
		for (Map.Entry<String, String> entry : dictionary.entrySet()) {
			dictionaryAux.put(entry.getValue(), entry.getKey());
		}
		dictionary.putAll(dictionaryAux);
		dictionary.put("LONG", "+");
		dictionary.put("DOUBLE", "$");
		dictionary.put("BIGDECIMAL", "$");
		dictionary.put("BYTE[]", "*");

	}

	public static boolean isPropertyClass(String type) {
		if ("ENTITYCHILDREN ENTITYCHILDREN ENTITYCLASS MANYEXTERNALOBJECT".contains(type)) {
			return true;
		}
		return false;
	}

	public static boolean isInternalPropertyClass(String type) {
		if ("ENTITYCHILDREN ONEINTERNALOBJECT".contains(type)) {
			return true;
		}
		return false;
	}

	public static boolean isExternalPropertyClass(String type) {
		if ("ENTITYCLASS MANYEXTERNALOBJECT".contains(type)) {
			return true;
		}
		return false;
	}

	public static Object event(Map callMap, AdmAlgoappBean app) {
		Object result = null;
		String call = String.valueOf(callMap.get("call"));
		if (call != null && !call.isEmpty()) {
			int lastIndex = call.lastIndexOf(".");
			final String clazz = call.substring(0, lastIndex);
			final String method = call.substring(lastIndex + 1);
			Method m = null;
			try {
				Class<?> clazz1 = Class.forName(clazz);

				while (clazz1 != null) {

					Method[] methods = Class.forName(clazz).getMethods();
					// Method[] methods = Macros.class.getMethods();
					for (Method met : methods) {
						if (method.equals(met.getName())) {
							m = met;
							break;
						}

					}
					if (m != null) {
						break;
					}
					clazz1 = clazz1.getSuperclass();

				}
			} catch (ClassNotFoundException e) {
				// throw e;
			} catch (Exception e) {
				throw new IllegalArgumentException("Fail to find method name: " + call, e);
			}

			if (m == null) {
				throw new IllegalArgumentException("The method " + call + " does not exist");
			}
			Object target = null;
			if (!Modifier.isStatic(m.getModifiers())) {

			}
			try {
				if (m.getReturnType().equals(Void.TYPE)) {
					m.invoke(target, app);
				} else {
					result = m.invoke(target, app);
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
    public static void updateChildren(List<UIComponent> children, UIComponent comp) throws Throwable {
        boolean notFound = true;
        for (int i = 0; i < children.size(); i++) {
            UIComponent compAux = children.get(i);
            if (compAux.getId().equals(comp.getId())) {
                children.set(i, comp);
                notFound = false;
                break;
            }
        }
        if (notFound) {
            children.add(comp);
        }
    }
    public static void generateElementsContainerMap(Map<String, List<UIComponent>> elementsContainerMap, DevRequirement bean) {
        try {
        	List<DevComponentContainer> componentContainerList = bean.getComponentContainerList();
        	List<DevComponentContainer> componentContainerListAux = new ArrayList<DevComponentContainer>();
            for (Map.Entry<String, List<UIComponent>> object : elementsContainerMap.entrySet()) {
                String object1 = object.getKey();
                List<UIComponent> elementList = object.getValue();
                List<DevPrototypeComponentChildren> children = new ArrayList<DevPrototypeComponentChildren>();
                for (int i = 0; i < elementList.size(); i++) {
                    UIComponent comp = elementList.get(i);
                    Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
                    f2.setAccessible(true);
                    List<DevPrototypeComponentProperty> property = ComponentFactory.componentSerializer(f2, comp, null, false);
                    children.add(new DevPrototypeComponentChildren(comp.getClass().getName(), property));
                }
                DevComponentContainer componentContainer = null;
                for (DevComponentContainer devComponentContainer : componentContainerList) {
                    if (devComponentContainer.getName().equals(object1)) {
                        //devComponentContainer.getPrototypeComponentChildrenList().clear();
                        devComponentContainer.setPrototypeComponentChildrenList(children);
                        componentContainer = devComponentContainer;
                        break;
                    }
                }
                if (componentContainer == null) {
                    componentContainer = new DevComponentContainer();
                    componentContainer.setName(object1);
                    componentContainer.setPrototypeComponentChildrenList(children);
                    //bean.getComponentContainerList().add(componentContainer);
                }
                componentContainerListAux.add(componentContainer);
                elementsContainerMap.put(componentContainer.getName(), AlgodevUtil.generateComponentList(componentContainer));
            }
            bean.setComponentContainerList(componentContainerListAux);
        } catch (Throwable ex) {
            Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    }
    
    public static AdmAlgoappBean getAlgoAppInstance(DevRequirement requirement, BaseBean baseBean, BaseDao baseDao){
    	AdmAlgoappBean algoApp = new AdmAlgoappBean();
        algoApp.setRequirement(requirement);
        algoApp.setBaseBean(baseBean);
        algoApp.setBaseDao(baseDao);
        algoApp.generateElementsContainerMap(requirement);
        return algoApp;
    }
    
    /**
     * Geração de JSon passando a nome da classe.
     */
    public static void populateEntityClassByClassName(DevEntityClass entity){
    	try {
    		entity.getEntityPropertyDescriptorList().clear();
			Class clazz = Class.forName(entity.getCanonicalClassName());			
			Field[] fields = clazz.getDeclaredFields();
			entity.setLabel(clazz.getSimpleName());
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(!Modifier.isStatic(field.getModifiers()) && !field.getName().startsWith("_")){
					DevEntityPropertyDescriptor prop = new DevEntityPropertyDescriptor();
					prop.setPropertyLabel(field.getName());
					prop.setPropertyType(field.getType().getSimpleName().toUpperCase());
					List<DevEntityPropertyDescriptorConfig> configList = new ArrayList();
					prop.setEntityPropertyDescriptorConfigList(configList);
					entity.getEntityPropertyDescriptorList().add(prop);
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, e);
		}
    	
    }    
}

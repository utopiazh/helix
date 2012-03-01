package com.linkedin.helix.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.linkedin.helix.ZNRecord;
import com.linkedin.helix.ZNRecordDecorator;

/**
 * Current states of partitions in a resource
 */
public class CurrentState extends ZNRecordDecorator
{
  private static Logger LOG = Logger.getLogger(CurrentState.class);

  public enum CurrentStateProperty {
    SESSION_ID, CURRENT_STATE, STATE_MODEL_DEF, STATE_MODEL_FACTORY_NAME, RESOURCE
  }

  public CurrentState(String resourceName)
  {
    super(resourceName);
  }

  public CurrentState(ZNRecord record)
  {
    super(record);
  }

  public String getResourceName()
  {
    return _record.getId();
  }

  public Map<String, String> getPartitionStateMap()
  {
    Map<String, String> map = new HashMap<String, String>();
    Map<String, Map<String, String>> mapFields = _record.getMapFields();
    for (String partitionName : mapFields.keySet())
    {
      Map<String, String> tempMap = mapFields.get(partitionName);
      if (tempMap != null)
      {
        map.put(partitionName, tempMap.get(CurrentStateProperty.CURRENT_STATE.toString()));
      }
    }
    return map;
  }

  public String getSessionId()
  {
    return _record.getSimpleField(CurrentStateProperty.SESSION_ID.toString());
  }

  public void setSessionId(String sessionId)
  {
    _record.setSimpleField(CurrentStateProperty.SESSION_ID.toString(), sessionId);
  }

  public String getState(String partitionName)
  {
    Map<String, Map<String, String>> mapFields = _record.getMapFields();
    Map<String, String> mapField = mapFields.get(partitionName);
    if (mapField != null)
    {
      return mapField.get(CurrentStateProperty.CURRENT_STATE.toString());
    }
    return null;
  }

  public void setStateModelDefRef(String stateModelName)
  {
    _record.setSimpleField(CurrentStateProperty.STATE_MODEL_DEF.toString(), stateModelName);
  }

  public String getStateModelDefRef()
  {
    return _record.getSimpleField(CurrentStateProperty.STATE_MODEL_DEF.toString());
  }

  public void setState(String partitionName, String state)
  {
    Map<String, Map<String, String>> mapFields = _record.getMapFields();
    if (mapFields.get(partitionName) == null)
    {
      mapFields.put(partitionName, new TreeMap<String, String>());
    }
    mapFields.get(partitionName).put(CurrentStateProperty.CURRENT_STATE.toString(), state);
  }

  public void setStateModelFactoryName(String factoryName)
  {
    _record.setSimpleField(CurrentStateProperty.STATE_MODEL_FACTORY_NAME.toString(), factoryName);
  }

  public String getStateModelFactoryName()
  {
    return _record.getSimpleField(CurrentStateProperty.STATE_MODEL_FACTORY_NAME.toString());
  }

  @Override
  public boolean isValid()
  {
    if (getStateModelDefRef() == null)
    {
      LOG.error("Current state does not contain state model ref. id:" + getResourceName());
      return false;
    }
    if (getSessionId() == null)
    {
      LOG.error("CurrentState does not contain session id, id : " + getResourceName());
      return false;
    }
    return true;
  }

}
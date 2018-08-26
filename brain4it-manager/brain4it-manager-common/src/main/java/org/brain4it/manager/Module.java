/*
 * Brain4it
 * 
 * Copyright (C) 2018, Ajuntament de Sant Feliu de Llobregat
 * 
 * This program is licensed and may be used, modified and redistributed under 
 * the terms of the European Public License (EUPL), either version 1.1 or (at 
 * your option) any later version as soon as they are approved by the European 
 * Commission.
 * 
 * Alternatively, you may redistribute and/or modify this program under the 
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation; either  version 3 of the License, or (at your option) 
 * any later version. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *   
 * See the licenses for the specific language governing permissions, limitations 
 * and more details.
 *   
 * You should have received a copy of the EUPL1.1 and the LGPLv3 licenses along 
 * with this program; if not, you may find them at: 
 *   
 *   https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *   http://www.gnu.org/licenses/ 
 *   and 
 *   https://www.gnu.org/licenses/lgpl.txt
 */

package org.brain4it.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.brain4it.client.RestClient;
import org.brain4it.io.Parser;
import org.brain4it.io.Printer;
import org.brain4it.lang.BList;
import org.brain4it.lang.Utils;

/**
 *
 * @author realor
 */
public class Module
{
  private static final String LIST_FUNCTIONS = "(functions)";
  private final ArrayList<ModuleListener> listeners = 
    new ArrayList<ModuleListener>();
  
  private Server server;
  private String name;
  private String accessKey;
  private BList functions;
  private final Set<String> functionNames = 
    Collections.synchronizedSet(new HashSet<String>());
  private BList metadata;
  
  public Module(Server server)
  {
    this(server, null, null);
  }

  public Module(Server server, String name)
  {
    this(server, name, null);
  }
  
  public Module(Server server, String name, String accessKey)
  {
    this.server = server;
    this.name = name;
    setAccessKey(accessKey);
  }
  
  public Server getServer()
  {
    return server;
  }
  
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getAccessKey()
  {
    return accessKey;
  }

  public void setAccessKey(String accessKey)
  {
    if (accessKey != null)
    {
      accessKey = accessKey.trim();
      if (accessKey.length() == 0) accessKey = null;
    }
    
    if ((accessKey != null && !accessKey.equals(this.accessKey)) || 
        (accessKey == null && this.accessKey != null))
    {
      this.accessKey = accessKey;
      ModuleEvent event = new ModuleEvent(this, ModuleEvent.ACCESS_KEY_CHANGED);
      notifyAccessKeyChanged(event);
    }
  }

  public BList getMetadata()
  {
    return metadata;
  }

  public void setMetadata(BList metadata)
  {
    this.metadata = metadata;
  }
  
  public RestClient getRestClient()
  {
    String serverUrl = getServer().getUrl();
    return new RestClient(serverUrl, 
      accessKey == null ? getServer().getAccessKey() : accessKey);
  }
  
  public void loadMetadata(final Callback callback)
  {
    RestClient restClient = getRestClient();
    restClient.get(getName(), "module-metadata", new RestClient.Callback()
    {
      @Override
      public void onSuccess(RestClient client, String resultString)
      {
        try
        {
          metadata = (BList)Parser.fromString(resultString);
          if (callback != null)
          {
            callback.actionCompleted(Module.this, "loadMetadata");
          }
        }
        catch (Exception ex)
        {          
          if (callback != null)
          {
            callback.actionFailed(Module.this, "loadMetadata", ex);
          }
        }
      }

      @Override
      public void onError(RestClient client, Exception ex)
      {
        if (callback != null)
        {
          callback.actionFailed(Module.this, "loadMetadata", ex);
        }
      }
    });
  }

  public void saveMetadata(BList metadata, final Callback callback)
  {
    RestClient restClient = getRestClient();    
    String body = Printer.toString(metadata);
    restClient.put(getName(), "module-metadata", body, new RestClient.Callback()
    {
      @Override
      public void onSuccess(RestClient client, String resultString)
      {
        try
        {
          if (callback != null)
          {
            callback.actionCompleted(Module.this, "saveMetadata");
          }
        }
        catch (Exception ex)
        {
          if (callback != null)
          {
            callback.actionFailed(Module.this, "saveMetadata", ex);
          }
        }
      }

      @Override
      public void onError(RestClient client, Exception ex)
      {
        if (callback != null)
        {
          callback.actionFailed(Module.this, "saveMetadata", ex);
        }
      }
    });
  }
  
  public void saveData(String reference, String data, final Callback callback)
  {
    String command = "(if (not (exists " + reference + ")) (set " + 
      reference + " " + data + "))";
    RestClient restClient = getRestClient();
    restClient.execute(getName(), command, new RestClient.Callback()
    {
      @Override
      public void onSuccess(RestClient client, String resultString)
      {
        if (callback != null)
        {
          callback.actionCompleted(Module.this, "saveData");
        }
      }

      @Override
      public void onError(RestClient client, Exception ex)
      {
        if (callback != null)
        {
          callback.actionFailed(Module.this, "saveData", ex);
        }
      }
    });
  }
  
  public void findFunctions(final Callback callback)
  {
    RestClient restClient = getRestClient();
    restClient.execute(getName(), LIST_FUNCTIONS, new RestClient.Callback()
    {
      @Override
      public void onSuccess(RestClient client, String resultString)
      {
        try
        {
          functions = (BList)Parser.fromString(resultString);
          functionNames.clear();
          for (int i = 0; i < functions.size(); i++)
          {
            functionNames.add(Utils.toString(functions.get(i)));
          }
          ModuleEvent event = new ModuleEvent(Module.this, 
            ModuleEvent.FUNCTIONS_UPDATED);
          notifyFunctionsUpdated(event);
          if (callback != null)
          {
            callback.actionCompleted(Module.this, "findFunctions");
          }
        }
        catch (Exception ex)
        {
          if (callback != null)
          {
            callback.actionFailed(Module.this, "findFunctions", ex);
          }
        }
      }

      @Override
      public void onError(RestClient client, Exception ex)
      {
        if (callback != null)
        {
          callback.actionFailed(Module.this, "findFunctions", ex);
        }
      }
    });
  }
  
  public BList getFunctions()
  {
    return functions;
  }

  public Set<String> getFunctionNames()
  {
    return functionNames;
  }

  public void addModuleListener(ModuleListener listener)
  {
    listeners.add(listener);
  }
  
  public void removeModuleListener(ModuleListener listener)
  {
    listeners.remove(listener);
  }

  private void notifyAccessKeyChanged(ModuleEvent event)
  {
    ModuleListener[] array = new ModuleListener[listeners.size()];        
    listeners.toArray(array);
    for (ModuleListener listener : array)
    {
      listener.accessKeyChanged(event);
    }    
  }

  private void notifyFunctionsUpdated(ModuleEvent event)
  {
    ModuleListener[] array = new ModuleListener[listeners.size()];        
    listeners.toArray(array);
    for (ModuleListener listener : array)
    {
      listener.functionsUpdated(event);
    }    
  }
  
  @Override
  public String toString()
  {
    return name;
  }
  
  public interface Callback
  {
    public void actionCompleted(Module module, String action);
    public void actionFailed(Module module, String action, Exception error);
  }
}

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

package org.brain4it.lang;

import java.util.Map;
import java.util.Stack;

/**
 * The BPL evaluation context.
 *
 * All code in BPL run inside a Context.
 *
 * A context contains:
 * <ul>
 * <li>A global scope list ({@link org.brain4it.lang.BList}) where global
 * variables are stored.</li>
 * <li>A stack of local scopes lists ({@link org.brain4it.lang.BList})
 * that contains the local variables.</li>
 * <li>A {@link java.util.Map} that contains the implementation of the built-in
 * functions available in this context.</li>
 * <li>A {@link org.brain4it.lang.BList} that contains the stack of user
 * functions calls.</li>
 * </ul>
 *
 * This class has methods to evaluate expressions and resolve variables
 * (soft references).
 *
 * The variable resolution follows these rules:
 * If the variable is found in local scope, its value is returned,
 * else if the variable is found in global scope, its value is returned,
 * otherwise, null is returned.
 *
 * @author realor
 */

public class Context
{
  public static final Function IDENTITY_FUNCTION = new IdentityFunction();
  public static final Function REFERENCE_FUNCTION = new ReferenceFunction();
  public static final Function DEFAULT_FUNCTION = new DefaultFunction();

  private final BList globalScope;
  private final Stack<BList> localScopes = new Stack<BList>();
  private final BList callStack = new BList();
  private final Map<String, Function> functions;

  public Context(BList globalScope, Map<String, Function> functions)
  {
    this.globalScope = globalScope;
    this.localScopes.push(new BList());
    this.functions = functions;
  }

  public Context(Context context)
  {
    this(context.getGlobalScope(), context.getFunctions());
  }

  public BList getGlobalScope()
  {
    return globalScope;
  }

  public BList getLocalScope()
  {
    // = localScope(0)
    return localScopes.peek(); // localScopes never is empty
  }

  public BList getLocalScope(int depth)
  {
    int size = localScopes.size();
    if (depth < 0 || depth >= size) return null;
    return localScopes.get(size - depth - 1);
  }

  public void pushLocalScope(BList scope)
  {
    localScopes.push(scope);
  }

  public BList popLocalScope()
  {
    if (localScopes.size() == 1)
      throw new RuntimeException("Can't remove local scope");
    return localScopes.pop();
  }

  public Map<String, Function> getFunctions()
  {
    return functions;
  }

  public Object get(String name)
  {
    BList scope = localScopes.peek();
    synchronized (scope)
    {
      int index = scope.getIndexOfName(name);
      if (index != -1)
      {
        return scope.get(index);
      }
    }
    return globalScope.get(name);
  }

  public void set(String name, Object value)
  {
    BList scope = localScopes.peek();
    synchronized (scope)
    {
      int index = scope.getIndexOfName(name);
      if (index != -1)
      {
        scope.put(index, value);
        return;
      }
    }
    globalScope.put(name, value);
  }

  public void setLocal(String name, Object value)
  {
    // assume value is not a path
    BList scope = localScopes.peek();
    scope.put(name, value);
  }

  public void setLocal(BSoftReference reference, Object value)
  {
    if (reference instanceof BSingleReference)
    {
      setLocal(reference.getName(), value);
    }
    else throw new RuntimeException("Invalid local reference: " + reference);
  }

  public boolean delete(String name)
  {
    BList scope = localScopes.peek();
    synchronized (scope)
    {
      int index = scope.getIndexOfName(name);
      if (index != -1)
      {
        scope.remove(index);
        return true;
      }
    }
    synchronized (globalScope)
    {
      int index = globalScope.getIndexOfName(name);
      if (index != -1)
      {
        globalScope.remove(index);
        return true;
      }
    }
    return false;
  }

  public boolean exists(String name)
  {
    BList scope = localScopes.peek();
    if (scope.has(name))
    {
      return true;
    }
    else
    {
      return globalScope.has(name);
    }
  }

  /**
   * Evaluates BPL code.
   *
   * Code evaluation is always performed with this method.
   * All BPL data but BLists and BSoftReferences evaluate themselves.
   * If the given code throws a Java Exception it is converted to BException
   * unless it be an InterruptedException.
   *
   * @param code the BPL code to evaluate
   * @return the result of evaluating the given code
   * @throws BException when the execution throws an Exception
   * @throws InterruptedException when the execution was interrupted (via kill)
   */
  public final Object evaluate(Object code)
    throws BException, InterruptedException
  {
    try
    {
      if (code instanceof BList)
      {
        BList list = (BList)code;
        return list.function.invoke(this, list);
      }
      else if (code instanceof BSoftReference)
      {
        BSoftReference reference = (BSoftReference)code;
        return reference.getReferencedData(this);
      }
      return code;
    }
    catch (InterruptedException ex)
    {
      throw ex;
    }
    catch (BException ex)
    {
      throw ex.addSourceInfo(code, callStack);
    }
    catch (Exception ex)
    {
      throw new BException(ex).addSourceInfo(code, callStack);
    }
  }

  public boolean isUserFunction(BList function)
  {
    return Utils.isUserFunction(function);
  }

  public Object invokeUserFunction(BList function, BList argExprs)
    throws Exception
  {
    return invokeUserFunction(function, argExprs, 0);
  }

  public Object invokeUserFunction(BList function, BList argExprs,
    int fromIndex) throws Exception
  {
    // (function (argsDefs) expr1 expr2 ... )
    Object result = null;
    BList localScope = new BList();

    Object second = function.get(1);
    if (second instanceof BList)
    {
      BList argDefs = (BList)second;

      // passing parameters
      int j = fromIndex;
      for (int i = 0; i < argDefs.size(); i++)
      {
        Object argDef = argDefs.get(i);
        if (argDef instanceof BReference)
        {
          BReference reference = (BReference)argDef;
          String argName = reference.getName();
          String callArgName = argDefs.getName(i);
          if (callArgName == null) // (... vx ...)
          {
            Object argValue = j < argExprs.size() ? argExprs.get(j) : null;
            argValue = evaluate(argValue);
            localScope.put(argName, argValue);
            j++;
          }
          else // (... "vx" => x ...)
          {
            Object argValue = argExprs.get(callArgName);
            argValue = evaluate(argValue);
            localScope.put(argName, argValue);
          }
        }
        else if (argDef instanceof String) // (... "vx" ...)
        {
          String argName = (String)argDef;
          Object argValue = argExprs.get(argName);
          argValue = evaluate(argValue);
          localScope.put(argName, argValue);
        }
      }
    }
    else if (second instanceof BSingleReference)
    {
      BSingleReference reference = (BSingleReference)second;
      localScope.put(reference.getName(), argExprs.sublist(fromIndex));
    }

    // execute function code
    localScopes.push(localScope);
    for (int i = 2; i < function.size(); i++)
    {
      result = evaluate(function.get(i));
    }
    localScopes.pop();

    return result;
  }

  static class IdentityFunction implements Function
  {
    @Override
    public Object invoke(Context context, BList args) throws Exception
    {
      return args;
    }
  }

  static class ReferenceFunction implements Function
  {
    @Override
    public Object invoke(Context context, BList args) throws Exception
    {
      Object result;
      BSoftReference reference = (BSoftReference)args.get(0);
      Object value = reference.getReferencedData(context);
      if (value instanceof BHardReference)
      {
        BHardReference fnReference = (BHardReference)value;
        result = fnReference.function.invoke(context, args);
      }
      else if (value instanceof BList)
      {
        BList list = (BList)value;
        if (context.isUserFunction(list))
        {
          context.callStack.add(reference);
          result = context.invokeUserFunction(list, args, 1);
          context.callStack.remove(context.callStack.size() - 1);
        }
        else
        {
          result = args;
        }
      }
      else
      {
        result = args;
      }
      return result;
    }
  }

  static class DefaultFunction implements Function
  {
    @Override
    public Object invoke(Context context, BList args) throws Exception
    {
      Object result;
      if (args.size() > 0)
      {
        Object first = args.get(0);
        if (first instanceof BHardReference)
        {
          BHardReference fnReference = (BHardReference)first;
          args.function = fnReference.function;
          result = args.function.invoke(context, args);
        }
        else if (first instanceof BSoftReference)
        {
          args.function = REFERENCE_FUNCTION;
          result = args.function.invoke(context, args);
        }
        else
        {
          args.function = IDENTITY_FUNCTION;
          result = args;
        }
      }
      else
      {
        args.function = IDENTITY_FUNCTION;
        result = args;
      }
      return result;
    }
  }
}

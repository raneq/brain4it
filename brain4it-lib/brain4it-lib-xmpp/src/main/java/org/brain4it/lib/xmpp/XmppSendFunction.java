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

package org.brain4it.lib.xmpp;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.brain4it.lang.Context;
import org.brain4it.lang.BList;
import org.brain4it.lang.Utils;
import org.brain4it.lib.XmppLibrary;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jxmpp.jid.impl.JidCreate;

/**
 *
 * @author realor
 */
public class XmppSendFunction extends XmppFunction
{
  public XmppSendFunction(XmppLibrary library)
  {
    super(library);
  }

  @Override
  public Object invoke(Context context, BList args) throws Exception
  {
    Utils.checkArguments(args, 3);    
    String connectionId = (String)context.evaluate(args.get(1));
    String recipient = (String)context.evaluate(args.get(2));
    String message = (String)context.evaluate(args.get(3));
    
    XMPPTCPConnection connection = library.getConnection(connectionId);
    if (connection != null)
    {
      ChatManager chatManager = ChatManager.getInstanceFor(connection);
      Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(recipient));
      chat.send(message);
      return message.length();
    }
    return 0;
  }
}

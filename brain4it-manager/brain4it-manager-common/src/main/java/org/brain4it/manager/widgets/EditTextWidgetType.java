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
package org.brain4it.manager.widgets;

import org.brain4it.lang.BList;
import static org.brain4it.manager.widgets.WidgetProperty.*;

/**
 *
 * @author realor
 */
public class EditTextWidgetType extends WidgetType
{
  public static final String AUTO_SCROLL = "auto-scroll";

  /* auto-scroll property values */
  public static final String AUTO_SCROLL_TOP = "top";
  public static final String AUTO_SCROLL_BOTTOM = "bottom";

  public EditTextWidgetType()
  {
    addProperty(LABEL, STRING, false, null);
    addProperty(FONT_FAMILY, STRING, false, "arial");
    addProperty(FONT_SIZE_PROPERTY);
    addProperty(GET_VALUE, STRING, true, null);
    addProperty(SET_VALUE, STRING, true, null);
    addProperty(INVOKE_INTERVAL_PROPERTY);
    addProperty(AUTO_SCROLL, STRING, false, null);
  }

  @Override
  public String getWidgetType()
  {
    return EDIT_TEXT;
  }

  public String getAutoScroll(BList properties) throws Exception
  {
    return getProperty(AUTO_SCROLL).getString(properties);
  }
}

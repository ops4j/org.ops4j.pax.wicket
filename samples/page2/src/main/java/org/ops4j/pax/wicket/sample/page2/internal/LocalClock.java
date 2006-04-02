/*
 * Copyright 2006 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.sample.page2.internal;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LocalClock
{
    private Locale m_locale;

    public LocalClock( Locale locale )
    {
        m_locale = locale;
    }

    public String getTimeZoneId()
    {
        int style = DateFormat.MEDIUM;
        DateFormat df = DateFormat.getTimeInstance( style, m_locale );
        TimeZone tz = df.getTimeZone();
        String id = tz.getID();
        return id;
    }

    public String getDisplayName()
    {
        int style = DateFormat.MEDIUM;
        DateFormat df = DateFormat.getTimeInstance( style, m_locale );
        TimeZone tz = df.getTimeZone();
        String name = tz.getDisplayName();
        return name;
    }

    public String getCurrentTime()
    {
        int style = DateFormat.MEDIUM;
        DateFormat df = DateFormat.getTimeInstance( style, m_locale );
        String time = df.format( new Date() );
        return time;
    }
}

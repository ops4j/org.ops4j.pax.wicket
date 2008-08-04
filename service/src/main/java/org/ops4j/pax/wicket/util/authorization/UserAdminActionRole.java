/*
 * Copyright (c) 2008 David Leangen
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util.authorization;

import java.lang.annotation.*;

/**
 * Marks the class as having a role. The role is simply based on the class
 * name.
 * 
 * Note that I did not add package-level annotations for the simple reason
 * that I did not need them. Feel free to add them in if you need them.
 * 
 * @author David Leangen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.TYPE )
@Documented
@Inherited
public @interface UserAdminActionRole
{
    Class value();
}

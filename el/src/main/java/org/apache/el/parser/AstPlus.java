/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Generated By:JJTree: Do not edit this line. AstPlus.java */

package org.apache.el.parser;

import com.guet.flexbox.el.ELException;

import org.apache.el.lang.ELArithmetic;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;


/**
 * @author Jacob Hookom [jacob@hookom.net]
 */
public final class AstPlus extends ArithmeticNode {
    public AstPlus(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Class<?> type0 = this.children[0].getType(ctx);
        Class<?> type1 = this.children[1].getType(ctx);
        if (type0 == String.class || type1 == String.class) {
            return String.class;
        }
        return super.getType(ctx);
    }

    @Override
    public Object getValue(EvaluationContext ctx)
            throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Object obj1 = this.children[1].getValue(ctx);
        if (obj0 instanceof String || obj1 instanceof String) {
            return ELSupport.coerceToString(ctx, obj0) + ELSupport.coerceToString(ctx, obj1);
        }
        return ELArithmetic.add(obj0, obj1);
    }
}

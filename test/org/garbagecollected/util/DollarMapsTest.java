/*
 * Copyright (C) 2008 Robbie Vanbrabant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.garbagecollected.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.garbagecollected.util.DollarMaps.$;
import static org.garbagecollected.util.DollarMaps.$$;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.garbagecollected.util.DollarMaps.$;
import org.garbagecollected.util.DollarMaps.$$;
import org.junit.Test;


public class DollarMapsTest {
    
    @Test
    public void $returnedMapIsOK() {
        Map<Integer, String> map = $(1, "value").asHashMap();
        assertTrue("Expecting a HashMap", 
                   HashMap.class.isAssignableFrom(map.getClass()));
        assertNotNull(map);
        assertEquals(1, map.size());
        Entry<Integer, String> entry = map.entrySet().iterator().next();
        assertEquals(Integer.valueOf(1), entry.getKey());
        assertEquals("value", entry.getValue());
    }
    
    @Test
    public void $asTreeMap() {
        Class<?> clazz = 
            $(1, "value").asTreeMap().getClass();
        assertTrue("Expecting a TreeMap", 
                   TreeMap.class.isAssignableFrom(clazz));
    }
    @Test
    public void $asConcurrentHashMap() {
        Class<?> clazz = 
            $(1, "value").asConcurrentHashMap().getClass();
        assertTrue("Expecting a ConcurrentHashMap", 
                   ConcurrentHashMap.class.isAssignableFrom(clazz));
    }
    @Test
    public void $asLinkedHashMap() {
        Class<?> clazz = 
            $(1, "value").asLinkedHashMap().getClass();
        assertTrue("Expecting a LinkedHashMap", 
                   LinkedHashMap.class.isAssignableFrom(clazz));
    }
    @Test
    public void $asWeakHashMap() {
        Class<?> clazz = 
            $(1, "value").asWeakHashMap().getClass();
        assertTrue("Expecting a WeakHashMap", 
                   WeakHashMap.class.isAssignableFrom(clazz));
    }
    
    @Test
    public void $iterator() {
        Entry<Integer, String> entry = $(1, "value").iterator().next();
        assertEquals(Integer.valueOf(1), entry.getKey());
        assertEquals("value", entry.getValue());
    }
    
    @Test
    public void initializerWorks() {
        assertEquals(1, $($(1,"value").asHashMap()).asHashMap().size());
    }
    
    @Test
    public void chainingWorks() {
        $.class.isAssignableFrom($(1,"one").$(2, "").getClass());
    }
    
    @Test
    public void cantModifyUnderlyingMap() {
        $<Integer, String> dollar = $(1, "value");
        dollar.asLinkedHashMap().remove(1);
        assertEquals(1, dollar.asLinkedHashMap().size());
    }
    
    @Test(expected=java.lang.UnsupportedOperationException.class)
    public void $iteratorRemove() {
        $(1, "value").iterator().remove();
    }
    
    @Test(expected=java.lang.NullPointerException.class)
    public void nullPointerRegularConstruction() {
        $((Integer)null, "value").asConcurrentHashMap();
    }
    
    @Test(expected=java.lang.NullPointerException.class)
    public void nullPointerWithInitializer() {
        $($((Integer)null,"value").asHashMap()).asConcurrentHashMap();
    }
    
    @Test
    public void predictableOrdering() {
        $<Integer, String> dollar = $(1,"one").$(2, "two").$(3, "three");
        Iterator<Entry<Integer, String>> iterator = dollar.iterator();
        assertEquals(Integer.valueOf(1), iterator.next().getKey());
        assertEquals(Integer.valueOf(2), iterator.next().getKey());
        assertEquals(Integer.valueOf(3), iterator.next().getKey());
    }
    
    @Test
    public void easyIteration() {
        $$<String> dollar = $$("1","one").$("2", "two").$("3", "three");
        int count = 0;
        for (@SuppressWarnings("unused") String[] s : dollar.asEasy())
          count++;
        assertEquals(3, count);
    }
    
    @Test
    public void easyStreamIteration() {
        $$<String> dollar = $$("1","one").$("2", "two").$("3", "three");
        int count = 0;
        for (@SuppressWarnings("unused") String[] s : dollar.asEasyStream()) {
          count++;
        }
        assertEquals(3, count);
    }
    
    @SuppressWarnings({"unchecked", "unused"})
    @Test
    public void wildCards() {
      // TODO poorly handled use case
      Map<Class<?>,?> TYPES =  $(Collections.<Class<?>,Object>emptyMap())
                              .$(int.class, 0)
                              .$(long.class, 0L)
                              .$(boolean.class, false)
                              .$(byte.class, 0)
                              .$(short.class, 0)
                              .$(float.class, 0.0F)
                              .$(double.class, 0.0D)
                              .$(char.class, '\u0000')
                              .asHashMap();
    }
}

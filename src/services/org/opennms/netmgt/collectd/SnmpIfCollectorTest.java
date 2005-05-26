//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2005 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
// OpenNMS Licensing       <license@opennms.org>
//     http://www.opennms.org/
//     http://www.opennms.com/
//
package org.opennms.netmgt.collectd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.opennms.protocols.snmp.SnmpSMI;


public class SnmpIfCollectorTest extends SnmpCollectorTestCase {
    
    
    private Map m_ifMap;

    protected void setUp() throws Exception {
        super.setUp();
        m_ifMap = new TreeMap();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testZeroVars() throws Exception {
        IfInfo ifInfo = new IfInfo(1, 24, "lo", "P");
        
        ifInfo.setOidList(new ArrayList(m_objList));
        
        addIfInfo(ifInfo);
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        assertInterfaceMibObjectsPresent(collector.getEntries());
    }
    
    

    private void assertInterfaceMibObjectsPresent(List entries) {
        assertNotNull(entries);
        assertEquals(m_ifMap.size(), entries.size());
        int index = 0;
        for (Iterator it = m_ifMap.values().iterator(); it.hasNext();) {
            IfInfo info = (IfInfo) it.next();
            SNMPCollectorEntry entry = (SNMPCollectorEntry) entries.get(index);
            assertEquals(String.valueOf(info.getIndex()), entry.get(SNMPCollectorEntry.IF_INDEX));
            assertMibObjectsPresent(entry, m_objList);
            index++;
        }
    }

    public void testInvalidVar() throws Exception {
        IfInfo ifInfo = new IfInfo(1, 24, "lo", "P");
        
        addMibObject("ifInOctets", "1.3.6.1.2.1.2.2.2.10", "ifIndex", "counter");
        
        ifInfo.setOidList(new ArrayList(m_objList));
        
        addIfInfo(ifInfo);
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        // remove the failing element.  Now entries should match
        m_objList.remove(0);
        assertInterfaceMibObjectsPresent(collector.getEntries());
        
    }
    
    public void testBadApple() throws Exception {
        IfInfo ifInfo = new IfInfo(1, 24, "lo", "P");
        
        addIfSpeed();
        addIfInOctets();
        // the oid below is wrong.  Make sure we collect the others anyway
        addMibObject("ifOutOctets", "1.3.6.1.2.1.2.2.2.16", "ifIndex", "counter");
        addIfInErrors();
        addIfOutErrors();
        addIfInDiscards();
        
        ifInfo.setOidList(new ArrayList(m_objList));
        
        addIfInfo(ifInfo);
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        // remove the bad apple before compare
        m_objList.remove(2);
        assertInterfaceMibObjectsPresent(collector.getEntries());
        
    }

    public void testBadAppleV2() throws Exception {
        m_peer.getParameters().setVersion(SnmpSMI.SNMPV2);

        IfInfo ifInfo = new IfInfo(1, 24, "lo", "P");
        
        addIfSpeed();
        addIfInOctets();
        // the oid below is wrong.  Make sure we collect the others anyway
        addMibObject("ifOutOctets", "1.3.66.1.2.1.2.2.299.16", "ifIndex", "counter");
        addIfInErrors();
        addIfOutErrors();
        addIfInDiscards();
        
        ifInfo.setOidList(new ArrayList(m_objList));
        
        addIfInfo(ifInfo);
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        // remove the bad apple before compare
        m_objList.remove(2);
        assertInterfaceMibObjectsPresent(collector.getEntries());
        
    }


    public void testManyVars() throws Exception {
        addIfTable();
        
        addIfInfo(createIfInfo(1, 24, "lo", "P"));
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        assertInterfaceMibObjectsPresent(collector.getEntries());
        
    }

    private IfInfo createIfInfo(int ifIndex, int ifType, String ifName, String ifCollType) {
        IfInfo ifInfo = new IfInfo(ifIndex, ifType, ifName, ifCollType);
        ifInfo.setOidList(new ArrayList(m_objList));
        return ifInfo;
    }

    public void testManyIfs() throws Exception {
        addIfTable();
        
        addIfInfo(createIfInfo(1, 24, "lo0", "P"));
        addIfInfo(createIfInfo(2, 55, "gif0", "S"));
        addIfInfo(createIfInfo(3, 57, "stf0", "C"));
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        assertInterfaceMibObjectsPresent(collector.getEntries());
    }

    // TODO: add test for very large v2 request
    public void testManyIfsV2() throws Exception {
        m_peer.getParameters().setVersion(SnmpSMI.SNMPV2);
        addIfTable();
        
        addIfInfo(createIfInfo(1, 24, "lo0", "P"));
        addIfInfo(createIfInfo(2, 55, "gif0", "S"));
        addIfInfo(createIfInfo(3, 57, "stf0", "C"));
        
        SnmpIfCollector collector = new SnmpIfCollector(getSession(), m_signaler, "1", m_ifMap, m_ifMap.size(), 50);
        waitForSignal();
        
        assertInterfaceMibObjectsPresent(collector.getEntries());
    }

    private void addIfInfo(IfInfo ifInfo) {
        m_ifMap.put(new Integer(ifInfo.getIndex()), ifInfo);
    }

}

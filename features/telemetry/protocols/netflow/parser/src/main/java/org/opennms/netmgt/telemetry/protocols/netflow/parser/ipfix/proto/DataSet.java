/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.telemetry.protocols.netflow.parser.ipfix.proto;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.opennms.netmgt.telemetry.protocols.netflow.parser.InvalidPacketException;
import org.opennms.netmgt.telemetry.protocols.netflow.parser.MissingTemplateException;
import org.opennms.netmgt.telemetry.protocols.netflow.parser.session.Template;
import org.opennms.netmgt.telemetry.protocols.netflow.parser.session.Session;

import com.google.common.base.MoreObjects;

public class DataSet extends FlowSet<DataRecord> {
    private final Session.Resolver resolver;

    public final Template template;

    public final List<DataRecord> records;

    public DataSet(final Packet packet,
                   final FlowSetHeader header,
                   final Session.Resolver resolver,
                   final ByteBuffer buffer) throws InvalidPacketException, MissingTemplateException {
        super(packet, header);

        this.resolver = Objects.requireNonNull(resolver);
        this.template = this.resolver.lookupTemplate(this.header.setId);

        // For variable length fields we assume at least the length value (1 byte) to be present
        final int minimumRecordLength = this.template.stream()
                .mapToInt(f -> f.length() != DataRecord.VARIABLE_SIZED ? f.length() : 1).sum();

        final List<DataRecord> records = new LinkedList();
        while (buffer.remaining() >= minimumRecordLength) {
            records.add(new DataRecord(this, this.resolver, this.template, buffer));
        }

        if (records.size() == 0) {
            throw new InvalidPacketException(buffer, "Empty set");
        }

        this.records = Collections.unmodifiableList(records);
    }

    @Override
    public Iterator<DataRecord> iterator() {
        return this.records.iterator();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("header", header)
                .add("records", records)
                .toString();
    }
}

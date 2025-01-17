/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2014-2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.service;

import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.provision.LocationAwareDnsLookupClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultHostnameResolver implements HostnameResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultHostnameResolver.class);

    private final LocationAwareDnsLookupClient m_locationAwareDnsLookupClient;

    public DefaultHostnameResolver(LocationAwareDnsLookupClient locationAwareDnsLookupClient) {
        m_locationAwareDnsLookupClient = Objects.requireNonNull(locationAwareDnsLookupClient);
    }

    @Override
    public CompletableFuture<String> getHostnameAsync(final InetAddress addr, final String location) {
        LOG.debug("Performing reverse lookup on {} at location {}", addr, location);
        return m_locationAwareDnsLookupClient.reverseLookup(addr, location).handle((result, e) -> {
            if (e == null) {
                LOG.debug("Reverse lookup returned {} for {} at location {}", result, addr, location);
                return result;
            } else {
                LOG.warn("Reverse lookup failed for {} at location {}. Using IP address as hostname.", addr, location);
                return InetAddressUtils.str(addr);
            }
        });
    }
}

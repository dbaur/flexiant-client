/*
 * Copyright 2014 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.flexiant.domain.impl;

import de.uniulm.omi.flexiant.domain.impl.generic.ResourceInLocationImpl;
import de.uniulm.omi.flexiant.extility.Ip;
import de.uniulm.omi.flexiant.extility.IpType;
import de.uniulm.omi.flexiant.extility.NetworkType;
import de.uniulm.omi.flexiant.extility.Nic;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Wrapper for the flexiant server class.
 *
 * @see de.uniulm.omi.flexiant.extility.Server
 */
public class Server extends ResourceInLocationImpl {

    public Server(final de.uniulm.omi.flexiant.extility.Server server) {
        super(server);
        checkNotNull(server);
    }

    protected de.uniulm.omi.flexiant.extility.Server getServer() {
        return (de.uniulm.omi.flexiant.extility.Server) this.resource;
    }

    @Nullable
    public String getPublicIpAddress() {
        for (final Nic nic : this.getServer().getNics()) {
            if (nic.getNetworkType().equals(NetworkType.IP)) {
                for (final Ip ip : nic.getIpAddresses()) {
                    if (ip.getType().equals(IpType.IPV_4)) {
                        return ip.getIpAddress();
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public String getPrivateIpAddress() {
        return this.getPublicIpAddress();
    }

    @Nullable
    public String getInitialUser() {
        return this.getServer().getInitialUser();
    }

    @Nullable
    public String getInitialPassword() {
        return this.getServer().getInitialPassword();
    }
}
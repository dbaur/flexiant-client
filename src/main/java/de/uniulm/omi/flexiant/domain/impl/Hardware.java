package de.uniulm.omi.flexiant.domain.impl;

import de.uniulm.omi.flexiant.domain.api.ResourceInLocation;
import de.uniulm.omi.flexiant.extility.ProductComponent;
import de.uniulm.omi.flexiant.extility.ProductOffer;
import de.uniulm.omi.flexiant.extility.Value;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Hardware implements ResourceInLocation {

    private static final String DISK_KEY = "size";
    private static final String RAM_KEY = "ram";
    private static final String CPU_KEY = "cpu";

    private final ProductOffer disk;
    private final ProductOffer machine;
    private final String locationUUID;

    public static Hardware from(final ProductOffer machine, final ProductOffer disk, final String locationUUID) {
        return new Hardware(machine, disk, locationUUID);
    }

    public static Set<Hardware> from(final List<ProductOffer> offers) {

        checkNotNull(offers);

        Set<ProductOffer> machineOffers = new HashSet<ProductOffer>();
        Set<ProductOffer> diskOffers = new HashSet<ProductOffer>();

        for (ProductOffer productOffer : offers) {

            for (ProductComponent productComponent : productOffer.getComponentConfig()) {
                for (Value value : productComponent.getProductConfiguredValues()) {

                    if (value.getKey().equals(DISK_KEY) && value.getValue() != null) {
                        diskOffers.add(productOffer);
                    }

                    if ((value.getKey().equals(RAM_KEY) || value.getKey().equals(CPU_KEY)) && value.getValue() != null) {
                        machineOffers.add(productOffer);
                    }
                }
            }
        }

        Set<Hardware> hardware = new HashSet<Hardware>();
        for (ProductOffer machineOffer : machineOffers) {
            for (ProductOffer diskOffer : diskOffers) {
                for (String locationUUID : machineOffer.getClusters()) {
                    if (diskOffer.getClusters().contains(locationUUID)) {
                        hardware.add(Hardware.from(machineOffer, diskOffer, locationUUID));
                    }
                }
            }
        }

        return hardware;
    }

    private Hardware(final ProductOffer machine, final ProductOffer disk, final String locationUUID) {

        checkNotNull(machine);
        checkNotNull(disk);
        checkNotNull(locationUUID);
        checkArgument(!locationUUID.isEmpty());

        checkArgument(this.searchForValueInProductOffer(CPU_KEY, machine) != null, "Machine Offer does not contain cpu key.");
        checkArgument(this.searchForValueInProductOffer(RAM_KEY, machine) != null, "Machine Offer does not contain ram key.");
        checkArgument(this.searchForValueInProductOffer(DISK_KEY, disk) != null, "Disk Offer does not contain disk key.");

        checkArgument(machine.getClusters().contains(locationUUID));
        checkArgument(disk.getClusters().contains(locationUUID));

        this.machine = machine;
        this.disk = disk;
        this.locationUUID = locationUUID;
    }

    @Override
    public String getId() {
        return machine.getResourceUUID() + ":" + disk.getResourceUUID();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Hardware && ((Hardware) obj).getId().equals(this.getId()) && ((Hardware) obj).getLocationUUID().equals(this.getLocationUUID());
    }

    @Override
    public int hashCode() {
        return (this.getId() + "/" + this.getLocationUUID()).hashCode();
    }

    @Override
    public String getName() {
        return "CPU" + this.getCores() + "RAM" + this.getRam();
    }

    @Override
    public String toString() {
        return String.format("FlexiantHardware{cores=%d, ram=%d}", this.getCores(), this.getRam());
    }

    public int getCores() {
        String value = this.searchForValueInProductOffer(CPU_KEY, this.machine);
        checkNotNull(value, "Machine offer does not contain cpu key.");
        return Integer.parseInt(value);
    }

    public int getRam() {
        String value = this.searchForValueInProductOffer(RAM_KEY, this.machine);
        checkNotNull(value, "Machine offer does not contain ram key.");
        return Integer.parseInt(value);
    }

    @Nullable
    private String searchForValueInProductOffer(String key, ProductOffer productOffer) {

        checkNotNull(productOffer);
        checkNotNull(key);
        checkArgument(!key.isEmpty());

        for (ProductComponent productComponent : productOffer.getComponentConfig()) {
            for (Value value : productComponent.getProductConfiguredValues()) {
                if (value.getKey().equals(key)) {
                    return value.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getLocationUUID() {
        return this.locationUUID;
    }
}